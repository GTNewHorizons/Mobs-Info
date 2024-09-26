package com.kuba6000.mobsinfo.mixin.early.minecraft;

import net.minecraft.entity.monster.EntitySlime;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = EntitySlime.class)
public interface EntitySlimeAccessor {

    @Invoker
    void callSetSlimeSize(int size);
}
