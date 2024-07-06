package com.kuba6000.mobsinfo.nei.scrollable;

import java.awt.Point;
import java.awt.Rectangle;
import java.lang.ref.WeakReference;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.item.ItemStack;

import org.lwjgl.opengl.GL11;

import com.kuba6000.mobsinfo.mixin.minecraft.GuiAccessor;
import com.kuba6000.mobsinfo.mixin.minecraft.GuiContainerAccessor;

import codechicken.lib.gui.GuiDraw;
import codechicken.nei.NEIClientUtils;
import codechicken.nei.PositionedStack;
import codechicken.nei.guihook.GuiContainerManager;
import codechicken.nei.recipe.GuiRecipe;

public class Scrollbar {

    public int x;
    public int y;
    public int width;
    public int height;
    public boolean dynamicHeight;
    public int scrollOffset = 0;
    public int yMax;

    public WeakReference<IScrollableGUI> myGUI;

    public Scrollbar(IScrollableGUI myGUI, int x, int y) {
        this.myGUI = new WeakReference<>(myGUI);
        this.x = x;
        this.y = y;
        this.width = 168 - x;
        this.dynamicHeight = true;
    }

    public Scrollbar(IScrollableGUI myGUI, int x, int y, int width, int height) {
        this.myGUI = new WeakReference<>(myGUI);
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void reportMaxContentDrawn(int yMax) {
        this.yMax = yMax;
    }

    public boolean mouseScrolled(GuiRecipe<?> gui, int scroll, int recipe) {
        Point mouse = GuiDraw.getMousePosition();
        Point offset = gui.getRecipePosition(recipe);

        int bot = this.y
            + (this.dynamicHeight ? (((GuiContainerAccessor) gui).getYSize() - offset.y - 5 - this.y) : this.height);
        if (!NEIClientUtils.shiftKey()) {
            if (yMax > bot) {
                if (new Rectangle(
                    ((GuiContainerAccessor) gui).getGuiLeft() + offset.x + this.x,
                    ((GuiContainerAccessor) gui).getGuiTop() + offset.y + this.y,
                    this.width,
                    this.dynamicHeight ? (((GuiContainerAccessor) gui).getYSize() - offset.y - 5 - this.y)
                        : this.height).contains(mouse)) {
                    // - w dół
                    // + w góre
                    bot -= this.scrollOffset;
                    if (scroll < 0) {
                        int maxScroll = yMax - bot;
                        if (maxScroll > 0) {
                            scrollOffset += Math.max(-maxScroll, 10 * scroll);
                        }
                    } else {
                        if (this.scrollOffset < 0) {
                            scrollOffset += Math.min(-this.scrollOffset, 10 * scroll);
                        }
                    }
                    return true;
                }
            }
        } else {
            final Point relMouse = new Point(
                mouse.x - ((GuiContainerAccessor) gui).getGuiLeft() - offset.x,
                mouse.y - ((GuiContainerAccessor) gui).getGuiTop() - offset.y);
            PositionedStack overStack = getItemUnderMouse(recipe, relMouse.x, relMouse.y);

            if (overStack != null) {
                // just assume it's a custom permutation switcher :P
                overStack.setPermutationToRender(0);
                return true;
            }

            return false;
        }
        return false;
    }

    public void beginScissor(int recipe) {
        GL11.glPushMatrix();
        GL11.glTranslatef(0, scrollOffset, 0);
        Minecraft mc = Minecraft.getMinecraft();
        ScaledResolution res = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
        int scaleFactor = res.getScaleFactor();

        GuiRecipe<?> gui = (GuiRecipe<?>) mc.currentScreen;

        Point offset = gui.getRecipePosition(recipe);

        int ysize;
        if (this.dynamicHeight) {
            ysize = ((GuiContainerAccessor) gui).getYSize();
            ysize -= offset.y + 5;
        } else ysize = this.height + this.y;

        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor(
            (((GuiContainerAccessor) gui).getGuiLeft() + this.x) * scaleFactor,
            (res.getScaledHeight() - (((GuiContainerAccessor) gui).getGuiTop() + offset.y + ysize)) * scaleFactor,
            this.width * scaleFactor,
            (this.dynamicHeight ? (ysize - this.y) : this.height) * scaleFactor);
    }

    public void endScissor() {
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        GL11.glPopMatrix();
    }

    private int oldRecipe = -1;

    public void beginBackground(int recipe) {
        if (oldRecipe != recipe) {
            oldRecipe = recipe;
            scrollOffset = 0;
            yMax = 0;
        }
        beginScissor(recipe);
    }

    public void endBackground(int recipe) {
        endScissor();
        // if we can scroll, draw scrollable area and scrollbar
        GuiRecipe<?> gui = (GuiRecipe<?>) Minecraft.getMinecraft().currentScreen;
        Point offset = gui.getRecipePosition(recipe);
        int bot = this.y
            + (this.dynamicHeight ? (((GuiContainerAccessor) gui).getYSize() - offset.y - 5 - this.y) : this.height);
        if (yMax > bot) {
            GuiDraw.changeTexture("mobsinfo:textures/gui/Scrollbar.png");
            int ySize = (this.dynamicHeight ? (((GuiContainerAccessor) gui).getYSize() - offset.y - 5 - this.y)
                : this.height);
            GuiDraw.drawTexturedModalRect(this.x + this.width + 4, this.y, 0, 0, 10, ySize);
            double filled = (double) ySize / (ySize + (yMax - bot));
            GuiDraw.drawTexturedModalRect(
                this.x + this.width + 4 + 2,
                this.y + 2 + (int) ((-this.scrollOffset) * filled),
                10,
                0,
                6,
                (int) ((ySize - 4) * filled));
        }
    }

    public void beginForeground(int recipe) {
        beginScissor(recipe);
    }

    public void endForeground(int recipe) {
        drawForeground(recipe);
        endScissor();
    }

    private void drawForeground(int recipe) {
        // draw items
        Point mouse = GuiDraw.getMousePosition();
        GuiRecipe<?> gui = (GuiRecipe<?>) Minecraft.getMinecraft().currentScreen;

        Point offset = gui.getRecipePosition(recipe);

        int x = mouse.x - (offset.x + ((GuiContainerAccessor) gui).getGuiLeft());
        int y = mouse.y - (offset.y + ((GuiContainerAccessor) gui).getGuiTop());

        GL11.glDisable(GL11.GL_LIGHTING);

        for (PositionedStack input : myGUI.get()
            .getAllItems(recipe)) {
            if (new Rectangle(input.relx, input.rely + this.scrollOffset, 16, 16).contains(x, y)) {
                GL11.glDisable(GL11.GL_DEPTH_TEST);
                GL11.glColorMask(true, true, true, false);
                ((GuiAccessor) gui).callDrawGradientRect(
                    input.relx,
                    input.rely,
                    input.relx + 16,
                    input.rely + 16,
                    -2130706433,
                    -2130706433);
                GL11.glColorMask(true, true, true, true);
                GL11.glEnable(GL11.GL_DEPTH_TEST);
            }
            GuiContainerManager.drawItem(input.relx, input.rely, input.item);
        }

        GL11.glEnable(GL11.GL_LIGHTING);

        // Looks like drawItem() leaves colour blending active, so reset it.
        GL11.glColor4f(1, 1, 1, 1);
    }

    public PositionedStack getItemUnderMouse(int recipe, int x, int y) {
        for (PositionedStack item : myGUI.get()
            .getAllItems(recipe)) {
            if (new Rectangle(item.relx, item.rely + this.scrollOffset, 16, 16).contains(x, y)) return item;
        }
        return null;
    }

    public ItemStack getStackUnderMouse(int recipe, int x, int y) {
        PositionedStack stack = getItemUnderMouse(recipe, x, y);
        return stack == null ? null : stack.item;
    }

    public List<String> handleTooltip(GuiRecipe<?> gui, List<String> currenttip, int recipe) {
        Point mouse = GuiDraw.getMousePosition();
        Point offset = gui.getRecipePosition(recipe);

        int x = mouse.x - (offset.x + ((GuiContainerAccessor) gui).getGuiLeft());
        int y = mouse.y - (offset.y + ((GuiContainerAccessor) gui).getGuiTop()) - this.scrollOffset;
        return myGUI.get()
            .handleTooltip(gui, currenttip, recipe, x, y);
    }

    // public boolean hideItemPanelSlot(GuiContainer gui, int x, int y, int w, int h){
    // GuiRecipe<?> guiRecipe = (GuiRecipe<?>) gui;
    // for (Integer recipe : guiRecipe.getRecipeIndices()) {
    // Point offset = guiRecipe.getRecipePosition(recipe);
    // int bot = this.y + (this.dynamicHeight ? (((GuiContainerAccessor)gui).getYSize() - offset.y - 5 - this.y) :
    // this.height);
    // if (yMax > bot){
    // int xx = x - ((GuiContainerAccessor)guiRecipe).getGuiLeft() - (this.x + this.width + offset.x);
    // int yy = y - ((GuiContainerAccessor)guiRecipe).getGuiTop() - (this.y + offset.y);
    // return xx >= -w && xx <= 10 && yy >= -h && yy <= (this.dynamicHeight ? (((GuiContainerAccessor)gui).getYSize() -
    // offset.y - 5 - this.y) : this.height);
    // }
    // }
    // return false;
    // }

}
