package com.kuba6000.mobsinfo.nei;

import static com.kuba6000.mobsinfo.MobsInfo.MODID;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.WeightedRandomFishable;
import net.minecraftforge.common.FishingHooks;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;

import com.kuba6000.mobsinfo.api.helper.TranslationHelper;
import com.kuba6000.mobsinfo.config.Config;

import codechicken.lib.gui.GuiDraw;
import codechicken.nei.NEIClientUtils;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.GuiRecipe;
import codechicken.nei.recipe.IUsageHandler;
import codechicken.nei.recipe.RecipeCatalysts;
import codechicken.nei.recipe.TemplateRecipeHandler;
import cpw.mods.fml.common.registry.GameRegistry;

public class MobHandlerFishing extends TemplateRecipeHandler {

    enum Translations {

        TITLE,
        FORMAT,
        FORMAT_1,
        FORMAT_2,
        FORMAT_3,
        FORMAT_4,
        FISH,
        JUNK,
        TREASURE,
        TOOLTIP_CHANCE,

        ;

        final String key;

        Translations() {
            key = "mobsinfo.mobhandlerfishing." + this.name()
                .toLowerCase();
        }

        public String get() {
            return StatCollector.translateToLocal(key);
        }

        public String get(Object... args) {
            return TranslationHelper.translateFormattedFixed(key, args);
        }

        public String getKey() {
            return key;
        }

        @Override
        public String toString() {
            return get();
        }
    }

    private static final Logger LOG = LogManager.getLogger(MODID + "[Fishing Handler]");
    private static FishingRecipe recipe = null;
    private static final int itemsPerRow = 8, itemXShift = 18, itemYShift = 18, nextRowYShift = 30, itemsYStart = 75;
    private static final double fishBaseChance = 0.85d, junkBaseChance = 0.10d, treasureBaseChance = 0.05d;

    public MobHandlerFishing() {

        if (recipe == null) {
            recipe = new FishingRecipe();
        }
    }

    @Override
    public TemplateRecipeHandler newInstance() {
        return new MobHandlerFishing();
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (Minecraft.getMinecraft().currentScreen instanceof GuiRecipe<?>guiRecipe && guiRecipe.getHandler() == this
            && !arecipes.isEmpty()
            && !NEIClientUtils.shiftKey()
            && cycleticks % 10 == 0) {
            recipe.all.forEach(stack -> stack.setPermutationToRender(0));
        }
    }

    @Override
    public String getOverlayIdentifier() {
        return "mobsinfo.mobhandlerfishing";
    }

    @Override
    public String getGuiTexture() {
        return "mobsinfo:textures/gui/MobHandlerInfernal.png";
    }

    @Override
    public String getRecipeName() {
        return Translations.TITLE.get();
    }

    @Override
    public int getRecipeHeight(int recipe) {
        return MobHandlerFishing.recipe.height;
    }

    @Override
    public IUsageHandler getUsageAndCatalystHandler(String inputId, Object... ingredients) {
        if (!Config.FishingHandler.enabled) return newInstance();
        if (inputId.equals("item")) {
            TemplateRecipeHandler handler = newInstance();
            ItemStack candidate = (ItemStack) ingredients[0];
            if (RecipeCatalysts.containsCatalyst(handler, candidate)) {
                handler.loadCraftingRecipes(getOverlayIdentifier(), (Object) null);
                return handler;
            }
        }
        return this.getUsageHandler(inputId, ingredients);
    }

    @Override
    public void loadCraftingRecipes(String outputId, Object... results) {
        if (!Config.FishingHandler.enabled) return;
        if (recipe == null) return;
        if (outputId.equals(getOverlayIdentifier())) {
            arecipes.add(recipe);
            return;
        }
        super.loadCraftingRecipes(outputId, results);
    }

    @Override
    public void loadCraftingRecipes(ItemStack result) {
        if (!Config.FishingHandler.enabled) return;
        if (recipe == null) return;
        for (PositionedStack pstack : recipe.all) {
            if (pstack.containsWithNBT(result)) {
                arecipes.add(recipe);
                return;
            }
        }
    }

    @Override
    public void loadUsageRecipes(ItemStack ingredient) {
        if (!Config.FishingHandler.enabled) return;
        if (recipe == null) return;
        if (ingredient != null && ingredient.getItem() == Items.fishing_rod) {
            arecipes.add(recipe);
        }
    }

    @Override
    public void drawBackground(int recipeID) {
        GL11.glColor4f(1f, 1f, 1f, 1f);
        GuiDraw.changeTexture(getGuiTexture());
        GuiDraw.drawTexturedModalRect(0, 0, 0, 0, 168, 105);

        for (FishingCategory category : recipe.categories) {
            for (int row = 0; row < category.rows; row++) {
                int y = category.y + 11 + itemYShift * row;
                GuiDraw.drawTexturedModalRect(6, y, 0, 192, itemsPerRow * itemXShift, itemYShift);
                if (row > 0) GuiDraw.drawTexturedModalRect(6, y - 1, 0, 193, itemsPerRow * itemXShift, 2);
            }
        }
    }

