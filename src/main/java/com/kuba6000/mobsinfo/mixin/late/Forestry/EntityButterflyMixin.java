package com.kuba6000.mobsinfo.mixin.late.Forestry;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.kuba6000.mobsinfo.loader.MobRecipeLoader;

import forestry.api.lepidopterology.ButterflyManager;
import forestry.api.lepidopterology.IButterfly;
import forestry.lepidopterology.entities.EntityButterfly;

@SuppressWarnings("unused")
@Mixin(value = EntityButterfly.class)
public class EntityButterflyMixin {

    @Shadow(remap = false)
    private IButterfly contained;

    @Inject(method = "dropFewItems", at = @At("HEAD"))
    private void mobsinfo$dropFewItems(boolean playerKill, int lootLevel, CallbackInfo ci) {
        if (MobRecipeLoader.isInGenerationProcess && contained == null) {
            contained = ButterflyManager.butterflyRoot
                .templateAsIndividual(ButterflyManager.butterflyRoot.getDefaultTemplate());
        }
    }

}
