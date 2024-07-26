package com.kuba6000.mobsinfo.loader;

import static com.kuba6000.mobsinfo.MobsInfo.MODID;
import static com.kuba6000.mobsinfo.loader.MobRecipeLoader.randomEnchantmentDetectedString;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kuba6000.mobsinfo.api.DummyWorld;
import com.kuba6000.mobsinfo.api.RandomSequencer;
import com.kuba6000.mobsinfo.api.VillagerRecipe;
import com.kuba6000.mobsinfo.api.VillagerTrade;
import com.kuba6000.mobsinfo.api.utils.ItemID;
import com.kuba6000.mobsinfo.api.utils.ModUtils;
import com.kuba6000.mobsinfo.config.Config;
import com.kuba6000.mobsinfo.nei.VillagerTradesHandler;
import com.kuba6000.mobsinfo.network.LoadConfigPacket;

import cpw.mods.fml.common.registry.VillagerRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class VillagerTradesLoader {

    private static final Logger LOG = LogManager.getLogger(MODID + "[Villager Recipe Loader]");

    private static boolean alreadyGenerated = false;

    public static void generateVillagerTrades() {
        if (alreadyGenerated) return;
        alreadyGenerated = true;

        if (!Config.VillagerTradesHandler.enabled) return;

        LOG.info("Generating Recipe Map for Villager Trades Helper");
        final long startTime = System.currentTimeMillis();

        DummyWorld world = new DummyWorld();

        RandomSequencer frand = new RandomSequencer();
        world.rand = frand;

        // vanilla recipes?

        ModUtils.TriConsumer<ArrayList<VillagerTrade>, EntityVillager, Integer> detectCustomRecipes = (recipes,
            villager, villagerID) -> {
            try {
                frand.newRound();

                TradeList trades = new TradeList();
                TradeCollector collector = new TradeCollector();
                boolean second = false;
                do {
                    MerchantRecipeList list = new MerchantRecipeList();
                    VillagerRegistry.manageVillagerTrades(list, villager, villagerID, world.rand);
                    collector.collectTrades(trades, list, frand.chance);

                    if (second && frand.chance < 0.0000001d) {
                        LOG.warn("Skipping " + villagerID + "(CustomHandler) because it's too randomized");
                        break;
                    }
                    second = true;
                } while (frand.nextRound());
                frand.newRound();
                collector.newRound();

                if (!trades.itemsToTrade.isEmpty()) {
                    for (TradeInstance value : trades.itemsToTrade.values()) {
                        recipes.add(new VillagerTrade(value.i1, value.i2, value.o, value.chance));
                    }
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        };

        { // profession 0
            EntityVillager entity = new EntityVillager(world);

            entity.setProfession(0);

            ArrayList<VillagerTrade> recipes = new ArrayList<>();
            recipes.add(new VillagerTrade(Items.wheat, null, Items.emerald, 0.9d));
            recipes.add(new VillagerTrade(Item.getItemFromBlock(Blocks.wool), null, Items.emerald, 0.5d));
            recipes.add(new VillagerTrade(Items.chicken, null, Items.emerald, 0.5d));
            recipes.add(new VillagerTrade(Items.cooked_fished, null, Items.emerald, 0.4d));

            recipes.add(new VillagerTrade(Items.emerald, null, Items.bread, 0.9d));
            recipes.add(new VillagerTrade(Items.emerald, null, Items.melon, 0.3d));
            recipes.add(new VillagerTrade(Items.emerald, null, Items.apple, 0.3d));
            recipes.add(new VillagerTrade(Items.emerald, null, Items.cookie, 0.3d));
            recipes.add(new VillagerTrade(Items.emerald, null, Items.shears, 0.3d));
            recipes.add(new VillagerTrade(Items.emerald, null, Items.flint_and_steel, 0.3d));
            recipes.add(new VillagerTrade(Items.emerald, null, Items.cooked_chicken, 0.3d));
            recipes.add(new VillagerTrade(Items.emerald, null, Items.arrow, 0.3d));

            recipes.add(
                new VillagerTrade(
                    new ItemStack(Blocks.gravel, 10),
                    new ItemStack(Items.emerald),
                    new ItemStack(Items.flint, 5, 0),
                    0.5d));

            detectCustomRecipes.accept(recipes, entity, 0);

            VillagerRecipe.recipes.put(0, new VillagerRecipe(recipes, 0, entity));
        }

        { // profession 1
            EntityVillager entity = new EntityVillager(world);

            entity.setProfession(1);
            ArrayList<VillagerTrade> recipes = new ArrayList<>();
            recipes.add(new VillagerTrade(Items.paper, null, Items.emerald, 0.8d));
            recipes.add(new VillagerTrade(Items.book, null, Items.emerald, 0.8d));
            recipes.add(new VillagerTrade(Items.written_book, null, Items.emerald, 0.3d));

            recipes.add(new VillagerTrade(Items.emerald, null, Item.getItemFromBlock(Blocks.bookshelf), 0.8d));
            recipes.add(new VillagerTrade(Items.emerald, null, Item.getItemFromBlock(Blocks.glass), 0.2d));
            recipes.add(new VillagerTrade(Items.emerald, null, Items.compass, 0.2d));
            recipes.add(new VillagerTrade(Items.emerald, null, Items.clock, 0.2d));

            recipes.add(
                new VillagerTrade(
                    new VillagerTrade.TradeItem(new ItemStack(Items.book)),
                    new VillagerTrade.TradeItem(new ItemStack(Items.emerald)).setPossibleSizes(5, 64),
                    new VillagerTrade.TradeItem(new ItemStack(Items.book), null, 1),
                    0.07d));

            detectCustomRecipes.accept(recipes, entity, 1);

            VillagerRecipe.recipes.put(1, new VillagerRecipe(recipes, 1, entity));
        }

        { // profession 2
            EntityVillager entity = new EntityVillager(world);

            entity.setProfession(2);
            ArrayList<VillagerTrade> recipes = new ArrayList<>();
            recipes.add(new VillagerTrade(Items.emerald, null, Items.ender_eye, 0.3d));
            recipes.add(new VillagerTrade(Items.emerald, null, Items.experience_bottle, 0.2d));
            recipes.add(new VillagerTrade(Items.emerald, null, Items.redstone, 0.4d));
            recipes.add(new VillagerTrade(Items.emerald, null, Item.getItemFromBlock(Blocks.glowstone), 0.3d));

            Item[] aitem = new Item[] { Items.iron_sword, Items.diamond_sword, Items.iron_chestplate,
                Items.diamond_chestplate, Items.iron_axe, Items.diamond_axe, Items.iron_pickaxe,
                Items.diamond_pickaxe };

            for (Item item : aitem) {
                recipes.add(
                    new VillagerTrade(
                        new VillagerTrade.TradeItem(new ItemStack(item)),
                        new VillagerTrade.TradeItem(new ItemStack(Items.emerald, 4)),
                        new VillagerTrade.TradeItem(new ItemStack(item), null, 5 + 7),
                        0.05d));
            }

            detectCustomRecipes.accept(recipes, entity, 2);

            VillagerRecipe.recipes.put(2, new VillagerRecipe(recipes, 2, entity));
        }

        { // profession 3
            EntityVillager entity = new EntityVillager(world);

            entity.setProfession(3);
            ArrayList<VillagerTrade> recipes = new ArrayList<>();
            recipes.add(new VillagerTrade(Items.coal, null, Items.emerald, 0.7d));
            recipes.add(new VillagerTrade(Items.iron_ingot, null, Items.emerald, 0.5d));
            recipes.add(new VillagerTrade(Items.gold_ingot, null, Items.emerald, 0.5d));
            recipes.add(new VillagerTrade(Items.diamond, null, Items.emerald, 0.5d));

            recipes.add(new VillagerTrade(Items.emerald, null, Items.iron_sword, 0.5d));
            recipes.add(new VillagerTrade(Items.emerald, null, Items.diamond_sword, 0.5d));
            recipes.add(new VillagerTrade(Items.emerald, null, Items.iron_axe, 0.3d));
            recipes.add(new VillagerTrade(Items.emerald, null, Items.diamond_axe, 0.3d));
            recipes.add(new VillagerTrade(Items.emerald, null, Items.iron_pickaxe, 0.5d));
            recipes.add(new VillagerTrade(Items.emerald, null, Items.diamond_pickaxe, 0.5d));
            recipes.add(new VillagerTrade(Items.emerald, null, Items.iron_shovel, 0.2d));
            recipes.add(new VillagerTrade(Items.emerald, null, Items.diamond_shovel, 0.2d));
            recipes.add(new VillagerTrade(Items.emerald, null, Items.iron_hoe, 0.2d));
            recipes.add(new VillagerTrade(Items.emerald, null, Items.diamond_hoe, 0.2d));
            recipes.add(new VillagerTrade(Items.emerald, null, Items.iron_boots, 0.2d));
            recipes.add(new VillagerTrade(Items.emerald, null, Items.diamond_boots, 0.2d));
            recipes.add(new VillagerTrade(Items.emerald, null, Items.iron_helmet, 0.2d));
            recipes.add(new VillagerTrade(Items.emerald, null, Items.diamond_helmet, 0.2d));
            recipes.add(new VillagerTrade(Items.emerald, null, Items.iron_chestplate, 0.2d));
            recipes.add(new VillagerTrade(Items.emerald, null, Items.diamond_chestplate, 0.2d));
            recipes.add(new VillagerTrade(Items.emerald, null, Items.iron_leggings, 0.2d));
            recipes.add(new VillagerTrade(Items.emerald, null, Items.diamond_leggings, 0.2d));
            recipes.add(new VillagerTrade(Items.emerald, null, Items.chainmail_boots, 0.1d));
            recipes.add(new VillagerTrade(Items.emerald, null, Items.chainmail_helmet, 0.1d));
            recipes.add(new VillagerTrade(Items.emerald, null, Items.chainmail_chestplate, 0.1d));
            recipes.add(new VillagerTrade(Items.emerald, null, Items.chainmail_leggings, 0.1d));

            detectCustomRecipes.accept(recipes, entity, 3);

            VillagerRecipe.recipes.put(3, new VillagerRecipe(recipes, 3, entity));
        }

        { // profession 4
            EntityVillager entity = new EntityVillager(world);

            entity.setProfession(4);
            ArrayList<VillagerTrade> recipes = new ArrayList<>();
            recipes.add(new VillagerTrade(Items.coal, null, Items.emerald, 0.7d));
            recipes.add(new VillagerTrade(Items.porkchop, null, Items.emerald, 0.5d));
            recipes.add(new VillagerTrade(Items.beef, null, Items.emerald, 0.5d));

            recipes.add(new VillagerTrade(Items.emerald, null, Items.saddle, 0.1d));
            recipes.add(new VillagerTrade(Items.emerald, null, Items.leather_chestplate, 0.3d));
            recipes.add(new VillagerTrade(Items.emerald, null, Items.leather_boots, 0.3d));
            recipes.add(new VillagerTrade(Items.emerald, null, Items.leather_helmet, 0.3d));
            recipes.add(new VillagerTrade(Items.emerald, null, Items.leather_leggings, 0.3d));
            recipes.add(new VillagerTrade(Items.emerald, null, Items.cooked_porkchop, 0.3d));
            recipes.add(new VillagerTrade(Items.emerald, null, Items.cooked_beef, 0.3d));

            detectCustomRecipes.accept(recipes, entity, 4);

            VillagerRecipe.recipes.put(4, new VillagerRecipe(recipes, 4, entity));
        }

        // custom villagers

        MobRecipeLoader.isInGenerationProcess = true;
        for (Integer id : VillagerRegistry.getRegisteredVillagers()) {
            try {
                ResourceLocation l = VillagerRegistry.getVillagerSkin(id, null);
                LOG.info(
                    "Generating recipes for profession " + id + (l != null ? ("(" + l.getResourceDomain() + ")") : ""));
                EntityVillager villager = new EntityVillager(world);
                villager.setProfession(id);

                frand.newRound();

                TradeList trades = new TradeList();
                TradeCollector collector = new TradeCollector();
                boolean second = false;
                do {
                    MerchantRecipeList list = new MerchantRecipeList();
                    VillagerRegistry.manageVillagerTrades(list, villager, id, world.rand);
                    collector.collectTrades(trades, list, frand.chance);

                    if (second && frand.chance < 0.0000001d) {
                        LOG.warn(
                            "Skipping " + id
                                + (l != null ? ("(" + l.getResourceDomain() + ")") : "")
                                + " because it's too randomized");
                        break;
                    }
                    second = true;
                } while (frand.nextRound());
                frand.newRound();
                collector.newRound();

                VillagerRecipe recipe = new VillagerRecipe(new ArrayList<>(trades.itemsToTrade.size()), id, villager);
                for (TradeInstance value : trades.itemsToTrade.values()) {
                    recipe.trades.add(new VillagerTrade(value.i1, value.i2, value.o, value.chance));
                }
                VillagerRecipe.recipes.put(id, recipe);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        MobRecipeLoader.isInGenerationProcess = false;

        final long endTime = System.currentTimeMillis();
        LOG.info("Villager trade information generation took {} ms", endTime - startTime);

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
