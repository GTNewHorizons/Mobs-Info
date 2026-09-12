package com.kuba6000.mobsinfo.loader.extras;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Bootstrap;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemRecord;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.living.LivingDropsEvent;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockedStatic;

import com.kuba6000.mobsinfo.api.DummyWorld;
import com.kuba6000.mobsinfo.api.IChanceModifier;
import com.kuba6000.mobsinfo.api.MobDrop;
import com.kuba6000.mobsinfo.api.MobRecipe;
import com.kuba6000.mobsinfo.mixin.early.minecraft.EntityLivingBaseAccessor;

import cpw.mods.fml.common.Loader;

public class LootPlusPlusTest {

    private MockedStatic<Loader> forge;
    private Class<?> helper;
    private final Map<String, Object> originals = new HashMap<>();

    @Before
    public void setup() throws Exception {
        Loader loader = mock(Loader.class);
        forge = mockStatic(Loader.class);
        forge.when(Loader::instance)
            .thenReturn(loader);
        Bootstrap.func_151354_b();
        // Loot++ also registers unrelated tool materials in its static initializer.
        // Forge's Java 8 enum extension is not available in this plain Java 17 test JVM.
        try (MockedStatic<EnumHelper> enums = mockStatic(EnumHelper.class)) {
            enums
                .when(
                    () -> EnumHelper.addToolMaterial(anyString(), anyInt(), anyInt(), anyFloat(), anyFloat(), anyInt()))
                .thenAnswer(call -> mock(Item.ToolMaterial.class));
            enums.when(() -> EnumHelper.addArmorMaterial(anyString(), anyInt(), any(int[].class), anyInt()))
                .thenAnswer(call -> mock(ItemArmor.ArmorMaterial.class));
            helper = Class.forName("com.tmtravlr.lootplusplus.LootPPHelper");
        }
        set("entityDropAdditions", new HashMap<>());
        set("entityDropRemovals", new HashMap<>());
        set("allRecords", new ArrayList<>());
        set("creepersDropRecords", false);
        set("creepersDropAllRecords", false);
    }

    private void set(String name, Object value) throws Exception {
        if (!originals.containsKey(name)) originals.put(
            name,
            helper.getField(name)
                .get(null));
        helper.getField(name)
            .set(null, value);
    }

    @After
    public void cleanup() throws Exception {
        for (Map.Entry<String, Object> entry : originals.entrySet()) helper.getField(entry.getKey())
            .set(null, entry.getValue());
        forge.close();
    }

    @Test
    public void removesOnlyItemsMatchingMetadataAndOptionalNbt() throws Exception {
        ItemStack exact = new ItemStack(Items.dye, 1, 2);
        exact.stackTagCompound = new NBTTagCompound();
        exact.stackTagCompound.setString("variant", "selected");
        ItemStack wildcard = new ItemStack(Items.coal, 1, 32767);
        HashMap<String, Set<ItemStack>> removals = new HashMap<>();
        Set<ItemStack> rules = new HashSet<>();
        rules.add(exact);
        rules.add(wildcard);
        removals.put("Zombie", rules);
        set("entityDropRemovals", removals);
        ArrayList<MobDrop> drops = new ArrayList<>();
        drops.add(MobDrop.create(exact.copy()));
        drops.add(MobDrop.create(new ItemStack(Items.dye, 1, 2)));
        drops.add(MobDrop.create(new ItemStack(Items.dye, 1, 3)));
        drops.add(MobDrop.create(new ItemStack(Items.coal, 1, 1)));
        drops.add(MobDrop.create(Items.bone));
        process(handler(), "Zombie", drops);
        assertEquals(3, drops.size());
        assertTrue(
            drops.stream()
                .anyMatch(drop -> drop.stack.getItem() == Items.bone));
        assertFalse(
            drops.stream()
                .anyMatch(drop -> drop.stack.getItem() == Items.coal));
        assertTrue(
            drops.stream()
                .filter(drop -> drop.stack.getItem() == Items.dye)
                .allMatch(drop -> drop.stack.stackTagCompound == null));
    }

    private IExtraLoader handler() throws Exception {
        return (IExtraLoader) Class.forName("com.kuba6000.mobsinfo.loader.extras.LootPlusPlus")
            .getConstructor()
            .newInstance();
    }

