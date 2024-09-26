package com.kuba6000.mobsinfo.mixin.early.minecraft;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import cpw.mods.fml.common.eventhandler.EventBus;

@Mixin(value = EventBus.class, remap = false)
public interface EventBusAccessor {

    @Accessor
    int getBusID();

}
