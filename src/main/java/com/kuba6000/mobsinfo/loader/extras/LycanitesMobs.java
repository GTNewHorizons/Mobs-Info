package com.kuba6000.mobsinfo.loader.extras;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.kuba6000.mobsinfo.api.IChanceModifier;
import com.kuba6000.mobsinfo.api.MobDrop;
import com.kuba6000.mobsinfo.api.MobRecipe;

import io.netty.buffer.ByteBuf;
import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.api.entity.EntityCreatureBase;
import lycanite.lycanitesmobs.api.info.DropRate;
import lycanite.lycanitesmobs.api.info.ItemInfo;
import lycanite.lycanitesmobs.api.info.Subspecies;

public class LycanitesMobs implements IExtraLoader {

    @Override
    public void process(String k, ArrayList<MobDrop> drops, MobRecipe recipe) {

        // lycanite.lycanitesmobs.api.entity.EntityCreatureBase.dropFewItems
        if (recipe.entity instanceof EntityCreatureBase) {
            EntityCreatureBase ent = (EntityCreatureBase) recipe.entity;
            if (!ent.isMinion() && !ent.isBoundPet()) {
                int subspeciesScale = 1;
                if (ent.getSubspeciesIndex() > 2) {
                    subspeciesScale = Subspecies.rareDropScale;
                } else if (ent.getSubspeciesIndex() > 0) {
                    subspeciesScale = Subspecies.uncommonDropScale;
                }

                for (DropRate drop : ent.drops) {
                    if (drop.subspeciesID != -1 && drop.subspeciesID != ent.getSubspeciesIndex()) continue;

                    MobDrop mobDrop = new MobDrop(
                        drop.getItemStack(ent, 1),
                        MobDrop.DropType.Normal,
                        (int) (MobDrop.getChanceBasedOnFromTo(drop.minAmount, drop.maxAmount) * drop.chance
                            * (drop.subspeciesID == -1 ? subspeciesScale : 1d)
                            * (ent.extraMobBehaviour != null && ent.extraMobBehaviour.itemDropMultiplierOverride != 1.0
                                ? ent.extraMobBehaviour.itemDropMultiplierOverride
                                : 1d)
                            * 10000d),
                        null,
                        null,
                        true,
                        false);
                    mobDrop.clampChance();
                    drops.add(mobDrop);
                }
            }
        }

        // lycanite.lycanitesmobs.EventListener.onLivingDrops

        if (ItemInfo.seasonalItemDropChance > 0.0) {
            boolean alwaysDrop = false;
            if (recipe.entity instanceof EntityCreatureBase) {
                if (((EntityCreatureBase) recipe.entity).isMinion()) {
                    return;
                }
                if (((EntityCreatureBase) recipe.entity).getSubspecies() != null) {
                    alwaysDrop = true;
                }
            }
            {
                double chance = alwaysDrop ? 100d : (int) (ItemInfo.seasonalItemDropChance * 100d);
                MobDrop drop = new MobDrop(
                    new ItemStack(ObjectManager.getItem("halloweentreat"), 1),
                    MobDrop.DropType.Additional,
                    (int) (chance * 100d),
                    null,
                    null,
                    false,
                    false);
                drop.variableChance = true;
                drop.chanceModifiers.add(new IChanceModifier.NormalChance(chance));
                drop.chanceModifiers.add(new LycanitesMobsOnlyHalloween());
                drops.add(drop);
            }
            {
                double chance = alwaysDrop ? 100d : (int) (ItemInfo.seasonalItemDropChance * 100d);
                MobDrop drop = new MobDrop(
                    new ItemStack(ObjectManager.getItem("wintergift"), 1),
                    MobDrop.DropType.Additional,
                    (int) (chance * 100d),
                    null,
                    null,
                    false,
                    false);
                drop.variableChance = true;
                drop.chanceModifiers.add(new IChanceModifier.NormalChance(chance));
                drop.chanceModifiers.add(new LycanitesMobsOnlyYuletide());
                drop.chanceModifiers.add(new LycanitesMobsOrOnYuletideDay(chance * 0.5d));
                drops.add(drop);
            }
            {
                double chance = (alwaysDrop ? 100d : (int) (ItemInfo.seasonalItemDropChance * 100d)) * 0.5d;
                MobDrop drop = new MobDrop(
                    new ItemStack(ObjectManager.getItem("wintergiftlarge"), 1),
                    MobDrop.DropType.Additional,
                    (int) (chance * 100d),
                    null,
                    null,
                    false,
                    false);
                drop.variableChance = true;
                drop.chanceModifiers.add(new IChanceModifier.NormalChance(chance));
                drop.chanceModifiers.add(new LycanitesMobsOnlyYuletideDay());
                drops.add(drop);
            }

        }
    }

