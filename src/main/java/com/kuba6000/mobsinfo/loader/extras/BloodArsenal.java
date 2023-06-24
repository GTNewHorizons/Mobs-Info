package com.kuba6000.mobsinfo.loader.extras;

import java.util.ArrayList;
import java.util.Arrays;

import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.item.ItemStack;

import com.arc.bloodarsenal.common.items.ModItems;
import com.kuba6000.mobsinfo.api.MobDrop;
import com.kuba6000.mobsinfo.api.MobRecipe;

import cpw.mods.fml.common.registry.GameRegistry;

public class BloodArsenal implements IExtraLoader {

    @Override
    public void process(String k, ArrayList<MobDrop> drops, MobRecipe recipe) {
        if (GameRegistry.findItem(com.arc.bloodarsenal.common.BloodArsenal.MODID, "heart") != null) {
            if (recipe.entity instanceof EntityZombie) {
                MobDrop drop = new MobDrop(
                    new ItemStack(ModItems.heart),
                    MobDrop.DropType.Normal,
                    0,
                    null,
                    null,
                    false,
                    false);
                drop.variableChance = true;
                drop.variableChanceInfo.addAll(
                    Arrays.asList(Translations.CHANCE.get(1d), "* " + Translations.DROPS_ONLY_WITH_WEAKNESS.get()));
                drops.add(drop);
            } else if (recipe.entity instanceof EntityVillager) {
                MobDrop drop = new MobDrop(
                    new ItemStack(ModItems.heart),
                    MobDrop.DropType.Normal,
                    0,
                    null,
                    null,
                    false,
                    false);
                drop.variableChance = true;
                drop.variableChanceInfo.addAll(
                    Arrays.asList(Translations.CHANCE.get(25d), "* " + Translations.DROPS_ONLY_WITH_WEAKNESS.get()));
                drops.add(drop);
            }
        }
        if (GameRegistry.findItem(com.arc.bloodarsenal.common.BloodArsenal.MODID, "wolf_hide") != null) {
            if (recipe.entity instanceof EntityWolf) {
                drops.add(
                    new MobDrop(
                        new ItemStack(ModItems.wolf_hide),
                        MobDrop.DropType.Normal,
                        4000,
                        null,
                        null,
                        false,
                        false));
            }
        }
    }
}
