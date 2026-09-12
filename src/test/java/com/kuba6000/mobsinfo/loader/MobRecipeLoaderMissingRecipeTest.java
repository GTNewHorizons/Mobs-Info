package com.kuba6000.mobsinfo.loader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import net.minecraft.init.Bootstrap;
import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.common.MinecraftForge;

import org.junit.Test;
import org.mockito.MockedStatic;

import com.kuba6000.mobsinfo.api.MobRecipe;
import com.kuba6000.mobsinfo.api.event.PostMobsRegistrationEvent;
import com.kuba6000.mobsinfo.api.event.PreMobRegistrationEvent;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class MobRecipeLoaderMissingRecipeTest {

    @Test
    public void missingServerEntriesDoNotAbortRegistrationOrCreateEmptyRecipes() {
        Loader forgeLoader = mock(Loader.class);
        Map<String, Object> previousBlackboard = Launch.blackboard;
        try (MockedStatic<Loader> forge = mockStatic(Loader.class)) {
            forge.when(Loader::instance)
                .thenReturn(forgeLoader);
            Bootstrap.func_151354_b();
            Launch.blackboard = previousBlackboard == null ? new HashMap<>() : new HashMap<>(previousBlackboard);
            Launch.blackboard.put("fml.deobfuscatedEnvironment", true);
            when(forgeLoader.activeModContainer()).thenReturn(mock(ModContainer.class));
            MobRecipeLoader.GeneralMobList.clear();
            RegistrationObserver observer = new RegistrationObserver();
            MinecraftForge.EVENT_BUS.register(observer);
            try {
                MobRecipeLoader.processMobRecipeMap(
                    new LinkedHashSet<>(Arrays.asList("test:missing", "test:also_missing")),
                    new HashMap<>());
                assertEquals(Collections.emptyList(), observer.processed);
                assertTrue(observer.completed);
                assertTrue(MobRecipe.MobNameToRecipeMap.isEmpty());
            } finally {
                MinecraftForge.EVENT_BUS.unregister(observer);
                MobRecipeLoader.GeneralMobList.clear();
                MobRecipe.MobNameToRecipeMap.clear();
            }
        } finally {
            Launch.blackboard = previousBlackboard;
        }
    }

    public static class RegistrationObserver {

        final List<String> processed = new ArrayList<>();
        boolean completed;

        // Missing data must not produce a fabricated recipe, and the registration batch must finish.
        @SubscribeEvent
        public void onRegistration(Event event) {
            if (event instanceof PreMobRegistrationEvent registration) {
                processed.add(registration.currentMob);
            } else if (event instanceof PostMobsRegistrationEvent) {
                completed = true;
            }
        }
    }
}
