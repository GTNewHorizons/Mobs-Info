package com.kuba6000.mobsinfo.api;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.BiomeGenBase;

import com.kuba6000.mobsinfo.api.helper.ByteBufHelper;
import com.kuba6000.mobsinfo.loader.extras.Translations;

import io.netty.buffer.ByteBuf;

/**
 * Chance modifier, used in {@link MobDrop} to provide information about variable chance.
 * Remember to always implement a default no argument constructor for {@link #loadFromByteBuf} purposes
 */
public interface IChanceModifier {

    /**
     * This chance modifier priority, higher priority = higher in the tooltip. Default is zero
     */
    default int getPriority() {
        return 0;
    }

    /**
     * Description, shown in the tooltip
     */
    String getDescription();

    /**
     * Override if you need to add multiple lines of tooltip
     */
    default void applyTooltip(List<String> currentTooltip) {
        currentTooltip.add(getDescription());
    }

    /**
     * Apply this modification
     *
     * @param chance   Basic drop chance, you should return it back modified/unmodified after your checks
     * @param world    World
     * @param drops    Current drops
     * @param attacker Attacking entity
     * @param victim   Attacked mob
     * @return Modified chance
     */
    double apply(double chance, @Nonnull World world, @Nonnull List<ItemStack> drops, Entity attacker,
        EntityLiving victim);

    /**
     * If you are using any variables in the custom constructor, you should properly send it
     */
    default void writeToByteBuf(ByteBuf byteBuf) {};

    /**
     * If you are using any variables in the custom constructor, you should properly send it
     */
    default void readFromByteBuf(ByteBuf byteBuf) {};

    static void saveToByteBuf(ByteBuf byteBuf, IChanceModifier modifier) {
        String className = modifier.getClass()
            .getName();
        ByteBufHelper.writeString(byteBuf, className);
        modifier.writeToByteBuf(byteBuf);
    }

