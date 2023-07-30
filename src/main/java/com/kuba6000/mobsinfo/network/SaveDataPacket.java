package com.kuba6000.mobsinfo.network;

import java.io.IOException;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;

import com.kuba6000.mobsinfo.MobsInfo;
import com.kuba6000.mobsinfo.nei.MobHandler;
import com.kuba6000.mobsinfo.savedata.PlayerData;
import com.kuba6000.mobsinfo.savedata.PlayerDataManager;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class SaveDataPacket implements IMessage {

    NBTTagCompound tag;

    @SuppressWarnings("unused")
    public SaveDataPacket() {}

    public SaveDataPacket(PlayerData data) {
        tag = data.toNBTData();
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        PacketBuffer packetBuffer = new PacketBuffer(buf);
        try {
            tag = packetBuffer.readNBTTagCompoundFromBuffer();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        PacketBuffer packetBuffer = new PacketBuffer(buf);
        try {
            packetBuffer.writeNBTTagCompoundToBuffer(tag);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static class Handler implements IMessageHandler<SaveDataPacket, IMessage> {

        @Override
        public IMessage onMessage(SaveDataPacket message, MessageContext ctx) {
            MobsInfo.info("Received Mob Handler config, parsing");
            PlayerData localData = PlayerDataManager.getPlayer(null);
            localData.loadFromNBT(message.tag);
            MobHandler.sortCachedRecipes();
            return null;
        }
    }
}
