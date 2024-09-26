/*
 * spotless:off
 * MobsInfo - Minecraft addon
 * Copyright (C) 2023-2024  kuba6000
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

import static com.kuba6000.mobsinfo.MobsInfo.MODID;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityMagmaCube;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.io.Files;
import com.google.gson.Gson;
import com.kuba6000.mobsinfo.api.DummyWorld;
import com.kuba6000.mobsinfo.api.IChanceModifier;
import com.kuba6000.mobsinfo.api.IMobInfoProvider;
import com.kuba6000.mobsinfo.api.LoaderReference;
import com.kuba6000.mobsinfo.api.MobDrop;
import com.kuba6000.mobsinfo.api.MobDropSimplified;
import com.kuba6000.mobsinfo.api.MobOverride;
import com.kuba6000.mobsinfo.api.MobRecipe;
import com.kuba6000.mobsinfo.api.RandomSequencer;
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
import com.kuba6000.mobsinfo.mixin.early.minecraft.EntityAccessor;
import com.kuba6000.mobsinfo.mixin.early.minecraft.EntityLivingAccessor;
import com.kuba6000.mobsinfo.mixin.early.minecraft.EntityLivingBaseAccessor;
import com.kuba6000.mobsinfo.mixin.early.minecraft.EntitySlimeAccessor;
import com.kuba6000.mobsinfo.mixin.late.InfernalMobs.InfernalMobsCoreAccessor;
import com.kuba6000.mobsinfo.nei.MobHandler;
import com.kuba6000.mobsinfo.network.LoadConfigPacket;
import com.mojang.authlib.GameProfile;

import atomicstryker.infernalmobs.common.InfernalMobsCore;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import thaumcraft.common.items.wands.ItemWandCasting;

public class MobRecipeLoader {

    private static final Logger LOG = LogManager.getLogger(MODID + "[Mob Recipe Loader]");

    private static final String addRandomArmorName = isDeobfuscatedEnvironment ? "addRandomArmor" : "func_82164_bB";
    private static final String enchantEquipmentName = isDeobfuscatedEnvironment ? "enchantEquipment" : "func_82162_bC";

    private static boolean alreadyGenerated = false;
    public static boolean isInGenerationProcess = false;
    public static final String randomEnchantmentDetectedString = "RandomEnchantmentDetected";
    // This is in nanoseconds
    private static final long GET_DROPS_TIMEOUT = (long) (Config.MobHandler.mobTimeout * 1e9);

    public static class dropinstance {

        public boolean isDamageRandomized = false;
        public HashMap<Integer, Integer> damagesPossible = new HashMap<>();
        public boolean isEnchatmentRandomized = false;
        public int enchantmentLevel = 0;
        public final ItemStack stack;
        public final ItemID itemId;
        private double dropchance = 0d;
        private int dropcount = 1;

        public dropinstance(ItemStack s, droplist owner) {
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
        public final boolean isProvidedFromAPI;

        public GeneralMappedMob(EntityLiving mob, MobRecipe recipe, ArrayList<MobDrop> drops) {
            this.mob = mob;
            this.recipe = recipe;
            this.drops = drops;
            this.isProvidedFromAPI = false;
        }

        public GeneralMappedMob(EntityLiving mob, MobRecipe recipe, ArrayList<MobDrop> drops,
            boolean isProvidedFromAPI) {
            this.mob = mob;
            this.recipe = recipe;
            this.drops = drops;
            this.isProvidedFromAPI = isProvidedFromAPI;
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
    private static EntityLiving currentEntity = null;

    public static void preGenerationEntityModifiers(EntityLiving entity, String mobName) {
        if (entity instanceof EntitySkeleton && mobName.equals("witherSkeleton")) {
            ((EntitySkeleton) entity).setSkeletonType(1);
        } else if (entity instanceof EntitySlime) {
            // noinspection ConstantValue
            ((EntitySlimeAccessor) entity).callSetSlimeSize(entity.getClass() == EntityMagmaCube.class ? 2 : 1);
        } else if (entity instanceof EntityBat) {
            ((EntityBat) entity).setIsBatHanging(false);
        }
    }

    @SuppressWarnings({ "unchecked", "UnstableApiUsage" })
    public static void generateMobRecipeMap() {

        if (alreadyGenerated) return;
        alreadyGenerated = true;
        if (!Config.MobHandler.mobHandlerEnabled) return;
        VanillaMobRecipeLoader.init();

        World f = new DummyWorld() {

            @Override
            public boolean spawnEntityInWorld(Entity p_72838_1_) {
                if (p_72838_1_ instanceof EntityItem) {
                    if (isInGenerationProcess && currentEntity != null) {
                        currentEntity.entityDropItem(((EntityItem) p_72838_1_).getEntityItem(), 0F);
                        return true;
                    }
                }
                return false;
            }
        };
        f.isRemote = true; // quick hack to get around achievements

        RandomSequencer frand = new RandomSequencer();
        f.rand = frand;

        File cache = Config.getConfigFile("MobRecipeLoader.cache");
        Gson gson = GSONUtils.GSON_BUILDER.create();

        String modlistversion;
        if (Config.MobHandler.regenerationTrigger == Config.MobHandler._CacheRegenerationTrigger.ModAdditionRemoval)
            modlistversion = ModUtils.getModListVersionForMobDrops(false);
        else modlistversion = ModUtils.getModListVersionForMobDrops(true);

        isInGenerationProcess = true;

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
                        String mobName = entry.getKey();
                        bar.step(mobName);
                        GeneralMappedMob vanillaMob = VanillaMobRecipeLoader.vanillaMobList.get(mobName);
                        if (vanillaMob != null
                            && (vanillaMob.mob.getClass() == EntityList.stringToClassMapping.get(mobName)
                                || (mobName.equals("witherSkeleton")
                                    && !EntityList.stringToClassMapping.containsKey("witherSkeleton")))) {
                            GeneralMobList.put(mobName, vanillaMob);
                            continue;
                        }
                        try {
                            EntityLiving e;
                            if (mobName.equals("witherSkeleton")
                                && !EntityList.stringToClassMapping.containsKey("witherSkeleton")) {
                                e = new EntitySkeleton(f);
                            } else e = (EntityLiving) ((Class<?>) EntityList.stringToClassMapping.get(mobName))
                                .getConstructor(new Class[] { World.class })
                                .newInstance(new Object[] { f });
                            preGenerationEntityModifiers(e, mobName);
                            ArrayList<MobDrop> drops = entry.getValue();
                            if (drops == null) {
                                if (e instanceof IMobInfoProvider provider) {
                                    drops = new ArrayList<>();
                                    provider.provideDropsInformation(drops);
                                    for (MobDrop drop : drops) {
                                        drop.clampChance();
                                    }
                                    GeneralMobList.put(
                                        mobName,
                                        new GeneralMappedMob(
                                            e,
                                            MobRecipe.generateMobRecipe(e, mobName, drops),
                                            drops,
                                            true));
                                }
                            } else {
                                drops.forEach(MobDrop::reconstructStack);
                                GeneralMobList.put(
                                    mobName,
                                    new GeneralMappedMob(e, MobRecipe.generateMobRecipe(e, mobName, drops), drops));
                            }
                        } catch (Exception ignored) {}
                    }
                    bar.end();
                    LOG.info("Parsed cached map, skipping generation");
                    isInGenerationProcess = false;
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

        LOG.info("Generating Recipe Map for Mob Handler");

        long time = System.currentTimeMillis();

        dropCollector collector = new dropCollector();

        boolean registeringWitherSkeleton = !EntityList.stringToClassMapping.containsKey("witherSkeleton");
        if (registeringWitherSkeleton) EntityList.stringToClassMapping.put("witherSkeleton", EntitySkeleton.class);
        ProgressBarWrapper bar = new ProgressBarWrapper(
            "Generating Mob Recipe Map",
            EntityList.stringToClassMapping.size());
        EntityList.stringToClassMapping.forEach((name, entity) -> {
            bar.step(name);
            if (entity == null) return;

            GeneralMappedMob vanillaMob = VanillaMobRecipeLoader.vanillaMobList.get(name);
            if (vanillaMob != null && vanillaMob.mob.getClass() == entity) {
                GeneralMobList.put(name, vanillaMob);
                return;
            }

            if (Modifier.isAbstract(entity.getModifiers())) {
                if (Config.Debug.loggingLevel == Config.Debug.LoggingLevel.Detailed)
                    LOG.info("Entity " + name + " is abstract, skipping");
                return;
            }

            if (!EntityLiving.class.isAssignableFrom(entity)) {
                // not a EntityLiving
                if (Config.Debug.loggingLevel == Config.Debug.LoggingLevel.Detailed)
                    LOG.info("Entity " + name + " is not a LivingEntity, skipping");
                return;
            }

            EntityLiving e;
            try {
                e = (EntityLiving) entity.getConstructor(new Class[] { World.class })
                    .newInstance(new Object[] { f });
            } catch (NoSuchMethodException ex) {
                // No constructor ?
                if (Config.Debug.loggingLevel == Config.Debug.LoggingLevel.Detailed)
                    LOG.info("Entity " + name + " doesn't have constructor, skipping");
                return;
            } catch (NoClassDefFoundError ex) {
                // Its using classes from Client ? Then it's not important to include
                if (Config.Debug.loggingLevel == Config.Debug.LoggingLevel.Detailed)
                    LOG.info("Entity " + name + " is using undefined classes, skipping");
                return;
            } catch (Throwable ex) {
                ex.printStackTrace();
                return;
            }

            if (!(registeringWitherSkeleton && name.equals("witherSkeleton")) && e.getCommandSenderName()
                .startsWith("entity.")) {
                LOG.warn("Entity " + name + " doesn't have localized name!");
                // return;
            }

            // POWERFULL GENERATION

            try {

                e.captureDrops = true;

                preGenerationEntityModifiers(e, name);

                ((EntityAccessor) e).setRand(frand);

                if (e instanceof IMobInfoProvider provider) {
                    boolean illegalOverride = false;
                    Class<?> clazz = entity;
                    do {
                        try {
                            clazz.getDeclaredMethod("provideDropsInformation", ArrayList.class);
                            break;
                        } catch (Exception exception) {
                            try {
                                clazz.getDeclaredMethod("dropFewItems", boolean.class, int.class);
                                illegalOverride = true;
                                break;
                            } catch (Exception ignored) {}
                            try {
                                clazz.getDeclaredMethod("dropRareDrop", int.class);
                                illegalOverride = true;
                                break;
                            } catch (Exception ignored) {}
                            clazz = clazz.getSuperclass();
                        }
                    } while (true);
                    if (illegalOverride) {
                        LOG.error(
                            "Entity {} is implementing IMobInfoProvider interface in its superclass and doesn't override provideDropsInformation WHILE also overriding dropFewItems or dropRareItems! IGNORING this provider and using normal generation!!!",
                            name);
                    } else {
                        ArrayList<MobDrop> moboutputs = new ArrayList<>();
                        provider.provideDropsInformation(moboutputs);
                        for (MobDrop moboutput : moboutputs) {
                            moboutput.clampChance();
                        }
                        GeneralMobList.put(
                            name,
                            new GeneralMappedMob(
                                e,
                                MobRecipe.generateMobRecipe(e, name, moboutputs),
                                moboutputs,
                                true));
                        return;
                    }
                }

                droplist drops = new droplist();
                droplist raredrops = new droplist();
                droplist superraredrops = new droplist();
                droplist additionaldrops = new droplist();
                droplist dropslooting = new droplist();

                frand.newRound();
                collector.newRound();

                // If the mob is from Witchery, handle it
                Runnable checkForWitchery = () -> {
                    if (entity.getName()
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
                    final long start = System.nanoTime();
                    do {
                        if (!callerCanceller.get()) break;

                        collector.addDrop(dList, e.capturedDrops, frand.chance);

                        if (second && frand.chance < 0.0000001d) {
                            LOG.warn("Skipping " + name + " " + dListName + " dropmap because it's too randomized");
                            break;
                        }

                        if (second && System.nanoTime() - start >= GET_DROPS_TIMEOUT) {
                            LOG.warn("{} {} dropmap took too long, skipping", name, dListName);
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

                currentEntity = e;

                if (entity.getName()
                    .startsWith("dqr.entity.")
                    && !entity.getName()
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

                    if (registeringWitherSkeleton && e instanceof EntitySkeleton && name.equals("witherSkeleton")) {
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
                        if (entity.getName()
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
                                    if (LoaderReference.Thaumcraft.isLoaded)
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
                                LOG.warn("Skipping " + name + " additional dropmap because it's too randomized");
                                break;
                            }
                            second = true;

                        } while (frand.nextRound());
                    } catch (Exception ignored) {}

                    frand.newRound();
                    collector.newRound();
                }

                currentEntity = null;

                if (drops.isEmpty() && raredrops.isEmpty() && additionaldrops.isEmpty()) {
                    ArrayList<MobDrop> arr = new ArrayList<>();
                    GeneralMobList.put(name, new GeneralMappedMob(e, MobRecipe.generateMobRecipe(e, name, arr), arr));
                    if (Config.Debug.loggingLevel == Config.Debug.LoggingLevel.Detailed) LOG.info("Mapped " + name);
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
                    .put(name, new GeneralMappedMob(e, MobRecipe.generateMobRecipe(e, name, moboutputs), moboutputs));

                if (Config.Debug.loggingLevel == Config.Debug.LoggingLevel.Detailed) LOG.info("Mapped " + name);
            } catch (Throwable ex) {
                currentEntity = null;
                LOG.error("Something went wrong while generating recipes for " + name + ", stacktrace: ");
                ex.printStackTrace();
            }
        });

        if (registeringWitherSkeleton) EntityList.stringToClassMapping.remove("witherSkeleton");

        time -= System.currentTimeMillis();
        time = -time;

        LOG.info("Recipe map generated! Mapped " + GeneralMobList.size() + " entities! It took " + time + "ms");

        bar.end();

        isInGenerationProcess = false;

        LOG.info("Saving generated map to file");
        MobRecipeLoaderCacheStructure s = new MobRecipeLoaderCacheStructure();
        s.version = modlistversion;
        s.moblist = new HashMap<>();
        GeneralMobList.forEach((k, v) -> s.moblist.put(k, v.isProvidedFromAPI ? null : v.drops));
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
                    LOG.info("Entity {} is blacklisted, skipping", k);
                continue;
            }

            MobRecipe recipe = v.recipe;
            recipe = recipe.copy();
            ArrayList<MobDrop> drops = v.copyDrops();

            recipe.generateSpawnList();

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

            for (MobDrop drop : drops) {
                drop.chanceModifiers
                    .sort(Collections.reverseOrder(Comparator.comparingInt(IChanceModifier::getPriority)));
            }

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

            // TODO: sync with server
            recipe.generateSpawnList();

            if (MinecraftForge.EVENT_BUS.post(new PreMobRegistrationEvent(k, drops, recipe))) return;

            // TODO: sync with server
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

            for (MobDrop drop : drops) {
                drop.chanceModifiers
                    .sort(Collections.reverseOrder(Comparator.comparingInt(IChanceModifier::getPriority)));
            }

            drops.sort(Comparator.comparing(d -> d.type)); // Fix GUI
            recipe.refresh();

            MobNameToRecipeMap.put(k, recipe);
            MobHandler.addRecipe(v.mob, drops);
            if (Config.Debug.loggingLevel == Config.Debug.LoggingLevel.Detailed) LOG.info("Registered " + k);
        });
        MinecraftForge.EVENT_BUS.post(new PostMobsRegistrationEvent());
        LOG.info("Registered " + MobNameToRecipeMap.size() + " entities!");
        LOG.info("Sorting NEI map");
        MobHandler.sortCachedRecipes();
    }
}
