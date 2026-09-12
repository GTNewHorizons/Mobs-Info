package com.kuba6000.mobsinfo.loader.extras;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.kuba6000.mobsinfo.api.ConstructableItemStack;
import com.kuba6000.mobsinfo.api.IChanceModifier;
import com.kuba6000.mobsinfo.api.MobDrop;
import com.kuba6000.mobsinfo.api.MobRecipe;

import cpw.mods.fml.common.registry.GameRegistry;
import io.netty.buffer.ByteBuf;
import minefantasy.mf2.api.helpers.ToolHelper;
import minefantasy.mf2.api.tool.IHuntingItem;
import minefantasy.mf2.config.ConfigHardcore;

public class MineFantasyII implements IExtraLoader {

    @Override
    public void process(String name, ArrayList<MobDrop> drops, MobRecipe recipe) {
        boolean huntingRestricted = ConfigHardcore.hunterKnife && isAnimal(recipe.entity);
        // Snapshot before adding direct world drops: cancellation only suppresses event.drops.
        ArrayList<MobDrop> original = new ArrayList<>(drops);
        boolean leather = original.stream()
            .anyMatch(drop -> drop.stack.getItem() == Items.leather);
        ArrayList<ItemStack> reducedFood = new ArrayList<>();
        for (MobDrop drop : original) {
            if (drop.stack.getItem() == Items.leather) {
                drops.remove(drop);
                continue;
            }
            if (ConfigHardcore.lessHunt && isAnimal(recipe.entity) && drop.stack.getItem() instanceof ItemFood) {
                if (reducedFood.stream()
                    .anyMatch(stack -> stack.isItemEqual(drop.stack))) {
                    drops.remove(drop);
                    continue;
                }
                reducedFood.add(drop.stack.copy());
                drop.stack = drop.stack.copy();
                drop.stack.stackSize = 1;
                drop.reconstructableStack = new ConstructableItemStack(drop.stack);
                drop.chance = 10000;
                drop.chanceModifiers.clear();
                drop.variableChance = false;
                drop.withLooting();
                condition(drop, new OriginalItem(drop.stack));
            }
            if (huntingRestricted) condition(drop, new HuntingWeapon());
            if (recipe.entity instanceof EntitySkeleton && drop.stack.getItem() == Items.arrow)
                condition(drop, new BowSkeleton());
        }
        // EventManagerMF.killEntity / dropBook, MineFantasy II 2.8.14.6.
        if (recipe.entity instanceof EntityWitch) {
            add(drops, "MF_Com_skillbook_engineering", 0.25);
            add(drops, "MF_Com_skillbook_provisioning", 0.75);
        }
        if (recipe.entity instanceof EntityVillager || recipe.entity instanceof EntityZombie) {
            double chance = recipe.entity instanceof EntityVillager ? 0.2 : 0.04;
            add(drops, "MF_Com_skillbook_engineering", chance * 0.1);
            add(drops, "MF_Com_skillbook_artisanry", chance * 0.3);
            add(drops, "MF_Com_skillbook_construction", chance * 0.3);
            add(drops, "MF_Com_skillbook_provisioning", chance * 0.3);
        }
        // EventManagerMF.tryDropItems. These first additions spawn before the hunting gate.
        addSack(drops, recipe.entity);
        if (recipe.entity instanceof EntityChicken) {
            MobDrop feathers = MobDrop.create(Items.feather)
                .withChance(2.5)
                .withLooting();
            feathers.clampChance();
            drops.add(feathers);
        }
        if (recipe.entity instanceof EntityAgeable
            && recipe.entity.getCreatureAttribute() != EnumCreatureAttribute.UNDEAD) {
            MobDrop guts = add(drops, "MF_Com_guts", 0.05);
            if (guts != null) condition(guts, new GutsLooting());
        }
        if (name.contains("Horse")) addMeat(drops, "horse", huntingRestricted);
        if (name.contains("Wolf")) addMeat(drops, "wolf", huntingRestricted);
        addHide(drops, recipe.entity, leather);
    }

