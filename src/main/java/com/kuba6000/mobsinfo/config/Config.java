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

package com.kuba6000.mobsinfo.config;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

import com.kuba6000.mobsinfo.Tags;

public class Config {

    private enum Category {

        MOB_HANDLER("MobHandler"),
        DEBUG("Debug"),
        COMPATIBILITY("Compatibility");

        final String categoryName;

        Category(String s) {
            categoryName = s;
        }

        public String get() {
            return categoryName;
        }

        @Override
        public String toString() {
            return get();
        }
    }

    public static class MobHandler {

        public static boolean mobHandlerEnabled = true;

        public enum _CacheRegenerationTrigger {

            Never,
            ModAdditionRemoval,
            ModAdditionRemovalChange,
            Always;

            public static _CacheRegenerationTrigger get(int oridinal) {
                return values()[oridinal];
            }
        }

        public static _CacheRegenerationTrigger regenerationTrigger = _CacheRegenerationTrigger.ModAdditionRemovalChange;
        public static boolean includeEmptyMobs = true;
        public static String[] mobBlacklist;
        public static boolean hiddenMode = false;

        private static void load(Configuration configuration) {
            Category category = Category.MOB_HANDLER;
            mobHandlerEnabled = configuration.get(category.get(), "Enabled", true, "Enable \"Mob Info\" NEI page")
                .getBoolean();
            StringBuilder c = new StringBuilder("When will cache regeneration trigger? ");
            for (_CacheRegenerationTrigger value : _CacheRegenerationTrigger.values()) c.append(value.ordinal())
                .append(" - ")
                .append(value.name())
                .append(", ");
            regenerationTrigger = _CacheRegenerationTrigger.get(
                configuration
                    .get(
                        category.get(),
                        "CacheRegenerationTrigger",
                        _CacheRegenerationTrigger.ModAdditionRemovalChange.ordinal(),
                        c.toString())
                    .getInt());
            includeEmptyMobs = configuration
                .get(category.get(), "IncludeEmptyMobs", true, "Include mobs that have no drops in NEI")
                .getBoolean();
            mobBlacklist = configuration
                .get(
                    category.get(),
                    "MobBlacklist",
                    new String[] { "Giant", "Thaumcraft.TravelingTrunk", "chisel.snowman", "OpenBlocks.Luggage",
                        "OpenBlocks.MiniMe", "SpecialMobs.SpecialCreeper", "SpecialMobs.SpecialZombie",
                        "SpecialMobs.SpecialPigZombie", "SpecialMobs.SpecialSlime", "SpecialMobs.SpecialSkeleton",
                        "SpecialMobs.SpecialEnderman", "SpecialMobs.SpecialCaveSpider", "SpecialMobs.SpecialGhast",
                        "SpecialMobs.SpecialWitch", "SpecialMobs.SpecialSpider", "TwilightForest.HydraHead",
                        "TwilightForest.RovingCube", "TwilightForest.Harbinger Cube", "TwilightForest.Adherent",
                        "SpecialMobs.SpecialSilverfish", },
                    "These mobs will be skipped when generating recipe map")
                .getStringList();
            hiddenMode = configuration
                .get(
                    category.get(),
                    "HiddenMode",
                    false,
                    "Hidden mode will make all mobs hidden in NEI until you kill them.")
                .getBoolean();
        }
    }

    public static class Debug {

        public enum LoggingLevel {

            Basic,
            Detailed;

            public static LoggingLevel get(int oridinal) {
                return values()[oridinal];
            }
        }

        public static LoggingLevel loggingLevel = LoggingLevel.Basic;
        public static boolean showRenderErrors = false;

        private static void load(Configuration configuration) {
            Category category = Category.DEBUG;
            showRenderErrors = configuration.get(category.get(), "ShowRenderErrors", false)
                .getBoolean();
            loggingLevel = LoggingLevel.get(
                configuration
                    .get(
                        category.get(),
                        "LoggingLevel",
                        0,
                        "0 - Default, log only basic, summary information. 1 - More detailed logs")
                    .getInt());
        }
    }

    public static class Compatibility {

        public static boolean enableMobHandlerInfernal = true;
        public static boolean addAllEnderIOSpawnersToNEI = false;

        private static void load(Configuration configuration) {
            Category category = Category.COMPATIBILITY;
            enableMobHandlerInfernal = configuration
                .get(
                    category.get(),
                    "enableInfernalDrops",
                    true,
                    "Enables \"Infernal Drops\" NEI page if Infernal-Mobs mod is loaded.")
                .getBoolean();
            addAllEnderIOSpawnersToNEI = configuration
                .get(
                    category.get(),
                    "addAllEnderIOSpawnersToNEI",
                    false,
                    "Adds all mob variants EnderIO powered spawners to NEI.")
                .getBoolean();
        }
    }

    public static File configFile;
    public static File configDirectory;

    public static void init(File configFile) {
        configDirectory = new File(configFile, Tags.MODID);
        Config.configFile = new File(configDirectory, Tags.MODID + ".cfg");
    }

    public static File getConfigFile(String file) {
        return new File(configDirectory, file);
    }

    public static void synchronizeConfiguration() {
        Configuration configuration = new Configuration(configFile);
        configuration.load();

        MobHandler.load(configuration);
        Debug.load(configuration);
        Compatibility.load(configuration);

        if (configuration.hasChanged()) {
            configuration.save();
        }
    }
}
