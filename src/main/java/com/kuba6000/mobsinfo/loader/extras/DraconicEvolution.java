package com.kuba6000.mobsinfo.loader.extras;

import static com.kuba6000.mobsinfo.MobsInfo.MODID;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.brandon3055.draconicevolution.common.ModItems;
import com.brandon3055.draconicevolution.common.entity.EntityCustomDragon;
import com.brandon3055.draconicevolution.common.handler.ConfigHandler;
import com.brandon3055.draconicevolution.common.handler.MinecraftForgeEventHandler;
import com.kuba6000.mobsinfo.api.IChanceModifier;
import com.kuba6000.mobsinfo.api.MobDrop;
import com.kuba6000.mobsinfo.api.MobRecipe;

import io.netty.buffer.ByteBuf;

public class DraconicEvolution implements IExtraLoader {

    private static final Logger LOG = LogManager.getLogger(MODID + "[Extra Loader][Draconic Evolution]");

    private final MinecraftForgeEventHandler DEEventHandler = new MinecraftForgeEventHandler();
    private Method isValidEntityMethod = null;

    @Override
    public void process(String k, ArrayList<MobDrop> drops, MobRecipe recipe) {
        if (recipe.entity instanceof EntityDragon || k.equals("HardcoreEnderExpansion.Dragon")) {
            MobDrop drop = new MobDrop(
                new ItemStack(ModItems.dragonHeart, recipe.entity instanceof EntityCustomDragon ? 2 : 1),
                MobDrop.DropType.Normal,
                10000,
                null,
                null,
                false,
                false);
            drops.add(drop);

            MobDrop drop2 = new MobDrop(
                new ItemStack(ModItems.draconiumDust),
                MobDrop.DropType.Normal,
                (int) (MobDrop.getChanceBasedOnFromTo(30, 30 + 29) * 10000d),
                null,
                null,
                false,
                false);
            drop2.clampChance();

            drops.add(drop2);

        }

        try {
            if (isValidEntityMethod == null) {
                isValidEntityMethod = MinecraftForgeEventHandler.class
                    .getDeclaredMethod("isValidEntity", EntityLivingBase.class);
                isValidEntityMethod.setAccessible(true);
            }
            if (!((boolean) isValidEntityMethod.invoke(DEEventHandler, recipe.entity))) return;
        } catch (Exception e) {
            LOG.error(
                "Fatal: failed to check if entity is valid for mob soul drop, skipping mob soul drop for {}",
                k,
                e);
            return;
        }

        ItemStack soul = new ItemStack(ModItems.mobSoul);
        soul.stackTagCompound = new NBTTagCompound();
        soul.stackTagCompound.setString("Name", k.equals("witherSkeleton") ? "Skeleton" : k);
        if (recipe.entity instanceof EntitySkeleton)
            soul.stackTagCompound.setInteger("SkeletonType", ((EntitySkeleton) recipe.entity).getSkeletonType());

        MobDrop drop = new MobDrop(soul, MobDrop.DropType.Normal, 0, null, null, false, false);
        drop.variableChance = true;

        double baseChance = (1d / (recipe.entity instanceof EntityAnimal ? ConfigHandler.passiveSoulDropChance
            : ConfigHandler.soulDropChance)) * 100d;

        drop.chanceModifiers.add(new DraconicEvolutionSoulChanceModifier(baseChance));

        drops.add(drop);
    }

    private static class DraconicEvolutionSoulChanceModifier implements IChanceModifier {

        // DE's MinecraftForgeEventHandler#getDropChanceFromItem(ItemStack) is private, so it is
        // reflectively invoked to always stay in sync with DE's own soul chance formula, mirroring
        // the isValidEntity reflection in the outer loader.
        private static Method getDropChanceFromItemMethod = null;
        private static final MinecraftForgeEventHandler DE_EVENT_HANDLER = new MinecraftForgeEventHandler();

        double baseChance;

        @SuppressWarnings("unused")
        DraconicEvolutionSoulChanceModifier() {
            // Constructor is called via reflection in IChanceModifier.loadFromByteBuf()
        }

        DraconicEvolutionSoulChanceModifier(double baseChance) {
            this.baseChance = baseChance;
        }

        @Override
        public String getDescription() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void applyTooltip(List<String> currentTooltip) {
            currentTooltip.addAll(
                Arrays.asList(
                    Translations.VARIABLE_CHANCE.get() + ":",
                    Translations.DRACONIC_EVOLUTION_MOB_SOUL.get(),
                    Translations.DRACONIC_EVOLUTION_MOB_SOUL_1.get(),
                    Translations.DRACONIC_EVOLUTION_MOB_SOUL_2
                        .get(0d, baseChance * 1, baseChance * 2, baseChance * 3, baseChance * 4, baseChance * 5),
                    Translations.DRACONIC_EVOLUTION_MOB_SOUL_3.get(
                        baseChance * 1,
                        baseChance * 2,
                        baseChance * 3,
                        baseChance * 4,
                        baseChance * 5,
                        baseChance * 6),
                    Translations.DRACONIC_EVOLUTION_MOB_SOUL_4.get(
                        baseChance * 2,
                        baseChance * 3,
                        baseChance * 4,
                        baseChance * 5,
                        baseChance * 6,
                        baseChance * 7),
                    Translations.DRACONIC_EVOLUTION_MOB_SOUL_5.get(
                        baseChance * 3,
                        baseChance * 4,
                        baseChance * 5,
                        baseChance * 6,
                        baseChance * 7,
                        baseChance * 8)));
        }

        @Override
        public double apply(double chance, @Nonnull World world, @Nonnull List<ItemStack> drops, Entity attacker,
            EntityLiving victim) {
            if (!(attacker instanceof EntityPlayer)) return 0d;
            ItemStack weapon = ((EntityPlayer) attacker).getHeldItem();
            if (weapon == null) return 0d;
            // DE souls ignore Looting entirely (they are spawned directly, bypassing loot tables),
            // so this modifier deliberately leaves the EEC looting channel untouched as well.
            return baseChance * getSoulChanceModifier(weapon);
        }

        private static int getSoulChanceModifier(ItemStack weapon) {
            try {
                if (getDropChanceFromItemMethod == null) {
                    getDropChanceFromItemMethod = MinecraftForgeEventHandler.class
                        .getDeclaredMethod("getDropChanceFromItem", ItemStack.class);
                    getDropChanceFromItemMethod.setAccessible(true);
                }
                return (int) getDropChanceFromItemMethod.invoke(DE_EVENT_HANDLER, weapon);
            } catch (Exception e) {
                LOG.error("Fatal: failed to get mob soul drop chance modifier from weapon, assuming 0", e);
                return 0;
            }
        }

        @Override
        public void writeToByteBuf(ByteBuf byteBuf) {
            byteBuf.writeDouble(baseChance);
        }

        @Override
        public void readFromByteBuf(ByteBuf byteBuf) {
            baseChance = byteBuf.readDouble();
        }
    }
}
