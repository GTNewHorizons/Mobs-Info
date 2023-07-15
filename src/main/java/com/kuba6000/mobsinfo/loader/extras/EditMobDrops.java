package com.kuba6000.mobsinfo.loader.extras;

import java.util.ArrayList;

import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.item.ItemStack;

import com.kuba6000.mobsinfo.api.MobDrop;
import com.kuba6000.mobsinfo.api.MobRecipe;

import editmobdrops.AddedDrop;
import editmobdrops.handlers.ConfigHandler;

public class EditMobDrops implements IExtraLoader {

    @Override
    public void process(String k, ArrayList<MobDrop> drops, MobRecipe recipe) {
        ConfigHandler.mobsToClear.stream()
            .filter(
                c -> recipe.entity.getClass()
                    .equals(c))
            .findAny()
            .ifPresent(unused -> drops.clear());

        for (AddedDrop addedDrop : ConfigHandler.itemsToAdd) {
            ItemStack item = new ItemStack(addedDrop.item, 1, addedDrop.metadata);
            if (addedDrop.nbtTag != null) {
                item.setTagCompound(addedDrop.nbtTag);
            }

            if (addedDrop.chances.get(0) > 0d) {
                MobDrop drop = new MobDrop(
                    item,
                    MobDrop.DropType.Normal,
                    (int) (MobDrop.getChanceBasedOnFromTo(addedDrop.minStack, addedDrop.maxStack) * 10000d
                        * (addedDrop.chances.get(0) / 100d)),
                    null,
                    null,
                    false,
                    false);
                drop.clampChance();
                drops.add(drop);
            }

            if (addedDrop.chances.get(1) > 0d
                && (recipe.entity instanceof EntityMob || recipe.entity instanceof EntityDragon
                    || recipe.entity instanceof EntityGhast
                    || recipe.entity instanceof EntitySlime)) {
                MobDrop drop = new MobDrop(
                    item,
                    MobDrop.DropType.Normal,
                    (int) (MobDrop.getChanceBasedOnFromTo(addedDrop.minStack, addedDrop.maxStack) * 10000d
                        * (addedDrop.chances.get(1) / 100d)),
                    null,
                    null,
                    false,
                    false);
                drop.clampChance();
                drops.add(drop);
            }
            if (addedDrop.chances.get(2) > 0d && recipe.entity instanceof IBossDisplayData) {
                MobDrop drop = new MobDrop(
                    item,
                    MobDrop.DropType.Normal,
                    (int) (MobDrop.getChanceBasedOnFromTo(addedDrop.minStack, addedDrop.maxStack) * 10000d
                        * (addedDrop.chances.get(2) / 100d)),
                    null,
                    null,
                    false,
                    false);
                drop.clampChance();
                drops.add(drop);
            }
            for (int i = 0; i < ConfigHandler.mobGroups.size(); ++i) {
                if (i + 3 < addedDrop.chances.size() && addedDrop.chances.get(i + 3) > 0d
                    && (ConfigHandler.mobGroups.get(i)).contains(recipe.entity.getClass())) {
                    MobDrop drop = new MobDrop(
                        item,
                        MobDrop.DropType.Normal,
                        (int) (MobDrop.getChanceBasedOnFromTo(addedDrop.minStack, addedDrop.maxStack) * 10000d
                            * (addedDrop.chances.get(i + 3) / 100d)),
                        null,
                        null,
                        false,
                        false);
                    drop.clampChance();
                    drops.add(drop);
                }
            }
        }

        ConfigHandler.singleMobItems.stream()
            .filter(
                c -> recipe.entity.getClass()
                    .equals(c.entityFrom))
            .forEach(addedDrop -> {
                ItemStack item = new ItemStack(addedDrop.item, 1, addedDrop.metadata);
                MobDrop drop = new MobDrop(
                    item,
                    MobDrop.DropType.Normal,
                    (int) (MobDrop.getChanceBasedOnFromTo(addedDrop.minStack, addedDrop.maxStack) * 10000d
                        * (addedDrop.chances.get(0) / 100d)),
                    null,
                    null,
                    false,
                    false);
                drop.clampChance();
                drops.add(drop);
            });

    }

}
