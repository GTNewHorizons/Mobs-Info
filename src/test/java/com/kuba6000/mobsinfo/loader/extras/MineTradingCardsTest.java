package com.kuba6000.mobsinfo.loader.extras;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.init.Bootstrap;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDropsEvent;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.MockedStatic;

import com.kuba6000.mobsinfo.api.DummyWorld;
import com.kuba6000.mobsinfo.api.MobDrop;
import com.kuba6000.mobsinfo.api.MobRecipe;

import cpw.mods.fml.common.Loader;

public class MineTradingCardsTest {

    private Class<?> config;
    private Class<?> items;
    private final Map<Field, Object> originals = new HashMap<>();
    private static int nextItemId = 20000;
    private MockedStatic<Loader> forge;

    @BeforeClass
    public static void bootstrap() {
        Loader loader = mock(Loader.class);
        try (MockedStatic<Loader> forge = mockStatic(Loader.class)) {
            forge.when(Loader::instance)
                .thenReturn(loader);
            Bootstrap.func_151354_b();
        }
    }

    @Before
    public void configureMod() throws Exception {
        Loader loader = mock(Loader.class);
        forge = mockStatic(Loader.class);
        forge.when(Loader::instance)
            .thenReturn(loader);
        // Use the optional mod's public configuration, without a separate test dependency.
        config = Class.forName("com.is.mtc.handler.DropHandler");
        items = Class.forName("com.is.mtc.MineTradingCards");
        for (Field field : config.getFields()) {
            if (!Modifier.isStatic(field.getModifiers()) || Modifier.isFinal(field.getModifiers())) continue;
            originals.put(field, field.get(null));
            if (field.getType() == float.class) field.setFloat(null, 0);
            else if (field.getType() == boolean.class) field.setBoolean(null, false);
            else if (field.getType() == String[].class) field.set(null, new String[0]);
        }
        for (Field field : items.getFields()) {
            if (!Modifier.isStatic(field.getModifiers()) || !Item.class.isAssignableFrom(field.getType())) continue;
            originals.put(field, field.get(null));
            field.set(
                null,
                field.getName()
                    .startsWith("card")
                        ? field.getType()
                            .getConstructor(int.class)
                            .newInstance(0)
                        : field.getType()
                            .getConstructor()
                            .newInstance());
            int id = nextItemId++;
            Item.itemRegistry.addObject(id, "mobsinfo_test:mtc_" + id, field.get(null));
        }
    }

    @After
    public void restoreMod() throws Exception {
        for (Map.Entry<Field, Object> entry : originals.entrySet()) entry.getKey()
            .set(null, entry.getValue());
        forge.close();
    }

    @Test
    public void respectsConfiguredRatesAndEntityCategoriesWithoutKillOrLootingRestrictions() throws Exception {
        set("CAN_DROP_PACKS_MOB", true);
        set("PACK_DROP_RATE_COM", 4f);
        ArrayList<MobDrop> drops = dropsFor(EntityZombie.class);
        assertEquals(1, drops.size());
        assertSame(
            items.getField("packCommon")
                .get(null),
            drops.get(0).stack.getItem());
        assertEquals(2500, drops.get(0).chance);
        assertFalse(drops.get(0).lootable);
        assertFalse(drops.get(0).playerOnly);
        assertFalse(drops.get(0).variableChance);
        assertTrue(dropsFor(EntityCow.class).isEmpty());
        set("CAN_DROP_PACKS_ANIMAL", true);
        assertEquals(2500, dropsFor(EntityCow.class).get(0).chance);
        set("PACK_DROP_RATE_COM", 0f);
        assertTrue(dropsFor(EntityZombie.class).isEmpty());
    }

    private void set(String name, Object value) throws Exception {
        config.getField(name)
            .set(null, value);
    }

    @Test
    public void singleDropUsesWeightedSelectionAmongSuccessfulRolls() throws Exception {
        set("CAN_DROP_PACKS_MOB", true);
        set("PACK_DROP_RATE_COM", 2f);
        set("PACK_DROP_RATE_UNC", 4f);
        set("ONLY_ONE_DROP", true);
        ArrayList<MobDrop> drops = dropsFor(EntityZombie.class);
        // Common alone: 3/8; both: 1/8, with 2/3 selecting common => 11/24.
        // Uncommon alone: 1/8; both: 1/8, with 1/3 selecting uncommon => 1/6.
        assertEquals(2, drops.size());
        assertEquals(4583, find(drops, "packCommon").chance);
        assertEquals(1666, find(drops, "packUncommon").chance);
        set("ONLY_ONE_DROP", false);
        drops = dropsFor(EntityZombie.class);
        assertEquals(5000, find(drops, "packCommon").chance);
        assertEquals(2500, find(drops, "packUncommon").chance);
    }

    private MobDrop find(ArrayList<MobDrop> drops, String field) throws Exception {
        Object item = items.getField(field)
            .get(null);
        return drops.stream()
            .filter(drop -> drop.stack.getItem() == item)
            .findFirst()
            .get();
    }

