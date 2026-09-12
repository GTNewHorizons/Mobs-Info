package com.kuba6000.mobsinfo.loader;

import static com.kuba6000.mobsinfo.MobsInfo.MODID;
import static com.kuba6000.mobsinfo.loader.MobRecipeLoader.randomEnchantmentDetectedString;

import java.io.File;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Multimap;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.kuba6000.mobsinfo.api.ConstructableItemStack;
import com.kuba6000.mobsinfo.api.DummyWorld;
import com.kuba6000.mobsinfo.api.IVillagerInfoProvider;
import com.kuba6000.mobsinfo.api.RandomSequencer;
import com.kuba6000.mobsinfo.api.VillagerRecipe;
import com.kuba6000.mobsinfo.api.VillagerTrade;
import com.kuba6000.mobsinfo.api.utils.GSONUtils;
import com.kuba6000.mobsinfo.api.utils.ItemID;
import com.kuba6000.mobsinfo.api.utils.ModUtils;
import com.kuba6000.mobsinfo.config.Config;
import com.kuba6000.mobsinfo.mixin.early.minecraft.EntityAccessor;
import com.kuba6000.mobsinfo.mixin.early.minecraft.VillagerRegistryAccessor;
import com.kuba6000.mobsinfo.nei.VillagerTradesHandler;
import com.kuba6000.mobsinfo.network.LoadConfigPacket;

