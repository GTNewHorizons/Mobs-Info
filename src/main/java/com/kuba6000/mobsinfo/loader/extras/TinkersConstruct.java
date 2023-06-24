package com.kuba6000.mobsinfo.loader.extras;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.monster.IMob;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import com.kuba6000.mobsinfo.api.ConstructableItemStack;
import com.kuba6000.mobsinfo.api.MobDrop;
import com.kuba6000.mobsinfo.api.MobRecipe;

import tconstruct.armor.TinkerArmor;
import tconstruct.tools.TinkerTools;
import tconstruct.util.config.PHConstruct;

public class TinkersConstruct implements IExtraLoader {

    @Override
    public void process(String k, ArrayList<MobDrop> drops, MobRecipe recipe) {

        // tconstruct.world.TinkerWorldEvents.onLivingDrop

        if (recipe.entity.getClass() == EntityGhast.class) {
            if (PHConstruct.uhcGhastDrops) {
                for (MobDrop drop : drops) {
                    if (drop.stack.getItem() == Items.ghast_tear) {
                        drop.stack = new ItemStack(Items.gold_ingot);
                        drop.reconstructableStack = new ConstructableItemStack(drop.stack);
                    }
                }
            } else {
                for (MobDrop drop : drops) {
                    if (drop.stack.getItem() == Items.ghast_tear) {
                        drop.chance += 10000;
                        drop.clampChance();
                    }
                }
            }
        }

        // tconstruct.tools.TinkerToolEvents.onLivingDrop

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
                    Translations.BASE_CHANCE.get(0d),
                    Translations.TINKERS_CONSTRUCT_BEHEADING.get(10d),
                    Translations.TINKERS_CONSTRUCT_BEHEADING_1.get(20d)));
            drops.add(drop);

            if (((EntitySkeleton) recipe.entity).getSkeletonType() == 1) {
                MobDrop drop2 = new MobDrop(
                    new ItemStack(TinkerTools.materials, 1, 8),
                    MobDrop.DropType.Normal,
                    2000,
                    null,
                    null,
                    true,
                    false);
                drops.add(drop2);
            }

        }

        if (recipe.entity.getClass() == EntityZombie.class) {
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
                    Translations.BASE_CHANCE.get(0d),
                    Translations.TINKERS_CONSTRUCT_BEHEADING.get(10d),
                    Translations.TINKERS_CONSTRUCT_BEHEADING_1.get(20d)));
            drops.add(drop);

            MobDrop drop2 = new MobDrop(
                new ItemStack(TinkerTools.materials, 1, 2),
                MobDrop.DropType.Normal,
                0,
                null,
                null,
                true,
                false);
            drop2.variableChance = true;
            drop2.variableChanceInfo.addAll(
                Arrays.asList(Translations.CHANCE.get(10d), "* " + Translations.DROPS_ONLY_USING.get("Cleaver")));
            drops.add(drop2);
        }

        if (recipe.entity.getClass() == EntityCreeper.class) {
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
                    Translations.BASE_CHANCE.get(0d),
                    Translations.TINKERS_CONSTRUCT_BEHEADING.get(5d),
                    Translations.TINKERS_CONSTRUCT_BEHEADING_1.get(10d)));
            drops.add(drop);
        }

        // tconstruct.armor.TinkerArmorEvents.onLivingDrop

        if (recipe.entity instanceof IMob) {
            MobDrop drop = new MobDrop(
                new ItemStack(TinkerArmor.heartCanister, 1, 1),
                MobDrop.DropType.Normal,
                50,
                null,
                null,
                false,
                false);
            drops.add(drop);
        }

        if (recipe.entity instanceof IBossDisplayData) {
            String entityName = recipe.entity.getClass()
                .getSimpleName()
                .toLowerCase();
            for (String name : PHConstruct.heartDropBlacklist) if (name.toLowerCase(Locale.US)
                .equals(entityName)) return;

            MobDrop drop = new MobDrop(
                new ItemStack(TinkerArmor.heartCanister, recipe.entity instanceof EntityDragon ? 5 : 1, 3),
                MobDrop.DropType.Normal,
                10000,
                null,
                null,
                false,
                false);
            drops.add(drop);
        }

    }
}