    @Override
    public void drawForeground(int recipeID) {
        int y = 0, yshift = 10, x = 7;

        GuiDraw.drawString(Translations.TITLE.get(), x, y += yshift, ColorUtils.textDefault.getColor(), false);
        GuiDraw.drawString(Translations.FORMAT.get(), x, y += yshift, ColorUtils.textDefault.getColor(), false);
        GuiDraw.drawString(Translations.FORMAT_1.get(), x, y += yshift, ColorUtils.textDefault.getColor(), false);
        GuiDraw.drawString(Translations.FORMAT_2.get(), x, y += yshift, ColorUtils.textDefault.getColor(), false);
        GuiDraw.drawString(Translations.FORMAT_3.get(), x, y += yshift, ColorUtils.textDefault.getColor(), false);
        GuiDraw.drawString(Translations.FORMAT_4.get(), x, y += yshift, ColorUtils.textDefault.getColor(), false);

        for (FishingCategory category : recipe.categories) {
            GuiDraw.drawString(
                category.title.get(category.chance * 100d),
                6,
                category.y,
                ColorUtils.textDefault.getColor(),
                false);
        }
    }

    @Override
    public List<String> handleItemTooltip(GuiRecipe<?> gui, ItemStack stack, List<String> currenttip, int recipeID) {
        if (recipe == null) return currenttip;
        FishingPositionedStack pstack = (FishingPositionedStack) recipe.all.stream()
            .filter(f -> gui.isMouseOver(f, recipeID))
            .findFirst()
            .orElse(null);
        if (pstack != null) pstack.handleTooltip(currenttip);
        return currenttip;
    }

    private static class FishingPositionedStack extends PositionedStack {

        private final double chance;
        private final WeightedRandomFishable fishable;
        private final Random random = new Random();
        private boolean randomable = false;

        public FishingPositionedStack(WeightedRandomFishable fishable, int x, int y, double chance) {
            super(fishable.field_150711_b, x, y, false);
            this.chance = chance;
            this.fishable = fishable;
            this.randomable = fishable.field_150710_d || fishable.field_150712_c > 0;
            setPermutationToRender(0);
        }

        @Override
        public void setPermutationToRender(int index) {
            if (fishable != null) {
                if (!randomable) {
                    item = fishable.field_150711_b.copy();
                    return;
                }
                try {
                    item = fishable.func_150708_a(random);
                } catch (Exception e) {
                    item = fishable.field_150711_b.copy();
                    GameRegistry.UniqueIdentifier ui = GameRegistry.findUniqueIdentifierFor(item.getItem());
                    LOG.error(
                        "Fishing permutation randomization failed on {}:{}, marking this item as not randomable! Printing stacktrace:",
                        ui.toString(),
                        this.item.getItemDamage());
                    e.printStackTrace();
                    randomable = false;
                }
            }
        }

        @Override
        public boolean containsWithNBT(ItemStack candidate) {
            if (candidate == null) return false;
            ItemStack expected = fishable.field_150711_b.copy();
            if (fishable.field_150710_d && expected.getItem() == Items.book) {
                expected.func_150996_a(Items.enchanted_book);
            }
            if (expected.getItem() != candidate.getItem()) return false;
            ItemStack actual = candidate.copy();
            if (fishable.field_150712_c > 0) actual.setItemDamage(expected.getItemDamage());
            if (fishable.field_150710_d) {
                removeRandomEnchantments(expected);
                removeRandomEnchantments(actual);
            }
            return expected.isItemEqual(actual) && ItemStack.areItemStackTagsEqual(expected, actual);
        }

        private static void removeRandomEnchantments(ItemStack stack) {
            NBTTagCompound tag = stack.getTagCompound();
            if (tag == null) return;
            tag.removeTag(stack.getItem() == Items.enchanted_book ? "StoredEnchantments" : "ench");
            if (tag.hasNoTags()) stack.setTagCompound(null);
        }

        public void handleTooltip(List<String> currenttip) {
            currenttip.add(Translations.TOOLTIP_CHANCE.get(chance * 100d));
        }
    }

    private static class FishingCategory {

        final Translations title;
        final double chance;
        final int y;
        final int rows;
        final List<PositionedStack> stacks = new ArrayList<>();

        FishingCategory(Translations title, double chance, List<WeightedRandomFishable> entries, int y) {
            this.title = title;
            this.chance = chance;
            this.y = y;
            rows = (entries.size() + itemsPerRow - 1) / itemsPerRow;
            int totalWeight = WeightedRandom.getTotalWeight(entries);
            for (int index = 0; index < entries.size(); index++) {
                WeightedRandomFishable entry = entries.get(index);
                stacks.add(
                    new FishingPositionedStack(
                        entry,
                        7 + (index % itemsPerRow) * itemXShift,
                        y + 12 + (index / itemsPerRow) * itemYShift,
                        chance * entry.itemWeight / totalWeight));
            }
        }
    }

    private class FishingRecipe extends TemplateRecipeHandler.CachedRecipe {

        final List<FishingCategory> categories = new ArrayList<>();
        final List<PositionedStack> all = new ArrayList<>();
        private final int height;

        FishingRecipe() {
            int y = itemsYStart;
            y = addCategory(Translations.FISH, fishBaseChance, FishingHooks.fish, y);
            y = addCategory(Translations.JUNK, junkBaseChance, FishingHooks.junk, y);
            y = addCategory(Translations.TREASURE, treasureBaseChance, FishingHooks.treasure, y);
            height = y + 12;
        }

        private int addCategory(Translations title, double chance, List<WeightedRandomFishable> entries, int y) {
            if (entries.isEmpty()) return y;
            FishingCategory category = new FishingCategory(title, chance, entries, y);
            categories.add(category);
            all.addAll(category.stacks);
            return y + nextRowYShift + (category.rows - 1) * itemYShift;
        }

        @Override
        public PositionedStack getResult() {
            return null;
        }

        @Override
        public List<PositionedStack> getOtherStacks() {
            return all;
        }
    }
}
