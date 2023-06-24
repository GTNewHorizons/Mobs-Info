package com.kuba6000.mobsinfo.loader.extras;

import java.util.ArrayList;
import java.util.Arrays;

import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import com.kuba6000.mobsinfo.api.MobDrop;
import com.kuba6000.mobsinfo.api.MobRecipe;

import fox.spiteful.forbidden.Config;
import fox.spiteful.forbidden.items.ForbiddenItems;
import thaumcraft.api.entities.ITaintedMob;

public class ForbiddenMagic implements IExtraLoader {

    @Override
    public void process(String k, ArrayList<MobDrop> drops, MobRecipe recipe) {
        // fox.spiteful.forbidden.FMEventHandler.onLivingDrops

        if (recipe.entity instanceof ITaintedMob) {
            MobDrop drop = new MobDrop(
                new ItemStack(ForbiddenItems.resource, 1, 3),
                MobDrop.DropType.Normal,
                0,
                null,
                null,
                false,
                false);
            drop.variableChance = true;
            drop.variableChanceInfo
                .addAll(Arrays.asList(Translations.CHANCE.get(50d), Translations.DROPS_ONLY_WITH_WEAKNESS_2.get()));
            drops.add(drop);
        }

        if (Config.silverfishEmeralds && recipe.entity instanceof EntitySilverfish) {
            MobDrop drop = new MobDrop(
                new ItemStack(ForbiddenItems.resource, 1, 0),
                MobDrop.DropType.Normal,
                286,
                null,
                null,
                false,
                false);
            drop.variableChance = true;
            drop.variableChanceInfo
                .add(Translations.CHANCE.get(2.86d) + " (" + Translations.OR_BIOME.get(10d, "Extreme Hills") + ")");
            drops.add(drop);
        }

        if (Config.greedyEnch && (recipe.entity instanceof EntityVillager || recipe.entity instanceof IMob)) {
            MobDrop drop;
            if (recipe.entity instanceof EntityVillager) {
                drop = new MobDrop(
                    new ItemStack(Items.emerald, 1),
                    MobDrop.DropType.Normal,
                    0,
                    null,
                    null,
                    false,
                    false);
                drop.variableChance = true;
                drop.variableChanceInfo.addAll(
                    Arrays.asList(
                        Translations.CHANCE.get(100d),
                        "* " + Translations.DROPS_ONLY_WITH_ENCHANT.get("Greedy")));
            } else {
                drop = new MobDrop(
                    new ItemStack(ForbiddenItems.resource, 1, 0),
                    MobDrop.DropType.Normal,
                    0,
                    null,
                    null,
                    false,
                    false);
                drop.variableChance = true;
                drop.variableChanceInfo.addAll(
                    Arrays.asList(
                        Translations.CHANCE.get(8.57d),
                        "* " + Translations.DROPS_ONLY_WITH_ENCHANT.get("Greedy")));
            }
            drops.add(drop);
        }

        if (recipe.entity.getClass() == EntitySkeleton.class) {
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
                Arrays.asList(Translations.CHANCE.get(15.38d), "* " + Translations.DROPS_ONLY_USING.get("Skull Axe")));
            drops.add(drop);
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
                Arrays.asList(Translations.CHANCE.get(11.54d), "* " + Translations.DROPS_ONLY_USING.get("Skull Axe")));
            drops.add(drop);
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
                Arrays.asList(Translations.CHANCE.get(11.54d), "* " + Translations.DROPS_ONLY_USING.get("Skull Axe")));
            drops.add(drop);
        }

        MobDrop drop = new MobDrop(
            new ItemStack(ForbiddenItems.deadlyShards, 1, 5),
            MobDrop.DropType.Normal,
            (int) (MobDrop.getChanceBasedOnFromTo(1, 3) * 10000d * 0.1333333d),
            null,
            null,
            false,
            false);
        drop.clampChance();
        drop.variableChance = true;
        drop.variableChanceInfo.addAll(
            Arrays.asList(
                Translations.CHANCE.get(((double) drop.chance / 100d)),
                "* " + Translations.DROPS_ONLY_IN_DIMENSION.get("NETHER"),
                "* " + Translations.FORBIDDEN_MAGIC_NON_PLAYER.get()));
        drop.chance = 0;
        drops.add(drop);

        if (recipe.entity instanceof IMob) {
            MobDrop drop2 = new MobDrop(
                new ItemStack(ForbiddenItems.deadlyShards, 1, 0),
                MobDrop.DropType.Normal,
                328,
                null,
                null,
                false,
                false);
            drops.add(drop2);

            if (recipe.entity instanceof IBossDisplayData) {
                MobDrop drop3 = new MobDrop(
                    new ItemStack(ForbiddenItems.deadlyShards, 2, 3),
                    MobDrop.DropType.Normal,
                    10000,
                    null,
                    null,
                    false,
                    false);
                drops.add(drop3);
            }

            MobDrop drop3 = new MobDrop(
                new ItemStack(ForbiddenItems.deadlyShards, 1, 6),
                MobDrop.DropType.Normal,
                0,
                null,
                null,
                false,
                false);
            drop3.variableChance = true;
            drop3.variableChanceInfo.addAll(
                Arrays.asList(
                    Translations.CHANCE.get(0d),
                    "* " + Translations.EACH_LEVEL_OF_GIVES.get("looting", "+5%"),
                    "* " + Translations.EACH_LEVEL_OF_GIVES.get("treasure focus on wand", "+5%")));
            drops.add(drop3);
        }
    }
}
