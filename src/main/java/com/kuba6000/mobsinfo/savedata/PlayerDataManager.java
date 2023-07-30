package com.kuba6000.mobsinfo.savedata;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.world.WorldEvent;

import com.kuba6000.mobsinfo.api.utils.ModUtils;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class PlayerDataManager extends WorldSavedData {

    private static final String playerDataName = "mobsinfo_playerdata";

    static PlayerDataManager Instance = null;
    private final HashMap<UUID, PlayerData> players = new HashMap<>();

    private static PlayerData localPlayerData = new PlayerData();

    public static void Initialize(World world) {
        if (Instance != null) {
            Instance.players.clear();
        }
        Instance = (PlayerDataManager) world.mapStorage.loadData(PlayerDataManager.class, playerDataName);
        if (Instance == null) {
            Instance = new PlayerDataManager();
            world.mapStorage.setData(playerDataName, Instance);
        }
        Instance.markDirty();
    }

    @SuppressWarnings("unused")
    public PlayerDataManager(String p_i2141_1_) {
        super(p_i2141_1_);
    }

    public PlayerDataManager() {
        super(playerDataName);
    }

    @Override
    public void readFromNBT(NBTTagCompound NBTData) {
        if (!NBTData.hasKey("players", Constants.NBT.TAG_LIST)) return;
        players.clear();
        NBTTagList list = NBTData.getTagList("players", Constants.NBT.TAG_COMPOUND);
        for (int i = 0, imax = list.tagCount(); i < imax; i++) {
            NBTTagCompound playerNBTData = list.getCompoundTagAt(i);
            if (!playerNBTData.hasKey("uuid")) continue;
            UUID uuid = UUID.fromString(playerNBTData.getString("uuid"));
            PlayerData pData = new PlayerData(playerNBTData.getCompoundTag("data"));
            players.put(uuid, pData);
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound NBTData) {
        NBTTagList list = new NBTTagList();
        for (Map.Entry<UUID, PlayerData> playerDataEntry : players.entrySet()) {
            NBTTagCompound playerNBTData = new NBTTagCompound();
            playerNBTData.setString(
                "uuid",
                playerDataEntry.getKey()
                    .toString());
            playerNBTData.setTag(
                "data",
                playerDataEntry.getValue()
                    .toNBTData());
            list.appendTag(playerNBTData);
        }
        NBTData.setTag("players", list);
    }

    public static PlayerData getPlayer(EntityPlayer player) {
        if (ModUtils.isClientThreaded()) {
            return localPlayerData;
        }
        UUID uuid = player.getPersistentID();
        if (!Instance.players.containsKey(uuid)) {
            PlayerData pData = new PlayerData();
            Instance.players.put(uuid, pData);
            Instance.markDirty();
            return pData;
        }
        return Instance.players.get(uuid);
    }

    @Override
    public void markDirty() {
        super.markDirty();

    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event) {
        if (event.world.isRemote || event.world.provider.dimensionId != 0) return;
        Initialize(event.world);
    }

    @Retention(value = RUNTIME)
    @Target(value = FIELD)
    public @interface SyncToClient {}
}
