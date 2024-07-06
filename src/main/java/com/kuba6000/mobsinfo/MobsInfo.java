/*
 * spotless:off
 * MobsInfo - Minecraft addon
 * Copyright (C) 2023  kuba6000
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library. If not, see <https://www.gnu.org/licenses/>.
 * spotless:on
 */

package com.kuba6000.mobsinfo;

import static com.kuba6000.mobsinfo.MobsInfo.MODID;
import static com.kuba6000.mobsinfo.MobsInfo.MODNAME;

import java.lang.reflect.Field;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.kuba6000.mobsinfo.network.LoadConfigPacket;
import com.kuba6000.mobsinfo.network.SaveDataPacket;

import cpw.mods.fml.common.LoadController;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLConstructionEvent;
import cpw.mods.fml.common.event.FMLEvent;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLLoadCompleteEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerAboutToStartEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppedEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

@SuppressWarnings("unused")
@Mod(
    modid = MODID,
    version = Tags.VERSION,
    name = MODNAME,
    acceptedMinecraftVersions = "[1.7.10]",
    dependencies = "required-after: gtnhmixins@[2.0.1,); " + "required-after: NotEnoughItems; "
        + "after: EnderIO; "
        + "after: Thaumcraft; "
        + "after: InfernalMobs; "
        + "after: MineTweaker3; ")
public class MobsInfo {

    public static final String MODID = "mobsinfo";
    public static final String MODNAME = "MobsInfo";
    @Mod.Instance
    public static MobsInfo instance;

    public static final SimpleNetworkWrapper NETWORK = new SimpleNetworkWrapper(MODID);

    static {
        NETWORK.registerMessage(new LoadConfigPacket.Handler(), LoadConfigPacket.class, 0, Side.CLIENT);
        NETWORK.registerMessage(new SaveDataPacket.Handler(), SaveDataPacket.class, 1, Side.CLIENT);
    }

    private static final Logger LOG = LogManager.getLogger(MODID);

    @SidedProxy(clientSide = "com.kuba6000.mobsinfo.ClientProxy", serverSide = "com.kuba6000.mobsinfo.CommonProxy")
    public static CommonProxy proxy;

    @Subscribe
    public void iAmDependingOnEverything(FMLEvent event) {
        if (event instanceof FMLPreInitializationEvent) {
            ModContainer activecontainer = Loader.instance()
                .activeModContainer();
            List<ModContainer> list = Loader.instance()
                .getActiveModList();
            list.remove(activecontainer);
            list.add(activecontainer);
        }
    }

    @Mod.EventHandler
    public void construction(FMLConstructionEvent event) {
        // hack into load order only if JustAnotherSpawner mod is loaded
        if (!Loader.isModLoaded("JustAnotherSpawner")) return;
        try {
            Field loaderControllerField = Loader.class.getDeclaredField("modController");
            loaderControllerField.setAccessible(true);
            LoadController controller = (LoadController) loaderControllerField.get(Loader.instance());
            Field masterEventBus = LoadController.class.getDeclaredField("masterChannel");
            masterEventBus.setAccessible(true);
            EventBus bus = (EventBus) masterEventBus.get(controller);
            bus.register(this);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            //
        }
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        proxy.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }

    @Mod.EventHandler
    public void loadComplete(FMLLoadCompleteEvent event) {
        proxy.loadComplete(event);
    }

    @Mod.EventHandler
    public void serverAboutToStart(FMLServerAboutToStartEvent event) {
        proxy.serverAboutToStart(event);
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        proxy.serverStarting(event);
    }

    @Mod.EventHandler
    public void serverStarted(FMLServerStartedEvent event) {
        proxy.serverStarted(event);
    }

    @Mod.EventHandler
    public void serverStopping(FMLServerStoppingEvent event) {
        proxy.serverStopping(event);
    }

    @Mod.EventHandler
    public void serverStopped(FMLServerStoppedEvent event) {
        proxy.serverStopped(event);
    }

    public static void debug(String message) {
        LOG.debug(message);
    }

    public static void info(String message) {
        LOG.info(message);
    }

    public static void warn(String message) {
        LOG.warn(message);
    }

    public static void error(String message) {
        LOG.error(message);
    }
}
