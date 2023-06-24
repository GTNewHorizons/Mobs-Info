package com.kuba6000.mobsinfo.loader.extras;

import java.util.ArrayList;
import java.util.Arrays;

import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import com.kuba6000.mobsinfo.api.MobDrop;
import com.kuba6000.mobsinfo.api.MobRecipe;

import vazkii.botania.common.entity.EntityDoppleganger;
import vazkii.botania.common.item.ModItems;

public class Botania implements IExtraLoader {

    @Override
    public void process(String k, ArrayList<MobDrop> drops, MobRecipe recipe) {
        if (recipe.entity instanceof EntitySkeleton) {
            MobDrop drop = new MobDrop(
                new ItemStack(Items.skull, 1, ((EntitySkeleton) recipe.entity).getSkeletonType()),
                MobDrop.DropType.Normal,
                0,
                null,
                null,
                false,
                false);
            drop.variableChance = true;
            drop.variableChanceInfo.addAll(
                Arrays.asList(
                    Translations.CHANCE.get(15.38d),
                    "* " + Translations.DROPS_ONLY_USING.get("Elementium Axe")));
            drops.add(drop);
        } else if (recipe.entity instanceof EntityZombie && !(recipe.entity instanceof EntityPigZombie)) {
            MobDrop drop = new MobDrop(
                new ItemStack(Items.skull, 1, 2),
                MobDrop.DropType.Normal,
                0,
                null,
                null,
                false,
                false);
            drop.variableChance = true;
            drop.variableChanceInfo.addAll(
                Arrays.asList(
                    Translations.CHANCE.get(7.69d),
                    "* " + Translations.DROPS_ONLY_USING.get("Elementium Axe")));
            drops.add(drop);
        } else if (recipe.entity instanceof EntityCreeper) {
            MobDrop drop = new MobDrop(
                new ItemStack(Items.skull, 1, 4),
                MobDrop.DropType.Normal,
                0,
                null,
                null,
                false,
                false);
            drop.variableChance = true;
            drop.variableChanceInfo.addAll(
                Arrays.asList(
                    Translations.CHANCE.get(7.69d),
                    "* " + Translations.DROPS_ONLY_USING.get("Elementium Axe")));
            drops.add(drop);
        } else if (recipe.entity instanceof EntityDoppleganger) {
            MobDrop drop = new MobDrop(
                new ItemStack(ModItems.gaiaHead),
                MobDrop.DropType.Normal,
                0,
                null,
                null,
                false,
                false);
            drop.variableChance = true;
            drop.variableChanceInfo.addAll(
                Arrays.asList(
                    Translations.CHANCE.get(7.69d),
                    "* " + Translations.DROPS_ONLY_USING.get("Elementium Axe")));
            drops.add(drop);
        }
    }
}