    @Test
    public void bossAmountsAreAdditiveFractionalAndIndependentOfOrdinaryDropSwitches() throws Exception {
        set("ONLY_ONE_DROP", true);
        set("BOSS_DROPS", new String[] { "common_pack:2.25", "legendary_pack:0.25", "bad", "unknown:4" });
        set("ENDER_DRAGON_DROPS", new String[] { "common_pack:5" });
        ArrayList<MobDrop> drops = dropsFor(EntityWither.class);
        assertEquals(2, drops.size());
        assertEquals(2.25, expectedAmount(find(drops, "packCommon")), 0.0003);
        assertEquals(0.25, expectedAmount(find(drops, "packLegendary")), 0.0001);
        for (MobDrop drop : drops) {
            assertFalse(drop.lootable);
            assertFalse(drop.playerOnly);
            assertTrue(drop.chance <= 10000);
        }
        assertEquals(5d, expectedAmount(find(dropsFor(EntityDragon.class), "packCommon")), 0.0001);
        assertTrue(dropsFor(EntityZombie.class).isEmpty());
        set("CAN_DROP_PACKS_MOB", true);
        set("PACK_DROP_RATE_COM", 2f);
        assertEquals(2.75, expectedAmount(find(dropsFor(EntityWither.class), "packCommon")), 0.0003);
    }

    private double expectedAmount(MobDrop drop) {
        return drop.stack.stackSize * drop.chance / 10000d;
    }

    @Test
    public void predictedAmountsAgreeWithTheRealModHandlerAcrossAllDropTypes() throws Exception {
        set("CAN_DROP_CARDS_MOB", true);
        set("CAN_DROP_PACKS_MOB", true);
        set("CAN_DROP_CARDS_ANIMAL", true);
        // Different card/pack switches catch mistakes in category selection.
        set("CAN_DROP_PACKS_ANIMAL", false);
        String[] rates = { "CARD_DROP_RATE_COM", "CARD_DROP_RATE_UNC", "CARD_DROP_RATE_RAR", "CARD_DROP_RATE_ANC",
            "CARD_DROP_RATE_LEG", "PACK_DROP_RATE_COM", "PACK_DROP_RATE_UNC", "PACK_DROP_RATE_RAR",
            "PACK_DROP_RATE_ANC", "PACK_DROP_RATE_LEG", "PACK_DROP_RATE_STD", "PACK_DROP_RATE_EDT",
            "PACK_DROP_RATE_CUS" };
        for (int i = 0; i < rates.length; i++) set(rates[i], (float) (i + 2));
        set("BOSS_DROPS", new String[] { "common_pack:2.25", "legendary_card:0.5", "common_pack:0.5" });
        set("ENDER_DRAGON_DROPS", new String[] { "edition_pack:3.75", "ancient_card:1" });
        for (boolean single : new boolean[] { false, true }) {
            set("ONLY_ONE_DROP", single);
            compareWithRealHandler(EntityZombie.class);
            compareWithRealHandler(EntityCow.class);
            compareWithRealHandler(EntityWither.class);
            compareWithRealHandler(EntityDragon.class);
            // Slimes are IMob, but not EntityMob: MTC does not give them ordinary drops.
            compareWithRealHandler(EntitySlime.class);
        }
        // Rates below one guarantee the first roll, but still carry distinct selection weights.
        set("PACK_DROP_RATE_COM", 0.5f);
        set("CARD_DROP_RATE_LEG", 1f);
        compareWithRealHandler(EntityZombie.class);
    }

    private void compareWithRealHandler(Class<? extends EntityLiving> entityType) throws Exception {
        World world = new DummyWorld();
        world.rand = new Random(102314L);
        EntityLiving entity = mock(entityType);
        entity.worldObj = world;
        Object originalHandler = config.getConstructor()
            .newInstance();
        Method onEvent = config.getMethod("onEvent", LivingDropsEvent.class);
        Map<Item, Double> observed = new HashMap<>();
        int trials = 20000;
        for (int i = 0; i < trials; i++) {
            ArrayList<EntityItem> actual = new ArrayList<>();
            onEvent.invoke(originalHandler, new LivingDropsEvent(entity, DamageSource.generic, actual, 0, false, 0));
            for (EntityItem drop : actual) {
                ItemStack stack = drop.getEntityItem();
                observed.merge(stack.getItem(), (double) stack.stackSize / trials, Double::sum);
            }
        }
        ArrayList<MobDrop> predicted = dropsFor(entityType);
        assertEquals("Drop types for " + entityType.getSimpleName(), observed.size(), predicted.size());
        for (MobDrop drop : predicted) {
            assertEquals(
                "Mean amount for " + entityType.getSimpleName() + ": " + drop.reconstructableStack.itemIdentifier,
                observed.getOrDefault(drop.stack.getItem(), 0d),
                expectedAmount(drop),
                0.025);
        }
    }

    private ArrayList<MobDrop> dropsFor(Class<? extends EntityLiving> entityType) throws Exception {
        IExtraLoader handler = new MineTradingCards();
        ArrayList<MobDrop> drops = new ArrayList<>();
        MobRecipe recipe = MobRecipe.generateMobRecipe(mock(entityType), "test", drops);
        handler.process("test", drops, recipe);
        return drops;
    }
}
