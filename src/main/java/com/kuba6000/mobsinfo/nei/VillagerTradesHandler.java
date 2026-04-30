package com.kuba6000.mobsinfo.nei;

import static com.kuba6000.mobsinfo.MobsInfo.MODID;
import static com.kuba6000.mobsinfo.nei.VillagerTradesHandler.Translations.*;

import java.awt.Point;
import java.awt.Rectangle;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.BufferUtils;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import com.kuba6000.mobsinfo.MobsInfo;
import com.kuba6000.mobsinfo.api.VillagerRecipe;
import com.kuba6000.mobsinfo.api.VillagerTrade;
import com.kuba6000.mobsinfo.api.helper.TranslationHelper;
import com.kuba6000.mobsinfo.api.utils.FastRandom;
import com.kuba6000.mobsinfo.api.utils.MobUtils;
import com.kuba6000.mobsinfo.config.Config;
import com.kuba6000.mobsinfo.mixin.early.minecraft.GuiContainerAccessor;

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
import cpw.mods.fml.common.registry.VillagerRegistry;

public class VillagerTradesHandler extends TemplateRecipeHandler {

    enum Translations {

        NAME,
        MOD,
        CHANCE,
        REAL_CHANCE,

        ;

        final String key;

        Translations() {
            key = "mobsinfo.villagertradeshandler." + this.name()
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

    private static final String[] vanillaVillagers = { "farmer", "librarian", "priest", "blacksmith", "butcher" };
    private static final List<VillagerCachedRecipe> cachedRecipes = new ArrayList<>();
    private static final Logger LOG = LogManager.getLogger(MODID + "[Villager Trades Handler]");
    private static final VillagerTradesHandler instance = new VillagerTradesHandler();
    public static int cycleTicksStatic = Math.abs((int) System.currentTimeMillis());

    public static void addRecipe(VillagerRecipe recipe) {
        if (recipe != null) addRecipe(recipe.trades, recipe.mob);
    }

    public static void addRecipe(List<VillagerTrade> recipeList, EntityVillager displayMob) {
        ArrayList<Pair<Pair<Pair<VillagerCachedRecipe.PositionedTradeItem, VillagerCachedRecipe.PositionedTradeItem>, VillagerCachedRecipe.PositionedTradeItem>, VillagerTrade>> tradeList = new ArrayList<>(
            recipeList.size());
        int x = 7;
        int y = 12 + 83;
        boolean second = false;
        for (VillagerTrade recipe : recipeList) {
            tradeList.add(
                Pair.of(
                    Pair.of(
                        Pair.of(
                            new VillagerCachedRecipe.PositionedTradeItem(recipe.getFirstInput(), x, y),
                            recipe.hasSecondInput()
                                ? new VillagerCachedRecipe.PositionedTradeItem(recipe.getSecondInput(), x + 18, y)
                                : null),
                        new VillagerCachedRecipe.PositionedTradeItem(recipe.getOutput(), x + 59, y)),
                    recipe));
            if (!second) {
                x += 59 + 20;
                second = true;
            } else {
                x = 7;
                y += 20;
                second = false;
            }
        }
        instance.addRecipeInt(tradeList, displayMob, y + 30);
    }

    public static void clearRecipes() {
        cachedRecipes.clear();
    }

    public static void sortCachedRecipes() {
        cachedRecipes.sort((o1, o2) -> {
            boolean m1 = o1.mod.equals("Minecraft");
            boolean m2 = o2.mod.equals("Minecraft");
            if (m1 && !m2) return -1;
            else if (!m1 && m2) return 1;
            if (!o1.mod.equals(o2.mod)) return o1.mod.compareTo(o2.mod);
            else return o1.profession.compareTo(o2.profession);
        });
    }

    private void addRecipeInt(
        ArrayList<Pair<Pair<Pair<VillagerCachedRecipe.PositionedTradeItem, VillagerCachedRecipe.PositionedTradeItem>, VillagerCachedRecipe.PositionedTradeItem>, VillagerTrade>> tradeList,
        EntityVillager displayMob, int maxHeight) {
        cachedRecipes.add(new VillagerCachedRecipe(tradeList, displayMob, maxHeight));
    }

    @Override
    public TemplateRecipeHandler newInstance() {
        return new VillagerTradesHandler();
    }

    @Override
    public String getOverlayIdentifier() {
        return "mobsinfo.villagertradeshandler";
    }

    @Override
    public String getGuiTexture() {
        return "mobsinfo:textures/gui/VillagerTrades.png";
    }

    @Override
    public String getRecipeName() {
        return "Villager Trades";
    }

    public VillagerTradesHandler() {
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
    }

    private static final FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);

    private HashMap<Rectangle, VillagerTrade> recipeRects = new HashMap<>();