    @Test
    public void weightedItemDropsKeepEntityEntriesInThePoolAndReadNewConfiguration() throws Exception {
        IExtraLoader handler = handler();
        ArrayList<MobDrop> empty = new ArrayList<>();
        process(handler, "Zombie", empty);
        assertTrue(empty.isEmpty());
        ItemStack diamond = new ItemStack(Items.diamond, 20, 5);
        diamond.stackTagCompound = new NBTTagCompound();
        diamond.stackTagCompound.setString("variant", "configured");
        Object group = group(0.2f, false, false);
        addEntry(group, entry(diamond, 1, 3, 1));
        addEntry(group, entry(new ItemStack(Items.iron_ingot), 1, 1, 1));
        NBTTagCompound entity = new NBTTagCompound();
        entity.setString("id", "Pig");
        addEntry(
            group,
            Class.forName("com.tmtravlr.lootplusplus.LootPPHelper$DropInfo")
                .getConstructor(NBTTagCompound.class, int.class)
                .newInstance(entity, 2));
        configure("Zombie", group);
        ArrayList<MobDrop> drops = new ArrayList<>();
        process(handler, "Zombie", drops);
        assertEquals(2, drops.size());
        MobDrop first = drops.get(0);
        assertEquals(1000, first.chance);
        assertEquals(1, first.stack.stackSize);
        assertEquals(5, first.stack.getItemDamage());
        assertEquals(diamond.stackTagCompound, first.stack.stackTagCompound);
        assertNotSame(diamond.stackTagCompound, first.stack.stackTagCompound);
        assertEquals(500, drops.get(1).chance);
        ArrayList<MobDrop> repeated = new ArrayList<>();
        process(handler, "Zombie", repeated);
        assertEquals(2, repeated.size());
        assertEquals(1000, repeated.get(0).chance);
    }

    private Object group(float chance, boolean player, boolean looting) throws Exception {
        return Class.forName("com.tmtravlr.lootplusplus.LootPPHelper$EntityDropInfo")
            .getConstructor(float.class, boolean.class, boolean.class)
            .newInstance(chance, player, looting);
    }

    @Test
    public void syntheticRecipeNamesUseTheActualEntityRegistrationForConfiguration() throws Exception {
        Object group = group(1f, false, false);
        addEntry(group, entry(new ItemStack(Items.diamond), 1, 1, 1));
        configure("Skeleton", group);
        EntitySkeleton skeleton = mock(EntitySkeleton.class);
        when(skeleton.getSkeletonType()).thenReturn(1);
        ArrayList<MobDrop> drops = new ArrayList<>();
        handler().process("witherSkeleton", drops, MobRecipe.generateMobRecipe(skeleton, "witherSkeleton", drops));
        assertEquals(1, drops.size());
        assertSame(Items.diamond, drops.get(0).stack.getItem());
    }

    @Test
    public void creeperRecordsFollowConfigurationAndRequireASkeletonKill() throws Exception {
        set("creepersDropRecords", true);
        set("creepersDropAllRecords", true);
        ArrayList<ItemRecord> records = new ArrayList<>();
        records.add((ItemRecord) Items.record_13);
        records.add((ItemRecord) Items.record_cat);
        set("allRecords", records);
        EntityCreeper creeper = mock(EntityCreeper.class);
        ArrayList<MobDrop> drops = new ArrayList<>();
        drops.add(MobDrop.create(Items.gunpowder));
        handler().process("Creeper", drops, MobRecipe.generateMobRecipe(creeper, "Creeper", drops));
        assertEquals(3, drops.size());
        for (MobDrop drop : drops) if (drop.stack.getItem() instanceof ItemRecord) {
            assertEquals(5000, drop.chance);
            assertEquals(0, evaluate(drop, mock(EntityPlayer.class), creeper), 0);
            assertEquals(50, evaluate(drop, mock(EntitySkeleton.class), creeper), 0);
            assertFalse(drop.playerOnly);
            assertFalse(drop.lootable);
        }
        set("creepersDropRecords", false);
        drops = new ArrayList<>();
        // An ordinary item drop must survive: the disabled special onDeath drop is separate.
        drops.add(MobDrop.create(Items.record_13));
        drops.add(MobDrop.create(Items.gunpowder));
        handler().process("Creeper", drops, MobRecipe.generateMobRecipe(creeper, "Creeper", drops));
        assertEquals(2, drops.size());
        assertSame(Items.record_13, drops.get(0).stack.getItem());
        assertSame(Items.gunpowder, drops.get(1).stack.getItem());
    }

