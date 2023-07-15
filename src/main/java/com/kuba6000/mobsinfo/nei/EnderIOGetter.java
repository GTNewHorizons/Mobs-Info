package com.kuba6000.mobsinfo.nei;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

import com.kuba6000.mobsinfo.mixin.EnderIO.BlockPoweredSpawnerAccessor;

import crazypants.enderio.EnderIO;

class EnderIOGetter {

    public static Block blockPoweredSpawner() {
        return EnderIO.blockPoweredSpawner;
    }

    public static ItemStack BlockPoweredSpawner$createItemStackForMob(String type) {
        return ((BlockPoweredSpawnerAccessor) EnderIO.blockPoweredSpawner).callCreateItemStackForMob(type);
    }
}
