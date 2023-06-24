package com.kuba6000.mobsinfo.nei;

import static com.kuba6000.mobsinfo.config.Config.Compatibility.enableMobHandlerInfernal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;

import com.kuba6000.mobsinfo.MobsInfo;
import com.kuba6000.mobsinfo.Tags;
import com.kuba6000.mobsinfo.api.helper.TranslationHelper;
import com.kuba6000.mobsinfo.api.utils.FastRandom;
import com.kuba6000.mobsinfo.mixin.InfernalMobs.InfernalMobsCoreAccessor;

import atomicstryker.infernalmobs.common.InfernalMobsCore;
import codechicken.lib.gui.GuiDraw;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.GuiCraftingRecipe;
import codechicken.nei.recipe.GuiRecipe;
import codechicken.nei.recipe.GuiUsageRecipe;
import codechicken.nei.recipe.IUsageHandler;
import codechicken.nei.recipe.RecipeCatalysts;
import codechicken.nei.recipe.TemplateRecipeHandler;
import cpw.mods.fml.common.event.FMLInterModComms;

public class MobHandlerInfernal extends TemplateRecipeHandler {

    enum Translations {

        TITLE,
        FORMAT,
        FORMAT_1,
        FORMAT_2,
        FORMAT_3,
        FORMAT_4,
        ELITE,
        ULTRA,
        INFERNO,
        TOOLTIP_CHANCE,
        TOOLTIP_CHANCE_ALWAYS,

        ;

        final String key;

