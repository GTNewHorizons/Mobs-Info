package com.kuba6000.mobsinfo.loader.extras;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import com.emoniph.witchery.brewing.potions.PotionParalysis;
import com.emoniph.witchery.entity.EntityCovenWitch;
import com.emoniph.witchery.entity.EntityDemon;
import com.emoniph.witchery.entity.EntityOwl;
import com.emoniph.witchery.entity.EntityToad;
import com.emoniph.witchery.util.EntityUtil;
import com.kuba6000.mobsinfo.api.MobDrop;
import com.kuba6000.mobsinfo.api.MobRecipe;

public class Witchery implements IExtraLoader {

    @Override
    public void process(String k, ArrayList<MobDrop> drops, MobRecipe recipe) {

        // com.emoniph.witchery.common.GenericEvents.onLivingDeath

        double chance = 0d;

        if (recipe.entity instanceof IBossDisplayData) chance = 1d;
        else if (recipe.entity instanceof EntityPigZombie || recipe.entity instanceof EntityEnderman) chance = 0.09d;
        else if (PotionParalysis.isVillager(recipe.entity)) chance = 0.1d;
        else if (recipe.entity.isEntityUndead()) chance = 0.02d;

        if (chance > 0d) {
            MobDrop drop = new MobDrop(
                com.emoniph.witchery.Witchery.Items.GENERIC.itemVampireBookPage.createStack(),
                MobDrop.DropType.Normal,
                0,
                null,
                null,
                false,
                false);
            drop.variableChance = true;
            drop.variableChanceInfo.addAll(
                Arrays
                    .asList(Translations.CHANCE.get((chance * 100d)), "* " + Translations.WITCHERY_VAMPIRE_BOOK.get()));
            drops.add(drop);
        }

        if (!EntityUtil.isNoDrops(recipe.entity)) {

            if (recipe.entity instanceof EntitySkeleton) {
                if (((EntitySkeleton) recipe.entity).getSkeletonType() == 0) {
                    MobDrop drop2 = new MobDrop(
                        new ItemStack(Items.skull, 1, 0),
                        MobDrop.DropType.Normal,
                        0,
                        null,
                        null,
                        true,
                        false);
                    drop2.variableChance = true;
                    drop2.variableChanceInfo.addAll(
                        Arrays
                            .asList(Translations.CHANCE.get(5d), "* " + Translations.DROPS_ONLY_USING.get("Arthana")));
                    drops.add(drop2);
                }
                MobDrop drop2 = new MobDrop(
                    com.emoniph.witchery.Witchery.Items.GENERIC.itemSpectralDust.createStack(),
                    MobDrop.DropType.Normal,
                    0,
                    null,
                    null,
                    true,
                    false);
                drop2.variableChance = true;
                drop2.variableChanceInfo.addAll(
                    Arrays.asList(Translations.CHANCE.get(4d), "* " + Translations.DROPS_ONLY_USING.get("Arthana")));
                drops.add(drop2);
            } else if (recipe.entity instanceof EntityZombie) {
                MobDrop drop2 = new MobDrop(
                    new ItemStack(Items.skull, 1, 2),
                    MobDrop.DropType.Normal,
                    0,
                    null,
                    null,
                    true,
                    false);
                drop2.variableChance = true;
                drop2.variableChanceInfo.addAll(
                    Arrays.asList(Translations.CHANCE.get(2d), "* " + Translations.DROPS_ONLY_USING.get("Arthana")));
                drops.add(drop2);
                MobDrop drop3 = new MobDrop(
                    com.emoniph.witchery.Witchery.Items.GENERIC.itemSpectralDust.createStack(),
                    MobDrop.DropType.Normal,
                    0,
                    null,
                    null,
                    true,
                    false);
                drop3.variableChance = true;
                drop3.variableChanceInfo.addAll(
                    Arrays.asList(Translations.CHANCE.get(3d), "* " + Translations.DROPS_ONLY_USING.get("Arthana")));
                drops.add(drop3);
            } else if (recipe.entity instanceof EntityCreeper) {
                MobDrop drop2 = new MobDrop(
                    new ItemStack(Items.skull, 1, 4),
                    MobDrop.DropType.Normal,
                    0,
                    null,
                    null,
                    true,
                    false);
                drop2.variableChance = true;
                drop2.variableChanceInfo.addAll(
                    Arrays.asList(Translations.CHANCE.get(1d), "* " + Translations.DROPS_ONLY_USING.get("Arthana")));
                drops.add(drop2);
                MobDrop drop3 = new MobDrop(
                    com.emoniph.witchery.Witchery.Items.GENERIC.itemCreeperHeart.createStack(),
                    MobDrop.DropType.Normal,
                    200,
                    null,
                    null,
                    true,
                    false);
                drop3.variableChance = true;
                drop3.variableChanceInfo
                    .add(Translations.CHANCE.get(2d) + " (" + Translations.OR_USING.get(8d, "Arthana") + ")");
                drops.add(drop3);
            } else if (recipe.entity instanceof EntityDemon) {
                MobDrop drop2 = new MobDrop(
                    com.emoniph.witchery.Witchery.Items.GENERIC.itemDemonHeart.createStack(),
                    MobDrop.DropType.Normal,
                    0,
                    null,
                    null,
                    true,
                    false);
                drop2.variableChance = true;
                drop2.variableChanceInfo
                    .addAll(Arrays.asList("Chance: 33%", "* " + Translations.DROPS_ONLY_USING.get("Arthana")));
                drops.add(drop2);
            } else if (recipe.entity instanceof EntityBat) {
                MobDrop drop2 = new MobDrop(
                    com.emoniph.witchery.Witchery.Items.GENERIC.itemBatWool.createStack(),
                    MobDrop.DropType.Normal,
                    3300,
                    null,
                    null,
                    true,
                    false);
                drop2.variableChance = true;
                drop2.variableChanceInfo
                    .add(Translations.CHANCE.get(33d) + " (" + Translations.OR_USING.get(75d, "Arthana") + ")");
                drops.add(drop2);
            } else if (recipe.entity instanceof EntityWolf) {
                MobDrop drop2 = new MobDrop(
                    com.emoniph.witchery.Witchery.Items.GENERIC.itemDogTongue.createStack(),
                    MobDrop.DropType.Normal,
                    3300,
                    null,
                    null,
                    true,
                    false);
                drop2.variableChance = true;
                drop2.variableChanceInfo
                    .add(Translations.CHANCE.get(33d) + " (" + Translations.OR_USING.get(75d, "Arthana") + ")");
                drops.add(drop2);
                MobDrop drop3 = new MobDrop(
                    new ItemStack(com.emoniph.witchery.Witchery.Blocks.WOLFHEAD, 1, 0),
                    MobDrop.DropType.Normal,
                    2500,
                    null,
                    null,
                    true,
                    false);
                drops.add(drop3);
            } else if (recipe.entity instanceof EntityOwl) {
                MobDrop drop2 = new MobDrop(
                    com.emoniph.witchery.Witchery.Items.GENERIC.itemOwletsWing.createStack(),
                    MobDrop.DropType.Normal,
                    2000,
                    null,
                    null,
                    true,
                    false);
                drop2.variableChance = true;
                drop2.variableChanceInfo
                    .add(Translations.CHANCE.get(20d) + " (" + Translations.OR_USING.get(50d, "Arthana") + ")");
                drops.add(drop2);
            } else if (recipe.entity instanceof EntitySheep) {
                MobDrop drop2 = new MobDrop(
                    com.emoniph.witchery.Witchery.Items.GENERIC.itemMuttonRaw.createStack(),
                    MobDrop.DropType.Normal,
                    0,
                    null,
                    null,
                    false,
                    false);
                drop2.variableChance = true;
                drop2.variableChanceInfo
                    .addAll(Arrays.asList(Translations.CHANCE.get(75d), "* " + Translations.WITCHERY_WAREWOLF));
                drops.add(drop2);
            } else if (recipe.entity instanceof EntityToad) {
                MobDrop drop2 = new MobDrop(
                    com.emoniph.witchery.Witchery.Items.GENERIC.itemToeOfFrog.createStack(),
                    MobDrop.DropType.Normal,
                    2000,
                    null,
                    null,
                    true,
                    false);
                drop2.variableChance = true;
                drop2.variableChanceInfo
                    .add(Translations.CHANCE.get(20d) + " (" + Translations.OR_USING.get(50d, "Arthana") + ")");
                drops.add(drop2);
            } else {
                Class<?> theClass = recipe.entity.getClass();
                if (theClass != null) {
                    String name = theClass.getSimpleName();
                    if (!name.isEmpty()) {
                        String upperName = name.toUpperCase(Locale.ROOT);
                        if (!upperName.contains("WOLF") && !name.contains("Dog") && !name.contains("Fox")) {
                            if ((upperName.contains("FIREBAT") || name.contains("Bat"))) {
                                MobDrop drop2 = new MobDrop(
                                    com.emoniph.witchery.Witchery.Items.GENERIC.itemBatWool.createStack(),
                                    MobDrop.DropType.Normal,
                                    3300,
                                    null,
                                    null,
                                    true,
                                    false);
                                drop2.variableChance = true;
                                drop2.variableChanceInfo.add(
                                    Translations.CHANCE.get(33d) + " ("
                                        + Translations.OR_USING.get(75d, "Arthana")
                                        + ")");
                                drops.add(drop2);
                            }
                        } else {
                            MobDrop drop2 = new MobDrop(
                                com.emoniph.witchery.Witchery.Items.GENERIC.itemDogTongue.createStack(),
                                MobDrop.DropType.Normal,
                                3300,
                                null,
                                null,
                                true,
                                false);
                            drop2.variableChance = true;
                            drop2.variableChanceInfo.add(
                                Translations.CHANCE.get(33d) + " (" + Translations.OR_USING.get(75d, "Arthana") + ")");
                            drops.add(drop2);
                            if ((upperName.contains("WOLF") || name.contains("Dog"))) {
                                MobDrop drop3 = new MobDrop(
                                    com.emoniph.witchery.Witchery.Items.GENERIC.itemDogTongue.createStack(),
                                    MobDrop.DropType.Normal,
                                    2500,
                                    null,
                                    null,
                                    false,
                                    false);
                                drops.add(drop3);
                            }
                        }
                    }
                }
            }

        }

        // com.emoniph.witchery.item.ItemWitchHand.EventHooks.onLivingDeath

        if (recipe.entity instanceof EntityWitch || recipe.entity instanceof EntityCovenWitch) {
            MobDrop drop2 = new MobDrop(
                new ItemStack(com.emoniph.witchery.Witchery.Items.WITCH_HAND),
                MobDrop.DropType.Normal,
                3300,
                null,
                null,
                true,
                false);
            drop2.variableChance = true;
            drop2.variableChanceInfo
                .add(Translations.CHANCE.get(33d) + " (" + Translations.OR_USING.get(50d, "Arthana") + ")");
            drops.add(drop2);
        }

        // com.emoniph.witchery.common.GenericEvents.onLivingDrops // lets just ignore that
    }
}
