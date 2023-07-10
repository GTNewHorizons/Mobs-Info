package com.kuba6000.mobsinfo.mixin.DQRespect;

import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import com.kuba6000.mobsinfo.mixin.minecraft.EntityAccessor;

import dqr.entity.mobEntity.monsterTensei.DqmEntitySweetbag;

@SuppressWarnings("unused")
@Mixin(value = DqmEntitySweetbag.class)
public class DqmEntitySweetbagMixin {

    @ModifyVariable(method = "dropFewItems", at = @At("STORE"), ordinal = 0)
    Random randomModifier(Random original) {
        return ((EntityAccessor) this).getRand();
    }

}
