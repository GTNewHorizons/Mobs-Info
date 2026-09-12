package com.kuba6000.mobsinfo.mixin.early.minecraft;

import net.minecraft.entity.EntityLivingBase;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = EntityLivingBase.class)
public interface EntityLivingBaseAccessor {

    @Accessor("recentlyHit")
    int getRecentlyHit();

    @Invoker
    void callDropFewItems(boolean recentlyHit, int lootingLevel);

    @Invoker
    void callDropRareDrop(int lootingLevel);
}
