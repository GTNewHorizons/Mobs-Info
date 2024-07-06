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

import static com.kuba6000.mobsinfo.MobsInfo.MODID;
import static com.kuba6000.mobsinfo.MobsInfo.MODNAME;

import net.minecraft.nbt.NBTTagCompound;

import com.kuba6000.mobsinfo.api.LoaderReference;
import com.kuba6000.mobsinfo.nei.scrollable.ScrollableContainerHandler;

import codechicken.nei.guihook.GuiContainerManager;
import cpw.mods.fml.common.event.FMLInterModComms;

public class IMCForNEI {

    public static void IMCSender() {

        sendHandler("mobsinfo.mobhandler", "minecraft:diamond_sword", 168, 192, 1, 6);
        sendCatalyst("mobsinfo.mobhandler", "minecraft:diamond_sword");
        if (LoaderReference.InfernalMobs) {
            sendHandler("mobsinfo.mobhandlerinfernal", "minecraft:diamond_sword", 168, 192, 1, 6);
            sendCatalyst("mobsinfo.mobhandlerinfernal", "minecraft:diamond_sword");
        }
        sendHandler("mobsinfo.villagertradeshandler", "minecraft:emerald", 168, 192, 1, 6);
        sendCatalyst("mobsinfo.villagertradeshandler", "minecraft:emerald");
        sendCatalyst("mobsinfo.villagertradeshandler", "minecraft:spawn_egg:120");

        GuiContainerManager.addObjectHandler(new ScrollableContainerHandler());
    }

    private static void sendHandler(String aName, String aBlock, int width, int height, int maxrecipesperpage,
        int yshift) {
        NBTTagCompound aNBT = new NBTTagCompound();
        aNBT.setString("handler", aName);
        aNBT.setString("modName", MODNAME);
        aNBT.setString("modId", MODID);
        aNBT.setBoolean("modRequired", true);
        aNBT.setString("itemName", aBlock);
        aNBT.setInteger("handlerHeight", height);
        aNBT.setInteger("handlerWidth", width);
        aNBT.setInteger("maxRecipesPerPage", maxrecipesperpage);
        aNBT.setInteger("yShift", yshift);
        FMLInterModComms.sendMessage("NotEnoughItems", "registerHandlerInfo", aNBT);
    }

    private static void sendCatalyst(String aName, String aStack, int aPriority) {
        NBTTagCompound aNBT = new NBTTagCompound();
        aNBT.setString("handlerID", aName);
        aNBT.setString("itemName", aStack);
        aNBT.setInteger("priority", aPriority);
        FMLInterModComms.sendMessage("NotEnoughItems", "registerCatalystInfo", aNBT);
    }

    private static void sendCatalyst(String aName, String aStack) {
        sendCatalyst(aName, aStack, 0);
    }
}
