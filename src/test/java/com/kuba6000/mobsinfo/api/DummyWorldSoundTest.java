package com.kuba6000.mobsinfo.api;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Bootstrap;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.PlaySoundAtEntityEvent;

import org.junit.Test;
import org.mockito.MockedStatic;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class DummyWorldSoundTest {

    @Test
    public void simulatedSoundsDoNotReachClientAudioHandlers() {
        Loader forgeLoader = mock(Loader.class);
        try (MockedStatic<Loader> forge = mockStatic(Loader.class)) {
            forge.when(Loader::instance)
                .thenReturn(forgeLoader);
            Bootstrap.func_151354_b();
            DummyWorld world = new DummyWorld();
            when(forgeLoader.activeModContainer()).thenReturn(mock(ModContainer.class));
            SoundObserver observer = new SoundObserver();
            MinecraftForge.EVENT_BUS.register(observer);
            try {
                MinecraftForge.EVENT_BUS.post(new PlaySoundAtEntityEvent(mock(EntityPig.class), "control", 1f, 1f));
                assertEquals(1, observer.sounds);
                observer.sounds = 0;
                world.playSoundAtEntity(mock(EntityPig.class), "mob.pig.say", 1f, 1f);
                world.playSoundToNearExcept(mock(EntityPlayer.class), "random.pop", 1f, 1f);
                assertEquals(0, observer.sounds);
            } finally {
                MinecraftForge.EVENT_BUS.unregister(observer);
            }
        }
    }

    public static class SoundObserver {

        int sounds;

        @SubscribeEvent
        public void onSound(Event event) {
            if (event instanceof PlaySoundAtEntityEvent) sounds++;
        }
    }
}
