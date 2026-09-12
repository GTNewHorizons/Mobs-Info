# Random generation safety

`RandomSequencer` limits a round to 10,000 calls across `nextInt(bound)`,
`nextFloat()`, `nextBoolean()` and the weighted comparison entry points
`nextIntCompared()` / `nextFloatCompared()`. Forced results count too. Exceeding the limit
clears the recorded choices and throws `GenerationLimitExceededException`.
`newRound()` and `nextRound()` reset the call budget.

This prevents retry-until-success loops from exhausting the heap when enumeration
repeatedly supplies a rejected value. It does not interrupt code that makes no
calls to these methods, or calculate the missing distribution of an aborted mob.

The mob loader skips the failed mob, cleans up generation state and continues.
The cache records mobs skipped by this limit and warns when that cache is loaded.
The safeguard works without weighted random comparison optimization.
Weighted comparison calls share the same budget as ordinary draws; a comparison
delegating to an ordinary draw counts once. This per-call safeguard complements
the early path-count estimate: it can abort a retry loop before its first path
returns, while the estimate rejects excessive branching between completed paths.

## Verification

Run the unit tests with:

```text
gradlew test --tests "*RandomSequencerBudgetTest"
```

The Forge fixture exercises the public `generateMobRecipeMap()` entry point with
real mixins. It checks drop, constructor and equipment retry loops, followed by
healthy mobs whose guaranteed drops must survive. When ManaMetal is installed,
it also runs the reported Nightmare mob and checks that generation continues.
It also checks that a mob whose name lookup throws cannot abort the batch,
using a self-contained fixture that does not require Pokecube Legacy.

```text
gradlew runServer -PgenerationBudgetIntegration -PrunServerWorkingDirectory=build/generation-budget-integration
```

Use an isolated server directory with an accepted Minecraft EULA. The fixture
exits before starting a world. Success prints `GENERATION_BUDGET_INTEGRATION_PASS`
and, when ManaMetal was tested, `MANAMETAL_GENERATION_BUDGET_PASS`.
The fixture JAR is only added with this property and is never published with the mod.
