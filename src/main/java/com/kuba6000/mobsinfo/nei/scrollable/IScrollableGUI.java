package com.kuba6000.mobsinfo.nei.scrollable;

import java.util.List;

import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.GuiRecipe;

public interface IScrollableGUI {

    Scrollbar getScrollbar();

    List<PositionedStack> getAllItems(int recipe);

    default List<String> handleTooltip(GuiRecipe<?> gui, List<String> currenttip, int recipe, int x, int y) {
        return currenttip;
    }

}
