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

package com.kuba6000.mobsinfo.loader;

import static com.kuba6000.mobsinfo.api.MobRecipe.MobNameToRecipeMap;
import static com.kuba6000.mobsinfo.api.utils.ModUtils.isClientSided;
import static com.kuba6000.mobsinfo.api.utils.ModUtils.isDeobfuscatedEnvironment;

import java.io.File;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.function.Supplier;

import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityMagmaCube;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.profiler.Profiler;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.chunk.storage.IChunkLoader;
import net.minecraft.world.storage.IPlayerFileData;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.io.Files;
import com.google.gson.Gson;
import com.kuba6000.mobsinfo.Tags;
import com.kuba6000.mobsinfo.api.LoaderReference;
import com.kuba6000.mobsinfo.api.MobDrop;
import com.kuba6000.mobsinfo.api.MobDropSimplified;
import com.kuba6000.mobsinfo.api.MobOverride;
import com.kuba6000.mobsinfo.api.MobRecipe;
import com.kuba6000.mobsinfo.api.event.PostMobRegistrationEvent;
import com.kuba6000.mobsinfo.api.event.PostMobsRegistrationEvent;
import com.kuba6000.mobsinfo.api.event.PreMobRegistrationEvent;
import com.kuba6000.mobsinfo.api.event.PreMobsRegistrationEvent;
import com.kuba6000.mobsinfo.api.helper.ProgressBarWrapper;
import com.kuba6000.mobsinfo.api.utils.FastRandom;
import com.kuba6000.mobsinfo.api.utils.GSONUtils;
import com.kuba6000.mobsinfo.api.utils.ItemID;
import com.kuba6000.mobsinfo.api.utils.ModUtils;
import com.kuba6000.mobsinfo.config.Config;
import com.kuba6000.mobsinfo.config.OverridesConfig;
import com.kuba6000.mobsinfo.loader.extras.ExtraLoader;
import com.kuba6000.mobsinfo.mixin.InfernalMobs.InfernalMobsCoreAccessor;
import com.kuba6000.mobsinfo.mixin.minecraft.EntityAccessor;
import com.kuba6000.mobsinfo.mixin.minecraft.EntityLivingAccessor;
import com.kuba6000.mobsinfo.mixin.minecraft.EntityLivingBaseAccessor;
import com.kuba6000.mobsinfo.mixin.minecraft.EntitySlimeAccessor;
import com.kuba6000.mobsinfo.nei.MobHandler;
import com.kuba6000.mobsinfo.network.LoadConfigPacket;
import com.mojang.authlib.GameProfile;

import atomicstryker.infernalmobs.common.InfernalMobsCore;
import atomicstryker.infernalmobs.common.mods.api.ModifierLoader;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import thaumcraft.common.items.wands.ItemWandCasting;

public class MobRecipeLoader {

    private static final Logger LOG = LogManager.getLogger(Tags.MODID + "[Mob Recipe Loader]");

    private static final String addRandomArmorName = isDeobfuscatedEnvironment ? "addRandomArmor" : "func_82164_bB";
    private static final String enchantEquipmentName = isDeobfuscatedEnvironment ? "enchantEquipment" : "func_82162_bC";

    private static boolean alreadyGenerated = false;
    public static boolean isInGenerationProcess = false;
    public static final String randomEnchantmentDetectedString = "RandomEnchantmentDetected";

    public static class fakeRand extends Random {

        private static class nexter {

            private final int type;
            private final int bound;
            private int next;

            public nexter(int type, int bound) {
                this.next = 0;
                this.bound = bound;
                this.type = type;
            }

            private int getType() {
                return type;
            }

            private boolean getBoolean() {
                return next == 1;
            }

            private int getInt() {
                return next;
            }

            private float getFloat() {
                return next * 0.1f;
            }

            private boolean next() {
                next++;
                return next >= bound;
            }
        }

        private final ArrayList<nexter> nexts = new ArrayList<>();
        private int walkCounter = 0;
        private double chance;
        private boolean exceptionOnEnchantTry = false;
        private int maxWalkCount = -1;
        private float forceFloatValue = -1.f;

        @Override
        public int nextInt(int bound) {
            if (exceptionOnEnchantTry && bound == Enchantment.enchantmentsBookList.length) return -1;
            if (nexts.size() <= walkCounter) { // new call
                if (maxWalkCount == walkCounter) {
                    return 0;
                }
                nexts.add(new nexter(0, bound));
                walkCounter++;
                chance /= bound;
                return 0;
            }
            chance /= bound;
            return nexts.get(walkCounter++)
                .getInt();
        }

        @Override
        public float nextFloat() {
            if (forceFloatValue != -1f) return forceFloatValue;
            if (nexts.size() <= walkCounter) { // new call
                if (maxWalkCount == walkCounter) {
                    return 0f;
                }
                nexts.add(new nexter(2, 10));
                walkCounter++;
                chance /= 10;
                return 0f;
            }
            chance /= 10;
            return nexts.get(walkCounter++)
                .getFloat();
        }

        @Override
        public boolean nextBoolean() {
            if (nexts.size() <= walkCounter) { // new call
                if (maxWalkCount == walkCounter) {
                    return false;
                }
                nexts.add(new nexter(1, 2));
                walkCounter++;
                chance /= 2;
                return false;
            }
            chance /= 2;
            return nexts.get(walkCounter++)
                .getBoolean();
        }

