package com.kuba6000.mobsinfo.loader.extras;

import java.util.ArrayList;
import java.util.Arrays;

import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.item.ItemStack;

import com.kuba6000.mobsinfo.api.MobDrop;
import com.kuba6000.mobsinfo.api.MobRecipe;

import WayofTime.alchemicalWizardry.ModItems;

public class BloodMagic implements IExtraLoader {

    @Override
    public void process(String k, ArrayList<MobDrop> drops, MobRecipe recipe) {
        if (!(recipe.entity instanceof EntityAnimal)) {
            MobDrop drop = new MobDrop(
                new ItemStack(ModItems.weakBloodShard),
                MobDrop.DropType.Normal,
                0,
                null,
                null,
                false,
                false);
            drop.variableChance = true;
            drop.variableChanceInfo.addAll(
                Arrays.asList(Translations.CHANCE.get(50d), "* " + Translations.DROPS_ONLY_WITH_WEAKNESS_2.get()));
            drops.add(drop);
        }
    }
}