    @Override
    public int getRecipeHeight(int recipe) {
        return ((VillagerCachedRecipe) arecipes.get(recipe)).maxHeight;
    }

    @Override
    public void drawBackground(int recipe) {
        GL11.glColor4f(1f, 1f, 1f, 1f);
        GuiDraw.changeTexture(getGuiTexture());
        GuiDraw.drawTexturedModalRect(0, 0, 0, 0, 168, 105);

        VillagerCachedRecipe currentRecipe = (VillagerCachedRecipe) arecipes.get(recipe);

        {
            recipeRects.clear();
            int x = 6;
            int y = 11 + 83;
            boolean second = false;
            for (int i = 0; i < currentRecipe.tradeList.size(); i++) {
                GuiDraw.drawTexturedModalRect(x, y, 0, 192, 77, 18);
                recipeRects.put(
                    new Rectangle(x + 35, y, 24, 18),
                    currentRecipe.tradeList.get(i)
                        .getValue());
                if (!second) {
                    x += 59 + 20;
                    second = true;
                } else {
                    x = 6;
                    y += 20;
                    second = false;
                }
            }
        }
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

        try {
            EntityLiving e = currentRecipe.displayMob;

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

        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        stackdepth -= GL11.glGetInteger(GL11.GL_MODELVIEW_STACK_DEPTH);
        if (stackdepth < 0) for (; stackdepth < 0; stackdepth++) GL11.glPopMatrix();
        if (stackdepth > 0) {
            for (; stackdepth > 0; stackdepth--) GL11.glPushMatrix();
            GL11.glLoadMatrix(matrixBuffer);
        }
        GL11.glPopAttrib();

        int err;
        while ((err = GL11.glGetError()) != GL11.GL_NO_ERROR) if (Config.Debug.showRenderErrors) LOG.error(
            "Profession {} | GL ERROR: {} / {}",
            currentRecipe.displayMob.getProfession(),
            err,
            GLU.gluErrorString(err));

        GL11.glDisable(GL11.GL_DEPTH_TEST);

    }

    @Override
    public void drawForeground(int recipe) {
        VillagerCachedRecipe currentRecipe = (VillagerCachedRecipe) arecipes.get(recipe);
        int y = 7, yshift = 10, x = 57;
        GuiDraw.drawString(
            NAME.get() + currentRecipe.profession,
            x,
            y += yshift,
            EnumColors.TEXT_DEFAULT.getColor(),
            false);
        if (Minecraft.getMinecraft().gameSettings.advancedItemTooltips && NEIClientUtils.shiftKey()) GuiDraw
            .drawString("ID: " + currentRecipe.professionID, x, y += yshift, EnumColors.TEXT_DEFAULT.getColor(), false);
        GuiDraw.drawString(MOD.get() + currentRecipe.mod, x, y += yshift, EnumColors.TEXT_DEFAULT.getColor(), false);

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
        for (VillagerCachedRecipe r : cachedRecipes) if (r.contains(r.mOutputs, result)) arecipes.add(r);
    }

    @Override
    public void loadUsageRecipes(ItemStack ingredient) {
        for (VillagerCachedRecipe r : cachedRecipes) if (r.contains(r.mInputs, ingredient)) arecipes.add(r);
    }

    public VillagerTradesHandler addAllRecipes() {
        arecipes.addAll(cachedRecipes);
        return this;
    }

    @Override
    public void onUpdate() {
        cycleTicksStatic++;
        if (Minecraft.getMinecraft().currentScreen instanceof GuiRecipe<?>guiRecipe) {
            for (Integer recipe : guiRecipe.getRecipeIndices()) {
                ((VillagerCachedRecipe) arecipes.get(recipe)).onUpdate();
            }
        }
    }

    @Override
    public List<String> handleTooltip(GuiRecipe<?> gui, List<String> currenttip, int recipe) {
        currenttip = super.handleTooltip(gui, currenttip, recipe);
        Point pos = GuiDraw.getMousePosition();
        Point offset = gui.getRecipePosition(recipe);
        Point relMouse = new Point(
            pos.x - ((GuiContainerAccessor) gui).getGuiLeft() - offset.x,
            pos.y - ((GuiContainerAccessor) gui).getGuiTop() - offset.y);
        VillagerCachedRecipe currentRecipe = (VillagerCachedRecipe) arecipes.get(recipe);
        for (Map.Entry<Rectangle, VillagerTrade> entry : recipeRects.entrySet()) {
            if (entry.getKey()
                .contains(relMouse)) {
                currenttip.addAll(
                    Arrays.asList(
                        CHANCE.get(
                            entry.getValue()
                                .getChance() * 100d),
                        REAL_CHANCE.get(
                            (entry.getValue()
                                .getChance() / currentRecipe.tradeList.size()) * 100d)));
                break;
            }
        }
        return currenttip;
    }

    class VillagerCachedRecipe extends CachedRecipe {

        static class PositionedTradeItem extends PositionedStack {

            private final VillagerTrade.TradeItem tradeItem;
            private final List<Integer> possibleSizes;
            private final Random rand;

            public PositionedTradeItem(VillagerTrade.TradeItem tradeItem, int x, int y) {
                super(tradeItem.stack, x, y, false);
                this.tradeItem = tradeItem;
                this.rand = new FastRandom();
                this.possibleSizes = tradeItem.possibleSizes == null ? null : new ArrayList<>(tradeItem.possibleSizes);

                setPermutationToRender(0);
            }

            @Override
            public void setPermutationToRender(int index) {
                if (this.item == null) this.item = this.items[0].copy();
                if (this.tradeItem == null) return; // not initialized
                if (tradeItem.enchantability != null) {
                    if (this.item.getItem() == Items.enchanted_book) this.item = this.items[0].copy();
                    if (this.item.hasTagCompound()) this.item.getTagCompound()
                        .removeTag("ench");
                    try {
                        EnchantmentHelper.addRandomEnchantment(rand, this.item, tradeItem.enchantability);
                    } catch (Exception e) {
                        GameRegistry.UniqueIdentifier ui = GameRegistry.findUniqueIdentifierFor(this.item.getItem());
                        LOG.error(
                            "addRandomEnchantment failed on {}:{}, marking this item as not enchantable! Printing stacktrace:",
                            ui.toString(),
                            this.item.getItemDamage());
                        e.printStackTrace();
                        tradeItem.enchantability = null;
                    }
                }
                if (possibleSizes != null) {
                    this.item.stackSize = possibleSizes.get(rand.nextInt(possibleSizes.size()));
                }
            }
        }

        private final ArrayList<Pair<Pair<Pair<PositionedTradeItem, PositionedTradeItem>, PositionedTradeItem>, VillagerTrade>> tradeList;
        private final ArrayList<PositionedStack> mOutputs;
        private final ArrayList<PositionedStack> mInputs;
        private final EntityVillager displayMob;
        private final int professionID;
        private final String profession;
        private final String mod;
        private final int maxHeight;

        public VillagerCachedRecipe(
            ArrayList<Pair<Pair<Pair<PositionedTradeItem, PositionedTradeItem>, PositionedTradeItem>, VillagerTrade>> tradeList,
            EntityVillager displayMob, int maxHeight) {
            this.tradeList = tradeList;
            this.mOutputs = new ArrayList<>();
            this.mInputs = new ArrayList<>();
            this.maxHeight = maxHeight;
            for (var trade : this.tradeList) {
                mOutputs.add(
                    trade.getLeft()
                        .getRight());
                mInputs.add(
                    trade.getLeft()
                        .getLeft()
                        .getLeft());
                if (trade.getLeft()
                    .getLeft()
                    .getRight() != null)
                    mInputs.add(
                        trade.getLeft()
                            .getLeft()
                            .getRight());
            }
            this.displayMob = displayMob;
            this.professionID = this.displayMob.getProfession();

            if (this.professionID >= 0 && this.professionID <= 4) {
                String professionString = vanillaVillagers[this.professionID];
                this.mod = "Minecraft";

                if (StatCollector.canTranslate("description.villager.profession." + professionString))
                    this.profession = StatCollector
                        .translateToLocal("description.villager.profession." + professionString);
                else this.profession = StringUtils.capitalize(professionString);
            } else {
                ResourceLocation villagerSkin = VillagerRegistry.getVillagerSkin(this.professionID, null);
                if (villagerSkin == null) {
                    this.mod = "Unknown";
                    this.profession = "Unknown-" + this.professionID;
                } else {
                    String professionString = "";
                    String path = villagerSkin.getResourcePath();
                    path = path.substring(path.lastIndexOf('/') + 1);
                    if (path.indexOf('.') != -1) professionString = path.substring(0, path.lastIndexOf('.'));
                    else professionString = path;
                    this.mod = villagerSkin.getResourceDomain();

                    if (StatCollector
                        .canTranslate("description.villager.profession." + this.mod + '.' + professionString))
                        this.profession = StatCollector
                            .translateToLocal("description.villager.profession." + this.mod + '.' + professionString);
                    else this.profession = StringUtils.capitalize(professionString);
                }
            }

        }

        @Override
        public PositionedStack getResult() {
            return null;
        }

        public void onUpdate() {
            if (!NEIClientUtils.shiftKey() && cycleTicksStatic % 10 == 0) {
                mOutputs.forEach(p -> p.setPermutationToRender(0));
                mInputs.forEach(p -> p.setPermutationToRender(0));
            }
        }

        @Override
        public List<PositionedStack> getOtherStacks() {
            return mOutputs;
        }

        @Override
        public List<PositionedStack> getIngredients() {
            return mInputs;
        }
    }
}
