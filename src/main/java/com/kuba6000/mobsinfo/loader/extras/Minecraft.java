package com.kuba6000.mobsinfo.loader.extras;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityMagmaCube;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.kuba6000.mobsinfo.api.IChanceModifier;
import com.kuba6000.mobsinfo.api.MobDrop;
import com.kuba6000.mobsinfo.api.MobRecipe;
import com.kuba6000.mobsinfo.api.SpawnInfo;

public class Minecraft implements IExtraLoader {

    @Override
    public void process(String k, ArrayList<MobDrop> drops, MobRecipe recipe) {
        if (recipe.entity.getClass() == EntitySlime.class) {
            drops.get(0).variableChance = true;
            drops.get(0).chanceModifiers.addAll(
                Arrays.asList(
                    new IChanceModifier.NormalChance((double) drops.get(0).chance / 100d),
                    new MinecraftSlime()));
        } else if (recipe.entity.getClass() == EntityMagmaCube.class) {
            drops.get(0).variableChance = true;
            drops.get(0).chanceModifiers.addAll(
                Arrays.asList(
                    new IChanceModifier.NormalChance((double) drops.get(0).chance / 100d),
                    new MinecraftMagmaCube()));
        } else
            if (recipe.entity.getClass() == EntityVillager.class || recipe.entity.getClass() == EntityIronGolem.class) {
                recipe.spawnList.add(SpawnInfo.ofStructure("Village"));
            } else if (k.equals("witherSkeleton")) {
                recipe.spawnList.add(SpawnInfo.ofStructure("Nether Fortress"));
            } else if (recipe.entity.getClass() == EntityDragon.class) {
                recipe.spawnList.add(new SpawnInfo.SpawnInfoEvent("Enter END"));
            }
    }

    private static class MinecraftSlime implements IChanceModifier {

        @Override
        public String getDescription() {
            return Translations.MINECRAFT_SLIME.get();
        }

        @Override
        public double apply(double chance, @Nonnull World world, @Nonnull List<ItemStack> drops, Entity attacker,
            EntityLiving victim) {
            if (!(victim instanceof EntitySlime)) return 0d;
            if (((EntitySlime) victim).getSlimeSize() == 1) return chance;
            return 0d;
        }

    }

    private static class MinecraftMagmaCube extends MinecraftSlime {

        @Override
        public String getDescription() {
            return Translations.MINECRAFT_MAGMA_CUBE.get();
        }

        @Override
        public double apply(double chance, @Nonnull World world, @Nonnull List<ItemStack> drops, Entity attacker,
            EntityLiving victim) {
            if (!(victim instanceof EntityMagmaCube)) return 0d;
            if (((EntityMagmaCube) victim).getSlimeSize() > 1) return chance;
            return 0d;
        }

    }

}
