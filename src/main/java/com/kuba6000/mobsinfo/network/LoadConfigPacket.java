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

package com.kuba6000.mobsinfo.network;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;

import com.kuba6000.mobsinfo.ClientProxy;
import com.kuba6000.mobsinfo.MobsInfo;
import com.kuba6000.mobsinfo.api.MobOverride;
import com.kuba6000.mobsinfo.config.Config;
import com.kuba6000.mobsinfo.loader.MobRecipeLoader;
import com.kuba6000.mobsinfo.loader.VillagerTradesLoader;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class LoadConfigPacket implements IMessage {

    public static final LoadConfigPacket instance = new LoadConfigPacket();

    public final HashSet<String> mobsToLoad = new HashSet<>();
    public final HashMap<String, MobOverride> mobsOverrides = new HashMap<>();

    public final HashSet<Integer> villagersToLoad = new HashSet<>();

    @Override
    public void fromBytes(ByteBuf buf) {
        if (!buf.readBoolean()) mobsToLoad.clear();
        else {
            Config.Compatibility.enableMobHandlerInfernal = buf.readBoolean();
            Config.MobHandler.hiddenMode = buf.readBoolean();
            mobsToLoad.clear();
            int mobssize = buf.readInt();
            for (int i = 0; i < mobssize; i++) {
                byte[] sbytes = new byte[buf.readInt()];
                buf.readBytes(sbytes);
                mobsToLoad.add(new String(sbytes, StandardCharsets.UTF_8));
            }
            int overridessize = buf.readInt();
            for (int i = 0; i < overridessize; i++) {
                byte[] sbytes = new byte[buf.readInt()];
                buf.readBytes(sbytes);
                mobsOverrides.put(new String(sbytes, StandardCharsets.UTF_8), MobOverride.readFromByteBuf(buf));
            }
        }
        if (!buf.readBoolean()) villagersToLoad.clear();
        else {
            int villagerSize = buf.readInt();
            villagersToLoad.clear();
            for (int i = 0; i < villagerSize; i++) {
                villagersToLoad.add(buf.readInt());
            }
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        if (!Config.MobHandler.mobHandlerEnabled) buf.writeBoolean(false);
        else {
            buf.writeBoolean(true);
            buf.writeBoolean(Config.Compatibility.enableMobHandlerInfernal);
            buf.writeBoolean(Config.MobHandler.hiddenMode);
            buf.writeInt(mobsToLoad.size());
            mobsToLoad.forEach(s -> {
                byte[] sbytes = s.getBytes(StandardCharsets.UTF_8);
                buf.writeInt(sbytes.length);
                buf.writeBytes(sbytes);
            });
            buf.writeInt(mobsOverrides.size());
            mobsOverrides.forEach((k, v) -> {
                byte[] sbytes = k.getBytes(StandardCharsets.UTF_8);
                buf.writeInt(sbytes.length);
                buf.writeBytes(sbytes);
                v.writeToByteBuf(buf);
            });
        }
        if (!Config.VillagerTradesHandler.enabled) buf.writeBoolean(false);
        else {
            buf.writeBoolean(true);
            buf.writeInt(villagersToLoad.size());
            villagersToLoad.forEach(buf::writeInt);
        }
    }

    public static class Handler implements IMessageHandler<LoadConfigPacket, IMessage> {

        @Override
        public IMessage onMessage(LoadConfigPacket message, MessageContext ctx) {
            ClientProxy.mobsToLoad = message.mobsToLoad;
            ClientProxy.mobsOverrides = message.mobsOverrides;
            MobsInfo.info("Received Mobs-Info config, parsing");
            MobRecipeLoader.processMobRecipeMap(message.mobsToLoad, message.mobsOverrides);
            VillagerTradesLoader.processVillagerTrades(message.villagersToLoad);
            return null;
        }
    }
}
