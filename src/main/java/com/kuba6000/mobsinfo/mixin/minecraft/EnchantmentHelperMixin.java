package com.kuba6000.mobsinfo.mixin.minecraft;

import static com.kuba6000.mobsinfo.loader.MobRecipeLoader.randomEnchantmentDetectedString;

import java.util.Random;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagInt;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.kuba6000.mobsinfo.api.RandomSequencer;
import com.kuba6000.mobsinfo.api.utils.FastRandom;
import com.kuba6000.mobsinfo.loader.MobRecipeLoader;

@SuppressWarnings("unused")
@Mixin(value = EnchantmentHelper.class)
public class EnchantmentHelperMixin {

    private static final Random rnd = new FastRandom();

    @Inject(method = "addRandomEnchantment", at = @At("HEAD"), require = 1)
    private static void mobsinfo$addRandomEnchantmentDetector(Random random, ItemStack itemStack,
        int enchantabilityLevel, CallbackInfoReturnable<ItemStack> callbackInfoReturnable) {
        if (MobRecipeLoader.isInGenerationProcess && random instanceof RandomSequencer) {
            itemStack.setTagInfo(randomEnchantmentDetectedString, new NBTTagInt(enchantabilityLevel));
        }
    }

    @ModifyVariable(method = "addRandomEnchantment", at = @At("HEAD"), ordinal = 0, argsOnly = true, require = 1)
    private static Random mobsinfo$addRandomEnchantmentModifier(Random random) {
        if (!MobRecipeLoader.isInGenerationProcess) return random;
        if (random instanceof RandomSequencer) return rnd;
        return random;
    }
}
