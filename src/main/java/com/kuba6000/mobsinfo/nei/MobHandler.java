/*
 * spotless:off
 * MobsInfo - Minecraft addon
 * Copyright (C) 2023  kuba6000
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library. If not, see <https://www.gnu.org/licenses/>.
 * spotless:on
 */

package com.kuba6000.mobsinfo.nei;

import static com.kuba6000.mobsinfo.MobsInfo.MODID;
import static com.kuba6000.mobsinfo.nei.MobHandler.Translations.BOSS;
import static com.kuba6000.mobsinfo.nei.MobHandler.Translations.SPAWNS_EVERYWHERE;
import static com.kuba6000.mobsinfo.nei.MobHandler.Translations.SPAWNS_IN;
import static com.kuba6000.mobsinfo.nei.MobHandler.Translations.SPAWNS_NOT_IN;

import java.awt.Point;
import java.awt.Rectangle;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.boss.BossStatus;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.IMob;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.MinecraftForge;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.BufferUtils;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import com.kuba6000.mobsinfo.MobsInfo;
import com.kuba6000.mobsinfo.api.IChanceModifier;
import com.kuba6000.mobsinfo.api.LoaderReference;
import com.kuba6000.mobsinfo.api.MobDrop;
import com.kuba6000.mobsinfo.api.MobRecipe;
import com.kuba6000.mobsinfo.api.SpawnInfo;
import com.kuba6000.mobsinfo.api.event.MobNEIRegistrationEvent;
import com.kuba6000.mobsinfo.api.helper.EnderIOHelper;
import com.kuba6000.mobsinfo.api.helper.InfernalMobsCoreHelper;
import com.kuba6000.mobsinfo.api.helper.TranslationHelper;
import com.kuba6000.mobsinfo.api.utils.FastRandom;
import com.kuba6000.mobsinfo.api.utils.MobUtils;
import com.kuba6000.mobsinfo.api.utils.ModUtils;
import com.kuba6000.mobsinfo.config.Config;
import com.kuba6000.mobsinfo.mixin.InfernalMobs.InfernalMobsCoreAccessor;
import com.kuba6000.mobsinfo.mixin.minecraft.GuiContainerAccessor;
import com.kuba6000.mobsinfo.nei.scrollable.IScrollableGUI;
import com.kuba6000.mobsinfo.nei.scrollable.Scrollbar;
import com.kuba6000.mobsinfo.savedata.PlayerData;
import com.kuba6000.mobsinfo.savedata.PlayerDataManager;

import atomicstryker.infernalmobs.common.InfernalMobsCore;
import codechicken.lib.gui.GuiDraw;
import codechicken.nei.NEIClientUtils;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.GuiCraftingRecipe;
import codechicken.nei.recipe.GuiRecipe;
import codechicken.nei.recipe.GuiUsageRecipe;
import codechicken.nei.recipe.IUsageHandler;
import codechicken.nei.recipe.RecipeCatalysts;
import codechicken.nei.recipe.TemplateRecipeHandler;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.registry.GameRegistry;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.objects.ItemData;
import gregtech.api.util.GT_OreDictUnificator;

public class MobHandler extends TemplateRecipeHandler implements IScrollableGUI {

    enum Translations {

        NORMAL_DROPS,
        RARE_DROPS,
        ADDITIONAL_DROPS,
        INFERNAL_DROPS,
        INFERNAL_CANNOT,
        INFERNAL_CAN,
        INFERNAL_ALWAYS,
        CANNOT_USE_VIAL,
        CHANCE,
        AVERAGE_REMINDER,
        MOD,
        MAX_HEALTH,
        BOSS,
        LOOTABLE,
        PLAYER_ONLY,
        PEACEFUL_ALLOWED,
        LOCKED,
        LOCKED_1,
        EXTENDED_INFO,
        SPAWNS_EVERYWHERE,
        SPAWNS_IN,
        SPAWNS_NOT_IN,

        ;

        final String key;

        Translations() {
            key = "mobsinfo.mobhandler." + this.name()
                .toLowerCase();
        }

        public String get() {
            return StatCollector.translateToLocal(key);
        }

