package com.kuba6000.mobsinfo.loader.extras;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

import java.net.URL;
import java.util.ArrayList;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.init.Bootstrap;
import net.minecraft.init.Items;

import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.MockedStatic;

import com.kuba6000.mobsinfo.api.MobDrop;
import com.kuba6000.mobsinfo.api.MobRecipe;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.registry.GameRegistry;

public class ReliquaryLegacyDropsTest {

    @BeforeClass
    public static void bootstrapMinecraft() {
        Loader forgeLoader = mock(Loader.class);
        try (MockedStatic<Loader> forge = mockStatic(Loader.class)) {
            forge.when(Loader::instance)
                .thenReturn(forgeLoader);
            Bootstrap.func_151354_b();
        }
    }

    @Test
    public void legacyReleaseReportsLootingWithoutExcludingFakePlayers() throws Exception {
        // Model the optional-mod boundary: stable 1.2 has the old item, not the new recipe API.
        ClassLoader legacy = new ClassLoader(getClass().getClassLoader()) {

            @Override
            protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
                if (name.startsWith("xreliquary.")) throw new ClassNotFoundException(name);
                return super.loadClass(name, resolve);
            }

            @Override
            public URL getResource(String name) {
                if (name.equals("xreliquary/items/ItemSquidBeak.class")) {
                    return getClass().getResource(
                        "/" + ReliquaryLegacyDropsTest.class.getName()
                            .replace('.', '/') + ".class");
                }
                return super.getResource(name);
            }
        };
        try (MockedStatic<Loader> forge = mockStatic(Loader.class);
            MockedStatic<GameRegistry> registry = mockStatic(GameRegistry.class)) {
            registry.when(() -> GameRegistry.findItem("xreliquary", "squid_beak"))
                .thenReturn(Items.flint);
            registry.when(() -> GameRegistry.findItem("xreliquary", "witch_hat"))
                .thenReturn(Items.leather_helmet);
            Reliquarry loader = new Reliquarry("1.2", legacy);
            ArrayList<MobDrop> squid = dropsFor(loader, mock(EntitySquid.class));
            assertEquals(1, squid.size());
            assertSame(Items.flint, squid.get(0).stack.getItem());
            assertTrue(squid.get(0).lootable);
            assertFalse(squid.get(0).variableChance);
            assertFalse(squid.get(0).playerOnly);
            ArrayList<MobDrop> witch = dropsFor(loader, mock(EntityWitch.class));
            assertEquals(1, witch.size());
            assertSame(Items.leather_helmet, witch.get(0).stack.getItem());
            assertFalse(witch.get(0).lootable);
            assertFalse(witch.get(0).playerOnly);
            assertTrue(dropsFor(loader, mock(EntityZombie.class)).isEmpty());
        }
    }

    private static ArrayList<MobDrop> dropsFor(Reliquarry loader, EntityLiving entity) {
        ArrayList<MobDrop> drops = new ArrayList<>();
        MobRecipe recipe = MobRecipe.generateMobRecipe(entity, "test", drops);
        loader.process("test", drops, recipe);
        return drops;
    }
}
