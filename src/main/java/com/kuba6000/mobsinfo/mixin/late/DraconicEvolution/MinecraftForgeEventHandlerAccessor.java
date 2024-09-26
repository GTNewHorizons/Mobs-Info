package com.kuba6000.mobsinfo.mixin.late.DraconicEvolution;

import net.minecraft.entity.EntityLivingBase;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import com.brandon3055.draconicevolution.common.handler.MinecraftForgeEventHandler;

@Mixin(value = MinecraftForgeEventHandler.class, remap = false)
public interface MinecraftForgeEventHandlerAccessor {

    @Invoker
    boolean callIsValidEntity(EntityLivingBase entity);

}
