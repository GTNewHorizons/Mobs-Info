package com.kuba6000.mobsinfo.mixin.early.minecraft;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import com.google.common.collect.Multimap;

import cpw.mods.fml.common.registry.VillagerRegistry;

@Mixin(value = VillagerRegistry.class, remap = false)
public interface VillagerRegistryAccessor {

    @Accessor
    Multimap<Integer, VillagerRegistry.IVillageTradeHandler> getTradeHandlers();

}
