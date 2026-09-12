package com.kuba6000.mobsinfo.villagerintegration;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Random;

import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;

import com.google.gson.JsonArray;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.kuba6000.mobsinfo.api.IVillagerInfoProvider;
import com.kuba6000.mobsinfo.api.VillagerRecipe;
import com.kuba6000.mobsinfo.api.VillagerTrade;
import com.kuba6000.mobsinfo.config.Config;
import com.kuba6000.mobsinfo.loader.VillagerTradesLoader;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLLoadCompleteEvent;
import cpw.mods.fml.common.registry.VillagerRegistry;

/** Exercises generation and its persisted cache in an isolated Forge process. */
@Mod(modid = "villagertradefixture", version = "1", dependencies = "required-after:mobsinfo")
public class VillagerTradeIntegration {

    private static final int REGULAR = 190;
    private static final int PROVIDER = 191;

    @Mod.EventHandler
    public void verify(FMLLoadCompleteEvent event) {
        try {
            VillagerRegistry.instance()
                .registerVillagerId(REGULAR);
            VillagerRegistry.instance()
                .registerVillageTradeHandler(REGULAR, new RegularHandler());
            VillagerRegistry.instance()
                .registerVillagerId(PROVIDER);
            VillagerRegistry.instance()
                .registerVillageTradeHandler(PROVIDER, new ProviderHandler());
            Config.VillagerTradesHandler.enabled = true;
            boolean fromCache = "cache".equals(System.getProperty("mobsinfo.villagerTradeIntegration"));
            if (fromCache) damageCache();
            Config.MobHandler.regenerationTrigger = fromCache
                ? Config.MobHandler._CacheRegenerationTrigger.ModAdditionRemovalChange
                : Config.MobHandler._CacheRegenerationTrigger.Always;
            VillagerTradesLoader.generateVillagerTrades();
            VillagerRecipe recipe = VillagerRecipe.recipes.get(REGULAR);
            if (recipe == null || recipe.trades.size() != 2) {
                throw new AssertionError("Only the two valid offers should survive generation");
            }
            if (recipe.trades.stream()
                .noneMatch(
                    trade -> trade.hasSecondInput() && trade.getSecondInput().stack.getItem() == Items.iron_ingot)) {
                throw new AssertionError("The valid two-input offer was lost or made cheaper");
            }
            JsonObject cache = new JsonParser()
                .parse(
                    new String(
                        Files.readAllBytes(
                            Config.getConfigFile("VillagerTradesLoader.cache")
                                .toPath()),
                        StandardCharsets.UTF_8))
                .getAsJsonObject();
            int savedOffers = cache.getAsJsonObject("handlerList")
                .getAsJsonArray(Integer.toString(REGULAR))
                .get(0)
                .getAsJsonObject()
                .getAsJsonArray("tradeList")
                .size();
            if (!fromCache && savedOffers != 2) throw new AssertionError("Invalid offers were persisted in the cache");
            VillagerRecipe provided = VillagerRecipe.recipes.get(PROVIDER);
            if (provided == null || provided.trades.size() != 2) {
                throw new AssertionError("Provider's invalid offers must be rejected without losing valid offers");
            }
            if (provided.trades.stream()
                .noneMatch(
                    trade -> trade.hasSecondInput() && trade.getOutput().stack.getItem() == Items.apple
                        && trade.getOutput().stack.stackSize == 3)) {
                throw new AssertionError("The provider's live offer was overwritten by an old serialized stack");
            }
            System.out.println("VILLAGER_TRADE_INTEGRATION_PASS");
            FMLCommonHandler.instance()
                .exitJava(0, true);
        } catch (Throwable failure) {
            failure.printStackTrace();
            FMLCommonHandler.instance()
                .exitJava(1, true);
        }
    }

