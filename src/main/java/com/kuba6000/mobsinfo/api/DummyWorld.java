package com.kuba6000.mobsinfo.api;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.profiler.Profiler;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.chunk.storage.IChunkLoader;
import net.minecraft.world.storage.IPlayerFileData;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;

public class DummyWorld extends World {

    public DummyWorld() {
        super(new ISaveHandler() {

            @Override
            public void saveWorldInfoWithPlayer(WorldInfo worldInfo, NBTTagCompound nbtTagCompound) {}

            @Override
            public void saveWorldInfo(WorldInfo worldInfo) {}

            @Override
            public WorldInfo loadWorldInfo() {
                return null;
            }

            @Override
            public IPlayerFileData getSaveHandler() {
                return null;
            }

            @Override
            public File getMapFileFromName(String mapName) {
                return null;
            }

            @Override
            public IChunkLoader getChunkLoader(WorldProvider worldProvider) {
                return null;
            }

            @Override
            public void flush() {}

            @Override
            public void checkSessionLock() {}

            @Override
            public String getWorldDirectoryName() {
                return null;
            }

            @Override
            public File getWorldDirectory() {
                return null;
            }

            @SuppressWarnings("unused") // for thermos compat
            public UUID getUUID() {
                return UUID.nameUUIDFromBytes("MobsInfoDummyWorld".getBytes(StandardCharsets.UTF_8));
            }
        }, "DUMMY_DIMENSION", new WorldSettings(new WorldInfo(new NBTTagCompound())), null, new Profiler());
    }

    @Override
    protected IChunkProvider createChunkProvider() {
        return null;
    }

    @Override
    public Entity getEntityByID(int aEntityID) {
        return null;
    }

    @Override
    public boolean setBlock(int aX, int aY, int aZ, Block aBlock, int aMeta, int aFlags) {
        return true;
    }

    @Override
    public float getSunBrightnessFactor(float p_72967_1_) {
        return 1.0F;
    }

    @Override
    public BiomeGenBase getBiomeGenForCoords(int aX, int aZ) {
        if ((aX >= 16) && (aZ >= 16) && (aX < 32) && (aZ < 32)) {
            return BiomeGenBase.plains;
        }
        return BiomeGenBase.ocean;
    }

    @Override
    public int getFullBlockLightValue(int aX, int aY, int aZ) {
        return 10;
    }

    @Override
    public int getBlockMetadata(int aX, int aY, int aZ) {
        return 0;
    }

    @Override
    public boolean canBlockSeeTheSky(int aX, int aY, int aZ) {
        if ((aX >= 16) && (aZ >= 16) && (aX < 32) && (aZ < 32)) {
            return aY > 64;
        }
        return true;
    }

    @Override
    protected int func_152379_p() {
        return 0;
    }

    @Override
    public boolean blockExists(int p_72899_1_, int p_72899_2_, int p_72899_3_) {
        return false;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public List getEntitiesWithinAABB(Class p_72872_1_, AxisAlignedBB p_72872_2_) {
        return new ArrayList();
    }

    @Override
    public Block getBlock(int aX, int aY, int aZ) {
        if (LoaderReference.TwilightForest && new Throwable().getStackTrace()[1].getClassName()
            .equals("twilightforest.client.renderer.entity.RenderTFSnowQueenIceShield")) return Blocks.packed_ice;
        if ((aX >= 16) && (aZ >= 16) && (aX < 32) && (aZ < 32)) {
            return aY == 64 ? Blocks.grass : Blocks.air;
        }
        return Blocks.air;
    }

    @Override
    public Explosion newExplosion(Entity p_72885_1_, double p_72885_2_, double p_72885_4_, double p_72885_6_,
        float p_72885_8_, boolean p_72885_9_, boolean p_72885_10_) {
        return null;
    }

    @Override
    public boolean spawnEntityInWorld(Entity p_72838_1_) {
        return false;
    }
}
