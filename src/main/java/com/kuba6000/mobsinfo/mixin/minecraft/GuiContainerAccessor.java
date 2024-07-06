package com.kuba6000.mobsinfo.mixin.minecraft;

import net.minecraft.client.gui.inventory.GuiContainer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = GuiContainer.class)
public interface GuiContainerAccessor {

    @Accessor
    int getGuiLeft();

    @Accessor
    int getGuiTop();

    @Accessor
    int getYSize();

}
