package com.kuba6000.mobsinfo.blacklistintegration;

import java.util.LinkedHashMap;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.init.Items;
import net.minecraft.world.World;

import com.kuba6000.mobsinfo.config.Config;
import com.kuba6000.mobsinfo.loader.MobRecipeLoader;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLLoadCompleteEvent;

@Mod(
    modid = "mobblacklistfixture",
    name = "Mob blacklist fixture",
    version = "1",
    dependencies = "required-after:mobsinfo")
public class MobBlacklistIntegration {

    private static final String TARGET = "fixture:blacklisted";

    @Mod.EventHandler
    public void verify(FMLLoadCompleteEvent event) {
        try {
            EntityList.stringToClassMapping = new LinkedHashMap<>();
            EntityList.stringToClassMapping.put(TARGET, Target.class);
            EntityList.stringToClassMapping.put("fixture:healthy", Healthy.class);
            EntityList.stringToClassMapping.put("witherSkeleton", Entity.class);
            Config.MobHandler.mobHandlerEnabled = true;
            String mode = System.getProperty("mobsinfo.mobBlacklistIntegration", "generate");
            boolean unblocked = mode.equals("unblock");
            boolean fromCache = mode.equals("cache");
            Config.MobHandler.mobBlacklist = unblocked ? new String[0] : new String[] { TARGET };
            Config.MobHandler.regenerationTrigger = mode.equals("generate")
                ? Config.MobHandler._CacheRegenerationTrigger.Always
                : Config.MobHandler._CacheRegenerationTrigger.Never;
            MobRecipeLoader.generateMobRecipeMap();
            if (unblocked) {
                if (Target.constructions == 0 || Target.dropCalls == 0
                    || !MobRecipeLoader.GeneralMobList.containsKey(TARGET)) {
                    throw new AssertionError("Removing a blacklist entry must regenerate the missing recipe");
                }
            } else if (Target.constructions != 0 || Target.dropCalls != 0
                || MobRecipeLoader.GeneralMobList.containsKey(TARGET)) {
                    throw new AssertionError("Blacklisted mob must be skipped before construction and drop generation");
                }
            if (!MobRecipeLoader.GeneralMobList.containsKey("fixture:healthy")) {
                throw new AssertionError("The non-blacklisted mob must still be generated");
            }
            if (fromCache ? Healthy.dropCalls != 0 : Healthy.dropCalls == 0) {
                throw new AssertionError(
                    "Adding an exclusion should reuse cache; removing a missing mob's exclusion should regenerate");
            }
            System.out.println("MOB_BLACKLIST_INTEGRATION_PASS");
            FMLCommonHandler.instance()
                .exitJava(0, true);
        } catch (Throwable failure) {
            failure.printStackTrace();
            FMLCommonHandler.instance()
                .exitJava(1, true);
        }
    }

    public static class Target extends EntityLiving {

        static int constructions;
        static int dropCalls;

        public Target(World world) {
            super(world);
            constructions++;
        }

        @Override
        protected void dropFewItems(boolean recentlyHit, int looting) {
            dropCalls++;
            dropItem(Items.diamond, 1);
        }
    }

    public static class Healthy extends EntityLiving {

        static int dropCalls;

        public Healthy(World world) {
            super(world);
        }

        @Override
        protected void dropFewItems(boolean recentlyHit, int looting) {
            dropCalls++;
            dropItem(Items.emerald, 1);
        }
    }
}
