# Drop generation optimization

## Design and research

Minecraft 1.7.10 executes drops as Java code. Mobs Info already replays that code
with `RandomSequencer`, so the JVM should remain responsible for executing mod
logic. A complete bytecode interpreter would duplicate JVM semantics and still
suffer from path explosion.

The initial optimization groups random values consumed by a direct comparison.
For example, `nextInt(100) == 0` has two weighted outcomes rather than 100.
Three such decisions require eight executions instead of one million. Values
stored in locals, used in arithmetic, used as item indices, or passed to another
method remain on the existing path.

Integration point: `EarlyMixinLoader.getASMTransformerClass()`. Forge's
`CoreModManager.FMLPluginWrapper.injectIntoClassLoader` registers these with
LaunchWrapper. Transform the supplied bytes, never reload a class resource or
resolve an entity hierarchy while transforming. Preserve existing stack map
frames and only recompute maximum stack size, avoiding recursive class loading.
Other transformers can run later, so compatibility with actual modpacks still
needs an in-game smoke test.

The bridge delegates to the original virtual Random method for ordinary RNGs.
Only explicitly enabled sequencers use weighted enumeration. This preserves
custom Random subclasses and random-state advancement during normal gameplay.
The transformer must not load Minecraft, configuration, or the recipe loader.

Float comparisons count the matching values of Java Random's 24-bit grid;
uniformly sampling ten decimal values is not an exact probability model.

## Implemented scope

- Direct `Random.nextInt(bound)` comparisons with an integer constant on the
  right: `==`, `!=`, `<`, `>=`, `>`, `<=`. The bound can be computed at runtime.
- Direct `Random.nextFloat()` comparisons with a float constant on the right,
  using the same six operators. Inclusive boundaries account for individual
  points of the 24-bit RNG grid, including zero and subnormal thresholds.
- Negated branches, conditional calls, existing try/catch blocks, mixed weighted
  and exhaustive choices, and repeated transformation are supported.
- Locals, arithmetic, reversed operand order, variable thresholds, double/long
  RNG calls, custom RNG call owners, and control-flow joins remain unchanged.
  This is a conservative whitelist, not a general data-flow analyzer.
- Ordinary Random instances and subclasses retain their virtual calls and RNG
  state advancement. Weighted dispatch requires the exact RandomSequencer class
  and its `useComparisonWeights` flag. Villager generation has its own opt-in
  configuration; see [villager trade generation](villager-trade-generation.md).
- Looting comparison now uses expected item counts. Observed damage variants
  accumulate probability times quantity and are converted to integer weights
  at export, with a scale of one million and a minimum weight of one per
  observed variant. This last conversion is approximate, as required by the
  existing integer wire format.

The transformer uses the ASM APIs available in Forge's ASM 5.0.3. A cheap byte
scan rejects classes without a `java/util/Random` reference before tree parsing.
Transformer errors leave the original bytes in place and emit a warning.

## Implementation sequence followed

1. Weighted integer choices, mixed with the existing sequencer, validated
   against exhaustive execution of small synthetic drop programs.
2. Direct integer comparison transformation and ordinary-RNG equivalence.
3. Direct float comparison transformation, including boundary values.
4. Generator opt-in, cache invalidation, bounded execution diagnostics, and
   review of aggregation assumptions affected by weighted paths.
5. Build, formatting, bytecode execution tests, and a documented performance
   comparison based on execution counts rather than unstable timing thresholds.

## Validation contract

The user approved tests through the public RandomSequencer API, the public
LaunchWrapper transformer boundary, and the drop collector. Tests execute transformed fixture classes
and observe results, probability distributions, exceptions, RNG state, and
enumeration cost. They do not assert instruction layouts or private helpers.

`gradlew test build` runs the JUnit suite, formatting/checkstyle checks, and the
reobfuscated jar build. The tests compare a one-million-execution reference
distribution with eight weighted executions, and verify float-grid counts,
custom RNG dispatch, subsequent RNG state, exceptions, fallback, idempotence,
coremod registration, probability precision, Looting, and damage aggregation.