import cpw.mods.fml.common.ProgressManager;
import cpw.mods.fml.common.registry.VillagerRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class VillagerTradesLoader {

    private static final Logger LOG = LogManager.getLogger(MODID + "[Villager Recipe Loader]");

    private static boolean alreadyGenerated = false;
    private static final int GENERATOR_VERSION = 3;

    public static final class TradeGenerationResult {

        public final ArrayList<VillagerTrade> trades;
        public final boolean complete;
        public final long executions;
        public final double enumeratedWeight;
        public final long comparisonCalls;
        public final long estimatedPaths;

        private TradeGenerationResult(ArrayList<VillagerTrade> trades, boolean complete, long executions,
            double enumeratedWeight, long comparisonCalls, long estimatedPaths) {
            this.trades = trades;
            this.complete = complete;
            this.executions = executions;
            this.enumeratedWeight = enumeratedWeight;
            this.comparisonCalls = comparisonCalls;
            this.estimatedPaths = estimatedPaths;
        }
    }

    /**
     * Enumerates one handler using the supplied sequencer's comparison mode. The caller supplies
     * the simulation villager/world and is responsible for their state. Providers bypass this path.
     * A nonpositive estimated path limit or negative timeout disables that budget. Limits are checked between
     * handler calls and cannot interrupt a handler that does not return. Partial weights are retained.
     */
    public static TradeGenerationResult generateTradesForHandler(VillagerRegistry.IVillageTradeHandler handler,
        EntityVillager villager, RandomSequencer random, long maxEstimatedPaths, double timeoutSeconds) {
        TradeList trades = new TradeList();
        TradeCollector collector = new TradeCollector();
        long executions = 0;
        long estimatedPaths = 1;
        double enumeratedWeight = 0;
        boolean complete = false;
        final long start = System.nanoTime();
        final int profession = villager == null ? -1 : villager.getProfession();
        final String handlerName = handler.getClass()
            .getName();
        random.newRound();
        try {
            while (true) {
                MerchantRecipeList list = new MerchantRecipeList();
                handler.manipulateTradesForVillager(villager, list, random);
                collector.collectTrades(trades, list, random.chance, profession, handlerName);
                executions++;
                enumeratedWeight += random.chance;
                estimatedPaths = Math.max(estimatedPaths, random.estimatedPathCount());
                if (!random.nextRound()) {
                    complete = true;
                    break;
                }
                if ((maxEstimatedPaths > 0 && estimatedPaths > maxEstimatedPaths)
                    || (timeoutSeconds >= 0 && System.nanoTime() - start >= (long) (timeoutSeconds * 1e9))) break;
            }
            ArrayList<VillagerTrade> results = new ArrayList<>();
            for (TradeInstance value : trades.itemsToTrade.values()) {
                VillagerTrade trade = new VillagerTrade(value.i1, value.i2, value.o, value.chance);
                if (prepareTrade(trade, profession, handlerName)) results.add(trade);
            }
            return new TradeGenerationResult(
                results,
                complete,
                executions,
                enumeratedWeight,
                random.comparisonCalls,
                estimatedPaths);
        } finally {
            random.newRound();
        }
    }

    private static class VillagerTradesLoaderCacheStructure {

        private static class VillagerTradesLoaderCacheStructure_Handler {

            String handler;
            boolean incomplete;
            ArrayList<VillagerTrade> tradeList;
        }

        String version;
        int generatorVersion;
        boolean comparisonWeights;
        double timeout;
        int maxEstimatedPaths;
        Map<Integer, ArrayList<VillagerTradesLoaderCacheStructure_Handler>> handlerList;
    }

    public static void generateVillagerTrades() {
        if (alreadyGenerated) return;
        alreadyGenerated = true;

        if (!Config.VillagerTradesHandler.enabled) return;

        VanillaVillagerTradesLoader.init();

        LOG.info("Generating Recipe Map for Villager Trades Handler");
        final long startTime = System.currentTimeMillis();

        DummyWorld world = new DummyWorld();

        RandomSequencer frand = new RandomSequencer();
        frand.useComparisonWeights = Config.VillagerTradesHandler.optimizeRandomComparisons
            && !Boolean.getBoolean("mobsinfo.disableRandomComparisonTransformer");
        world.rand = frand;

        File cache = Config.getConfigFile("VillagerTradesLoader.cache");
        Gson gson = GSONUtils.GSON_BUILDER.create();

        String modlistversion;
        if (Config.MobHandler.regenerationTrigger == Config.MobHandler._CacheRegenerationTrigger.ModAdditionRemoval)
            modlistversion = ModUtils.getModListVersionForVillagerRecipes(false);
        else modlistversion = ModUtils.getModListVersionForVillagerRecipes(true);

        final VillagerRegistry villagerRegistry = VillagerRegistry.instance();
        final Multimap<Integer, VillagerRegistry.IVillageTradeHandler> tradeHandlerMap = ((VillagerRegistryAccessor) villagerRegistry)
            .getTradeHandlers();
        final HashMap<String, ArrayList<VillagerRegistry.IVillageTradeHandler>> classNameToHandlerInstances = new HashMap<>();
        for (VillagerRegistry.IVillageTradeHandler value : tradeHandlerMap.values()) {
            classNameToHandlerInstances.computeIfAbsent(
                value.getClass()
                    .getName(),
                k -> new ArrayList<>())
                .add(value);
        }

        MobRecipeLoader.isInGenerationProcess = true;

        if (Config.MobHandler.regenerationTrigger != Config.MobHandler._CacheRegenerationTrigger.Always
            && cache.exists()) {
            LOG.info("Parsing Cached map");
            Reader reader = null;
            try {
                reader = Files.newReader(cache, StandardCharsets.UTF_8);
                VillagerTradesLoaderCacheStructure s = gson.fromJson(reader, VillagerTradesLoaderCacheStructure.class);
                if (s.generatorVersion == GENERATOR_VERSION && s.comparisonWeights == frand.useComparisonWeights
                    && s.timeout == Config.VillagerTradesHandler.handlerTimeout
                    && s.maxEstimatedPaths == Config.VillagerTradesHandler.maxEstimatedPathsPerHandler
                    && (Config.MobHandler.regenerationTrigger == Config.MobHandler._CacheRegenerationTrigger.Never
                        || modlistversion.equals(s.version))) {
                    ProgressManager.ProgressBar bar = ProgressManager
                        .push("Parsing cached Villager Trades Map", s.handlerList.size());
                    for (Map.Entry<Integer, ArrayList<VillagerTradesLoaderCacheStructure.VillagerTradesLoaderCacheStructure_Handler>> entry : s.handlerList
                        .entrySet()) {
                        int profession = entry.getKey();
                        bar.step("Profession " + profession);
                        try {
                            ArrayList<VillagerTrade> trades = new ArrayList<>();
                            if (profession >= 0 && profession <= 4) {
                                trades.addAll(VanillaVillagerTradesLoader.vanillaTrades.get(profession));
                            }
                            ArrayList<VillagerTradesLoaderCacheStructure.VillagerTradesLoaderCacheStructure_Handler> handlers = entry
                                .getValue();
                            EntityVillager villager = new EntityVillager(world);
                            villager.setProfession(profession);
                            for (VillagerTradesLoaderCacheStructure.VillagerTradesLoaderCacheStructure_Handler handler : handlers) {
                                if (handler.incomplete) LOG.warn(
                                    "Cached trades for profession {} handler {} are incomplete; increase HandlerTimeout/MaxEstimatedPathsPerHandler to regenerate",
                                    profession,
                                    handler.handler);
                                if (handler.tradeList == null) { // provider
                                    ArrayList<VillagerRegistry.IVillageTradeHandler> tradeHandlers = classNameToHandlerInstances
                                        .get(handler.handler);
                                    if (tradeHandlers != null && !tradeHandlers.isEmpty()
                                        && tradeHandlers.get(0) instanceof IVillagerInfoProvider) {
                                        for (VillagerRegistry.IVillageTradeHandler tradeHandler : tradeHandlers) {
                                            ((IVillagerInfoProvider) tradeHandler)
                                                .provideTrades(villager, profession, trades);
                                            trades.removeIf(trade -> !prepareTrade(trade, profession, handler.handler));
                                        }
                                    }
                                } else {
                                    for (VillagerTrade trade : handler.tradeList) {
                                        if (restoreTrade(trade, profession, handler.handler)) trades.add(trade);
                                    }
                                }
                            }
                            VillagerRecipe.recipes.put(profession, new VillagerRecipe(trades, profession, villager));
                        } catch (Exception ignored) {}
                    }
                    ProgressManager.pop(bar);
                    LOG.info("Parsed cached map, skipping generation");
                    MobRecipeLoader.isInGenerationProcess = false;
                    return;
                } else {
                    LOG.info("Cached map version mismatch, generating a new one");
                }
            } catch (Exception ignored) {
                LOG.warn("There was an exception while parsing cached map, generating a new one");
            } finally {
                if (reader != null) try {
                    reader.close();
                } catch (Exception ignored) {}
            }
        } else {
            LOG.info("Cached map doesn't exist or config option forced, generating a new one");
        }

        final VillagerTradesLoaderCacheStructure toCache = new VillagerTradesLoaderCacheStructure();
        toCache.version = modlistversion;
        toCache.generatorVersion = GENERATOR_VERSION;
        toCache.comparisonWeights = frand.useComparisonWeights;
        toCache.timeout = Config.VillagerTradesHandler.handlerTimeout;
        toCache.maxEstimatedPaths = Config.VillagerTradesHandler.maxEstimatedPathsPerHandler;
        toCache.handlerList = new HashMap<>();

        LOG.info("Generating villager recipes");

        final ArrayList<Integer> villagerIDs = new ArrayList<>(Arrays.asList(0, 1, 2, 3, 4));
        villagerIDs.addAll(VillagerRegistry.getRegisteredVillagers());

        ProgressManager.ProgressBar bar = ProgressManager.push("Generating Villager Traders Map", villagerIDs.size());

        for (final int id : villagerIDs) {
            bar.step("Profession " + id);
            try {

                EntityVillager villager = new EntityVillager(world);
                villager.setProfession(id);

                ArrayList<VillagerTrade> recipes = new ArrayList<>();

                if (id >= 0 && id <= 4) {
                    // Provide vanilla trades
                    recipes.addAll(VanillaVillagerTradesLoader.vanillaTrades.get(id));
                }

                // Custom handlers

                Collection<VillagerRegistry.IVillageTradeHandler> handlers = tradeHandlerMap.get(id);
                if (handlers == null || handlers.isEmpty()) {
                    LOG.info("Didn't found any registered handlers for profession {}", id);
                    VillagerRecipe.recipes.put(id, new VillagerRecipe(recipes, id, villager));
                    toCache.handlerList.put(id, new ArrayList<>(0));
                    continue;
                }

                LOG.info("Generating recipes from registered handlers for profession {} handlers: ", id);
                for (VillagerRegistry.IVillageTradeHandler handler : handlers) {
                    LOG.info(
                        " - {}{}",
                        handler.getClass()
                            .getName(),
                        handler instanceof IVillagerInfoProvider ? "(provider)" : "");
                }

                final ArrayList<VillagerTradesLoaderCacheStructure.VillagerTradesLoaderCacheStructure_Handler> handlersToCache = new ArrayList<>(
                    handlers.size());
                toCache.handlerList.put(id, handlersToCache);
                final HashMap<String, VillagerTradesLoaderCacheStructure.VillagerTradesLoaderCacheStructure_Handler> classNameToHandlerCacheHelper = new HashMap<>();

                frand.newRound();

                for (VillagerRegistry.IVillageTradeHandler handler : handlers) {
                    VillagerTradesLoaderCacheStructure.VillagerTradesLoaderCacheStructure_Handler handlerToCache = classNameToHandlerCacheHelper
                        .get(
                            handler.getClass()
                                .getName());
                    if (handlerToCache == null) {
                        handlerToCache = new VillagerTradesLoaderCacheStructure.VillagerTradesLoaderCacheStructure_Handler();
                        handlerToCache.handler = handler.getClass()
                            .getName();
                        handlerToCache.tradeList = new ArrayList<>();
                        handlersToCache.add(handlerToCache);
                        classNameToHandlerCacheHelper.put(handlerToCache.handler, handlerToCache);
                    }

                    if (handler instanceof IVillagerInfoProvider provider) {
                        provider.provideTrades(villager, id, recipes);
                        String handlerName = handlerToCache.handler;
                        recipes.removeIf(trade -> !prepareTrade(trade, id, handlerName));
                        handlerToCache.tradeList = null;
                        continue;
                    }
                    ((EntityAccessor) villager).setRand(frand);
                    final long handlerStart = System.nanoTime();
                    TradeGenerationResult generated = generateTradesForHandler(
                        handler,
                        villager,
                        frand,
                        Config.VillagerTradesHandler.maxEstimatedPathsPerHandler,
                        Config.VillagerTradesHandler.handlerTimeout);
                    handlerToCache.incomplete |= !generated.complete;
                    final double handlerMillis = (System.nanoTime() - handlerStart) / 1_000_000d;
                    if (!generated.complete) LOG.warn(
                        "Profession {} handler {} enumeration incomplete: {} executions, estimated paths {}, enumerated RNG weight {}, weighted comparisons {}",
                        id,
                        handlerToCache.handler,
                        generated.executions,
                        generated.estimatedPaths,
                        generated.enumeratedWeight,
                        generated.comparisonCalls);
                    if (handlerMillis >= 100 || Config.Debug.loggingLevel == Config.Debug.LoggingLevel.Detailed)
                        LOG.info(
                            "Profession {} handler {}: {} executions in {} ms, estimated paths {}, enumerated RNG weight {}, weighted comparisons {}",
                            id,
                            handlerToCache.handler,
                            generated.executions,
                            handlerMillis,
                            generated.estimatedPaths,
                            generated.enumeratedWeight,
                            generated.comparisonCalls);
                    recipes.addAll(generated.trades);
                    handlerToCache.tradeList.addAll(generated.trades);
                }

                VillagerRecipe.recipes.put(id, new VillagerRecipe(recipes, id, villager));
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        MobRecipeLoader.isInGenerationProcess = false;

        ProgressManager.pop(bar);

        final long endTime = System.currentTimeMillis();
        LOG.info(
            "Villager trades generation took {} ms, mapped {} recipes in total",
            endTime - startTime,
            VillagerRecipe.recipes.size());

        LOG.info("Saving generated map to file");
        Writer writer = null;
        try {
            writer = Files.newWriter(cache, StandardCharsets.UTF_8);
            gson.toJson(toCache, writer);
            writer.flush();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (writer != null) try {
                writer.close();
            } catch (Exception ignored) {}
        }
    }

    public static void processVillagerTrades() {
        LoadConfigPacket.instance.villagersToLoad.clear();
        for (VillagerRecipe value : VillagerRecipe.recipes.values()) {
            LoadConfigPacket.instance.villagersToLoad.add(value.profession);
        }
    }

    @SideOnly(Side.CLIENT)
    public static void processVillagerTrades(Set<Integer> villagersToLoad) {
        VillagerTradesHandler.clearRecipes();

        for (int i : villagersToLoad) {
            VillagerTradesHandler.addRecipe(VillagerRecipe.recipes.get(i));
        }
        VillagerTradesHandler.sortCachedRecipes();
    }

    private static class TradeInstance {

        VillagerTrade.TradeItem i1;
        VillagerTrade.TradeItem i2;
        VillagerTrade.TradeItem o;
        double chance = 0;

        void update(ItemStack first, ItemStack second, ItemStack output) {
            ItemStack item = first;
            if (item.stackSize != i1.stack.stackSize) {
                if (i1.possibleSizes == null) i1.possibleSizes = new HashSet<>();
                i1.possibleSizes.add(i1.stack.stackSize);
                i1.possibleSizes.add(item.stackSize);
            }
            item = second;
            if (item != null && item.stackSize != i2.stack.stackSize) {
                if (i2.possibleSizes == null) i2.possibleSizes = new HashSet<>();
                i2.possibleSizes.add(i2.stack.stackSize);
                i2.possibleSizes.add(item.stackSize);
            }
            item = output;
            if (item.stackSize != o.stack.stackSize) {
                if (o.possibleSizes == null) o.possibleSizes = new HashSet<>();
                o.possibleSizes.add(o.stack.stackSize);
                o.possibleSizes.add(item.stackSize);
            }
        }
    }

    private static class TradeList {

        HashMap<Pair<Pair<ItemID, ItemID>, ItemID>, TradeInstance> itemsToTrade = new HashMap<>();

        private Pair<Pair<ItemID, ItemID>, ItemID> key(ItemStack first, ItemStack second, ItemStack output) {
            return Pair.of(
                Pair.of(ItemID.createNoCopy(first), second == null ? null : ItemID.createNoCopy(second)),
                ItemID.createNoCopy(output));
        }

        TradeInstance addOrMerge(ItemStack first, ItemStack second, ItemStack output, double chance) {
            // Borrow handler stacks only for the lookup. Stored keys must own their NBT.
            TradeInstance instance = itemsToTrade.get(key(first, second, output));
            if (instance != null) {
                instance.update(first, second, output);
                instance.chance += chance;
            } else {
                instance = new TradeInstance();
                instance.i1 = new VillagerTrade.TradeItem(first.copy());
                instance.i2 = second == null ? null : new VillagerTrade.TradeItem(second.copy());
                instance.o = new VillagerTrade.TradeItem(output.copy());
                instance.chance = chance;
                itemsToTrade.put(
                    key(instance.i1.stack, instance.i2 == null ? null : instance.i2.stack, instance.o.stack),
                    instance);
            }
            return instance;
        }
    }

    private static class TradeCollector {

        void collectTrades(TradeList trades, MerchantRecipeList recipeList, double chance, int profession,
            String handler) {
            for (MerchantRecipe recipe : (ArrayList<MerchantRecipe>) recipeList) {
                // Most replays merge an existing offer. Copy only to normalize enchantments;
                // addOrMerge takes owned snapshots when it retains a new offer.
                if (recipe == null) {
                    LOG.warn("Skipping null villager trade: profession {}, handler {}", profession, handler);
                    continue;
                }
                ItemStack i1 = recipe.getItemToBuy();
                ItemStack i2 = recipe.hasSecondItemToBuy() ? recipe.getSecondItemToBuy() : null;
                ItemStack o = recipe.getItemToSell();
                if (!validStack(i1) || !validStack(o) || (recipe.hasSecondItemToBuy() && !validStack(i2))) {
                    LOG.warn(
                        "Skipping invalid villager trade: profession {}, handler {}, inputs [{}; {}], output {}",
                        profession,
                        handler,
                        describeStack(i1),
                        describeStack(i2),
                        describeStack(o));
                    continue;
                }
                boolean i1randomchomenchantdetected = i1.hasTagCompound()
                    && i1.stackTagCompound.hasKey(randomEnchantmentDetectedString);
                int i1randomenchantmentlevel = 0;
                if (i1randomchomenchantdetected) {
                    i1 = i1.copy();
                    i1randomenchantmentlevel = i1.stackTagCompound.getInteger(randomEnchantmentDetectedString);
                    i1.stackTagCompound.removeTag("ench");
                    i1.stackTagCompound.setInteger(randomEnchantmentDetectedString, 0);
                }
                boolean i2randomchomenchantdetected = i2 != null && i2.hasTagCompound()
                    && i2.stackTagCompound.hasKey(randomEnchantmentDetectedString);
                int i2randomenchantmentlevel = 0;
                if (i2randomchomenchantdetected) {
                    i2 = i2.copy();
                    i2randomenchantmentlevel = i2.stackTagCompound.getInteger(randomEnchantmentDetectedString);
                    i2.stackTagCompound.removeTag("ench");
                    i2.stackTagCompound.setInteger(randomEnchantmentDetectedString, 0);
                }
                boolean orandomchomenchantdetected = o.hasTagCompound()
                    && o.stackTagCompound.hasKey(randomEnchantmentDetectedString);
                int orandomenchantmentlevel = 0;
                if (orandomchomenchantdetected) {
                    o = o.copy();
                    orandomenchantmentlevel = o.stackTagCompound.getInteger(randomEnchantmentDetectedString);
                    o.stackTagCompound.removeTag("ench");
                    o.stackTagCompound.setInteger(randomEnchantmentDetectedString, 0);
                }
                TradeInstance instance = trades.addOrMerge(i1, i2, o, chance);
                if (i1randomchomenchantdetected) instance.i1.enchantability = i1randomenchantmentlevel;
                if (i2randomchomenchantdetected) instance.i2.enchantability = i2randomenchantmentlevel;
                if (orandomchomenchantdetected) instance.o.enchantability = orandomenchantmentlevel;
            }
        }

    }

    private static boolean validStack(ItemStack stack) {
        if (stack == null || stack.getItem() == null || stack.stackSize <= 0) return false;
        String name = Item.itemRegistry.getNameForObject(stack.getItem());
        return name != null && Item.itemRegistry.getObject(name) == stack.getItem();
    }

    private static boolean prepareTrade(VillagerTrade trade, int profession, String handler) {
        if (trade == null || !validTradeItem(trade.getFirstInput())
            || !validTradeItem(trade.getOutput())
            || (trade.hasSecondInput() && !validTradeItem(trade.getSecondInput()))) {
            LOG.warn(
                "Skipping invalid villager trade: profession {}, handler {}, inputs [{}; {}], output {}",
                profession,
                handler,
                trade == null ? null : describeTradeItem(trade.getFirstInput()),
                trade == null ? null : describeTradeItem(trade.getSecondInput()),
                trade == null ? null : describeTradeItem(trade.getOutput()));
            return false;
        }
        // Providers may update their live stacks after creating TradeItems.
        trade.getFirstInput().reconstructableStack = new ConstructableItemStack(trade.getFirstInput().stack);
        if (trade.hasSecondInput()) {
            trade.getSecondInput().reconstructableStack = new ConstructableItemStack(trade.getSecondInput().stack);
        }
        trade.getOutput().reconstructableStack = new ConstructableItemStack(trade.getOutput().stack);
        return true;
    }

    private static boolean restoreTrade(VillagerTrade trade, int profession, String handler) {
        if (trade == null || !hasSavedStack(trade.getFirstInput())
            || !hasSavedStack(trade.getOutput())
            || (trade.hasSecondInput() && !hasSavedStack(trade.getSecondInput()))) {
            LOG.warn("Skipping malformed cached villager trade: profession {}, handler {}", profession, handler);
            return false;
        }
        trade.reconstructStacks();
        return prepareTrade(trade, profession, handler);
    }

    private static boolean hasSavedStack(VillagerTrade.TradeItem item) {
        return item != null && item.reconstructableStack != null;
    }

    private static boolean validTradeItem(VillagerTrade.TradeItem item) {
        return item != null && validStack(item.stack);
    }

    private static String describeTradeItem(VillagerTrade.TradeItem item) {
        if (item == null) return "<missing trade item>";
        if (item.stack == null && item.reconstructableStack != null) {
            return "<unresolved " + item.reconstructableStack.itemIdentifier + ">";
        }
        return describeStack(item.stack);
    }

    private static String describeStack(ItemStack stack) {
        if (stack == null) return "<missing stack>";
        if (stack.getItem() == null) return "<missing item>";
        String name = Item.itemRegistry.getNameForObject(stack.getItem());
        return name == null ? "<unregistered " + stack.getItem()
            .getClass()
            .getName() + ">" : name;
    }

}
