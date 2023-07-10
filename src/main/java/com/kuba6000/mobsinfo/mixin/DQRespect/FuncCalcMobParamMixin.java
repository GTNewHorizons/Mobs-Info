package com.kuba6000.mobsinfo.mixin.DQRespect;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.gtnewhorizon.mixinextras.injector.ModifyReturnValue;
import com.kuba6000.mobsinfo.loader.MobRecipeLoader;

import dqr.functions.FuncCalcMobParam;

@SuppressWarnings("unused")
@Mixin(value = FuncCalcMobParam.class, remap = false)
public class FuncCalcMobParamMixin {

    @ModifyReturnValue(method = "getCalcDROP", at = @At(value = "RETURN"))
    boolean getCalcDROP(boolean original, int base, int per) {
        if (MobRecipeLoader.isInGenerationProcess) {
            double chance = (double) per / (double) base;
            MobRecipeLoader.DQRChances.add(chance);
            return true;
        }
        return original;
    }

}
