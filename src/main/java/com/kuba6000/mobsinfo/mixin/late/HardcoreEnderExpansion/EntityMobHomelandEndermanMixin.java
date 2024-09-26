package com.kuba6000.mobsinfo.mixin.late.HardcoreEnderExpansion;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.kuba6000.mobsinfo.loader.MobRecipeLoader;

import chylex.hee.entity.mob.EntityMobHomelandEnderman;
import chylex.hee.mechanics.misc.HomelandEndermen;

@SuppressWarnings("unused")
@Mixin(value = EntityMobHomelandEnderman.class)
public class EntityMobHomelandEndermanMixin {

    @Shadow(remap = false)
    private HomelandEndermen.HomelandRole homelandRole;

    @Inject(method = "entityInit", at = @At("RETURN"))
    private void mobsinfo$entityInit(CallbackInfo ci) {
        if (MobRecipeLoader.isInGenerationProcess) homelandRole = HomelandEndermen.HomelandRole.GUARD;
    }

}