    private static void addSack(ArrayList<MobDrop> drops, EntityLiving entity) {
        int id;
        if (entity.getEntityData()
            .hasKey("MF_LootDrop"))
            id = entity.getEntityData()
                .getInteger("MF_LootDrop");
        else if (ConfigHardcore.upgradeZombieWep && entity instanceof EntityZombie && !entity.isChild()) {
            // MonsterUpgrader assigns this tag during LivingUpdateEvent, not in the constructor.
            id = entity instanceof EntityPigZombie ? 1 : 0;
        } else return;
        id = id == 0 ? 0 : id == 1 ? 1 : 2;
        String[] sacks = { "loot_sack", "loot_sack_uc", "loot_sack_rare" };
        MobDrop sack = add(drops, sacks[id], 1d);
        if (sack != null) condition(sack, new TaggedSack(id));
    }

    public static class TaggedSack implements IChanceModifier {

        private int id;

        public TaggedSack() {}

        public TaggedSack(int id) {
            this.id = id;
        }

        @Override
        public String getDescription() {
            return Translations.MINEFANTASY2_TAGGED_SACK.get();
        }

        @Override
        public double apply(double chance, @Nonnull World world, @Nonnull List<ItemStack> drops, Entity attacker,
            EntityLiving victim) {
            if (victim == null || !victim.getEntityData()
                .hasKey("MF_LootDrop")) return 0;
            int value = victim.getEntityData()
                .getInteger("MF_LootDrop");
            return (value == 0 ? 0 : value == 1 ? 1 : 2) == id ? chance : 0;
        }

        @Override
        public void writeToByteBuf(ByteBuf buffer) {
            buffer.writeInt(id);
        }

        @Override
        public void readFromByteBuf(ByteBuf buffer) {
            id = buffer.readInt();
        }
    }

    private static void addMeat(ArrayList<MobDrop> drops, String animal, boolean huntingRestricted) {
        for (boolean cooked : new boolean[] { false, true }) {
            MobDrop meat = add(drops, "MF2_food_" + animal + (cooked ? "_cooked" : "_raw"), 1d);
            if (meat != null) {
                meat.withLooting();
                condition(meat, new Burning(cooked));
                if (huntingRestricted) condition(meat, new HuntingWeapon());
            }
        }
    }

    private static boolean isAnimal(EntityLiving entity) {
        return entity instanceof IAnimals && !(entity instanceof IMob);
    }

    private static void addHide(ArrayList<MobDrop> drops, EntityLiving entity, boolean replacesLeather) {
        String name = entity.getClass()
            .getName();
        boolean always = entity instanceof EntityWolf || entity instanceof EntityPig
            || entity instanceof EntitySheep
            || entity instanceof EntityCow
            || entity instanceof EntityHorse
            || name.endsWith("EntityWolf")
            || name.endsWith("EntityPig")
            || name.endsWith("EntitySheep")
            || name.endsWith("EntityCow")
            || name.endsWith("EntityHorse");
        if (!always && !replacesLeather) return;
        int size;
        if (name.endsWith("EntityCow") || name.endsWith("EntityHorse")) size = 3;
        else if (name.endsWith("EntitySheep")) size = 2;
        else if (name.endsWith("EntityPig")) size = 1;
        else {
            int body = entity.myEntitySize.ordinal() + 1;
            size = body <= 1 ? 0 : body == 2 ? 1 : body <= 4 ? 2 : 3;
        }
        if (entity.isChild()) size--;
        if (size <= 0) return;
        String[] hides = { "MF_Com_rawhideSmall", "MF_Com_rawhideMedium", "MF_Com_rawhideLarge" };
        MobDrop hide = add(drops, hides[Math.min(size, 3) - 1], 1d);
        if (hide == null) return;
        if (!always) condition(hide, new OriginalItem(new ItemStack(Items.leather), false));
        if (ConfigHardcore.hunterKnife) condition(hide, new HuntingWeapon());
    }

    public static class HuntingWeapon implements IChanceModifier {

        @Override
        public String getDescription() {
            return Translations.MINEFANTASY2_HUNTING_WEAPON.get();
        }