        public List<String> getAllLines() {
            ArrayList<String> lines = new ArrayList<>(Collections.singletonList(StatCollector.translateToLocal(key)));
            int i = 1;
            while (StatCollector.canTranslate(key + "_" + i))
                lines.add(StatCollector.translateToLocal(key + "_" + (i++)));
            return lines;
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

    private static final Logger LOG = LogManager.getLogger(MODID + "[Mob Handler]");
    private static final MobHandler instance = new MobHandler();
    private static final List<MobCachedRecipe> cachedRecipes = new ArrayList<>();
    public static int cycleTicksStatic = Math.abs((int) System.currentTimeMillis());
    private static final int itemsPerRow = 8, itemXShift = 18, itemYShift = 18, nextRowYShift = 35, itemsYStartMin = 83;
    private static int itemsYStart = itemsYStartMin;

    public static void addRecipe(EntityLiving e, List<MobDrop> drop) {
        List<MobPositionedStack> positionedStacks = new ArrayList<>();
        int xorigin = 7, xoffset = xorigin, yoffset = 12, normaldrops = 0, raredrops = 0, additionaldrops = 0,
            infernaldrops = 0;
        MobDrop.DropType i = null;
        for (MobDrop d : drop) {
            if (i == d.type) {
                xoffset += itemXShift;
                if (xoffset >= xorigin + (itemXShift * itemsPerRow)) {
                    xoffset = xorigin;
                    yoffset += itemYShift;
                }
            }
            if (i != null && i != d.type) {
                xoffset = xorigin;
                yoffset += nextRowYShift;
            }
            i = d.type;
            if (d.type == MobDrop.DropType.Normal) normaldrops++;
            else if (d.type == MobDrop.DropType.Rare) raredrops++;
            else if (d.type == MobDrop.DropType.Additional) additionaldrops++;
            else if (d.type == MobDrop.DropType.Infernal) break; // dont render infernal drops
            positionedStacks.add(new MobPositionedStack(d.stack.copy(), xoffset, yoffset, d));
        }
        instance.addRecipeInt(e, positionedStacks, normaldrops, raredrops, additionaldrops, infernaldrops);
    }

    private void addRecipeInt(EntityLiving e, List<MobPositionedStack> l, int normaldrops, int raredrops,
        int additionaldrops, int infernalDrops) {
        cachedRecipes.add(new MobCachedRecipe(e, l, normaldrops, raredrops, additionaldrops, infernalDrops));
    }

    public static void clearRecipes() {
        cachedRecipes.clear();
    }

    public static void sortCachedRecipes() {
        cachedRecipes.sort((o1, o2) -> {
            boolean u1 = o1.isUnlocked();
            boolean u2 = o2.isUnlocked();
            if (u1 && !u2) return -1;
            else if (!u1 && u2) return 1;
            boolean m1 = o1.mod.equals("Minecraft");
            boolean m2 = o2.mod.equals("Minecraft");
            if (m1 && !m2) return -1;
            else if (!m1 && m2) return 1;
            if (!o1.mod.equals(o2.mod)) return o1.mod.compareTo(o2.mod);
            else return o1.localizedName.compareTo(o2.localizedName);
        });
    }

    private final Scrollbar scrollbar;

    public MobHandler() {
        this.transferRects.add(new RecipeTransferRect(new Rectangle(7, 62, 16, 16), getOverlayIdentifier()));
        if (!NEI_Config.isAdded) {
            FMLInterModComms.sendRuntimeMessage(
                MobsInfo.instance,
                "NEIPlugins",
                "register-crafting-handler",
                "MobsInfo@" + getRecipeName() + "@" + getOverlayIdentifier());
            GuiCraftingRecipe.craftinghandlers.add(this);
            GuiUsageRecipe.usagehandlers.add(this);
        }
        this.scrollbar = new Scrollbar(this, 0, 83);
    }

    @Override
    public TemplateRecipeHandler newInstance() {
        return new MobHandler();
    }

    @Override
    public String getOverlayIdentifier() {
        return "mobsinfo.mobhandler";
    }

    @Override
    public String getGuiTexture() {
        return "mobsinfo:textures/gui/MobHandler.png";
    }

    private static final FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);

