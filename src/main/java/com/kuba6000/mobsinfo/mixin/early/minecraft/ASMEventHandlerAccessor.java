package com.kuba6000.mobsinfo.mixin.early.minecraft;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.eventhandler.ASMEventHandler;
import cpw.mods.fml.common.eventhandler.IEventListener;

@Mixin(value = ASMEventHandler.class, remap = false)
public interface ASMEventHandlerAccessor {

    @Accessor
    ModContainer getOwner();

    @Accessor
    IEventListener getHandler();

}
