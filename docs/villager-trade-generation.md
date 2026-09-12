# Weighted villager trade generation

Villager handlers use the same bytecode comparison hooks and RandomSequencer as
mob drops. Direct comparisons of `Random.nextInt(n)` or `nextFloat()` against
constants can therefore use weighted outcomes without another transformer.
The supported patterns and bytecode compatibility limits are described in
[the mob generation design](drop-generation-optimization.md).

## Execution and aggregation

`VillagerTradesLoader.generateTradesForHandler` is the boundary for enumerating
one `IVillageTradeHandler`. It executes the actual handler with a fresh
MerchantRecipeList per path, uses the path probability when collecting trades,
and reports executions, maximum observed path-count estimate, summed RNG weight, weighted comparison evaluations,
and whether enumeration finished. The sequencer is reset in a finally block,
including after a handler throws. It retains its configured comparison mode.

The full loader supplies the sequencer to the handler, the dummy world and the
simulation villager's RNG. Vanilla trades remain predefined, and
IVillagerInfoProvider implementations retain their provider path.

Offers still merge by input/output item identity, metadata and NBT. Possible
stack sizes remain sets in the existing trade format, not weighted size
distributions or a joint distribution of prices. Repeated offers use the current
stacks for lookup and quantity updates. Only new offers retain copied ItemStacks
and NBT; enchantment normalization also operates on a copy. A handler reusing
mutable stacks cannot overwrite earlier outcomes or invalidate stored NBT keys.

Large searches are rejected early. After each handler call, the sequencer
multiplies the current path's branching factors and compares that estimate with
the configured limit. This restores the intent of the old small-leaf-probability
cutoff, while counting a weighted comparison as only one or two representatives.
A rare offer with two weighted outcomes does not count as millions of paths.
The estimate is exact for a fixed sequence of uniform choices, but conditional
RNG calls can make it overestimate or underestimate the full tree. Later paths
are checked too; the time budget remains a fallback. Checks cannot interrupt a
single nonterminating handler. The separate sequencer call budget can interrupt
repeated RNG calls within that handler, including weighted comparisons.
Partial results keep their observed weights;
they are never renormalized to look complete.

## Configuration

These settings belong to the `villagertradeshandler` section of
`config/mobsinfo/mobsinfo.cfg`, separately from `mobhandler`:

```properties
B:OptimizeRandomComparisons=true
D:HandlerTimeout=10.0
I:MaxEstimatedPathsPerHandler=10000000
```

The optimization is enabled by default. A negative HandlerTimeout disables the
time budget; a nonpositive MaxEstimatedPathsPerHandler disables the estimate limit.
The former MaxPathsPerHandler setting is no longer read. The separate key avoids
reinterpreting a saved execution budget as an early rejection threshold.
Both apply per handler instance and profession. Handlers taking at least 100 ms
log their time, execution count, estimated paths, RNG weight and weighted comparisons at INFO.
`Debug.LoggingLevel=1` reports these counters for all handlers; incomplete
enumeration always produces a warning.

The shared JVM argument `-Dmobsinfo.disableRandomComparisonTransformer=true`
disables transformation for both mobs and villagers. The configuration switches
only control the use of weighted choices, not the presence of bytecode hooks.

## Cache

`VillagerTradesLoader.cache` now stores the generator version, effective
optimization mode, budgets and an incomplete flag for each cached handler.
Generator version 2 invalidates caches from the previous execution-cap policy.
Version 3 regenerates caches after integrating the per-round RNG call budget
and validation of malformed offers from master. Validation runs before aggregation
and when restoring cached offers, while retaining the lazy stack-copy behavior.
Changing the mode or budgets, or reading an older cache format, regenerates the
trade cache. This includes CacheRegenerationTrigger=Never: that setting bypasses
mod-list changes but not cache-format/settings compatibility checks.

Unchanged partial caches are reused with a warning to avoid repeated startup
delays. Increasing the budgets retries generation. Completeness is visible in
the cache and generation logs, not currently in the NEI offer tooltip.
Changing the villager-specific settings does not invalidate the mob-drop cache.

## Validation and practical limits

