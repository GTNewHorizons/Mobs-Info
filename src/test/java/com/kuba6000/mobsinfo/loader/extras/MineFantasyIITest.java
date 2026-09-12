package com.kuba6000.mobsinfo.loader.extras;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Bootstrap;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.FakePlayer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockedStatic;

import com.kuba6000.mobsinfo.api.DummyWorld;
import com.kuba6000.mobsinfo.api.IChanceModifier;
import com.kuba6000.mobsinfo.api.MobDrop;
import com.kuba6000.mobsinfo.api.MobRecipe;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.registry.GameRegistry;

public class MineFantasyIITest {

    private MockedStatic<Loader> forge;
    private MockedStatic<GameRegistry> registry;
    private final Map<String, Item> items = new HashMap<>();
    private final Map<String, Boolean> originalConfig = new HashMap<>();
    private static int nextId = 23000;

    @Before
    public void setup() throws Exception {
        Loader loader = mock(Loader.class);
        forge = mockStatic(Loader.class);
        forge.when(Loader::instance)
            .thenReturn(loader);
        Bootstrap.func_151354_b();
        for (String flag : new String[] { "hunterKnife", "lessHunt", "upgradeZombieWep" }) {
            originalConfig.put(
                flag,
                config().getField(flag)
                    .getBoolean(null));
            config().getField(flag)
                .setBoolean(null, false);
        }
        registry = mockStatic(GameRegistry.class);
        registry.when(() -> GameRegistry.findUniqueIdentifierFor(any(Item.class)))
            .thenCallRealMethod();
        registry.when(() -> GameRegistry.findItem(eq("minefantasy2"), anyString()))
            .thenAnswer(call -> {
                String name = call.getArgument(1);
                if (!items.containsKey(name)) {
                    Item item = new Item();
                    int id = nextId++;
                    Item.itemRegistry.addObject(id, "mobsinfo_test:mf_" + id, item);
                    items.put(name, item);
                }
                return items.get(name);
            });
    }

    @After
    public void cleanup() throws Exception {
        for (Map.Entry<String, Boolean> flag : originalConfig.entrySet()) config().getField(flag.getKey())
            .setBoolean(null, flag.getValue());
        registry.close();
        forge.close();
    }

    private Class<?> config() throws Exception {
        return Class.forName("minefantasy.mf2.config.ConfigHardcore");
    }

    @Test
    public void deathHandlerBooksHaveTheirUnconditionalProbabilities() throws Exception {
        ArrayList<MobDrop> witch = process(EntityWitch.class);
        assertEquals(2500, find(witch, "MF_Com_skillbook_engineering").chance);
        assertEquals(7500, find(witch, "MF_Com_skillbook_provisioning").chance);
        ArrayList<MobDrop> villager = process(EntityVillager.class);
        assertEquals(200, find(villager, "MF_Com_skillbook_engineering").chance);
        assertEquals(600, find(villager, "MF_Com_skillbook_artisanry").chance);
        ArrayList<MobDrop> zombie = process(EntityZombie.class);
        assertEquals(40, find(zombie, "MF_Com_skillbook_engineering").chance);
        assertEquals(120, find(zombie, "MF_Com_skillbook_construction").chance);
        for (MobDrop drop : witch) {
            assertFalse(drop.playerOnly);
            assertFalse(drop.lootable);
        }
    }

    private MobDrop find(ArrayList<MobDrop> drops, String name) {
        return drops.stream()
            .filter(drop -> drop.stack.getItem() == items.get(name))
            .findFirst()
            .get();
    }

