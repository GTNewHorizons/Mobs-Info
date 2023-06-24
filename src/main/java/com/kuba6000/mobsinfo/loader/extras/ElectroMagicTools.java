package com.kuba6000.mobsinfo.loader.extras;

import java.util.ArrayList;
import java.util.Arrays;

import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.item.ItemStack;

import com.kuba6000.mobsinfo.api.MobDrop;
import com.kuba6000.mobsinfo.api.MobRecipe;

import emt.init.EMTItems;
import thaumcraft.common.entities.monster.EntityTaintChicken;

public class ElectroMagicTools implements IExtraLoader {

    @Override
    public void process(String k, ArrayList<MobDrop> drops, MobRecipe recipe) {
        if (recipe.entity instanceof EntityCreeper) {
            MobDrop drop = new MobDrop(
                new ItemStack(EMTItems.itemEMTItems, 1, 6),
                MobDrop.DropType.Normal,
                0,
                null,
                null,
                false,
                false);
            drop.variableChance = true;
            drop.variableChanceInfo
                .addAll(Arrays.asList(Translations.CHANCE.get(100d), "* " + Translations.EMT_CREEPER.get()));
            drops.add(drop);
        }
        if (recipe.entity instanceof EntityTaintChicken) {
            MobDrop drop = new MobDrop(
                new ItemStack(EMTItems.itemEMTItems, 1, 13),
                MobDrop.DropType.Normal,
                (int) (MobDrop.getChanceBasedOnFromTo(0, 2) * 10000d),
                null,
                null,
                false,
                false);
            drop.clampChance();
            drops.add(drop);
        }
    }
}
