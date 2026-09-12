/*
 * spotless:off
 * MobsInfo - Minecraft addon
 * Copyright (C) 2023-2025  kuba6000
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

package com.kuba6000.mobsinfo.nei;

import static com.kuba6000.mobsinfo.MobsInfo.MODNAME;

import net.minecraft.item.Item;

import com.kuba6000.mobsinfo.MobsInfo;
import com.kuba6000.mobsinfo.Tags;
import com.kuba6000.mobsinfo.api.LoaderReference;
import com.kuba6000.mobsinfo.api.MobRecipe;
import com.kuba6000.mobsinfo.config.Config;

import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;
import codechicken.nei.recipe.TemplateRecipeHandler;
import cpw.mods.fml.common.event.FMLInterModComms;

public class NEI_Config implements IConfigureNEI {

    @Override
    public void loadConfig() {
        registerHandler(new MobHandler());
        if (LoaderReference.InfernalMobs.isLoaded) registerHandler(new MobHandlerInfernal());
        registerHandler(new VillagerTradesHandler());
        registerHandler(new MobHandlerFishing());

        if (LoaderReference.EnderIO.isLoaded && Config.Compatibility.addAllEnderIOSpawnersToNEI) {
            for (String s : MobRecipe.MobNameToRecipeMap.keySet()) {
                API.addItemVariant(
                    Item.getItemFromBlock(EnderIOGetter.blockPoweredSpawner()),
                    EnderIOGetter.BlockPoweredSpawner$createItemStackForMob(s));
            }
        }
    }

    private static void registerHandler(TemplateRecipeHandler handler) {
        FMLInterModComms.sendRuntimeMessage(
            MobsInfo.instance,
            "NEIPlugins",
            "register-crafting-handler",
            "MobsInfo@" + handler.getRecipeName() + "@" + handler.getOverlayIdentifier());
        API.registerRecipeHandler(handler);
        API.registerUsageHandler(handler);
    }

    @Override
    public String getName() {
        return MODNAME + " NEI Plugin";
    }

    @Override
    public String getVersion() {
        return Tags.VERSION;
    }
}