    @Override
    public void drawBackground(int recipe) {
        GL11.glColor4f(1f, 1f, 1f, 1f);
        GuiDraw.changeTexture(getGuiTexture());
        GuiDraw.drawTexturedModalRect(0, 0, 0, 0, 168, 166);

        MobCachedRecipe currentrecipe = ((MobCachedRecipe) arecipes.get(recipe));

        if (!currentrecipe.isUnlocked()) {
            GL11.glPushMatrix();

            GL11.glTranslatef(20.f, 20.f, 0.f);
            GL11.glScalef(4.f, 4.f, 0.f);
            GuiDraw.drawString("?", 0, 0, 0xFF555555, false);

            GL11.glPopMatrix();
            return;
        }

        scrollbar.beginBackground(recipe);
        {
            int x = 6, y = itemsYStart + 11, yshift = nextRowYShift;
            if (currentrecipe.normalOutputsCount > 0) {
                for (int i = 0; i < ((currentrecipe.normalOutputsCount - 1) / itemsPerRow) + 1; i++) {
                    GuiDraw.drawTexturedModalRect(x, y + (18 * i), 0, 192, 144, 18);
                    if (i > 0) GuiDraw.drawTexturedModalRect(x, y + ((18 * i) - 1), 0, 193, 144, 2);
                }
                y += yshift + ((currentrecipe.normalOutputsCount - 1) / itemsPerRow) * 18;
            }
            if (currentrecipe.rareOutputsCount > 0) {
                for (int i = 0; i < ((currentrecipe.rareOutputsCount - 1) / itemsPerRow) + 1; i++) {
                    GuiDraw.drawTexturedModalRect(x, y + (18 * i), 0, 192, 144, 18);
                    if (i > 0) GuiDraw.drawTexturedModalRect(x, y + ((18 * i) - 1), 0, 193, 144, 2);
                }
                y += yshift + ((currentrecipe.rareOutputsCount - 1) / itemsPerRow) * 18;
            }
            if (currentrecipe.additionalOutputsCount > 0) {
                for (int i = 0; i < ((currentrecipe.additionalOutputsCount - 1) / itemsPerRow) + 1; i++) {
                    GuiDraw.drawTexturedModalRect(x, y + (18 * i), 0, 192, 144, 18);
                    if (i > 0) GuiDraw.drawTexturedModalRect(x, y + ((18 * i) - 1), 0, 193, 144, 2);
                }
                y += yshift + ((currentrecipe.additionalOutputsCount - 1) / itemsPerRow) * 18;
            }
            if (currentrecipe.infernalOutputsCount > 0) {
                for (int i = 0; i < ((currentrecipe.infernalOutputsCount - 1) / itemsPerRow) + 1; i++) {
                    GuiDraw.drawTexturedModalRect(x, y + (18 * i), 0, 192, 144, 18);
                    if (i > 0) GuiDraw.drawTexturedModalRect(x, y + ((18 * i) - 1), 0, 193, 144, 2);
                }
                y += yshift + ((currentrecipe.additionalOutputsCount - 1) / itemsPerRow) * 18;
            }
            scrollbar.reportMaxContentDrawn(y);
        }
        scrollbar.endBackground(recipe);

        GuiDraw.changeTexture(getGuiTexture());

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glColor4f(1f, 1f, 1f, 1f);

        Minecraft mc = Minecraft.getMinecraft();

        ScaledResolution scale = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);

        int width = scale.getScaledWidth();
        int height = scale.getScaledHeight();
        int mouseX = Mouse.getX() * width / mc.displayWidth;
        int mouseZ = height - Mouse.getY() * height / mc.displayHeight - 1;

        // Get current x,y from matrix
        matrixBuffer.clear();
        GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, matrixBuffer);
        float x = matrixBuffer.get(12);
        float y = matrixBuffer.get(13);

