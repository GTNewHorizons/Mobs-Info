package com.kuba6000.mobsinfo.savedata;

import java.util.HashSet;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraftforge.common.util.Constants;

import com.kuba6000.mobsinfo.api.utils.ModUtils;

public class PlayerData {

    public HashSet<String> killedMobs = new HashSet<>();

    PlayerData() {}

    PlayerData(NBTTagCompound NBTData) {
        loadFromNBT(NBTData);
    }

    public void loadFromNBT(NBTTagCompound NBTData) {
        killedMobs.clear();
        if (NBTData.hasKey("killedMobs", Constants.NBT.TAG_LIST)) {
            NBTTagList list = NBTData.getTagList("killedMobs", Constants.NBT.TAG_STRING);
            for (int i = 0; i < list.tagCount(); ++i) {
                killedMobs.add(list.getStringTagAt(i));
            }
        }
    }

    public NBTTagCompound toNBTData() {
        NBTTagCompound NBTData = new NBTTagCompound();

        NBTTagList list = new NBTTagList();
        for (String killedMob : killedMobs) {
            list.appendTag(new NBTTagString(killedMob));
        }
        NBTData.setTag("killedMobs", list);

        return NBTData;
    }

    public void markDirty() {
        if (ModUtils.isClientThreaded()) return;
        PlayerDataManager.Instance.markDirty();
    }

}