A dedicated-server smoke test on Java 8 with Forge 10.13.4.1614, ASM 5.0.3 and
27 detected mods reached startup and generated 33 mob entries. A second start
read the new cache and shut down via the `stop` command. A third start with a
one-execution budget invalidated the previous cache and recorded incomplete
normal/Looting/equipment passes, while completed rare passes were not flagged.
This is a development
classpath smoke test, not a benchmark or certification of an arbitrary modpack.
Most entries in this small setup are providers or predefined vanilla recipes;
the execution-count comparison above uses synthetic loot code.

## Configuration and cache

In the `MobHandler` category:

- `OptimizeRandomComparisons=true` enables weighted choices for mob generation.
  Set false and restart to use the old RNG domains through the same hooks.
- `MaxEstimatedPathsPerMobPass=10000000` rejects a category/Looting variant
  when a completed path projects more than this many leaves. Zero or a negative
  number disables this estimate limit. The old MaxPathsPerMobPass setting is no
  longer read; this is not a limit on executions already performed.
- `MobTimeout` remains a time limit per category/variant. A negative value
  disables the time limit; it does not disable the independent path limit.
- `Debug.LoggingLevel=1` reports executions, estimated paths, time, enumerated RNG weight,
  maximum RNG depth (normal/rare passes), and weighted comparison evaluations.

To disable bytecode transformation itself, including when diagnosing conflicts
with other transformers or injection points, start the JVM with
`-Dmobsinfo.disableRandomComparisonTransformer=true`. The configuration flag
alone does not remove hooks from already loaded classes.

The cache includes a generator format version, the effective optimization mode,
both budgets, and categories stopped by a budget. A format or setting change
regenerates it even under `CacheRegenerationTrigger=Never`; that setting still
suppresses mod-list version checks. An unchanged partial cache is reused with
a warning rather than repeatedly delaying every startup. Increase the budgets
to retry it. Partial probabilities are not renormalized or presented as a
complete enumeration in the generation logs; the existing NEI display does not
yet expose this completeness metadata.

The intent of the old `chance < 1e-7` early exit is preserved: abandon a huge
search as soon as it is observed, rather than first executing millions of paths.
After each call, RandomSequencer.estimatedPathCount multiplies the branching
factors of that path. For ordinary uniform choices, the default threshold
corresponds to the old inverse-chance threshold. Weighted comparisons contribute
one or two representatives, so a very rare outcome does not imply expensive work.
Products saturate at Long.MAX_VALUE. Conditional RNG calls mean this is a
projection, not an exact count of unexplored leaves; it can overestimate or
underestimate total work. Later paths are checked too, and the timeout remains
a fallback. Checks happen after checking for completion and cannot interrupt
an individual mod method that never returns. The separate
[random-call budget](generation-budget-tests.md) interrupts methods that repeatedly
call the sequencer, including transformed comparisons, within one path.

Generator version 3 invalidates caches from the earlier execution-cap policy.
Version 4 additionally regenerates caches after integrating the per-round call
budget and the record of mobs skipped by that budget.
The estimate uses a separate configuration key so a saved one-million execution
limit does not silently become a one-million estimate limit. With the default
ten-million estimate limit, ordinary uniform enumeration with 1,555,200 paths
is allowed again (subject to timeout); a 60,466,176-path sequence stops after
the first observed path. Early rejection retains only the outcomes observed
so far, without renormalizing their weights.

A Forge smoke test with an intentionally low estimate threshold of 1 generated
347 mob entries and logged early rejection after one execution for affected
normal, Looting and additional passes. The cache stored generatorVersion 3 and
the estimate threshold. The server reached startup and shut down via `stop`.

## Limits

This does not enumerate arbitrary external RNGs, restore arbitrary mutable mob
state, infer all event-handler drops, or solve arbitrary loops. Exactness of
enumeration still assumes repeatable code under a fixed random prefix. The
legacy fallback and its approximations remain relevant for unsupported code.
In particular, the logged enumerated RNG weight is relative to the controlled
RNG model, not a proof of coverage of every possible in-game world state.

## Follow-up work

Use detailed logs on the target pack to find the remaining expensive entities.
Only then add recognizers for frequent helper methods or more involved value
flows. Full symbolic execution and Monte Carlo fallback are deliberately beyond
this implementation. Existing assumptions about mutable mob state, arbitrary
external RNGs, and the recipe format's conflation of expected count with drop
probability are not resolved by grouping comparisons.