        public void newRound() {
            walkCounter = 0;
            nexts.clear();
            chance = 1d;
            maxWalkCount = -1;
            exceptionOnEnchantTry = false;
            forceFloatValue = -1f;
        }

        public boolean nextRound() {
            walkCounter = 0;
            chance = 1d;
            while (nexts.size() > 0 && nexts.get(nexts.size() - 1)
                .next()) nexts.remove(nexts.size() - 1);
            return nexts.size() > 0;
        }
    }

    private static class dropinstance {

        public boolean isDamageRandomized = false;
        public HashMap<Integer, Integer> damagesPossible = new HashMap<>();
        public boolean isEnchatmentRandomized = false;
        public int enchantmentLevel = 0;
        public final ItemStack stack;
        public final ItemID itemId;
        private double dropchance = 0d;
        private int dropcount = 1;
        private final droplist owner;

        public dropinstance(ItemStack s, droplist owner) {
            this.owner = owner;
            stack = s;
            itemId = ItemID.createNoCopy(stack);
        }

        public int getchance(int chancemodifier) {
            dropchance = (double) Math.round(dropchance * 100000) / 100000d;
            return (int) (dropchance * chancemodifier);
        }

        @Override
        public int hashCode() {
            return itemId.hashCode();
        }
    }

    public static class droplist {

        private final ArrayList<dropinstance> drops = new ArrayList<>();
        private final HashMap<ItemID, Integer> dropschecker = new HashMap<>();

        public dropinstance add(dropinstance i, double chance) {
            if (contains(i)) {
                int ssize = i.stack.stackSize;
                i = get(dropschecker.get(i.itemId));
                i.dropchance += chance * ssize;
                i.dropcount += ssize;
                return i;
            }
            drops.add(i);
            i.dropchance += chance * i.stack.stackSize;
            i.dropcount += i.stack.stackSize - 1;
            i.stack.stackSize = 1;
            dropschecker.put(i.itemId, drops.size() - 1);
            return i;
        }

        public dropinstance get(int index) {
            return drops.get(index);
        }

        public dropinstance get(dropinstance i) {
            if (!contains(i)) return null;
            return get(dropschecker.get(i.itemId));
        }

        public boolean contains(dropinstance i) {
            return dropschecker.containsKey(i.itemId);
        }

        public boolean contains(ItemStack stack) {
            return dropschecker.containsKey(ItemID.createNoCopy(stack));
        }

        public boolean isEmpty() {
            return drops.isEmpty();
        }

        public int size() {
            return drops.size();
        }

        public int indexOf(dropinstance i) {
            if (!contains(i)) return -1;
            return dropschecker.get(i.itemId);
        }
    }

    private static class dropCollector {

        final HashMap<ItemID, Integer> damagableChecker = new HashMap<>();
        private boolean booksAlwaysRandomlyEnchanted = false;

        public void addDrop(droplist fdrops, ArrayList<EntityItem> listToParse, double chance) {
            for (EntityItem entityItem : listToParse) {
                ItemStack ostack = entityItem.getEntityItem();
                if (ostack == null) continue;
                dropinstance drop;
                boolean randomchomenchantdetected = ostack.hasTagCompound()
                    && ostack.stackTagCompound.hasKey(randomEnchantmentDetectedString);
                int randomenchantmentlevel = 0;
                if (randomchomenchantdetected) {
                    randomenchantmentlevel = ostack.stackTagCompound.getInteger(randomEnchantmentDetectedString);
                    ostack.stackTagCompound.removeTag("ench");
                    ostack.stackTagCompound.setInteger(randomEnchantmentDetectedString, 0);
                }
                if ((booksAlwaysRandomlyEnchanted || randomchomenchantdetected)
                    && Items.enchanted_book == ostack.getItem()) {
                    NBTTagCompound tagCompound = (NBTTagCompound) ostack.stackTagCompound.copy();
                    tagCompound.removeTag("StoredEnchantments");
                    ostack = new ItemStack(Items.book, ostack.stackSize, 0);
                    if (!tagCompound.hasNoTags()) ostack.stackTagCompound = tagCompound;
                    if (randomenchantmentlevel == 0) randomenchantmentlevel = 1;
                    randomchomenchantdetected = true;
                }
                boolean randomdamagedetected = false;
                int newdamage = -1;
                if (ostack.isItemStackDamageable()) {
                    int odamage = ostack.getItemDamage();
                    ostack.setItemDamage(1);
                    ItemID id = ItemID.createNoCopy(ostack);
                    damagableChecker.putIfAbsent(id, odamage);
                    int check = damagableChecker.get(id);
                    if (check != odamage) {
                        randomdamagedetected = true;
                        newdamage = odamage;
                        ostack.setItemDamage(check);
                    } else ostack.setItemDamage(odamage);
                }
                drop = fdrops.add(new dropinstance(ostack.copy(), fdrops), chance);
                if (!drop.isEnchatmentRandomized && randomchomenchantdetected) {
                    drop.isEnchatmentRandomized = true;
                    drop.enchantmentLevel = randomenchantmentlevel;
                }
                if (drop.isDamageRandomized && !randomdamagedetected) {
                    drop.damagesPossible.merge(drop.stack.getItemDamage(), 1, Integer::sum);
                }
                if (randomdamagedetected) {
                    if (!drop.isDamageRandomized) {
                        drop.isDamageRandomized = true;
                        drop.damagesPossible.merge(drop.stack.getItemDamage(), drop.dropcount - 1, Integer::sum);
                    }
                    if (newdamage == -1) newdamage = drop.stack.getItemDamage();
                    drop.damagesPossible.merge(newdamage, 1, Integer::sum);
                }
            }

            listToParse.clear();
        }

