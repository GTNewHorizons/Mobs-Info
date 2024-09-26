package com.kuba6000.mobsinfo.mixin.late.HardcoreEnderExpansion;

import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.kuba6000.mobsinfo.loader.MobRecipeLoader;
import com.kuba6000.mobsinfo.mixin.early.minecraft.EntityAccessor;

import chylex.hee.entity.mob.EntityMobLouse;
import chylex.hee.tileentity.spawner.LouseRavagedSpawnerLogic;

@SuppressWarnings("unused")
@Mixin(value = EntityMobLouse.class)
public class EntityMobLouseMixin {

    @Shadow(remap = false)
    private LouseRavagedSpawnerLogic.LouseSpawnData louseData;

    @Inject(method = "dropRareDrop", at = @At("HEAD"))
    private void mobsinfo$dropRareDrop(int lootingExtraLuck, CallbackInfo ci) {
        if (MobRecipeLoader.isInGenerationProcess) {
            Random random = ((EntityAccessor) this).getRand();
            louseData = new LouseRavagedSpawnerLogic.LouseSpawnData(
                (byte) random.nextInt(LouseRavagedSpawnerLogic.LouseSpawnData.maxLevel),
                random);
        }
    }

}