Tests run actual IVillageTradeHandler fixtures through the public generation
boundary, including classes transformed by ASM. They cover:

- Identical offer probabilities with 8,000 exhaustive executions versus 8
  weighted executions for three direct integer conditions.
- Correct probability for a 2.5% float threshold using the Java RNG grid.
- Random prices, optional second inputs and retained quantity variants.
- Mutable stacks and NBT reused across executions, including preservation of
  handler-owned enchantment tags during normalization.
- Early rejection of excessive estimates, overflow-safe estimation, rare weighted
  offers within the limit, and detection of a costly later branch.
- Partial results under estimate/time budgets and complete results at a budget
  boundary.
- Sequencer cleanup and subsequent generation after a failing handler.

The Forge registry setup in the test source set supplies two fixture items
without booting the full LaunchWrapper lifecycle. It is not packaged in the mod.

A dedicated-server smoke test exercised the real Tinkers' Construct handler,
the new cache and the execution budget. Changing the comparison mode regenerated
the cache, while a subsequent unchanged run reused it and reported its incomplete
handler. All three runs shut down via the server stop command.
Its direct weighted-comparison count
was zero: that handler builds trades primarily by choosing ingredients and
quantities. This optimization does not solve that combinatorial cost.
That smoke test covered the earlier execution-cap implementation. With the early
estimate policy, large unsupported handlers can be rejected after their first path.
A subsequent Forge smoke test confirmed this for TVillageTrades: one execution,
an estimated 549,378,366,500,390,625 paths and a partial cache. The old trade cache
was invalidated; the next unchanged run reused the new partial cache with a warning.

Likewise, no improvement is expected when a pack's slow code has no supported
comparison patterns. Mutable villager/world state, external RNG sources and
cross-handler interactions remain limitations of the original simulation model.

## GTNH regression investigation

The supplied GTNH startup logs measured 4,653 ms with the old generator and
5,992 ms with trade generator version 1. The old cache has no generatorVersion field, so
these runs are not an on/off comparison of weighted choices with equal budgets.
Wizard (profession 190) previously stopped at the tiny-path cutoff and recorded
16 offers. The new run reached one million executions and recorded 25 offers,
but covered only 1.65% of RNG weight. Both Wizard and Heretic reported zero
weighted comparisons: their expensive enumeration is not reduced by these hooks.
The same startup log contains 1,145 transformed-class entries and 2,627 comparison
sites, including three sites in HEE's WorldLoot. Transformation is active, but
class-wide site counts include world generation, AI and rendering; they do not
measure work saved during recipe enumeration. The supplied Thaumcraft JAR's
Wizard trade method has 14 nextInt calls used for quantities and item metadata,
with no comparisons of their results. Their bounds multiply to 60,466,176 paths.
The running pack can further modify that method through other transformers.

Heretic (profession 666) was a separate completeness regression caused by the version 1
execution budget. Its nine offers previously had weight approximately 1; the
one-million-path cache has weight 0.6430041152 for each. The supplied Forbidden
Magic bytecode has 1,555,200 combinations of quantities. Version 2 replaces
the execution cap with an early estimate threshold of 10,000,000. This allows
Heretic to finish, provided HandlerTimeout is not reached, and rejects Wizard
after its first path. This policy applies with weighted comparisons both on and off.

To reduce aggregation overhead, repeated offers no longer copy every stack/NBT
or allocate a second MerchantRecipe for collection. A Java 17 synthetic benchmark
of one million paths (one reused offer with NBT and two varying quantities),
four warmups and five measured runs, changed median time from 364.7 to 249.5 ms
and thread allocations from 748.1 to 332.1 MB. Both versions produced the same
complete offer, probability and quantity sets. This measures the collector
change, not a confirmed improvement to total GTNH startup time.

For an ASM comparison, use the same new JAR and budgets in both runs and change
only OptimizeRandomComparisons. That change invalidates the trade cache. To
measure a collector-only update with unchanged settings, rename the trade cache
before launch. The earlier collector-only refactor preserved the cache version;
the subsequent early-estimate policy increments it and regenerates automatically.
