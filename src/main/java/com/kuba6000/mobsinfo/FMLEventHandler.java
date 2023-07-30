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

import net.minecraft.entity.player.EntityPlayerMP;

import com.kuba6000.mobsinfo.network.LoadConfigPacket;
import com.kuba6000.mobsinfo.network.SaveDataPacket;
import com.kuba6000.mobsinfo.savedata.PlayerDataManager;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;

public class FMLEventHandler {

    // Gets fired only server-sided
    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.player instanceof EntityPlayerMP)) return;
        MobsInfo.info("Sending config to " + event.player.getDisplayName());
        MobsInfo.NETWORK.sendTo(LoadConfigPacket.instance, (EntityPlayerMP) event.player);
        MobsInfo.NETWORK
            .sendTo(new SaveDataPacket(PlayerDataManager.getPlayer(event.player)), (EntityPlayerMP) event.player);
    }
}
