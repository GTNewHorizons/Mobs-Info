package com.kuba6000.mobsinfo.nei.scrollable;

import java.awt.Point;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;

import com.kuba6000.mobsinfo.mixin.minecraft.GuiContainerAccessor;

import codechicken.nei.guihook.IContainerObjectHandler;
import codechicken.nei.recipe.GuiRecipe;
import codechicken.nei.recipe.IRecipeHandler;

public class ScrollableContainerHandler implements IContainerObjectHandler {

    @Override
    public void guiTick(GuiContainer gui) {

    }

    @Override
    public void refresh(GuiContainer gui) {

    }

    @Override
    public void load(GuiContainer gui) {

    }

    @Override
    public ItemStack getStackUnderMouse(GuiContainer gui, int mousex, int mousey) {
        if (gui instanceof GuiRecipe<?>) {
            IRecipeHandler handler = ((GuiRecipe<?>) gui).getHandler();
            if (handler instanceof IScrollableGUI) {
                for (Integer recipe : ((GuiRecipe<?>) gui).getRecipeIndices()) {
                    Point offset = ((GuiRecipe<?>) gui).getRecipePosition(recipe);
                    ItemStack s = ((IScrollableGUI) handler).getScrollbar()
                        .getStackUnderMouse(
                            recipe,
                            mousex - (offset.x + ((GuiContainerAccessor) gui).getGuiLeft()),
                            mousey - (offset.y + ((GuiContainerAccessor) gui).getGuiTop()));
                    if (s != null) return s;
                }

            }
        }
        return null;
    }

    @Override
    public boolean objectUnderMouse(GuiContainer gui, int mousex, int mousey) {
        return false;
    }

    @Override
    public boolean shouldShowTooltip(GuiContainer gui) {
        return true;
    }
}
