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

package com.kuba6000.mobsinfo.api.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import javax.xml.bind.DatatypeConverter;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;

import com.google.common.collect.Multimap;
import com.kuba6000.mobsinfo.MobsInfo;
import com.kuba6000.mobsinfo.mixin.early.minecraft.ASMEventHandlerAccessor;
import com.kuba6000.mobsinfo.mixin.early.minecraft.EventBusAccessor;
import com.kuba6000.mobsinfo.mixin.early.minecraft.VillagerRegistryAccessor;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.eventhandler.ASMEventHandler;
import cpw.mods.fml.common.eventhandler.IEventListener;
import cpw.mods.fml.common.registry.VillagerRegistry;

public class ModUtils {

    public static final boolean isDeobfuscatedEnvironment = (boolean) Launch.blackboard
        .get("fml.deobfuscatedEnvironment");
    public static boolean isClientSided = false;

    public static boolean isClientThreaded() {
        return FMLCommonHandler.instance()
            .getEffectiveSide()
            .isClient();
    }

    @FunctionalInterface
    public interface TriConsumer<T, U, V> {

        void accept(T t, U u, V v);
    }

    private static final HashMap<String, ModContainer> classNamesToModContainer = new HashMap<>();
    private static final Map.Entry<String, ModContainer> nullEntry = new AbstractMap.SimpleEntry<>("", null);

    private static void initClassNamesToMods() {
        if (classNamesToModContainer.isEmpty()) {
            classNamesToModContainer.put(
                "net.minecraft",
                Loader.instance()
                    .getMinecraftModContainer());
            Loader.instance()
                .getActiveModList()
                .forEach(m -> {
                    Object Mod = m.getMod();
                    if (Mod != null) {
                        Package modPackage = Mod.getClass()
                            .getPackage();
                        if (modPackage == null) { // HOW CAN THIS EVEN HAPPEN ?!
                            MobsInfo.warn("Mod " + m.getModId() + " package is not loaded yet!");
                            return;
                        }
                        classNamesToModContainer.put(modPackage.getName(), m);
                    }
                });
        }
    }

    public static String getModNameFromClassName(String classname) {
        initClassNamesToMods();
        Map.Entry<String, ModContainer> entry = classNamesToModContainer.entrySet()
            .stream()
            .filter(e -> classname.startsWith(e.getKey()))
            .findAny()
            .orElse(null);

        if (entry == null) return "Unknown";
        else return entry.getValue()
            .getName();
    }

    public static ModContainer getModContainerFromClassName(String classname) {
        initClassNamesToMods();
        return classNamesToModContainer.entrySet()
            .stream()
            .filter(e -> classname.startsWith(e.getKey()))
            .findAny()
            .orElse(nullEntry)
            .getValue();
    }

    private static String modListVersionForMobs = null;
    private static String modListVersionForMobsIgnoringModVersions = null;

    public static String getModListVersionForMobDrops(boolean includeModVersion) {
        if (includeModVersion && modListVersionForMobs != null) return modListVersionForMobs;
        if (!includeModVersion && modListVersionForMobsIgnoringModVersions != null)
            return modListVersionForMobsIgnoringModVersions;

        HashSet<ModContainer> modsWithEntities = new HashSet<>();
        // noinspection unchecked
        for (Class<? extends Entity> value : ((Map<String, Class<? extends Entity>>) EntityList.stringToClassMapping)
            .values()) {
            ModContainer mod = getModContainerFromClassName(value.getName());
            if (mod != null) modsWithEntities.add(mod);
        }

        IEventListener[] listeners = new LivingDeathEvent(null, null).getListenerList()
            .getListeners(((EventBusAccessor) MinecraftForge.EVENT_BUS).getBusID());
        for (IEventListener listener : listeners) {
            if (listener instanceof ASMEventHandler) {
                modsWithEntities.add(((ASMEventHandlerAccessor) listener).getOwner());
            }
        }

        listeners = new LivingDropsEvent(null, null, null, 0, false, 0).getListenerList()
            .getListeners(((EventBusAccessor) MinecraftForge.EVENT_BUS).getBusID());
        for (IEventListener listener : listeners) {
            if (listener instanceof ASMEventHandler) {
                modsWithEntities.add(((ASMEventHandlerAccessor) listener).getOwner());
            }
        }

        String sortedList = modsWithEntities.stream()
            .filter(m -> m.getMod() != null)
            .sorted(Comparator.comparing(ModContainer::getModId))
            .collect(
                StringBuilder::new,
                (a, b) -> a.append(b.getModId())
                    .append(includeModVersion ? b.getVersion() : ""),
                (a, b) -> a.append(", ")
                    .append(b))
            .toString();
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            String ver = DatatypeConverter.printHexBinary(md.digest(sortedList.getBytes(StandardCharsets.UTF_8)))
                .toUpperCase();
            if (includeModVersion) modListVersionForMobs = ver;
            else modListVersionForMobsIgnoringModVersions = ver;
            return ver;
        } catch (Exception e) {
            if (includeModVersion) modListVersionForMobs = sortedList;
            else modListVersionForMobsIgnoringModVersions = sortedList;
            return sortedList;
        }
    }

    private static String modListVersionForVillagers = null;
    private static String modListVersionForVillagersIgnoringModVersions = null;

    public static String getModListVersionForVillagerRecipes(boolean includeModVersion) {
        if (includeModVersion && modListVersionForVillagers != null) return modListVersionForVillagers;
        if (!includeModVersion && modListVersionForVillagersIgnoringModVersions != null)
            return modListVersionForVillagersIgnoringModVersions;

        VillagerRegistry villagerRegistry = VillagerRegistry.instance();
        Multimap<Integer, VillagerRegistry.IVillageTradeHandler> tradeHandlerMap = ((VillagerRegistryAccessor) villagerRegistry)
            .getTradeHandlers();

        Collection<Integer> villagerIDs = VillagerRegistry.getRegisteredVillagers();

        HashSet<ModContainer> modsWithEntities = new HashSet<>();

        for (VillagerRegistry.IVillageTradeHandler handler : tradeHandlerMap.values()) {
            ModContainer mod = getModContainerFromClassName(
                handler.getClass()
                    .getName());
            if (mod != null) modsWithEntities.add(mod);
        }

        String sortedList = villagerIDs.stream()
            .sorted()
            .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
            .append(
                (CharSequence) modsWithEntities.stream()
                    .filter(m -> m.getMod() != null)
                    .sorted(Comparator.comparing(ModContainer::getModId))
                    .collect(
                        StringBuilder::new,
                        (a, b) -> a.append(b.getModId())
                            .append(includeModVersion ? b.getVersion() : ""),
                        (a, b) -> a.append(", ")
                            .append(b)))
            .toString();
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            String ver = DatatypeConverter.printHexBinary(md.digest(sortedList.getBytes(StandardCharsets.UTF_8)))
                .toUpperCase();
            if (includeModVersion) modListVersionForVillagers = ver;
            else modListVersionForVillagersIgnoringModVersions = ver;
            return ver;
        } catch (Exception e) {
            if (includeModVersion) modListVersionForVillagers = sortedList;
            else modListVersionForVillagersIgnoringModVersions = sortedList;
            return sortedList;
        }
    }

}