        public void newRound() {
            damagableChecker.clear();
            booksAlwaysRandomlyEnchanted = false;
        }
    }

    public static class GeneralMappedMob {

        public final EntityLiving mob;
        public final MobRecipe recipe;
        public final ArrayList<MobDrop> drops;

        public GeneralMappedMob(EntityLiving mob, MobRecipe recipe, ArrayList<MobDrop> drops) {
            this.mob = mob;
            this.recipe = recipe;
            this.drops = drops;
        }

        public ArrayList<MobDrop> copyDrops() {
            ArrayList<MobDrop> copy = new ArrayList<>(drops.size());
            for (MobDrop drop : drops) {
                copy.add(drop.copy());
            }
            return copy;
        }
    }

    public static final HashMap<String, GeneralMappedMob> GeneralMobList = new HashMap<>();

    private static class MobRecipeLoaderCacheStructure {

        String version;
        Map<String, ArrayList<MobDrop>> moblist;
    }

    public static final List<Double> DQRChances = new ArrayList<>();

    @SuppressWarnings({ "unchecked", "UnstableApiUsage" })
    public static void generateMobRecipeMap() {

        if (alreadyGenerated) return;
        alreadyGenerated = true;
        if (!Config.MobHandler.mobHandlerEnabled) return;

        World f = new World(new ISaveHandler() {

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
        }, "DUMMY_DIMENSION", new WorldSettings(new WorldInfo(new NBTTagCompound())), null, new Profiler()) {

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
                    .equals("twilightforest.client.renderer.entity.RenderTFSnowQueenIceShield"))
                    return Blocks.packed_ice;
                if ((aX >= 16) && (aZ >= 16) && (aX < 32) && (aZ < 32)) {
                    return aY == 64 ? Blocks.grass : Blocks.air;
                }
                return Blocks.air;
            }
        };
        f.isRemote = true; // quick hack to get around achievements

        fakeRand frand = new fakeRand();
        f.rand = frand;

        File cache = Config.getConfigFile("MobRecipeLoader.cache");
        Gson gson = GSONUtils.GSON_BUILDER.create();

        String modlistversion;
        if (Config.MobHandler.regenerationTrigger == Config.MobHandler._CacheRegenerationTrigger.ModAdditionRemoval)
            modlistversion = ModUtils.getModListVersionIgnoringModVersions();
        else modlistversion = ModUtils.getModListVersion();

        if (Config.MobHandler.regenerationTrigger != Config.MobHandler._CacheRegenerationTrigger.Always
            && cache.exists()) {
            LOG.info("Parsing Cached map");
            Reader reader = null;
            try {
                reader = Files.newReader(cache, StandardCharsets.UTF_8);
                MobRecipeLoaderCacheStructure s = gson.fromJson(reader, MobRecipeLoaderCacheStructure.class);
                if (Config.MobHandler.regenerationTrigger == Config.MobHandler._CacheRegenerationTrigger.Never
                    || s.version.equals(modlistversion)) {
                    ProgressBarWrapper bar = new ProgressBarWrapper("Parsing cached Mob Recipe Map", s.moblist.size());
                    for (Map.Entry<String, ArrayList<MobDrop>> entry : s.moblist.entrySet()) {
                        bar.step(entry.getKey());
                        try {
                            EntityLiving e;
                            String mobName = entry.getKey();
                            if (mobName.equals("witherSkeleton")
                                && !EntityList.stringToClassMapping.containsKey("witherSkeleton")) {
                                e = new EntitySkeleton(f);
                                ((EntitySkeleton) e).setSkeletonType(1);
                            } else e = (EntityLiving) ((Class<?>) EntityList.stringToClassMapping.get(mobName))
                                .getConstructor(new Class[] { World.class })
                                .newInstance(new Object[] { f });
                            ArrayList<MobDrop> drops = entry.getValue();
                            drops.forEach(MobDrop::reconstructStack);
                            GeneralMobList.put(
                                mobName,
                                new GeneralMappedMob(e, MobRecipe.generateMobRecipe(e, mobName, drops), drops));
                        } catch (Exception ignored) {}
                    }
                    bar.end();
                    LOG.info("Parsed cached map, skipping generation");
                    return;
                } else {
                    LOG.info("Cached map version mismatch, generating a new one");
                }
            } catch (Exception ignored) {
                LOG.warn("There was an exception while parsing cached map, generating a new one");
            } finally {
                if (reader != null) try {
                    reader.close();
                } catch (Exception ignored) {}
            }
        } else {
            LOG.info("Cached map doesn't exist or config option forced, generating a new one");
        }

        isInGenerationProcess = true;

        LOG.info("Generating Recipe Map for Mob Handler");

        long time = System.currentTimeMillis();

        dropCollector collector = new dropCollector();

        // Stupid MC code, I need to cast myself
        Map<String, Class<? extends Entity>> stringToClassMapping = (Map<String, Class<? extends Entity>>) EntityList.stringToClassMapping;
        boolean registeringWitherSkeleton = !stringToClassMapping.containsKey("witherSkeleton");
        if (registeringWitherSkeleton) stringToClassMapping.put("witherSkeleton", EntitySkeleton.class);
        ProgressBarWrapper bar = new ProgressBarWrapper("Generating Mob Recipe Map", stringToClassMapping.size());
        stringToClassMapping.forEach((k, v) -> {
            bar.step(k);
            if (v == null) return;

            if (Modifier.isAbstract(v.getModifiers())) {
                if (Config.Debug.loggingLevel == Config.Debug.LoggingLevel.Detailed)
                    LOG.info("Entity " + k + " is abstract, skipping");
                return;
            }

            EntityLiving e;
            try {
                e = (EntityLiving) v.getConstructor(new Class[] { World.class })
                    .newInstance(new Object[] { f });
            } catch (ClassCastException ex) {
                // not a EntityLiving
                if (Config.Debug.loggingLevel == Config.Debug.LoggingLevel.Detailed)
                    LOG.info("Entity " + k + " is not a LivingEntity, skipping");
                return;
            } catch (NoSuchMethodException ex) {
                // No constructor ?
                if (Config.Debug.loggingLevel == Config.Debug.LoggingLevel.Detailed)
                    LOG.info("Entity " + k + " doesn't have constructor, skipping");
                return;
            } catch (NoClassDefFoundError ex) {
                // Its using classes from Client ? Then it's not important to include
                if (Config.Debug.loggingLevel == Config.Debug.LoggingLevel.Detailed)
                    LOG.info("Entity " + k + " is using undefined classes, skipping");
                return;
            } catch (Throwable ex) {
                ex.printStackTrace();
                return;
            }

            if (registeringWitherSkeleton && e instanceof EntitySkeleton && k.equals("witherSkeleton"))
                ((EntitySkeleton) e).setSkeletonType(1);
            else if (StatCollector.translateToLocal("entity." + k + ".name")
                .equals("entity." + k + ".name")) {
                    LOG.warn("Entity " + k + " does't have localized name!");
                    // return;
                }

            // POWERFULL GENERATION

            try {

                e.captureDrops = true;

                if (e instanceof EntitySlime) {
                    if (v == EntityMagmaCube.class) ((EntitySlimeAccessor) e).callSetSlimeSize(2);
                    else((EntitySlimeAccessor) e).callSetSlimeSize(1);
                }

                ((EntityAccessor) e).setRand(frand);

                droplist drops = new droplist();
                droplist raredrops = new droplist();
                droplist superraredrops = new droplist();
                droplist additionaldrops = new droplist();
                droplist dropslooting = new droplist();

                frand.newRound();
                collector.newRound();

                Runnable checkForWitchery = () -> {
                    if (v.getName()
                        .startsWith("com.emoniph.witchery")) {
                        ((EntityLivingBaseAccessor) e).callDropFewItems(true, 0);
                        frand.newRound();
                        frand.exceptionOnEnchantTry = true;
                        boolean enchantmentDetected = false;
                        try {
                            ((EntityLivingBaseAccessor) e).callDropFewItems(true, 0);
                        } catch (Exception ex) {
                            enchantmentDetected = true;
                        }
                        int w = frand.walkCounter;
                        frand.newRound();
                        if (enchantmentDetected) {
                            frand.maxWalkCount = w;
                            collector.booksAlwaysRandomlyEnchanted = true;
                        }
                        e.capturedDrops.clear();
                    }
                };

                ModUtils.TriConsumer<Supplier<Boolean>, droplist, String> doTheDrop = (callerCanceller, dList,
                    dListName) -> {
                    boolean second = false;
                    do {
                        if (!callerCanceller.get()) break;

                        collector.addDrop(dList, e.capturedDrops, frand.chance);

                        if (second && frand.chance < 0.0000001d) {
                            LOG.warn("Skipping " + k + " " + dListName + " dropmap because it's too randomized");
                            break;
                        }
                        second = true;

                    } while (frand.nextRound());

                    frand.newRound();
                    collector.newRound();
                };

                ModUtils.TriConsumer<Supplier<Boolean>, droplist, String> doTheDQRDrop = (callerCanceller, dList,
                    dListName) -> {
                    if (callerCanceller.get()) {
                        if (e.capturedDrops.size() != DQRChances.size()) {
                            int c = DQRChances.size();
                            DQRChances.clear();
                            throw new RuntimeException(
                                "DQR MOB PARSE EXCEPTION -> captured mobs " + e.capturedDrops.size()
                                    + " / DQRChances "
                                    + c);
                        }

                        for (int i = 0; i < e.capturedDrops.size(); i++) {
                            dList.add(
                                new dropinstance(
                                    e.capturedDrops.get(i)
                                        .getEntityItem(),
                                    dList),
                                DQRChances.get(i));
                        }
                    }

                    frand.newRound();
                    DQRChances.clear();
                    e.capturedDrops.clear();
                };

                if (v.getName()
                    .startsWith("dqr.entity.")
                    && !v.getName()
                        .equals("dqr.entity.mobEntity.monsterTensei.DqmEntitySweetbag")) {
                    doTheDQRDrop.accept(() -> {
                        ((EntityLivingBaseAccessor) e).callDropFewItems(true, 0);
                        return true;
                    }, drops, "normal");

                    doTheDQRDrop.accept(() -> {
                        ((EntityLivingBaseAccessor) e).callDropFewItems(true, 1);
                        return true;
                    }, dropslooting, "normal");

                    doTheDQRDrop.accept(() -> {
                        ((EntityLivingBaseAccessor) e).callDropRareDrop(0);
                        return true;
                    }, raredrops, "rare");

                    doTheDQRDrop.accept(() -> {
                        ((EntityLivingBaseAccessor) e).callDropRareDrop(1);
                        return true;
                    }, superraredrops, "rare");
                } else {
                    checkForWitchery.run();

                    doTheDrop.accept(() -> {
                        ((EntityLivingBaseAccessor) e).callDropFewItems(true, 0);
                        return true;
                    }, drops, "normal");

                    checkForWitchery.run();

                    doTheDrop.accept(() -> {
                        ((EntityLivingBaseAccessor) e).callDropFewItems(true, 1);
                        return true;
                    }, dropslooting, "normal");

                    doTheDrop.accept(() -> {
                        ((EntityLivingBaseAccessor) e).callDropRareDrop(0);
                        return true;
                    }, raredrops, "rare");

                    doTheDrop.accept(() -> {
                        ((EntityLivingBaseAccessor) e).callDropRareDrop(1);
                        return true;
                    }, superraredrops, "rare");

                    if (registeringWitherSkeleton && e instanceof EntitySkeleton && k.equals("witherSkeleton")) {
                        dropinstance i = new dropinstance(new ItemStack(Items.stone_sword), additionaldrops);
                        i.isDamageRandomized = true;
                        int maxdamage = i.stack.getMaxDamage();
                        int max = Math.max(maxdamage - 25, 1);
                        for (int d = Math.min(max, 25); d <= max; d++) i.damagesPossible.put(d, 1);
                        additionaldrops.add(i, 1d);
                    } else try {
                        Class<?> cl = e.getClass();
                        boolean detectedException;
                        do {
                            detectedException = false;
                            try {
                                cl.getDeclaredMethod(addRandomArmorName);
                            } catch (Exception ex) {
                                detectedException = true;
                                cl = cl.getSuperclass();
                            }
                        } while (detectedException && !cl.equals(Entity.class));
                        if (cl.equals(EntityLiving.class) || cl.equals(Entity.class)) throw new Exception();
                        cl = e.getClass();
                        do {
                            detectedException = false;
                            try {
                                cl.getDeclaredMethod(enchantEquipmentName);
                            } catch (Exception ex) {
                                detectedException = true;
                                cl = cl.getSuperclass();
                            }
                        } while (detectedException && !cl.equals(EntityLiving.class));
                        boolean usingVanillaEnchantingMethod = cl.equals(EntityLiving.class);
                        double chanceModifierLocal = 1f;
                        if (v.getName()
                            .startsWith("twilightforest.entity")) {
                            frand.forceFloatValue = 0f;
                            chanceModifierLocal = 0.25f;
                        }
                        boolean second = false;
                        do {
                            ((EntityLivingAccessor) e).callAddRandomArmor();
                            if (!usingVanillaEnchantingMethod) ((EntityLivingAccessor) e).callEnchantEquipment();
                            ItemStack[] lastActiveItems = e.getLastActiveItems();
                            for (int j = 0, lastActiveItemsLength = lastActiveItems.length; j
                                < lastActiveItemsLength; j++) {
                                ItemStack stack = lastActiveItems[j];
                                if (stack != null) {
                                    if (LoaderReference.Thaumcraft)
                                        if (stack.getItem() instanceof ItemWandCasting) continue; // crashes the game
                                                                                                  // when
                                    // rendering in GUI

                                    int randomenchant = -1;
                                    if (stack.hasTagCompound()
                                        && stack.stackTagCompound.hasKey(randomEnchantmentDetectedString)) {
                                        randomenchant = stack.stackTagCompound
                                            .getInteger(randomEnchantmentDetectedString);
                                        stack.stackTagCompound.removeTag("ench");
                                    }
                                    dropinstance i = additionaldrops.add(
                                        new dropinstance(stack.copy(), additionaldrops),
                                        frand.chance * chanceModifierLocal
                                            * (usingVanillaEnchantingMethod ? (j == 0 ? 0.75d : 0.5d) : 1d));
                                    if (!i.isDamageRandomized && i.stack.isItemStackDamageable()) {
                                        i.isDamageRandomized = true;
                                        int maxdamage = i.stack.getMaxDamage();
                                        int max = Math.max(maxdamage - 25, 1);
                                        for (int d = Math.min(max, 25); d <= max; d++) i.damagesPossible.put(d, 1);
                                    }
                                    if (!i.isEnchatmentRandomized && randomenchant != -1) {
                                        i.isEnchatmentRandomized = true;
                                        i.enchantmentLevel = randomenchant;
                                    }
                                    if (usingVanillaEnchantingMethod) {
                                        if (!stack.hasTagCompound()) stack.stackTagCompound = new NBTTagCompound();
                                        stack.stackTagCompound.setInteger(randomEnchantmentDetectedString, 14);
                                        dropinstance newdrop = additionaldrops.add(
                                            new dropinstance(stack.copy(), additionaldrops),
                                            frand.chance * chanceModifierLocal * (j == 0 ? 0.25d : 0.5d));
                                        newdrop.isEnchatmentRandomized = true;
                                        newdrop.enchantmentLevel = 14;
                                        newdrop.isDamageRandomized = i.isDamageRandomized;
                                        newdrop.damagesPossible = (HashMap<Integer, Integer>) i.damagesPossible.clone();
                                    }
                                }
                            }
                            Arrays.fill(e.getLastActiveItems(), null);

                            if (second && frand.chance < 0.0000001d) {
                                LOG.warn("Skipping " + k + " additional dropmap because it's too randomized");
                                break;
                            }
                            second = true;

                        } while (frand.nextRound());
                    } catch (Exception ignored) {}

                    frand.newRound();
                    collector.newRound();
                }

                if (drops.isEmpty() && raredrops.isEmpty() && additionaldrops.isEmpty()) {
                    ArrayList<MobDrop> arr = new ArrayList<>();
                    GeneralMobList.put(k, new GeneralMappedMob(e, MobRecipe.generateMobRecipe(e, k, arr), arr));
                    if (Config.Debug.loggingLevel == Config.Debug.LoggingLevel.Detailed) LOG.info("Mapped " + k);
                    return;
                }

                ArrayList<MobDrop> moboutputs = new ArrayList<>(
                    drops.size() + raredrops.size() + additionaldrops.size());

                for (dropinstance drop : drops.drops) {
                    ItemStack stack = drop.stack;
                    if (stack.hasTagCompound()) stack.stackTagCompound.removeTag(randomEnchantmentDetectedString);
                    int chance = drop.getchance(10000);
                    if (chance > 10000) {
                        int div = (int) Math.ceil(chance / 10000d);
                        stack.stackSize *= div;
                        chance /= div;
                    }
                    dropinstance dlooting = dropslooting.get(drop);
                    moboutputs.add(
                        new MobDrop(
                            stack,
                            MobDrop.DropType.Normal,
                            chance,
                            drop.isEnchatmentRandomized ? drop.enchantmentLevel : null,
                            drop.isDamageRandomized ? drop.damagesPossible : null,
                            dlooting != null && dlooting.dropcount > drop.dropcount,
                            false));
                }
                for (dropinstance drop : raredrops.drops) {
                    ItemStack stack = drop.stack;
                    if (stack.hasTagCompound()) stack.stackTagCompound.removeTag(randomEnchantmentDetectedString);
                    int chance = drop.getchance(250);
                    if (chance > 10000) {
                        int div = (int) Math.ceil(chance / 10000d);
                        stack.stackSize *= div;
                        chance /= div;
                    }
                    moboutputs.add(
                        new MobDrop(
                            stack,
                            MobDrop.DropType.Rare,
                            chance,
                            drop.isEnchatmentRandomized ? drop.enchantmentLevel : null,
                            drop.isDamageRandomized ? drop.damagesPossible : null,
                            false,
                            false));
                }
                for (dropinstance drop : superraredrops.drops) {
                    if (raredrops.contains(drop)) continue;
                    ItemStack stack = drop.stack;
                    if (stack.hasTagCompound()) stack.stackTagCompound.removeTag(randomEnchantmentDetectedString);
                    int chance = drop.getchance(50);
                    if (chance > 10000) {
                        int div = (int) Math.ceil(chance / 10000d);
                        stack.stackSize *= div;
                        chance /= div;
                    }
                    moboutputs.add(
                        new MobDrop(
                            stack,
                            MobDrop.DropType.Rare,
                            chance,
                            drop.isEnchatmentRandomized ? drop.enchantmentLevel : null,
                            drop.isDamageRandomized ? drop.damagesPossible : null,
                            false,
                            false));
                }
                for (dropinstance drop : additionaldrops.drops) {
                    ItemStack stack = drop.stack;
                    if (stack.hasTagCompound()) stack.stackTagCompound.removeTag(randomEnchantmentDetectedString);
                    int chance = drop.getchance(850);
                    if (chance > 10000) {
                        int div = (int) Math.ceil(chance / 10000d);
                        stack.stackSize *= div;
                        chance /= div;
                    }
                    moboutputs.add(
                        new MobDrop(
                            stack,
                            MobDrop.DropType.Additional,
                            chance,
                            drop.isEnchatmentRandomized ? drop.enchantmentLevel : null,
                            drop.isDamageRandomized ? drop.damagesPossible : null,
                            false,
                            false));
                }

                GeneralMobList
                    .put(k, new GeneralMappedMob(e, MobRecipe.generateMobRecipe(e, k, moboutputs), moboutputs));

                if (Config.Debug.loggingLevel == Config.Debug.LoggingLevel.Detailed) LOG.info("Mapped " + k);
            } catch (Exception ex) {
                LOG.error("Something went wrong while generating recipes for " + k + ", stacktrace: ");
                ex.printStackTrace();
            }
        });

        if (registeringWitherSkeleton) stringToClassMapping.remove("witherSkeleton");

        time -= System.currentTimeMillis();
        time = -time;

        LOG.info("Recipe map generated! Mapped " + GeneralMobList.size() + " entities! It took " + time + "ms");

        bar.end();

        isInGenerationProcess = false;

        LOG.info("Saving generated map to file");
        MobRecipeLoaderCacheStructure s = new MobRecipeLoaderCacheStructure();
        s.version = modlistversion;
        s.moblist = new HashMap<>();
        GeneralMobList.forEach((k, v) -> s.moblist.put(k, v.drops));
        Writer writer = null;
        try {
            writer = Files.newWriter(cache, StandardCharsets.UTF_8);
            gson.toJson(s, writer);
            writer.flush();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (writer != null) try {
                writer.close();
            } catch (Exception ignored) {}
        }
    }

    public static droplist getInfernalDrops() {
        InfernalMobsCoreAccessor infernalMobsCore = (InfernalMobsCoreAccessor) InfernalMobsCore.instance();
        droplist infernaldrops = new droplist();
        LOG.info("Generating Infernal drops");
        @SuppressWarnings("unchecked")
        ArrayList<ModifierLoader<?>> modifierLoaders = (ArrayList<ModifierLoader<?>>) infernalMobsCore
            .getModifierLoaders()
            .clone();
        if (!modifierLoaders.isEmpty()) {
            double chance = 1d / infernalMobsCore.getEliteRarity();
            ArrayList<ItemStack> elitelist = infernalMobsCore.getDropIdListElite();
            for (ItemStack stack : elitelist) {
                dropinstance instance = infernaldrops
                    .add(new dropinstance(stack.copy(), infernaldrops), chance / elitelist.size());
                instance.isEnchatmentRandomized = true;
                // noinspection ConstantConditions
                instance.enchantmentLevel = stack.getItem()
                    .getItemEnchantability();
            }
            ArrayList<ItemStack> ultralist = infernalMobsCore.getDropIdListUltra();
            chance *= 1d / infernalMobsCore.getUltraRarity();
            for (ItemStack stack : ultralist) {
                dropinstance instance = infernaldrops
                    .add(new dropinstance(stack.copy(), infernaldrops), chance / ultralist.size());
                instance.isEnchatmentRandomized = true;
                // noinspection ConstantConditions
                instance.enchantmentLevel = stack.getItem()
                    .getItemEnchantability();
            }
            ArrayList<ItemStack> infernallist = infernalMobsCore.getDropIdListInfernal();
            chance *= 1d / infernalMobsCore.getInfernoRarity();
            for (ItemStack stack : infernallist) {
                dropinstance instance = infernaldrops
                    .add(new dropinstance(stack.copy(), infernaldrops), chance / infernallist.size());
                instance.isEnchatmentRandomized = true;
                // noinspection ConstantConditions
                instance.enchantmentLevel = stack.getItem()
                    .getItemEnchantability();
            }
        }
        return infernaldrops;
    }

    @SuppressWarnings("UnstableApiUsage")
    public static File makeCustomDrops() {

        dropCollector collector = new dropCollector();
        Map<String, MobOverride> customDropsMap = new HashMap<>();
        isInGenerationProcess = true;

        for (Map.Entry<String, GeneralMappedMob> mob : GeneralMobList.entrySet()) {
            EntityLiving e = mob.getValue().mob;
            String k = mob.getKey();

            // There is high probability that the hooks are using custom random class
            // so we are just going to do approximation by calling it 10k times :LoL:
            ((EntityAccessor) e).setRand(new FastRandom());
            e.worldObj.isRemote = false;

            FakePlayer fp = new FakePlayer(
                FMLCommonHandler.instance()
                    .getMinecraftServerInstance().worldServers[0],
                new GameProfile(
                    UUID.nameUUIDFromBytes("[MobRecipeLoader]".getBytes(StandardCharsets.UTF_8)),
                    "[MobRecipeLoader]"));

            droplist dropscustom = new droplist();

            try {

                for (int i = 0; i < 10000; i++) {
                    if (ForgeHooks
                        .onLivingDrops(e, DamageSource.causePlayerDamage(fp), e.capturedDrops, 0, true, 100)) {
                        LOG.warn("Event onLivingDrops for " + k + " has been cancelled, I am gonna ignore that!");
                        break;
                    }
                    collector.addDrop(dropscustom, e.capturedDrops, 1.d / 10000.d);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            e.worldObj.isRemote = true;

            collector.newRound();

            if (!dropscustom.isEmpty()) {
                MobOverride override = new MobOverride();
                for (dropinstance drop : dropscustom.drops) {
                    ItemStack stack = drop.stack;
                    if (stack.hasTagCompound()) stack.stackTagCompound.removeTag(randomEnchantmentDetectedString);
                    int chance = drop.getchance(10000);
                    if (chance > 10000) {
                        int div = (int) Math.ceil(chance / 10000d);
                        stack.stackSize *= div;
                        chance /= div;
                    }
                    override.additions.add(
                        new MobDrop(
                            drop.stack,
                            MobDrop.DropType.Normal,
                            chance,
                            drop.isEnchatmentRandomized ? drop.enchantmentLevel : null,
                            drop.isDamageRandomized ? drop.damagesPossible : null,
                            false,
                            false));
                    customDropsMap.put(k, override);
                }
            }

        }

        Writer writer = null;
        Gson gson = GSONUtils.GSON_BUILDER_PRETTY.create();
        File f = new File("CustomDropsMap.json");
        try {
            writer = Files.newWriter(f, StandardCharsets.UTF_8);
            gson.toJson(customDropsMap, writer);
            writer.flush();
        } catch (Exception ex) {
            ex.printStackTrace();
            f = null;
        } finally {
            if (writer != null) try {
                writer.close();
            } catch (Exception ignored) {}
        }

        isInGenerationProcess = false;

        return f;
    }

    public static void processMobRecipeMap() {
        LOG.info("Loading config");

        Config.synchronizeConfiguration();
        OverridesConfig.LoadConfig();

        if (isClientSided) MobHandler.clearRecipes();
        MobNameToRecipeMap.clear();
        LoadConfigPacket.instance.mobsToLoad.clear();
        LoadConfigPacket.instance.mobsOverrides.clear();

        MinecraftForge.EVENT_BUS.post(new PreMobsRegistrationEvent());

        for (Map.Entry<String, GeneralMappedMob> entry : GeneralMobList.entrySet()) {
            String k = entry.getKey();
            GeneralMappedMob v = entry.getValue();
            if (Arrays.asList(Config.MobHandler.mobBlacklist)
                .contains(k)) {
                if (Config.Debug.loggingLevel == Config.Debug.LoggingLevel.Detailed)
                    LOG.info("Entity " + k + " is blacklisted, skipping");
                continue;
            }

            MobRecipe recipe = v.recipe;
            recipe = recipe.copy();
            ArrayList<MobDrop> drops = v.copyDrops();

            if (MinecraftForge.EVENT_BUS.post(new PreMobRegistrationEvent(k, drops, recipe))) continue;

            ExtraLoader.process(k, drops, recipe);

            MobOverride override;
            if ((override = OverridesConfig.overrides.get(k)) != null) {
                if (override.removeAll) {
                    drops.clear();
                } else for (MobDropSimplified removal : override.removals) {
                    drops.removeIf(removal::isMatching);
                }
                drops.addAll(override.additions);
                LoadConfigPacket.instance.mobsOverrides.put(k, override);
            }

            MinecraftForge.EVENT_BUS.post(new PostMobRegistrationEvent(k, drops, recipe));

            recipe.mOutputs.clear();
            recipe.mOutputs.addAll(drops);

            recipe.refresh();

            if (drops.isEmpty()) {
                if (Config.Debug.loggingLevel == Config.Debug.LoggingLevel.Detailed)
                    LOG.info("Entity " + k + " doesn't drop any items");
                if (!Config.MobHandler.includeEmptyMobs) continue;
            }
            MobNameToRecipeMap.put(k, recipe);
            LoadConfigPacket.instance.mobsToLoad.add(k);
            if (Config.Debug.loggingLevel == Config.Debug.LoggingLevel.Detailed) LOG.info("Registered " + k);
        }

        LOG.info("Registered " + MobNameToRecipeMap.size() + " entities!");

        MinecraftForge.EVENT_BUS.post(new PostMobsRegistrationEvent());
    }

    @SideOnly(Side.CLIENT)
    public static void processMobRecipeMap(HashSet<String> mobs, HashMap<String, MobOverride> overrides) {
        MobHandler.clearRecipes();
        MobNameToRecipeMap.clear();

        MinecraftForge.EVENT_BUS.post(new PreMobsRegistrationEvent());
        mobs.forEach(k -> {
            GeneralMappedMob v = GeneralMobList.get(k);

            MobRecipe recipe = v.recipe;
            recipe = recipe.copy();
            ArrayList<MobDrop> drops = v.copyDrops();

            if (MinecraftForge.EVENT_BUS.post(new PreMobRegistrationEvent(k, drops, recipe))) return;

            ExtraLoader.process(k, drops, recipe);

            recipe.mOutputs.clear();
            recipe.mOutputs.addAll(drops);

            MobOverride override;
            if ((override = overrides.get(k)) != null) {
                if (override.removeAll) {
                    drops.clear();
                    recipe.mOutputs.clear();
                } else for (MobDropSimplified removal : override.removals) {
                    drops.removeIf(removal::isMatching);
                    recipe.mOutputs.removeIf(removal::isMatching);
                }
                drops.addAll(override.additions);
                recipe.mOutputs.addAll(override.additions);
            }

            MinecraftForge.EVENT_BUS.post(new PostMobRegistrationEvent(k, drops, recipe));

            drops.sort(Comparator.comparing(d -> d.type)); // Fix GUI
            recipe.refresh();

            MobHandler.addRecipe(v.mob, drops);
            MobNameToRecipeMap.put(k, recipe);
            if (Config.Debug.loggingLevel == Config.Debug.LoggingLevel.Detailed) LOG.info("Registered " + k);
        });
        MinecraftForge.EVENT_BUS.post(new PostMobsRegistrationEvent());
        LOG.info("Registered " + MobNameToRecipeMap.size() + " entities!");
        LOG.info("Sorting NEI map");
        MobHandler.sortCachedRecipes();
    }
}
