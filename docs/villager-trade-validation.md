# Villager trade validation

The loader validates ordinary Forge trade handlers before collecting their offers,
and validates `IVillagerInfoProvider` results before publishing them. Required
stacks must contain registered items and positive quantities. An invalid required
second input rejects the entire offer; it must not become a cheaper one-input offer.

Before saving an offer, its serializable stacks are refreshed from its live stacks.
Cached offers are restored and validated individually, so a bad offer does not
discard the other trades of its profession. Vanilla and newly supplied provider
offers are already live and are not reconstructed from serialized snapshots.
Warnings identify the profession, handler and available item identifiers.

## Forge regression fixture

The fixture uses synthetic handlers, without third-party test mods. Run both steps
in the same isolated directory, with an accepted Minecraft EULA:

```text
gradlew runServer -PvillagerTradeIntegration=generate -PrunServerWorkingDirectory=build/villager-trade-integration
gradlew runServer -PvillagerTradeIntegration=cache -PrunServerWorkingDirectory=build/villager-trade-integration
```

The first step checks that valid offers survive alongside missing/unregistered
items and that only valid ordinary offers are saved. It also checks providers
with invalid offers and a live stack changed after its `TradeItem` was created.
The second step injects invalid entries into that fixture cache and checks that
both valid offers (including a two-input offer) survive without regeneration.
Provider offers must retain their current live item and quantity on cache loads.

Each successful run prints `VILLAGER_TRADE_INTEGRATION_PASS` and exits before a
world starts. The fixture JAR is included only when this property is supplied.
