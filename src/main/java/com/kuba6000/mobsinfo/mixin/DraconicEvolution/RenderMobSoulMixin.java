package com.kuba6000.mobsinfo.mixin.DraconicEvolution;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.brandon3055.draconicevolution.client.render.item.RenderMobSoul;

@SuppressWarnings("unused")
@Mixin(value = RenderMobSoul.class, remap = false)
public class RenderMobSoulMixin {

    @Inject(
        method = "renderItem",
        at = @At(
            value = "INVOKE",
            target = "Lorg/lwjgl/opengl/GL11;glScalef(FFF)V",
            ordinal = 0,
            shift = At.Shift.AFTER),
        locals = LocalCapture.CAPTURE_FAILHARD)
    private void renderItemFix(IItemRenderer.ItemRenderType type, ItemStack item, Object[] data, CallbackInfo ci,
        Entity mob) {
        Minecraft mc = Minecraft.getMinecraft();
        mob.setPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
        mob.lastTickPosX = mob.posX;
        mob.lastTickPosY = mob.posY;
        mob.lastTickPosZ = mob.posZ;
    }

}
