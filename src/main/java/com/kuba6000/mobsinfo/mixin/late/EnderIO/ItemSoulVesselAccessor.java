package com.kuba6000.mobsinfo.mixin.late.EnderIO;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import crazypants.enderio.item.ItemSoulVessel;

@Mixin(value = ItemSoulVessel.class, remap = false)
public interface ItemSoulVesselAccessor {

    @Invoker
    boolean callIsBlackListed(String entityId);

}
