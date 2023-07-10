package com.kuba6000.mobsinfo.mixin.ChocoCraft;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.gtnewhorizon.mixinextras.injector.ModifyReturnValue;
import com.kuba6000.mobsinfo.loader.MobRecipeLoader;

import chococraft.common.entities.EntityAnimalChocobo;

@SuppressWarnings("unused")
@Mixin(value = EntityAnimalChocobo.class, remap = false)
public class EntityAnimalChocoboMixin {

    @ModifyReturnValue(method = "isServer", at = @At("RETURN"))
    boolean modifyIsServer(boolean original) {
        if (MobRecipeLoader.isInGenerationProcess) return true;
        return original;
    }

}