    private static class LycanitesMobsOnlyHalloween implements IChanceModifier {

        @Override
        public String getDescription() {
            return Translations.HALLOWEEN_ONLY.get();
        }

        @Override
        public double apply(double chance, @Nonnull World world, @Nonnull List<ItemStack> drops, Entity attacker,
            EntityLiving victim) {
            Calendar calendar = Calendar.getInstance();
            if ((calendar.get(Calendar.DATE) == 31 && calendar.get(Calendar.MONTH) == Calendar.OCTOBER)
                || (calendar.get(Calendar.DATE) == 1 && calendar.get(Calendar.MONTH) == Calendar.NOVEMBER))
                return chance;
            return 0d;
        }
    }

    private static class LycanitesMobsOnlyYuletide implements IChanceModifier {

        @Override
        public String getDescription() {
            return Translations.YULETIDE_ONLY.get();
        }

        @Override
        public double apply(double chance, @Nonnull World world, @Nonnull List<ItemStack> drops, Entity attacker,
            EntityLiving victim) {
            Calendar calendar = Calendar.getInstance();
            if (calendar.get(Calendar.MONTH) == Calendar.DECEMBER && calendar.get(Calendar.DATE) > 9
                && calendar.get(Calendar.DATE) < 26) return chance;
            return 0d;
        }

        @Override
        public int getPriority() {
            return 10;
        }
    }

    private static class LycanitesMobsOnlyYuletideDay implements IChanceModifier {

        @Override
        public String getDescription() {
            return Translations.YULETIDE_DAY_ONLY.get();
        }

        @Override
        public double apply(double chance, @Nonnull World world, @Nonnull List<ItemStack> drops, Entity attacker,
            EntityLiving victim) {
            Calendar calendar = Calendar.getInstance();
            if (calendar.get(Calendar.MONTH) == Calendar.DECEMBER && calendar.get(Calendar.DATE) == 25) return chance;
            return 0d;
        }
    }

    private static class LycanitesMobsOrOnYuletideDay implements IChanceModifier {

        double chance;

        protected LycanitesMobsOrOnYuletideDay() {}

        public LycanitesMobsOrOnYuletideDay(double chance) {
            this.chance = chance;
        }

        @Override
        public String getDescription() {
            return Translations.LYCANITES_MOBS_OR_ON_YULETIDE_DAY.get(chance);
        }

        @Override
        public double apply(double chance, @Nonnull World world, @Nonnull List<ItemStack> drops, Entity attacker,
            EntityLiving victim) {
            Calendar calendar = Calendar.getInstance();
            if (calendar.get(Calendar.MONTH) == Calendar.DECEMBER && calendar.get(Calendar.DATE) == 25)
                return this.chance;
            return chance;
        }

        @Override
        public void writeToByteBuf(ByteBuf byteBuf) {
            byteBuf.writeDouble(chance);
        }

        @Override
        public void readFromByteBuf(ByteBuf byteBuf) {
            chance = byteBuf.readDouble();
        }
    }

}
