package com.kuba6000.mobsinfo.mixin.late.EnderIO;

import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import crazypants.enderio.machine.spawner.BlockPoweredSpawner;

@Mixin(value = BlockPoweredSpawner.class, remap = false)
public interface BlockPoweredSpawnerAccessor {

    @Invoker
    ItemStack callCreateItemStackForMob(String mob);

}
