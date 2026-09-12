# Mob generation blacklist

`MobHandler.MobBlacklist` uses exact entity registry names. Excluded mobs are
skipped before the loader constructs their entities or calculates their drops,
and their recipes are skipped when reading an existing cache. The filter also
applies before selecting a precomputed vanilla recipe for the output map.

The client uses its local blacklist. If a server requests a locally excluded mob,
the existing missing-recipe guard skips that entry instead of disconnecting.
Restart the game after changing the blacklist so the local recipe map is rebuilt
or reloaded with the new settings.

The cache records exclusions used during generation:

- Adding exclusions can reuse a broader cache, filtering the newly excluded mobs.
- Removing an exclusion used during generation regenerates the cache to restore
  the missing recipe, even with `CacheRegenerationTrigger=Never`.
- Reordering or duplicating blacklist entries has no effect on cache validity.
- Older caches without this information are regenerated once.

## Verification

The Forge fixture contains two synthetic mobs and requires no additional test mod
dependencies. In an isolated directory with an accepted Minecraft EULA, run:

```text
gradlew runServer -PmobBlacklistIntegration=generate -PrunServerWorkingDirectory=build/mob-blacklist-integration
gradlew runServer -PmobBlacklistIntegration=unblock -PrunServerWorkingDirectory=build/mob-blacklist-integration
gradlew runServer -PmobBlacklistIntegration=cache -PrunServerWorkingDirectory=build/mob-blacklist-integration
```

These steps verify skipping before construction, regenerating a newly enabled
mob, and filtering an existing cache without rerunning the healthy mob's drops.
Each prints `MOB_BLACKLIST_INTEGRATION_PASS` and exits before starting a world.
`MobRecipeLoaderMissingRecipeTest` covers server entries absent from the client's
local map through the public client processing entry point.