    private static void damageCache() throws Exception {
        JsonObject cache = new JsonParser()
            .parse(
                new String(
                    Files.readAllBytes(
                        Config.getConfigFile("VillagerTradesLoader.cache")
                            .toPath()),
                    StandardCharsets.UTF_8))
            .getAsJsonObject();
        JsonObject handler = cache.getAsJsonObject("handlerList")
            .getAsJsonArray(Integer.toString(REGULAR))
            .get(0)
            .getAsJsonObject();
        JsonArray originalOffers = handler.getAsJsonArray("tradeList");
        JsonArray offers = new JsonArray();
        offers.add(originalOffers.get(0));
        offers.add(originalOffers.get(1));
        handler.add("tradeList", offers);
        JsonObject twoInputs = null;
        for (int index = 0; index < offers.size(); index++) {
            JsonObject offer = offers.get(index)
                .getAsJsonObject();
            if (!offer.get("secondInput")
                .isJsonNull()) twoInputs = offer;
        }
        if (twoInputs == null) throw new AssertionError("Generate the fixture cache before testing its reload");
        for (String slot : new String[] { "firstInput", "secondInput", "output" }) {
            JsonObject broken = new JsonParser().parse(twoInputs.toString())
                .getAsJsonObject();
            broken.getAsJsonObject(slot)
                .getAsJsonObject("reconstructableStack")
                .getAsJsonObject("itemIdentifier")
                .addProperty("name", "missing_fixture_item");
            offers.add(broken);
        }
        JsonObject missingDescriptor = new JsonParser().parse(twoInputs.toString())
            .getAsJsonObject();
        missingDescriptor.getAsJsonObject("secondInput")
            .add("reconstructableStack", JsonNull.INSTANCE);
        offers.add(missingDescriptor);
        JsonObject missingOutput = new JsonParser().parse(twoInputs.toString())
            .getAsJsonObject();
        missingOutput.add("output", JsonNull.INSTANCE);
        offers.add(missingOutput);
        offers.add(JsonNull.INSTANCE);
        Files.write(
            Config.getConfigFile("VillagerTradesLoader.cache")
                .toPath(),
            cache.toString()
                .getBytes(StandardCharsets.UTF_8));
    }

    public static class RegularHandler implements VillagerRegistry.IVillageTradeHandler {

        @Override
        @SuppressWarnings("unchecked")
        public void manipulateTradesForVillager(EntityVillager villager, MerchantRecipeList offers, Random random) {
            if ("cache".equals(System.getProperty("mobsinfo.villagerTradeIntegration"))) {
                throw new AssertionError("Cached offers should be restored without regenerating the handler");
            }
            offers.add(new MerchantRecipe(new ItemStack(Items.emerald), new ItemStack(Items.diamond)));
            offers.add(new MerchantRecipe(new ItemStack(Items.emerald), new ItemStack(new UnregisteredItem())));
            offers.add(null);
            offers.add(new MerchantRecipe((ItemStack) null, new ItemStack(Items.apple)));
            offers.add(new MerchantRecipe(new ItemStack(Items.emerald), (ItemStack) null));
            offers.add(new MerchantRecipe(new ItemStack(Items.emerald), new ItemStack((Item) null)));
            offers.add(new MerchantRecipe(new ItemStack(new UnregisteredItem()), new ItemStack(Items.apple)));
            offers.add(
                new MerchantRecipe(
                    new ItemStack(Items.emerald),
                    new ItemStack(new UnregisteredItem()),
                    new ItemStack(Items.apple)));
            offers.add(
                new MerchantRecipe(
                    new ItemStack(Items.emerald),
                    new ItemStack(Items.iron_ingot),
                    new ItemStack(Items.apple)));
        }
    }

    public static class ProviderHandler implements VillagerRegistry.IVillageTradeHandler, IVillagerInfoProvider {

        @Override
        public void manipulateTradesForVillager(EntityVillager villager, MerchantRecipeList offers, Random random) {
            throw new AssertionError("The provider API should supply these trades");
        }

        @Override
        public void provideTrades(EntityVillager villager, int profession, ArrayList<VillagerTrade> offers) {
            offers.add(VillagerTrade.create(Items.emerald, Items.diamond));
            offers.add(null);
            offers
                .add(new VillagerTrade((VillagerTrade.TradeItem) null, null, VillagerTrade.createItem(Items.apple), 1));
            for (int slot = 0; slot < 3; slot++) {
                VillagerTrade broken = VillagerTrade.create(Items.emerald, Items.apple)
                    .withSecondaryInput(Items.iron_ingot);
                VillagerTrade.TradeItem item = slot == 0 ? broken.getFirstInput()
                    : slot == 1 ? broken.getSecondInput() : broken.getOutput();
                item.stack = null;
                offers.add(broken);
            }
            VillagerTrade unregistered = VillagerTrade.create(Items.emerald, Items.apple);
            unregistered.getOutput().stack = new ItemStack(new UnregisteredItem());
            offers.add(unregistered);
            VillagerTrade valid = VillagerTrade.create(Items.emerald, Items.diamond)
                .withSecondaryInput(Items.iron_ingot);
            // A provider may update its live stack after building the TradeItem.
            valid.getOutput().stack = new ItemStack(Items.apple, 3);
            offers.add(valid);
        }
    }

    public static class UnregisteredItem extends Item {
    }
}