    @Test
    public void additionalDropsPreserveLootingAndBurningRules() throws Exception {
        ArrayList<MobDrop> chicken = process(EntityChicken.class);
        MobDrop feather = chicken.stream()
            .filter(drop -> drop.stack.getItem() == Items.feather)
            .findFirst()
            .get();
        assertEquals(2.5, feather.stack.stackSize * feather.chance / 10000d, 0.0003);
        assertTrue(feather.lootable);
        MobDrop guts = find(chicken, "MF_Com_guts");
        assertFalse(guts.lootable);
        EntityZombie attacker = mock(EntityZombie.class);
        ItemStack sword = new ItemStack(Items.diamond_sword);
        sword.addEnchantment(Enchantment.looting, 2);
        when(attacker.getHeldItem()).thenReturn(sword);
        assertEquals(5d / 3d, evaluate(guts, attacker, entity(EntityChicken.class)), 0.00001);
        EntityHorse horse = entity(EntityHorse.class);
        ArrayList<MobDrop> horseDrops = process(horse, new ArrayList<>());
        MobDrop raw = find(horseDrops, "MF2_food_horse_raw");
        MobDrop cooked = find(horseDrops, "MF2_food_horse_cooked");
        assertEquals(100, evaluate(raw, null, horse), 0);
        assertEquals(0, evaluate(cooked, null, horse), 0);
        when(horse.isBurning()).thenReturn(true);
        assertEquals(0, evaluate(raw, null, horse), 0);
        assertEquals(100, evaluate(cooked, null, horse), 0);
    }

    private double evaluate(MobDrop drop, net.minecraft.entity.Entity attacker, EntityLiving victim) {
        double chance = drop.chance / 100d;
        for (IChanceModifier modifier : drop.chanceModifiers)
            chance = modifier.apply(chance, new DummyWorld(), Collections.emptyList(), attacker, victim);
        return chance;
    }

    @Test
    public void huntingReplacesHideAndConditionsOriginalFoodButNotEarlyGuts() throws Exception {
        config().getField("hunterKnife")
            .setBoolean(null, true);
        config().getField("lessHunt")
            .setBoolean(null, true);
        EntityCow cow = entity(EntityCow.class);
        ArrayList<MobDrop> base = new ArrayList<>();
        base.add(MobDrop.create(new ItemStack(Items.beef, 3)));
        base.add(
            MobDrop.create(Items.leather)
                .withChance(0.5));
        ArrayList<MobDrop> drops = process(cow, base);
        assertFalse(
            drops.stream()
                .anyMatch(drop -> drop.stack.getItem() == Items.leather));
        MobDrop hide = find(drops, "MF_Com_rawhideLarge");
        assertEquals(0, evaluate(hide, null, cow), 0);
        assertEquals(5, evaluate(find(drops, "MF_Com_guts"), null, cow), 0);
        // Synthetic external API item: an IHuntingItem that accepts the held stack.
        Class<?> hunting = Class.forName("minefantasy.mf2.api.tool.IHuntingItem");
        Item knife = mock(
            Item.class,
            withSettings().useConstructor()
                .extraInterfaces(hunting)
                .defaultAnswer(
                    call -> call.getMethod()
                        .getName()
                        .equals("canRetrieveDrops") ? true : RETURNS_DEFAULTS.answer(call)));
        ItemStack weapon = new ItemStack(knife);
        for (EntityPlayer player : new EntityPlayer[] { mock(EntityPlayer.class), mock(FakePlayer.class) }) {
            when(player.getHeldItem()).thenReturn(weapon);
            assertEquals(100, evaluate(hide, player, cow), 0);
            assertFalse(hide.playerOnly);
            MobDrop food = drops.stream()
                .filter(drop -> drop.stack.getItem() == Items.beef)
                .findFirst()
                .get();
            assertEquals(1, food.stack.stackSize);
            double chance = food.chance / 100d;
            for (IChanceModifier modifier : food.chanceModifiers) chance = modifier
                .apply(chance, new DummyWorld(), Collections.singletonList(new ItemStack(Items.beef, 3)), player, cow);
            assertEquals(100, chance, 0);
            assertEquals(0, evaluate(food, player, cow), 0);
        }
        // Source checks key presence, even if a different mod wrote false.
        cow.getEntityData()
            .setBoolean("hunterKill", false);
        assertEquals(100, evaluate(hide, null, cow), 0);
    }

