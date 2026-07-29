package com.kuba6000.mobsinfo.loader.extras;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

import com.kuba6000.mobsinfo.api.IChanceModifier;
import com.kuba6000.mobsinfo.api.MobDrop;
import com.kuba6000.mobsinfo.api.MobRecipe;

import fox.spiteful.forbidden.Config;
import fox.spiteful.forbidden.enchantments.DarkEnchantments;
import fox.spiteful.forbidden.items.ForbiddenItems;
import io.netty.buffer.ByteBuf;
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
            drop.chanceModifiers.addAll(
                Arrays.asList(new IChanceModifier.NormalChance(50d), new IChanceModifier.DropsOnlyWithWeaknessIII()));
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
            drop.chanceModifiers.addAll(
                Arrays.asList(
                    new IChanceModifier.NormalChance(2.86d),
                    new IChanceModifier.OrBiome(BiomeGenBase.extremeHills, 10d)));
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
                drop.chanceModifiers.addAll(
                    Arrays.asList(
                        new IChanceModifier.NormalChance(100d),
                        new IChanceModifier.DropsOnlyWithEnchant(DarkEnchantments.greedy)));
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
                drop.chanceModifiers.addAll(
                    Arrays.asList(
                        new IChanceModifier.NormalChance(8.57d),
                        new IChanceModifier.DropsOnlyWithEnchant(DarkEnchantments.greedy)));
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
            drop.chanceModifiers.addAll(
                Arrays.asList(
                    new IChanceModifier.NormalChance(15.38d),
                    new IChanceModifier.DropsOnlyUsing(ForbiddenItems.skullAxe)));
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
            drop.chanceModifiers.addAll(
                Arrays.asList(
                    new IChanceModifier.NormalChance(11.54d),
                    new IChanceModifier.DropsOnlyUsing(ForbiddenItems.skullAxe)));
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
            drop.chanceModifiers.addAll(
                Arrays.asList(
                    new IChanceModifier.NormalChance(11.54d),
                    new IChanceModifier.DropsOnlyUsing(ForbiddenItems.skullAxe)));
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
        drop.chanceModifiers.addAll(
            Arrays.asList(
                new IChanceModifier.NormalChance(((double) drop.chance / 100d)),
                new IChanceModifier.DropsOnlyInDimension(-1 /* NETHER */),
                new NonPlayerEntity()));
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
                drop3.chanceModifiers.addAll(Arrays.asList(new IChanceModifier.DropsOnlyInDimension(-1 /* NETHER */)));
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
            drop3.chanceModifiers.addAll(
                Arrays.asList(
                    new IChanceModifier.NormalChance(0d),
                    new IChanceModifier.EachLevelOfGives(Enchantment.looting, 5d),
                    new EachLevelOfGivesFocus()));
            drops.add(drop3);
        }
    }

    private static class EachLevelOfGivesFocus extends IChanceModifier.EachLevelOfGives {

        protected EachLevelOfGivesFocus() {}

        @Override
        public String getDescription() {
            return Translations.EACH_LEVEL_OF_GIVES.get("treasure focus on wand", 5d + "%");
        }

        @Override
        public double apply(double chance, @Nonnull World world, @Nonnull List<ItemStack> drops, Entity attacker,
            EntityLiving victim) {
            return chance;
        }

        @Override
        public void writeToByteBuf(ByteBuf byteBuf) {}

        @Override
        public void readFromByteBuf(ByteBuf byteBuf) {}
    }

    private static class NonPlayerEntity implements IChanceModifier {

        @Override
        public String getDescription() {
            return Translations.FORBIDDEN_MAGIC_NON_PLAYER.get();
        }

        @Override
        public double apply(double chance, @Nonnull World world, @Nonnull List<ItemStack> drops, Entity attacker,
            EntityLiving victim) {
            if (chance == 0d) return 0d;
            if (!(attacker instanceof EntityPlayer)) return chance;
            return 0d;
        }

    }
}
