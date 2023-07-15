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

package com.kuba6000.mobsinfo.nei;

import net.minecraft.item.Item;

import com.kuba6000.mobsinfo.Tags;
import com.kuba6000.mobsinfo.api.LoaderReference;
import com.kuba6000.mobsinfo.api.MobRecipe;
import com.kuba6000.mobsinfo.config.Config;

import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;

public class NEI_Config implements IConfigureNEI {

    public static boolean isAdded = true;

    @Override
    public void loadConfig() {
        isAdded = false;
        new MobHandler();
        if (LoaderReference.InfernalMobs) new MobHandlerInfernal();
        isAdded = true;

        if (LoaderReference.EnderIO && Config.Compatibility.addAllEnderIOSpawnersToNEI) {
            for (String s : MobRecipe.MobNameToRecipeMap.keySet()) {
                API.addItemVariant(
                    Item.getItemFromBlock(EnderIOGetter.blockPoweredSpawner()),
                    EnderIOGetter.BlockPoweredSpawner$createItemStackForMob(s));
            }
        }
    }

    @Override
    public String getName() {
        return Tags.MODNAME + " NEI Plugin";
    }

    @Override
    public String getVersion() {
        return Tags.VERSION;
    }
}