    @Test
    public void skeletonArrowsRequireABowAndBaseDropsAreNotDuplicated() throws Exception {
        EntitySkeleton skeleton = entity(EntitySkeleton.class);
        ArrayList<MobDrop> base = new ArrayList<>();
        base.add(
            MobDrop.create(Items.arrow)
                .withChance(0.5));
        base.add(MobDrop.create(Items.bone));
        ArrayList<MobDrop> drops = process(skeleton, base);
        assertEquals(2, drops.size());
        MobDrop arrow = drops.stream()
            .filter(drop -> drop.stack.getItem() == Items.arrow)
            .findFirst()
            .get();
        assertEquals(0, evaluate(arrow, null, skeleton), 0);
        when(skeleton.getHeldItem()).thenReturn(new ItemStack(Items.bow));
        assertEquals(50, evaluate(arrow, null, skeleton), 0);
    }

    @Test
    public void sacksRequireTheCorrespondingUpgradeButExplicitTagsIgnoreUpgradeConfiguration() throws Exception {
        config().getField("upgradeZombieWep")
            .setBoolean(null, true);
        EntityZombie zombie = entity(EntityZombie.class);
        MobDrop sack = find(process(zombie, new ArrayList<>()), "loot_sack");
        assertEquals(0, evaluate(sack, null, zombie), 0);
        zombie.getEntityData()
            .setInteger("MF_LootDrop", 0);
        assertEquals(100, evaluate(sack, null, zombie), 0);
        zombie.getEntityData()
            .setInteger("MF_LootDrop", 1);
        assertEquals(0, evaluate(sack, null, zombie), 0);
        EntityPigZombie pig = entity(EntityPigZombie.class);
        MobDrop uncommon = find(process(pig, new ArrayList<>()), "loot_sack_uc");
        pig.getEntityData()
            .setInteger("MF_LootDrop", 1);
        assertEquals(100, evaluate(uncommon, null, pig), 0);
        when(zombie.isChild()).thenReturn(true);
        zombie.getEntityData()
            .removeTag("MF_LootDrop");
        assertFalse(
            process(zombie, new ArrayList<>()).stream()
                .anyMatch(drop -> drop.stack.getItem() == items.get("loot_sack")));
        config().getField("upgradeZombieWep")
            .setBoolean(null, false);
        config().getField("hunterKnife")
            .setBoolean(null, true);
        EntityCow cow = entity(EntityCow.class);
        cow.getEntityData()
            .setInteger("MF_LootDrop", 2);
        MobDrop rare = find(process(cow, new ArrayList<>()), "loot_sack_rare");
        assertEquals(100, evaluate(rare, null, cow), 0);
        assertFalse(rare.playerOnly);
        assertFalse(rare.lootable);
    }

    private <T extends EntityLiving> T entity(Class<T> type) {
        T entity = mock(type);
        when(entity.getEntityData()).thenReturn(new NBTTagCompound());
        entity.myEntitySize = net.minecraft.entity.Entity.EnumEntitySize.SIZE_2;
        return entity;
    }

    @Test
    public void leatherReplacementMatchesTheItemRegardlessOfMetadata() throws Exception {
        EntityZombie zombie = entity(EntityZombie.class);
        ArrayList<MobDrop> base = new ArrayList<>();
        base.add(
            MobDrop.create(new ItemStack(Items.leather, 1, 7))
                .withChance(0.25));
        MobDrop hide = find(process(zombie, base), "MF_Com_rawhideSmall");
        double chance = hide.chance / 100d;
        for (IChanceModifier modifier : hide.chanceModifiers) chance = modifier.apply(
            chance,
            new DummyWorld(),
            Collections.singletonList(new ItemStack(Items.leather, 1, 7)),
            null,
            zombie);
        assertEquals(100, chance, 0);
        assertEquals(0, evaluate(hide, null, zombie), 0);
    }

    private ArrayList<MobDrop> process(Class<? extends EntityLiving> type) throws Exception {
        return process(entity(type), new ArrayList<>());
    }

    private ArrayList<MobDrop> process(EntityLiving entity, ArrayList<MobDrop> drops) {
        IExtraLoader handler = new MineFantasyII();
        handler.process(
            entity.getClass()
                .getSimpleName(),
            drops,
            MobRecipe.generateMobRecipe(entity, "test", drops));
        return drops;
    }
}
