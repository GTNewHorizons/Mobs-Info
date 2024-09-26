package com.kuba6000.mobsinfo.mixin.early.minecraft;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RendererLivingEntity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = RendererLivingEntity.class)
public interface RendererLivingEntityAccessor {

    @Accessor
    ModelBase getMainModel();

}
