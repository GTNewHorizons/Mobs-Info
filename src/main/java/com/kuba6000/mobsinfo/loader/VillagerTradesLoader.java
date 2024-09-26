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
import net.minecraft.item.ItemStack;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Multimap;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.kuba6000.mobsinfo.api.DummyWorld;
import com.kuba6000.mobsinfo.api.IVillagerInfoProvider;
import com.kuba6000.mobsinfo.api.RandomSequencer;
import com.kuba6000.mobsinfo.api.VillagerRecipe;
import com.kuba6000.mobsinfo.api.VillagerTrade;
import com.kuba6000.mobsinfo.api.helper.ProgressBarWrapper;
import com.kuba6000.mobsinfo.api.utils.GSONUtils;
import com.kuba6000.mobsinfo.api.utils.ItemID;
import com.kuba6000.mobsinfo.api.utils.ModUtils;
import com.kuba6000.mobsinfo.config.Config;
import com.kuba6000.mobsinfo.mixin.early.minecraft.VillagerRegistryAccessor;
import com.kuba6000.mobsinfo.nei.VillagerTradesHandler;
import com.kuba6000.mobsinfo.network.LoadConfigPacket;

import cpw.mods.fml.common.registry.VillagerRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class VillagerTradesLoader {

    private static final Logger LOG = LogManager.getLogger(MODID + "[Villager Recipe Loader]");

    private static boolean alreadyGenerated = false;

    private static class VillagerTradesLoaderCacheStructure {

        private static class VillagerTradesLoaderCacheStructure_Handler {

            String handler;
            ArrayList<VillagerTrade> tradeList;
        }

        String version;
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
                if (Config.MobHandler.regenerationTrigger == Config.MobHandler._CacheRegenerationTrigger.Never
                    || s.version.equals(modlistversion)) {
                    ProgressBarWrapper bar = new ProgressBarWrapper(
                        "Parsing cached Villager Trades Map",
                        s.handlerList.size());
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
                                if (handler.tradeList == null) { // provider
                                    ArrayList<VillagerRegistry.IVillageTradeHandler> tradeHandlers = classNameToHandlerInstances
                                        .get(handler.handler);
                                    if (tradeHandlers != null && !tradeHandlers.isEmpty()
                                        && tradeHandlers.get(0) instanceof IVillagerInfoProvider) {
                                        for (VillagerRegistry.IVillageTradeHandler tradeHandler : tradeHandlers) {
                                            ((IVillagerInfoProvider) tradeHandler)
                                                .provideTrades(villager, profession, trades);
                                        }
                                    }
                                } else {
                                    trades.addAll(handler.tradeList);
                                }
                            }
                            trades.forEach(VillagerTrade::reconstructStacks);
                            VillagerRecipe.recipes.put(profession, new VillagerRecipe(trades, profession, villager));
                        } catch (Exception ignored) {}
                    }
                    bar.end();
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
        toCache.handlerList = new HashMap<>();

        LOG.info("Generating villager recipes");

        final ArrayList<Integer> villagerIDs = new ArrayList<>(Arrays.asList(0, 1, 2, 3, 4));
        villagerIDs.addAll(VillagerRegistry.getRegisteredVillagers());

        ProgressBarWrapper bar = new ProgressBarWrapper("Generating Villager Traders Map", villagerIDs.size());

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

                TradeCollector collector = new TradeCollector();
                for (VillagerRegistry.IVillageTradeHandler handler : handlers) {
                    TradeList trades = new TradeList();
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
                        handlerToCache.tradeList = null;
                        continue;
                    }
                    boolean second = false;
                    do {
                        MerchantRecipeList list = new MerchantRecipeList();
                        handler.manipulateTradesForVillager(villager, list, frand);
                        collector.collectTrades(trades, list, frand.chance);

                        if (second && frand.chance < 0.0000001d) {
                            LOG.warn("Skipping {} because it's too randomized", id);
                            break;
                        }
                        second = true;
                    } while (frand.nextRound());
                    frand.newRound();
                    collector.newRound();

                    for (TradeInstance value : trades.itemsToTrade.values()) {
                        VillagerTrade trade = new VillagerTrade(value.i1, value.i2, value.o, value.chance);
                        recipes.add(trade);
                        handlerToCache.tradeList.add(trade);
                    }
                }

                VillagerRecipe.recipes.put(id, new VillagerRecipe(recipes, id, villager));
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        MobRecipeLoader.isInGenerationProcess = false;

        bar.end();

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

        boolean update(MerchantRecipe recipe) {
            ItemStack item = recipe.getItemToBuy();
            if (item.stackSize != i1.stack.stackSize) {
                if (i1.possibleSizes == null) i1.possibleSizes = new HashSet<>();
                i1.possibleSizes.add(i1.stack.stackSize);
                i1.possibleSizes.add(item.stackSize);
            }
            item = recipe.hasSecondItemToBuy() ? recipe.getSecondItemToBuy() : null;
            if (item != null && item.stackSize != i2.stack.stackSize) {
                if (i2.possibleSizes == null) i2.possibleSizes = new HashSet<>();
                i2.possibleSizes.add(i2.stack.stackSize);
                i2.possibleSizes.add(item.stackSize);
            }
            item = recipe.getItemToSell();
            if (item.stackSize != o.stack.stackSize) {
                if (o.possibleSizes == null) o.possibleSizes = new HashSet<>();
                o.possibleSizes.add(o.stack.stackSize);
                o.possibleSizes.add(item.stackSize);
            }

            return false;
        }
    }

    private static class TradeList {

        HashMap<Pair<Pair<ItemID, ItemID>, ItemID>, TradeInstance> itemsToTrade = new HashMap<>();

        TradeInstance addOrMerge(MerchantRecipe recipe, double chance) {
            var key = Pair.of(
                Pair.of(
                    ItemID.createNoCopy(recipe.getItemToBuy()),
                    recipe.hasSecondItemToBuy() ? ItemID.createNoCopy(recipe.getSecondItemToBuy()) : null),
                ItemID.createNoCopy(recipe.getItemToSell()));
            TradeInstance instance = itemsToTrade.get(key);
            if (instance != null) {
                instance.update(recipe);
                instance.chance += chance;
            } else {
                instance = new TradeInstance();
                instance.i1 = new VillagerTrade.TradeItem(recipe.getItemToBuy());
                instance.i2 = recipe.hasSecondItemToBuy() ? new VillagerTrade.TradeItem(recipe.getSecondItemToBuy())
                    : null;
                instance.o = new VillagerTrade.TradeItem(recipe.getItemToSell());
                instance.chance = chance;
                itemsToTrade.put(key, instance);
            }
            return instance;
        }
    }

    private static class TradeCollector {

        void collectTrades(TradeList trades, MerchantRecipeList recipeList, double chance) {
            for (MerchantRecipe recipe : (ArrayList<MerchantRecipe>) recipeList) {
                ItemStack i1 = recipe.getItemToBuy();
                ItemStack i2 = recipe.getSecondItemToBuy();
                ItemStack o = recipe.getItemToSell();
                boolean i1randomchomenchantdetected = i1.hasTagCompound()
                    && i1.stackTagCompound.hasKey(randomEnchantmentDetectedString);
                int i1randomenchantmentlevel = 0;
                if (i1randomchomenchantdetected) {
                    i1randomenchantmentlevel = i1.stackTagCompound.getInteger(randomEnchantmentDetectedString);
                    i1.stackTagCompound.removeTag("ench");
                    i1.stackTagCompound.setInteger(randomEnchantmentDetectedString, 0);
                }
                boolean i2randomchomenchantdetected = i2 != null && i2.hasTagCompound()
                    && i2.stackTagCompound.hasKey(randomEnchantmentDetectedString);
                int i2randomenchantmentlevel = 0;
                if (i2randomchomenchantdetected) {
                    i2randomenchantmentlevel = i2.stackTagCompound.getInteger(randomEnchantmentDetectedString);
                    i2.stackTagCompound.removeTag("ench");
                    i2.stackTagCompound.setInteger(randomEnchantmentDetectedString, 0);
                }
                boolean orandomchomenchantdetected = o.hasTagCompound()
                    && o.stackTagCompound.hasKey(randomEnchantmentDetectedString);
                int orandomenchantmentlevel = 0;
                if (orandomchomenchantdetected) {
                    orandomenchantmentlevel = o.stackTagCompound.getInteger(randomEnchantmentDetectedString);
                    o.stackTagCompound.removeTag("ench");
                    o.stackTagCompound.setInteger(randomEnchantmentDetectedString, 0);
                }
                TradeInstance instance = trades.addOrMerge(recipe, chance);
                if (i1randomchomenchantdetected) instance.i1.enchantability = i1randomenchantmentlevel;
                if (i2randomchomenchantdetected) instance.i2.enchantability = i2randomenchantmentlevel;
                if (orandomchomenchantdetected) instance.o.enchantability = orandomenchantmentlevel;
            }
        }

        void newRound() {

        }
    }

}