        @Override
        public double apply(double chance, @Nonnull World world, @Nonnull List<ItemStack> drops, Entity attacker,
            EntityLiving victim) {
            if (victim != null && victim.getEntityData()
                .hasKey("hunterKill")) return chance;
            if (!(attacker instanceof EntityLivingBase)) return 0;
            ItemStack weapon = ((EntityLivingBase) attacker).getHeldItem();
            if (weapon == null) return 0;
            if (weapon.getItem() instanceof IHuntingItem)
                return ((IHuntingItem) weapon.getItem()).canRetrieveDrops(weapon) ? chance : 0;
            return "knife".equalsIgnoreCase(ToolHelper.getCrafterTool(weapon)) ? chance : 0;
        }
    }

    public static class BowSkeleton implements IChanceModifier {

        @Override
        public String getDescription() {
            return Translations.MINEFANTASY2_BOW_SKELETON.get();
        }

        @Override
        public double apply(double chance, @Nonnull World world, @Nonnull List<ItemStack> drops, Entity attacker,
            EntityLiving victim) {
            return victim != null && victim.getHeldItem() != null
                && victim.getHeldItem()
                    .getItem() instanceof ItemBow ? chance : 0;
        }
    }

    public static class OriginalItem implements IChanceModifier {

        private ItemStack item;
        private boolean matchMetadata = true;

        public OriginalItem() {}

        public OriginalItem(ItemStack item) {
            this.item = item.copy();
        }

        public OriginalItem(ItemStack item, boolean matchMetadata) {
            this(item);
            this.matchMetadata = matchMetadata;
        }

        @Override
        public String getDescription() {
            return Translations.MINEFANTASY2_ORIGINAL_ITEM.get(item == null ? "?" : item.getDisplayName());
        }

        @Override
        public double apply(double chance, @Nonnull World world, @Nonnull List<ItemStack> drops, Entity attacker,
            EntityLiving victim) {
            return item != null && drops.stream()
                .anyMatch(
                    stack -> stack != null && stack.getItem() == item.getItem()
                        && (!matchMetadata || stack.isItemEqual(item))) ? chance : 0;
        }

        @Override
        public void writeToByteBuf(ByteBuf buffer) {
            new ConstructableItemStack(item).writeToByteBuf(buffer);
            buffer.writeBoolean(matchMetadata);
        }

        @Override
        public void readFromByteBuf(ByteBuf buffer) {
            item = ConstructableItemStack.readFromByteBuf(buffer)
                .construct();
            matchMetadata = buffer.readBoolean();
        }
    }

    private static void condition(MobDrop drop, IChanceModifier modifier) {
        if (!drop.variableChance) drop.withChanceModifiers(new IChanceModifier.NormalChance(drop.chance / 100d));
        drop.withChanceModifiers(modifier);
    }

    public static class GutsLooting implements IChanceModifier {

        @Override
        public String getDescription() {
            return Translations.MINEFANTASY2_GUTS_LOOTING.get();
        }

        @Override
        public double apply(double chance, @Nonnull World world, @Nonnull List<ItemStack> drops, Entity attacker,
            EntityLiving victim) {
            int looting = attacker instanceof EntityLivingBase
                ? EnchantmentHelper.getLootingModifier((EntityLivingBase) attacker)
                : 0;
            return chance / (1d + looting);
        }
    }

    public static class Burning implements IChanceModifier {

        private boolean burning;

        public Burning() {}

        public Burning(boolean burning) {
            this.burning = burning;
        }

        @Override
        public String getDescription() {
            return (burning ? Translations.MINEFANTASY2_BURNING : Translations.MINEFANTASY2_NOT_BURNING).get();
        }

        @Override
        public double apply(double chance, @Nonnull World world, @Nonnull List<ItemStack> drops, Entity attacker,
            EntityLiving victim) {
            return victim != null && victim.isBurning() == burning ? chance : 0;
        }

        @Override
        public void writeToByteBuf(ByteBuf buffer) {
            buffer.writeBoolean(burning);
        }

        @Override
        public void readFromByteBuf(ByteBuf buffer) {
            burning = buffer.readBoolean();
        }
    }

    private static MobDrop add(ArrayList<MobDrop> drops, String name, double amount) {
        Item item = GameRegistry.findItem("minefantasy2", name);
        if (item == null) return null;
        MobDrop drop = MobDrop.create(item)
            .withChance(amount);
        drop.clampChance();
        drops.add(drop);
        return drop;
    }
}