        int stackdepth = GL11.glGetInteger(GL11.GL_MODELVIEW_STACK_DEPTH);

        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);

        GL11.glPushMatrix();

        float healthScale = BossStatus.healthScale;
        int statusBarTime = BossStatus.statusBarTime;
        String bossName = BossStatus.bossName;
        boolean hasColorModifier = BossStatus.hasColorModifier;

        BossStatus.statusBarTime = 0;

        try {
            EntityLiving e = currentrecipe.mob;

            int mobx = 31, moby = 50;
            e.setPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
            e.lastTickPosX = e.posX;
            e.lastTickPosY = e.posY;
            e.lastTickPosZ = e.posZ;

            org.lwjgl.util.Rectangle v = MobUtils.getMobSizeInGui(e, mobx, moby, 20);

            // convert to local coordinate:
            float ylocal = (v.getY() + v.getHeight()) - y;
            float wantedy = 54.f;

            float new_scale = (40.f / v.getHeight());
            float new_scale_x = (38.f / v.getWidth());
            if (new_scale_x < new_scale) new_scale = new_scale_x;

            new_scale = (float) Math.round(20.f * new_scale) / 20.f;

            float a = moby - ylocal;
            float aa = a - (a * new_scale);
            float aaa = (wantedy - ylocal) - aa;

            // ARGS: x, y, scale, rot, rot, entity
            GuiInventory.func_147046_a(
                mobx,
                (int) (moby + aaa),
                Math.round(20.f * new_scale),
                (x + mobx) - mouseX,
                y + moby - 25 - mouseZ,
                e);

        } catch (Throwable ex) {
            Tessellator tes = Tessellator.instance;
            try {
                tes.draw();
            } catch (Exception ignored) {}
        }

        if (BossStatus.statusBarTime > 0 && currentrecipe.isBoss.isEmpty()) currentrecipe.isBoss = BossStatus.bossName;

        BossStatus.healthScale = healthScale;
        BossStatus.statusBarTime = statusBarTime;
        BossStatus.bossName = bossName;
        BossStatus.hasColorModifier = hasColorModifier;

        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        stackdepth -= GL11.glGetInteger(GL11.GL_MODELVIEW_STACK_DEPTH);
        if (stackdepth < 0) for (; stackdepth < 0; stackdepth++) GL11.glPopMatrix();
        if (stackdepth > 0) {
            for (; stackdepth > 0; stackdepth--) GL11.glPushMatrix();
            GL11.glLoadMatrix(matrixBuffer);
        }
        GL11.glPopAttrib();

        int err;
        while ((err = GL11.glGetError()) != GL11.GL_NO_ERROR) if (Config.Debug.showRenderErrors)
            LOG.error(currentrecipe.mobname + " | GL ERROR: " + err + " / " + GLU.gluErrorString(err));

        GL11.glDisable(GL11.GL_DEPTH_TEST);
    }

    private int drawStringWithWordWrap(String string, int x, int y, int yshift, int width, int color, boolean shadow) {
        @SuppressWarnings("unchecked")
        List<String> s = GuiDraw.fontRenderer.listFormattedStringToWidth(string, width);
        for (int i = 0, sSize = s.size(); i < sSize; i++) {
            String s1 = s.get(i);
            if (i > 0) GuiDraw.drawString(" " + s1, x, y, color, shadow);
            else GuiDraw.drawString(s1, x, y, color, shadow);
            y += yshift;
        }
        return yshift * s.size();
    }

    private boolean biomeTooltip = false;
    private int biomeTooltipX = 0;
    private int biomeTooltipY = 0;
    private int biomeTooltipWidth = 0;
    private int biomeTooltipHeight = 0;
    private boolean shouldDisplayExceptionList = false;
    private Set<SpawnInfo> biomeTooltipList = null;

    private void setBiomeSpawnTooltip(boolean enabled, int x, int y, int width, int height, boolean but,
        Set<SpawnInfo> tip) {
        biomeTooltip = enabled;
        biomeTooltipX = x;
        biomeTooltipY = y;
        biomeTooltipWidth = width;
        biomeTooltipHeight = height;
        shouldDisplayExceptionList = but;
        biomeTooltipList = tip;
    }

    @Override
    public void drawForeground(int recipe) {
        MobCachedRecipe currentrecipe = ((MobCachedRecipe) arecipes.get(recipe));
        int y = 7, yshift = 10, x = 57;

        y += drawStringWithWordWrap(currentrecipe.localizedName, x, y, yshift, 168 - x, 0xFF555555, false) - yshift;
        if (Minecraft.getMinecraft().gameSettings.advancedItemTooltips && NEIClientUtils.shiftKey())
            GuiDraw.drawString(currentrecipe.mobname, x, y += yshift, 0xFF555555, false);
        GuiDraw.drawString(Translations.MOD.get() + currentrecipe.mod, x, y += yshift, 0xFF555555, false);
        if (!currentrecipe.isUnlocked()) {
            x = 6;
            y = 83;
            GuiDraw.drawStringC(Translations.LOCKED.get(), 168 / 2, y += yshift, 0xFF555555, false);
            GuiDraw.drawStringC(Translations.LOCKED_1.get(), 168 / 2, y += yshift, 0xFF555555, false);
            return;
        }
        GuiDraw.drawString(Translations.MAX_HEALTH.get() + currentrecipe.maxHealth, x, y += yshift, 0xFF555555, false);
        switch (currentrecipe.infernaltype) {
            case -1:
                break;
            case 0:
                GuiDraw.drawString(Translations.INFERNAL_CANNOT.get(), x, y += yshift, 0xFF555555, false);
                break;
            case 1:
                GuiDraw.drawString(Translations.INFERNAL_CAN.get(), x, y += yshift, 0xFFFF0000, false);
                break;
            case 2:
                GuiDraw.drawString(Translations.INFERNAL_ALWAYS.get(), x, y += yshift, 0xFFFF0000, false);
                break;
        }

        if (!currentrecipe.isBoss.isEmpty())
            GuiDraw.drawString(EnumChatFormatting.BOLD + "" + BOSS.get(), x, y += yshift, 0xFFD68F00, false);

        if (currentrecipe.isPeacefulAllowed)
            GuiDraw.drawString(Translations.PEACEFUL_ALLOWED.get(), x, y += yshift, 0xFF005500, false);

        if (!currentrecipe.isUsableInVial)
            GuiDraw.drawString(Translations.CANNOT_USE_VIAL.get(), x, y += yshift, 0xFF555555, false);

        if (currentrecipe.spawnList != null && !currentrecipe.spawnList.isEmpty()) {
            int possiblePlaces = SpawnInfo.getAllKnownInfos()
                .size();
            if (currentrecipe.spawnList.size() >= possiblePlaces && !NEIClientUtils.shiftKey()) {
                GuiDraw.drawString(SPAWNS_EVERYWHERE.get(), x, y += yshift, 0xFF555555, false);
                setBiomeSpawnTooltip(false, 0, 0, 0, 0, false, null);
            } else if (currentrecipe.spawnList.size() < possiblePlaces / 2 || NEIClientUtils.shiftKey()) {
                GuiDraw.drawString(
                    EnumChatFormatting.UNDERLINE + SPAWNS_IN.get(currentrecipe.spawnList.size()),
                    x,
                    y += yshift,
                    0xFF555555,
                    false);
                setBiomeSpawnTooltip(
                    true,
                    x,
                    y,
                    GuiDraw.getStringWidth(SPAWNS_IN.get(currentrecipe.spawnList.size())),
                    8,
                    false,
                    currentrecipe.spawnList);
            } else {
                y += drawStringWithWordWrap(
                    EnumChatFormatting.UNDERLINE + SPAWNS_NOT_IN.get(possiblePlaces - currentrecipe.spawnList.size()),
                    x,
                    y + yshift,
                    yshift,
                    168 - x,
                    0xFF555555,
                    false);
                setBiomeSpawnTooltip(true, x, y - yshift, 168 - x, 18, true, currentrecipe.spawnList);
            }
        } else {
            // GuiDraw.drawString("Doesn't spawn naturally", x, y += yshift, 0xFF555555, false);
            setBiomeSpawnTooltip(false, 0, 0, 0, 0, false, null);
        }

        if (!currentrecipe.additionalInformation.isEmpty()) {
            for (String s : currentrecipe.additionalInformation) {
                GuiDraw.drawString(s, x, y += yshift, 0xFF555555, false);
            }
        }

        y += yshift;

        itemsYStart = Math.max(y, itemsYStartMin);

        currentrecipe.mOutputs
            .forEach(o -> { if (o instanceof MobPositionedStack) ((MobPositionedStack) o).setYStart(itemsYStart); });

        scrollbar.beginForeground(recipe);
        {
            x = 6;
            y = itemsYStart;
            yshift = nextRowYShift;
            if (currentrecipe.normalOutputsCount > 0) {
                GuiDraw.drawString(Translations.NORMAL_DROPS.get(), x, y, 0xFF555555, false);
                y += yshift + ((currentrecipe.normalOutputsCount - 1) / itemsPerRow) * 18;
            }
            if (currentrecipe.rareOutputsCount > 0) {
                GuiDraw.drawString(Translations.RARE_DROPS.get(), x, y, 0xFF555555, false);
                y += yshift + ((currentrecipe.rareOutputsCount - 1) / itemsPerRow) * 18;
            }
            if (currentrecipe.additionalOutputsCount > 0) {
                GuiDraw.drawString(Translations.ADDITIONAL_DROPS.get(), x, y, 0xFF555555, false);
                y += yshift + ((currentrecipe.additionalOutputsCount - 1) / itemsPerRow) * 18;
            }
            if (currentrecipe.infernalOutputsCount > 0) {
                GuiDraw.drawString(Translations.INFERNAL_DROPS.get(), x, y, 0xFF555555, false);
                y += yshift + ((currentrecipe.additionalOutputsCount - 1) / itemsPerRow) * 18;
            }
            yshift = 10;
        }
        scrollbar.endForeground(recipe);
    }

    @Override
    public String getRecipeName() {
        return "Mob Info";
    }

    @Override
    public IUsageHandler getUsageAndCatalystHandler(String inputId, Object... ingredients) {
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
        if (outputId.equals(getOverlayIdentifier())) {
            arecipes.addAll(cachedRecipes);
            return;
        }
        super.loadCraftingRecipes(outputId, results);
    }

    @Override
    public void loadCraftingRecipes(ItemStack result) {
        if (LoaderReference.Gregtech5) {
            List<ItemStack> results = GT5Helper.getAssociated(result);
            for (MobCachedRecipe r : cachedRecipes) if (results.stream()
                .anyMatch(i -> r.contains(r.mOutputs, i)) && r.isUnlocked()) arecipes.add(r);
        } else for (MobCachedRecipe r : cachedRecipes)
            if (r.contains(r.mOutputs, result) && r.isUnlocked()) arecipes.add(r);
    }

    @Override
    public void loadUsageRecipes(ItemStack ingredient) {
        if (LoaderReference.EnderIO) {
            Item item = ingredient.getItem();
            if (item == Item.getItemFromBlock(EnderIOGetter.blockPoweredSpawner())
                || item == EnderIOGetter.itemSoulVessel()
                || item == EnderIOGetter.itemBrokenSpawner()) {
                String mobType = EnderIOGetter.getContainedMobOrNull(ingredient);
                if (mobType == null) {
                    loadCraftingRecipes(getOverlayIdentifier(), (Object) null);
                    return;
                }
                for (MobCachedRecipe r : cachedRecipes) if (r.mobname.equals(mobType)) arecipes.add(r);
                return;
            }
        }
        for (MobCachedRecipe r : cachedRecipes) if (r.mInput.stream()
            .anyMatch(ingredient::isItemEqual)) arecipes.add(r);
    }

    public static boolean isUsageInfernalMob(ItemStack ingredient) {
        if (LoaderReference.EnderIO) {
            Item item = ingredient.getItem();
            if (item == Item.getItemFromBlock(EnderIOGetter.blockPoweredSpawner())
                || item == EnderIOGetter.itemSoulVessel()
                || item == EnderIOGetter.itemBrokenSpawner()) {
                String mobType = EnderIOGetter.getContainedMobOrNull(ingredient);
                if (mobType == null) return false;
                for (MobCachedRecipe r : cachedRecipes) if (r.mobname.equals(mobType)) return r.infernaltype > 0;
                return false;
            }
        }
        for (MobCachedRecipe r : cachedRecipes) if (r.mInput.stream()
            .anyMatch(ingredient::isItemEqual) && r.infernaltype > 0) return true;
        return false;
    }

    @Override
    public void onUpdate() {
        cycleTicksStatic++;
        for (Integer recipe : ((GuiRecipe<?>) Minecraft.getMinecraft().currentScreen).getRecipeIndices()) {
            ((MobCachedRecipe) arecipes.get(recipe)).onUpdate();
        }
    }

    @Override
    public boolean mouseScrolled(GuiRecipe<?> gui, int scroll, int recipe) {
        if (super.mouseScrolled(gui, scroll, recipe)) return true;
        return scrollbar.mouseScrolled(gui, scroll, recipe);
    }

    private static final Rectangle extendedTooltipRect = new Rectangle(28, 62, 8, 16);

    @Override
    public List<String> handleTooltip(GuiRecipe<?> gui, List<String> currenttip, int recipe) {
        currenttip = super.handleTooltip(gui, currenttip, recipe);
        Point pos = GuiDraw.getMousePosition();
        Point offset = gui.getRecipePosition(recipe);
        Point relMouse = new Point(
            pos.x - ((GuiContainerAccessor) gui).getGuiLeft() - offset.x,
            pos.y - ((GuiContainerAccessor) gui).getGuiTop() - offset.y);
        if (extendedTooltipRect.contains(relMouse)) {
            currenttip.addAll(Translations.EXTENDED_INFO.getAllLines());
        }
        if (biomeTooltip
            && new Rectangle(biomeTooltipX, biomeTooltipY, biomeTooltipWidth, biomeTooltipHeight).contains(relMouse)) {
            if (shouldDisplayExceptionList) {
                for (SpawnInfo info : SpawnInfo.getAllKnownInfos()
                    .stream()
                    .filter(Objects::nonNull)
                    .filter(
                        i -> biomeTooltipList.stream()
                            .noneMatch(p -> p.hashCode() == i.hashCode()))
                    .collect(Collectors.toList())) currenttip.add(info.getInfo());
            } else for (SpawnInfo entry : biomeTooltipList) {
                currenttip.add(entry.getInfo());
            }
        }
        return currenttip;
    }

    @Override
    public List<String> handleItemTooltip(GuiRecipe<?> gui, ItemStack stack, List<String> currenttip, int recipe) {
        MobCachedRecipe currentrecipe = ((MobCachedRecipe) arecipes.get(recipe));
        PositionedStack positionedStack = currentrecipe.mOutputs.stream()
            .filter(ps -> ps.item == stack)
            .findFirst()
            .orElse(null);
        if (positionedStack instanceof MobPositionedStack)
            currenttip.addAll(((MobPositionedStack) positionedStack).extraTooltip);
        return currenttip;
    }

    @Override
    public Scrollbar getScrollbar() {
        return scrollbar;
    }

    @Override
    public List<PositionedStack> getAllItems(int recipe) {
        return ((MobCachedRecipe) arecipes.get(recipe)).getOutputs();
    }

    public static class MobPositionedStack extends PositionedStack {

        public final MobDrop.DropType type;
        public final int chance;
        // not final in case of addRandomEnchantment fail
        public boolean enchantable;
        public final boolean randomdamage;
        public final List<Integer> damages;
        public final int enchantmentLevel;
        private final Random rand;
        public final List<String> extraTooltip;
        public final int yoffset;

        public MobPositionedStack(Object object, int x, int yoffset, MobDrop drop) {
            super(object, x, yoffset + itemsYStart, false);
            this.yoffset = yoffset;
            rand = new FastRandom();
            this.type = drop.type;
            this.chance = drop.chance;
            this.enchantable = drop.enchantable != null;
            if (this.enchantable) enchantmentLevel = drop.enchantable;
            else enchantmentLevel = 0;
            this.randomdamage = drop.damages != null;
            if (this.randomdamage) this.damages = new ArrayList<>(drop.damages.keySet());
            else this.damages = null;
            extraTooltip = new ArrayList<>();

            if (!drop.variableChance) {
                extraTooltip.add(
                    EnumChatFormatting.RESET
                        + Translations.CHANCE.get(chance == 0 ? "<0.01%" : (double) chance / 100d));
            } else {
                for (IChanceModifier chanceModifier : drop.chanceModifiers) {
                    chanceModifier.applyTooltip(extraTooltip);
                }
            }
            if (drop.lootable) extraTooltip.add(EnumChatFormatting.RESET + Translations.LOOTABLE.get());
            if (drop.playerOnly) {
                extraTooltip.add(EnumChatFormatting.RESET + Translations.PLAYER_ONLY.get());
            }
            if (drop.additionalInfo != null && !drop.additionalInfo.isEmpty()) extraTooltip.addAll(drop.additionalInfo);
            extraTooltip.add(EnumChatFormatting.RESET + Translations.AVERAGE_REMINDER.get());

            setPermutationToRender(0);
        }

        @Override
        public void setPermutationToRender(int index) {
            if (this.item == null) this.item = this.items[0].copy();
            if (enchantable) {
                if (this.item.getItem() == Items.enchanted_book) this.item = this.items[0].copy();
                if (this.item.hasTagCompound()) this.item.getTagCompound()
                    .removeTag("ench");
                try {
                    EnchantmentHelper.addRandomEnchantment(rand, this.item, enchantmentLevel);
                } catch (Exception e) {
                    GameRegistry.UniqueIdentifier ui = GameRegistry.findUniqueIdentifierFor(this.item.getItem());
                    LOG.error(
                        "addRandomEnchantment failed on {}:{}, marking this item as not enchantable! Printing stacktrace:",
                        ui.toString(),
                        this.item.getItemDamage());
                    e.printStackTrace();
                    enchantable = false;
                }
            }
            if (randomdamage) this.item.setItemDamage(damages.get(rand.nextInt(damages.size())));
        }

        public void setYStart(int ystart) {
            rely = ystart + yoffset;
        }
    }

    private class MobCachedRecipe extends TemplateRecipeHandler.CachedRecipe {

        public final EntityLiving mob;
        public final List<PositionedStack> mOutputs;
        public final List<ItemStack> mInput;
        public final String mobname;
        public final int infernaltype;
        public final PositionedStack ingredient;
        public final String localizedName;
        public final String mod;
        public final float maxHealth;
        public final int normalOutputsCount;
        public final int rareOutputsCount;
        public final int additionalOutputsCount;
        public final int infernalOutputsCount;
        public final boolean isUsableInVial;
        public final boolean isPeacefulAllowed;
        public final List<String> additionalInformation;
        public final Set<SpawnInfo> spawnList;
        public String isBoss = "";

        public MobCachedRecipe(EntityLiving mob, List<MobPositionedStack> mOutputs, int normalOutputsCount,
            int rareOutputsCount, int additionalOutputsCount, int infernalOutputsCount) {
            super();
            String classname = mob.getClass()
                .getName();
            this.mod = ModUtils.getModNameFromClassName(classname);
            this.mob = mob;
            this.maxHealth = mob.getMaxHealth();
            this.mOutputs = new ArrayList<>(mOutputs.size());
            this.mOutputs.addAll(mOutputs);
            this.normalOutputsCount = normalOutputsCount;
            this.rareOutputsCount = rareOutputsCount;
            this.additionalOutputsCount = additionalOutputsCount;
            this.infernalOutputsCount = infernalOutputsCount;
            this.mInput = new ArrayList<>();
            this.isPeacefulAllowed = !(mob instanceof IMob);
            int id = EntityList.getEntityID(mob);
            mobname = EntityList.getEntityString(mob);
            // noinspection ConstantConditions
            localizedName = mobname.equals("Skeleton") && ((EntitySkeleton) mob).getSkeletonType() == 1
                ? "Wither Skeleton"
                : (!mob.getCommandSenderName()
                    .startsWith("entity.") ? mob.getCommandSenderName() : mobname);
            if (id != 0) {
                this.mInput.add(new ItemStack(Items.spawn_egg, 1, id));
                this.mInput.add(new ItemStack(Blocks.mob_spawner, 1, id));
            }
            if (LoaderReference.EnderIO) {
                this.mInput.add(0, EnderIOGetter.BlockPoweredSpawner$createItemStackForMob(mobname));
                this.mInput.add(1, EnderIOGetter.ItemSoulVessel$createVesselWithEntityStub(mobname));
                this.mInput.add(2, EnderIOGetter.ItemBrokenSpawner$createStackForMobType(mobname));
            } // else if (id == 0) this.mInput.add(new ItemStack(Items.spawn_egg, 1, 0)); // ???
            if (!this.mInput.isEmpty()) ingredient = new PositionedStack(this.mInput, 38, 44, true);
            else ingredient = null;
            this.isUsableInVial = EnderIOHelper.canEntityBeCapturedWithSoulVial(mob, mobname);

            if (!LoaderReference.InfernalMobs) infernaltype = -1; // not supported
            else {
                InfernalMobsCoreAccessor infernalMobsCore = (InfernalMobsCoreAccessor) InfernalMobsCore.instance();
                if (!InfernalMobsCoreHelper.callIsClassAllowed((InfernalMobsCore) infernalMobsCore, mob))
                    infernaltype = 0; // not allowed
                else if (infernalMobsCore.callCheckEntityClassForced(mob)) infernaltype = 2; // forced
                else infernaltype = 1; // normal
            }
            this.additionalInformation = new ArrayList<>();
            this.spawnList = MobRecipe.getSpawnListByMobName(mobname);
            MinecraftForge.EVENT_BUS.post(new MobNEIRegistrationEvent(mobname, mob, this.additionalInformation));
        }

        public boolean isUnlocked() {
            if (!Config.MobHandler.hiddenMode) return true;
            PlayerData localData = PlayerDataManager.getPlayer(null);
            return localData.killedMobs.contains(mobname);
        }

        @Override
        public PositionedStack getIngredient() {
            if (!NEIClientUtils.shiftKey() && ingredient != null) {
                ingredient.setPermutationToRender((cycleTicksStatic / 10) % ingredient.items.length);
            }
            return ingredient;
        }

        @Override
        public PositionedStack getResult() {
            return null;
        }

        public void onUpdate() {
            if (!NEIClientUtils.shiftKey() && cycleTicksStatic % 10 == 0)
                mOutputs.forEach(p -> p.setPermutationToRender(0));
        }

        public List<PositionedStack> getOutputs() {
            if (!isUnlocked()) return Collections.emptyList();
            return mOutputs;
        }

        @Override
        public List<PositionedStack> getOtherStacks() {
            return Collections.emptyList();
        }
    }

    private static class GT5Helper {

        public static List<ItemStack> getAssociated(ItemStack aResult) {
            ItemData tPrefixMaterial = GT_OreDictUnificator.getAssociation(aResult);

            ArrayList<ItemStack> tResults = new ArrayList<>();
            tResults.add(aResult);
            tResults.add(GT_OreDictUnificator.get(true, aResult));
            if ((tPrefixMaterial != null) && (!tPrefixMaterial.mBlackListed)
                && (!tPrefixMaterial.mPrefix.mFamiliarPrefixes.isEmpty())) {
                for (OrePrefixes tPrefix : tPrefixMaterial.mPrefix.mFamiliarPrefixes) {
                    tResults.add(GT_OreDictUnificator.get(tPrefix, tPrefixMaterial.mMaterial.mMaterial, 1L));
                }
            }
            if (aResult.getUnlocalizedName()
                .startsWith("gt.blockores")) {
                for (int i = 0; i < 8; i++) {
                    tResults.add(new ItemStack(aResult.getItem(), 1, aResult.getItemDamage() % 1000 + i * 1000));
                }
            }

            return tResults;
        }
    }
}
