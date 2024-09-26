package com.kuba6000.mobsinfo.mixin.early.minecraft;

import net.minecraft.entity.passive.EntityVillager;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(EntityVillager.class)
public interface EntityVillagerAccessor {

    @Invoker
    void callAddDefaultEquipmentAndRecipies(int quantity);

}
