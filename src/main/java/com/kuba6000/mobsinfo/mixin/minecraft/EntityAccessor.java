package com.kuba6000.mobsinfo.mixin.minecraft;

import java.util.Random;

import net.minecraft.entity.Entity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = Entity.class)
public interface EntityAccessor {

    @Accessor
    void setRand(Random rand);

    @Accessor
    Random getRand();
}