    @Test
    public void lootingUsesTheStandardFlagWithoutChangingPlayerCreditConditions() throws Exception {
        Object group = group(0.2f, true, true);
        addEntry(group, entry(new ItemStack(Items.diamond), 1, 3, 1));
        configure("Zombie", group);
        ArrayList<MobDrop> drops = new ArrayList<>();
        process(handler(), "Zombie", drops);
        MobDrop drop = drops.get(0);
        assertTrue(drop.lootable);
        assertFalse(drop.playerOnly);
        // Public Minecraft state is exposed to the test without running the mixin transformer.
        EntityZombie victim = mock(EntityZombie.class, withSettings().extraInterfaces(EntityLivingBaseAccessor.class));
        when(((EntityLivingBaseAccessor) victim).getRecentlyHit()).thenReturn(100);
        ItemStack sword = new ItemStack(Items.diamond_sword);
        sword.addEnchantment(Enchantment.looting, 2);
        for (EntityPlayer player : new EntityPlayer[] { mock(EntityPlayer.class), mock(FakePlayer.class) }) {
            when(player.getHeldItem()).thenReturn(sword);
            assertEquals(40d, evaluate(drop, player, victim), 0.0001);
        }
        when(((EntityLivingBaseAccessor) victim).getRecentlyHit()).thenReturn(0);
        assertEquals(0d, evaluate(drop, mock(EntityPlayer.class), victim), 0);
    }

    private double evaluate(MobDrop drop, Entity attacker, EntityLiving victim) {
        double chance = drop.chance / 100d;
        for (IChanceModifier modifier : drop.chanceModifiers)
            chance = modifier.apply(chance, new DummyWorld(), Collections.emptyList(), attacker, victim);
        return chance;
    }

    @Test
    public void baseAmountsAgreeWithTheOriginalLootPlusPlusRollerWithoutLooting() throws Exception {
        Object group = group(0.2f, true, true);
        addEntry(group, entry(new ItemStack(Items.diamond), 1, 3, 1));
        addEntry(group, entry(new ItemStack(Items.iron_ingot), 1, 1, 3));
        configure("Zombie", group);
        ArrayList<MobDrop> predicted = new ArrayList<>();
        process(handler(), "Zombie", predicted);
        Method roller = Class.forName("com.tmtravlr.lootplusplus.LootPPEventHandler")
            .getMethod("dropEntityDropAdditions", LivingDropsEvent.class, group.getClass(), Random.class);
        EntityZombie victim = mock(EntityZombie.class, withSettings().extraInterfaces(EntityLivingBaseAccessor.class));
        when(((EntityLivingBaseAccessor) victim).getRecentlyHit()).thenReturn(100);
        victim.worldObj = new DummyWorld();
        EntityPlayer attacker = mock(EntityPlayer.class);
        Random random = new Random(122024L);
        Map<Item, Integer> counts = new HashMap<>();
        int trials = 30000;
        for (int i = 0; i < trials; i++) {
            ArrayList<EntityItem> actual = new ArrayList<>();
            roller.invoke(null, new LivingDropsEvent(victim, DamageSource.generic, actual, 0, true, 0), group, random);
            for (EntityItem entityItem : actual) {
                ItemStack stack = entityItem.getEntityItem();
                counts.merge(stack.getItem(), stack.stackSize, Integer::sum);
            }
        }
        for (MobDrop drop : predicted) assertEquals(
            counts.getOrDefault(drop.stack.getItem(), 0) / (double) trials,
            evaluate(drop, attacker, victim) * drop.stack.stackSize / 100d,
            0.015);
    }

    private Object entry(ItemStack stack, int min, int max, int weight) throws Exception {
        return Class.forName("com.tmtravlr.lootplusplus.LootPPHelper$DropInfo")
            .getConstructor(ItemStack.class, int.class, int.class, int.class)
            .newInstance(stack, min, max, weight);
    }

    @SuppressWarnings("unchecked")
    private void addEntry(Object group, Object entry) throws Exception {
        ((ArrayList<Object>) group.getClass()
            .getField("dropList")
            .get(group)).add(entry);
    }

    private void configure(String mob, Object group) throws Exception {
        HashMap<String, ArrayList<Object>> map = new HashMap<>();
        ArrayList<Object> groups = new ArrayList<>();
        groups.add(group);
        map.put(mob, groups);
        set("entityDropAdditions", map);
    }

    private void process(IExtraLoader handler, String name, ArrayList<MobDrop> drops) {
        EntityLiving entity = mock(EntityZombie.class);
        handler.process(name, drops, MobRecipe.generateMobRecipe(entity, name, drops));
    }
}
