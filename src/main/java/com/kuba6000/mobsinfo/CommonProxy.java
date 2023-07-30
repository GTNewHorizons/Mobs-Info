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

import net.minecraftforge.common.MinecraftForge;

import com.kuba6000.mobsinfo.command.CommandHandler;
import com.kuba6000.mobsinfo.config.Config;
import com.kuba6000.mobsinfo.loader.MobRecipeLoader;
import com.kuba6000.mobsinfo.savedata.PlayerDataManager;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.*;

public class CommonProxy {

    public void preInit(FMLPreInitializationEvent event) {
        MobsInfo.info("Initializing! Version: " + Tags.VERSION);

        Config.init(event.getModConfigurationDirectory());
        Config.synchronizeConfiguration();
        FMLCommonHandler.instance()
            .bus()
            .register(new FMLEventHandler());
        MinecraftForge.EVENT_BUS.register(new PlayerDataManager());
        MinecraftForge.EVENT_BUS.register(new ForgeEventHandler());
    }

    public void init(FMLInitializationEvent event) {}

    public void postInit(FMLPostInitializationEvent event) {}

    public void loadComplete(FMLLoadCompleteEvent event) {}

    public void serverAboutToStart(FMLServerAboutToStartEvent event) {}

    public void serverStarting(FMLServerStartingEvent event) {
        MobRecipeLoader.generateMobRecipeMap();
        MobRecipeLoader.processMobRecipeMap();

        CommandHandler cmd = new CommandHandler();
        event.registerServerCommand(cmd);
    }

    public void serverStarted(FMLServerStartedEvent event) {}

    public void serverStopping(FMLServerStoppingEvent event) {}

    public void serverStopped(FMLServerStoppedEvent event) {}
}