        Translations() {
            key = "mobsinfo.mobhandlerinfernal." + this.name()
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

    private static final Logger LOG = LogManager.getLogger(Tags.MODID + "[Mob Handler - Infernal drops]");
    private static InfernalRecipe recipe = null;
    private static final int itemsPerRow = 8, itemXShift = 18, itemYShift = 18, nextRowYShift = 35, itemsYStart = 80;
    public static int cycleTicksStatic = Math.abs((int) System.currentTimeMillis());

    public MobHandlerInfernal() {
        if (!NEI_Config.isAdded) {
            FMLInterModComms.sendRuntimeMessage(
                MobsInfo.instance,
                "NEIPlugins",
                "register-crafting-handler",
                "MobsInfo@" + getRecipeName() + "@" + getOverlayIdentifier());
            GuiCraftingRecipe.craftinghandlers.add(this);
            GuiUsageRecipe.usagehandlers.add(this);
        }
        if (recipe == null) {
            recipe = new InfernalRecipe();
        }
    }

    @Override
    public TemplateRecipeHandler newInstance() {
        return new MobHandlerInfernal();
    }

    @Override
    public String getOverlayIdentifier() {
        return "mobsinfo.mobhandlerinfernal";
    }

    @Override
    public String getGuiTexture() {
        return "mobsinfo:textures/gui/MobHandlerInfernal.png";
    }

    @Override
    public String getRecipeName() {
        return "Infernal Drops";
    }

    @Override
    public IUsageHandler getUsageAndCatalystHandler(String inputId, Object... ingredients) {
        if (!enableMobHandlerInfernal) return newInstance();
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
        if (!enableMobHandlerInfernal) return;
        if (outputId.equals(getOverlayIdentifier())) {
            arecipes.add(recipe);
            return;
        }
        super.loadCraftingRecipes(outputId, results);
    }

    @Override
    public void loadCraftingRecipes(ItemStack result) {
        if (!enableMobHandlerInfernal) return;
        if (recipe.contains(recipe.all, result)) arecipes.add(recipe);
    }

    @Override
    public void loadUsageRecipes(ItemStack ingredient) {
        if (!enableMobHandlerInfernal) return;
        if (MobHandler.isUsageInfernalMob(ingredient)) arecipes.add(recipe);
    }

    @Override
    public void onUpdate() {
        cycleTicksStatic++;
    }

    @Override
    public void drawBackground(int recipeID) {
        GL11.glColor4f(1f, 1f, 1f, 1f);
        GuiDraw.changeTexture(getGuiTexture());
        GuiDraw.drawTexturedModalRect(0, 0, 0, 0, 168, 192);

        {
            int x = 6, y = itemsYStart + 11, yshift = nextRowYShift;
            if (recipe.eliteCount > 0) {
                for (int i = 0; i < ((recipe.eliteCount - 1) / itemsPerRow) + 1; i++) {
                    GuiDraw.drawTexturedModalRect(x, y + (18 * i), 0, 192, 144, 18);
                    if (i > 0) GuiDraw.drawTexturedModalRect(x, y + ((18 * i) - 1), 0, 193, 144, 2);
                }
                y += yshift + ((recipe.eliteCount - 1) / itemsPerRow) * 18;
            }
            if (recipe.ultraCount > 0) {
                for (int i = 0; i < ((recipe.ultraCount - 1) / itemsPerRow) + 1; i++) {
                    GuiDraw.drawTexturedModalRect(x, y + (18 * i), 0, 192, 144, 18);
                    if (i > 0) GuiDraw.drawTexturedModalRect(x, y + ((18 * i) - 1), 0, 193, 144, 2);
                }
                y += yshift + ((recipe.ultraCount - 1) / itemsPerRow) * 18;
            }
            if (recipe.infernoCount > 0) {
                for (int i = 0; i < ((recipe.infernoCount - 1) / itemsPerRow) + 1; i++) {
                    GuiDraw.drawTexturedModalRect(x, y + (18 * i), 0, 192, 144, 18);
                    if (i > 0) GuiDraw.drawTexturedModalRect(x, y + ((18 * i) - 1), 0, 193, 144, 2);
                }
            }
        }

    }

    @Override
    public void drawForeground(int recipeID) {
        int y = 0, yshift = 10, x = 7;

        GuiDraw.drawString(Translations.TITLE.get(), x, y += yshift, 0xFF555555, false);
        GuiDraw.drawString(Translations.FORMAT.get(), x, y += yshift, 0xFF555555, false);
        GuiDraw.drawString(Translations.FORMAT_1.get(), x, y += yshift, 0xFF555555, false);
        GuiDraw.drawString(Translations.FORMAT_2.get(), x, y += yshift, 0xFF555555, false);
        GuiDraw.drawString(Translations.FORMAT_3.get(), x, y += yshift, 0xFF555555, false);
        GuiDraw.drawString(Translations.FORMAT_4.get(), x + 20, y += yshift, 0xFF555555, false);

        x = 6;
        y = itemsYStart;
        yshift = nextRowYShift;
        if (recipe.eliteCount > 0) {
            GuiDraw.drawString(Translations.ELITE.get(recipe.eliteChance * 100d, 100d), x, y, 0xFF555555, false);
            y += yshift + ((recipe.eliteCount - 1) / itemsPerRow) * 18;
        }
        if (recipe.ultraCount > 0) {
            GuiDraw.drawString(
                Translations.ULTRA.get(recipe.ultraChance * recipe.eliteChance * 100d, recipe.ultraChance * 100d),
                x,
                y,
                0xFF555555,
                false);
            y += yshift + ((recipe.ultraCount - 1) / itemsPerRow) * 18;
        }
        if (recipe.infernoCount > 0) {
            GuiDraw.drawString(
                Translations.INFERNO.get(
                    recipe.infernoChance * recipe.ultraChance * recipe.eliteChance * 100d,
                    recipe.infernoChance * recipe.ultraChance * 100d),
                x,
                y,
                0xFF555555,
                false);
        }
    }

    @Override
    public List<String> handleItemTooltip(GuiRecipe<?> gui, ItemStack stack, List<String> currenttip, int recipeID) {
        InfernalPositionedStack pstack = (InfernalPositionedStack) recipe.all.stream()
            .filter(f -> f.item == stack)
            .findFirst()
            .orElse(null);
        if (pstack != null) pstack.handleTooltip(currenttip);
        return currenttip;
    }

    private static class InfernalPositionedStack extends PositionedStack {

        private final Random rand;
        private final double chance;
        private final double chanceAlways;

        public InfernalPositionedStack(ItemStack stack, int x, int y, double chance, double chanceAlways) {
            super(stack, x, y, false);
            this.chance = chance;
            this.chanceAlways = chanceAlways;
            rand = new FastRandom();
            setPermutationToRender(0);
        }

        public void handleTooltip(List<String> currenttip) {
            currenttip.addAll(
                Arrays.asList(
                    Translations.TOOLTIP_CHANCE.get(chance * 100d),
                    Translations.TOOLTIP_CHANCE_ALWAYS.get(chanceAlways * 100d)));
        }

        @Override
        public void setPermutationToRender(int index) {
            if (this.rand == null) return;
            if (this.item == null) this.item = this.items[0].copy();
            if (this.item.getItem() instanceof ItemEnchantedBook) {
                this.item = ((ItemEnchantedBook) item.getItem()).func_92114_b(rand).theItemId;
            }
            if (this.item.hasTagCompound()) this.item.getTagCompound()
                .removeTag("ench");
            ((InfernalMobsCoreAccessor) InfernalMobsCore.instance()).callEnchantRandomly(
                rand,
                this.item,
                this.item.getItem()
                    .getItemEnchantability(),
                5);
        }
    }

    private class InfernalRecipe extends TemplateRecipeHandler.CachedRecipe {

        public final int eliteCount;
        public final int ultraCount;
        public final int infernoCount;

        public final double eliteChance;
        public final double ultraChance;
        public final double infernoChance;

        final List<PositionedStack> all;
        final List<PositionedStack> elite;
        final List<PositionedStack> ultra;
        final List<PositionedStack> inferno;

        public InfernalRecipe() {
            InfernalMobsCoreAccessor infernalMobsCore = (InfernalMobsCoreAccessor) InfernalMobsCore.instance();

            eliteChance = 1d / infernalMobsCore.getEliteRarity();
            ultraChance = 1d / infernalMobsCore.getUltraRarity();
            infernoChance = 1d / infernalMobsCore.getInfernoRarity();

            int xorigin = 7, xoffset = xorigin, yoffset = itemsYStart + 12;

            List<ItemStack> eliteStacks = infernalMobsCore.getDropIdListElite();
            eliteCount = eliteStacks.size();
            elite = new ArrayList<>();

            for (ItemStack eliteStack : eliteStacks) {
                elite.add(
                    new InfernalPositionedStack(
                        eliteStack.copy(),
                        xoffset,
                        yoffset,
                        eliteChance / eliteCount,
                        1d / eliteCount));
                xoffset += itemXShift;
                if (xoffset >= xorigin + (itemXShift * itemsPerRow)) {
                    xoffset = xorigin;
                    yoffset += itemYShift;
                }
            }

            xoffset = xorigin;
            yoffset += nextRowYShift;

            List<ItemStack> ultraStacks = infernalMobsCore.getDropIdListUltra();
            ultraCount = ultraStacks.size();
            ultra = new ArrayList<>();

            for (ItemStack ultraStack : ultraStacks) {
                ultra.add(
                    new InfernalPositionedStack(
                        ultraStack.copy(),
                        xoffset,
                        yoffset,
                        (ultraChance * eliteChance) / ultraCount,
                        ultraChance / ultraCount));
                xoffset += itemXShift;
                if (xoffset >= xorigin + (itemXShift * itemsPerRow)) {
                    xoffset = xorigin;
                    yoffset += itemYShift;
                }
            }

            xoffset = xorigin;
            yoffset += nextRowYShift;

            List<ItemStack> infernoStacks = infernalMobsCore.getDropIdListInfernal();
            infernoCount = infernoStacks.size();
            inferno = new ArrayList<>();

            for (ItemStack infernoStack : infernoStacks) {
                inferno.add(
                    new InfernalPositionedStack(
                        infernoStack.copy(),
                        xoffset,
                        yoffset,
                        (infernoChance * ultraChance * eliteChance) / infernoCount,
                        (infernoChance * ultraChance) / infernoCount));
                xoffset += itemXShift;
                if (xoffset >= xorigin + (itemXShift * itemsPerRow)) {
                    xoffset = xorigin;
                    yoffset += itemYShift;
                }
            }
            all = new ArrayList<>();
            all.addAll(elite);
            all.addAll(ultra);
            all.addAll(inferno);

        }

        @Override
        public PositionedStack getResult() {
            return null;
        }

        @Override
        public List<PositionedStack> getOtherStacks() {
            if (cycleTicksStatic % 10 == 0) all.forEach(p -> p.setPermutationToRender(0));
            return all;
        }
    }
}