    static IChanceModifier loadFromByteBuf(ByteBuf byteBuf) {
        String className = ByteBufHelper.readString(byteBuf);
        Class<?> cl;
        try {
            cl = Class.forName(className, false, null);
            if (!cl.isAssignableFrom(IChanceModifier.class)) {
                throw new SecurityException();
            }
            Constructor<? extends IChanceModifier> constructor = (Constructor<? extends IChanceModifier>) cl
                .getDeclaredConstructor();
            constructor.setAccessible(true);
            IChanceModifier modifier = constructor.newInstance();
            modifier.readFromByteBuf(byteBuf);
        } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | InstantiationException
            | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    class NormalChance implements IChanceModifier {

        double chance;

        protected NormalChance() {}

        public NormalChance(double chance) {
            this.chance = chance;
        }

        @Override
        public int getPriority() {
            return 999999;
        }

        @Override
        public String getDescription() {
            return Translations.CHANCE.get(chance);
        }

        @Override
        public double apply(double chance, @Nonnull World world, @Nonnull List<ItemStack> drops, Entity attacker,
            EntityLiving victim) {
            return this.chance;
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

    class BaseChance extends NormalChance {

        protected BaseChance() {}

        public BaseChance(double chance) {
            super(chance);
        }

        @Override
        public String getDescription() {
            return Translations.BASE_CHANCE.get(chance);
        }
    }

    class DropsOnlyWithWeaknessII implements IChanceModifier {

        @Override
        public String getDescription() {
            return Translations.DROPS_ONLY_WITH_WEAKNESS_2.get();
        }

        @Override
        public double apply(double chance, @Nonnull World world, @Nonnull List<ItemStack> drops, Entity attacker,
            EntityLiving victim) {
            if (chance == 0d) return 0d;
            if (victim == null) return 0d;
            if (victim.isPotionActive(Potion.weakness) && victim.getActivePotionEffect(Potion.weakness)
                .getAmplifier() >= 2) return chance;
            return 0d;
        }

    }

    class DropsOnlyUsing implements IChanceModifier {

        ItemStack weapon;

        protected DropsOnlyUsing() {}

        public DropsOnlyUsing(ItemStack weapon) {
            this.weapon = weapon;
        }

        public DropsOnlyUsing(Item weapon) {
            this.weapon = new ItemStack(weapon);
        }

        @Override
        public String getDescription() {
            return Translations.DROPS_ONLY_USING.get(weapon.getDisplayName());
        }

        @Override
        public double apply(double chance, @Nonnull World world, @Nonnull List<ItemStack> drops, Entity attacker,
            EntityLiving victim) {
            if (chance == 0d) return 0d;
            if (!(attacker instanceof EntityPlayer)) return 0d;
            ItemStack weapon = ((EntityPlayer) attacker).getHeldItem();
            if (weapon == null) return 0d;
            if (weapon.getItem() == this.weapon.getItem()) return chance;
            return 0d;
        }

        @Override
        public void writeToByteBuf(ByteBuf byteBuf) {
            new ConstructableItemStack(weapon).writeToByteBuf(byteBuf);
        }

        @Override
        public void readFromByteBuf(ByteBuf byteBuf) {
            weapon = ConstructableItemStack.readFromByteBuf(byteBuf)
                .construct();
        }
    }

    class OrUsing extends DropsOnlyUsing {

        double newChance;

        protected OrUsing() {}

        public OrUsing(ItemStack weapon, double newChance) {
            super(weapon);
            this.newChance = newChance;
        }

        public OrUsing(Item weapon, double newChance) {
            super(weapon);
            this.newChance = newChance;
        }

        @Override
        public String getDescription() {
            return Translations.OR_USING.get(newChance, weapon.getDisplayName());
        }

        @Override
        public double apply(double chance, @Nonnull World world, @Nonnull List<ItemStack> drops, Entity attacker,
            EntityLiving victim) {
            if (!(attacker instanceof EntityPlayer)) return 0d;
            ItemStack weapon = ((EntityPlayer) attacker).getHeldItem();
            if (weapon == null) return chance;
            if (weapon.getItem() == this.weapon.getItem()) return newChance;
            return chance;
        }

        @Override
        public void writeToByteBuf(ByteBuf byteBuf) {
            super.writeToByteBuf(byteBuf);
            byteBuf.writeDouble(newChance);
        }

        @Override
        public void readFromByteBuf(ByteBuf byteBuf) {
            super.readFromByteBuf(byteBuf);
            newChance = byteBuf.readDouble();
        }
    }

    class OrBiome implements IChanceModifier {

        BiomeGenBase biome;
        double newChance;

        protected OrBiome() {}

        public OrBiome(BiomeGenBase biome, double newChance) {
            this.biome = biome;
            this.newChance = newChance;
        }

        @Override
        public String getDescription() {
            return Translations.OR_BIOME.get(newChance, biome.biomeName);
        }

        @Override
        public double apply(double chance, @Nonnull World world, @Nonnull List<ItemStack> drops, Entity attacker,
            EntityLiving victim) {
            if (victim == null) return chance;
            if (world.getBiomeGenForCoords((int) victim.posX, (int) victim.posZ) == biome) return newChance;
            return chance;
        }

        @Override
        public void writeToByteBuf(ByteBuf byteBuf) {
            byteBuf.writeInt(biome.biomeID);
            byteBuf.writeDouble(newChance);
        }

        @Override
        public void readFromByteBuf(ByteBuf byteBuf) {
            biome = BiomeGenBase.getBiome(byteBuf.readInt());
            newChance = byteBuf.readDouble();
        }
    }

    class DropsOnlyWithEnchant implements IChanceModifier {

        EnchantmentData enchantmentData;

        protected DropsOnlyWithEnchant() {}

        public DropsOnlyWithEnchant(EnchantmentData enchantmentData) {
            this.enchantmentData = enchantmentData;
        }

        public DropsOnlyWithEnchant(Enchantment enchantment) {
            this.enchantmentData = new EnchantmentData(enchantment, 1);
        }

        public DropsOnlyWithEnchant(int enchantment) {
            this.enchantmentData = new EnchantmentData(enchantment, 1);
        }

        @Override
        public String getDescription() {
            return Translations.DROPS_ONLY_WITH_ENCHANT
                .get(StatCollector.translateToLocal(enchantmentData.enchantmentobj.getName()));
        }

        @Override
        public double apply(double chance, @Nonnull World world, @Nonnull List<ItemStack> drops, Entity attacker,
            EntityLiving victim) {
            if (chance == 0d) return 0d;
            if (!(attacker instanceof EntityPlayer)) return 0d;
            ItemStack weapon = ((EntityPlayer) attacker).getHeldItem();
            if (weapon == null) return 0d;
            if (EnchantmentHelper.getEnchantmentLevel(enchantmentData.enchantmentobj.effectId, weapon)
                >= enchantmentData.enchantmentLevel) return chance;
            return 0d;
        }

        @Override
        public void writeToByteBuf(ByteBuf byteBuf) {
            byteBuf.writeInt(enchantmentData.enchantmentobj.effectId);
            byteBuf.writeInt(enchantmentData.enchantmentLevel);
        }

        @Override
        public void readFromByteBuf(ByteBuf byteBuf) {
            enchantmentData = new EnchantmentData(byteBuf.readInt(), byteBuf.readInt());
        }
    }

    class DropsOnlyInDimension implements IChanceModifier {

        int dimension;

        protected DropsOnlyInDimension() {}

        public DropsOnlyInDimension(int dimension) {
            this.dimension = dimension;
        }

        @Override
        public String getDescription() {
            return Translations.DROPS_ONLY_IN_DIMENSION.get(
                WorldProvider.getProviderForDimension(dimension)
                    .getDimensionName());
        }

        @Override
        public double apply(double chance, @Nonnull World world, @Nonnull List<ItemStack> drops, Entity attacker,
            EntityLiving victim) {
            if (chance == 0d) return 0d;
            if (world.provider.dimensionId == dimension) return chance;
            return 0d;
        }

        @Override
        public void writeToByteBuf(ByteBuf byteBuf) {
            byteBuf.writeInt(dimension);
        }

        @Override
        public void readFromByteBuf(ByteBuf byteBuf) {
            dimension = byteBuf.readInt();
        }
    }

    class EachLevelOfGives implements IChanceModifier {

        Enchantment enchantment;
        double change;

        protected EachLevelOfGives() {}

        public EachLevelOfGives(Enchantment enchantment, double change) {
            this.enchantment = enchantment;
            this.change = change;
        }

        @Override
        public String getDescription() {
            return Translations.EACH_LEVEL_OF_GIVES
                .get(StatCollector.translateToLocal(enchantment.getName()), change + "%");
        }

        @Override
        public double apply(double chance, @Nonnull World world, @Nonnull List<ItemStack> drops, Entity attacker,
            EntityLiving victim) {
            if (!(attacker instanceof EntityPlayer)) return chance;
            ItemStack weapon = ((EntityPlayer) attacker).getHeldItem();
            if (EnchantmentHelper.getEnchantmentLevel(enchantment.effectId, weapon) > 0) return chance + change;
            return chance;
        }

        @Override
        public void writeToByteBuf(ByteBuf byteBuf) {
            byteBuf.writeInt(enchantment.effectId);
            byteBuf.writeDouble(change);
        }

        @Override
        public void readFromByteBuf(ByteBuf byteBuf) {
            enchantment = Enchantment.enchantmentsList[byteBuf.readInt()];
            change = byteBuf.readDouble();
        }
    }

    class PoweredCreeper implements IChanceModifier {

        @Override
        public String getDescription() {
            return Translations.POWERED_CREEPER.get();
        }

        @Override
        public double apply(double chance, @Nonnull World world, @Nonnull List<ItemStack> drops, Entity attacker,
            EntityLiving victim) {
            if (chance == 0d) return 0d;
            if (victim == null) return 0d;
            if (victim instanceof EntityCreeper && ((EntityCreeper) victim).getPowered()) return chance;
            return 0d;
        }

    }
}
