package com.kuba6000.mobsinfo.loader.extras;

import java.util.ArrayList;
import java.util.Arrays;

import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import com.kuba6000.mobsinfo.api.MobDrop;
import com.kuba6000.mobsinfo.api.MobRecipe;

public class Avaritia implements IExtraLoader {

    @Override
    public void process(String k, ArrayList<MobDrop> drops, MobRecipe recipe) {
        if (recipe.entity instanceof EntitySkeleton) {
            MobDrop drop = new MobDrop(
                new ItemStack(Items.skull, 1, 1),
                MobDrop.DropType.Normal,
                0,
                null,
                null,
                false,
                false);
            drop.variableChance = true;
            drop.variableChanceInfo
                .addAll(Arrays.asList(Translations.CHANCE.get(100d), "* " + Translations.AVARITIA_SKULL_SWORD.get()));
            drops.add(drop);
        }
    }
}
