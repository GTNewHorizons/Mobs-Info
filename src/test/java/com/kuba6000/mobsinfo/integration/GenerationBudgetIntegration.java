package com.kuba6000.mobsinfo.integration;

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

/** Run with runServer -PgenerationBudgetIntegration in a separate working directory. */
@Mod(modid = "generationbudgetfixture", version = "1", dependencies = "required-after:mobsinfo")
public class GenerationBudgetIntegration {

    @Mod.EventHandler
    public void verify(FMLLoadCompleteEvent event) {
        try {
            // Exercise the reported third-party mob too when the dev runtime includes ManaMetal.
            Class<? extends Entity> nightmare = EntityList.stringToClassMapping
                .get("manametalmod.EntityMobNightmareBase");
            EntityList.stringToClassMapping = new LinkedHashMap<>();
            EntityList.stringToClassMapping.put("fixture:drop_loop", DropLoop.class);
            EntityList.stringToClassMapping.put("fixture:after_drop", Healthy.class);
            EntityList.stringToClassMapping.put("fixture:constructor_loop", ConstructorLoop.class);
            EntityList.stringToClassMapping.put("fixture:after_constructor", Healthy.class);
            EntityList.stringToClassMapping.put("fixture:armor_loop", ArmorLoop.class);
            EntityList.stringToClassMapping.put("fixture:after_armor", Healthy.class);
            EntityList.stringToClassMapping.put("fixture:broken_name", BrokenName.class);
            EntityList.stringToClassMapping.put("fixture:after_name", Healthy.class);
            if (nightmare != null) {
                EntityList.stringToClassMapping.put("manametalmod.EntityMobNightmareBase", nightmare);
                EntityList.stringToClassMapping.put("fixture:after_manametal", Healthy.class);
            }
            // Keep the fixture focused on the custom mobs, without injecting a wither skeleton.
            EntityList.stringToClassMapping.put("witherSkeleton", Entity.class);
            Config.MobHandler.mobHandlerEnabled = true;
            Config.MobHandler.regenerationTrigger = Config.MobHandler._CacheRegenerationTrigger.Always;
            MobRecipeLoader.generateMobRecipeMap();
            for (String name : new String[] { "after_drop", "after_constructor", "after_armor", "after_name" }) {
                MobRecipeLoader.GeneralMappedMob mob = MobRecipeLoader.GeneralMobList.get("fixture:" + name);
                if (mob == null || mob.drops.isEmpty()) {
                    throw new AssertionError("Healthy mob lost after failed generation: " + name);
                }
                if (mob.drops.stream()
                    .noneMatch(drop -> drop.stack.getItem() == Items.diamond && drop.chance == 10000)) {
                    throw new AssertionError("Healthy mob lost its guaranteed drop: " + name);
                }
            }
            for (String name : new String[] { "drop_loop", "constructor_loop", "armor_loop", "broken_name" }) {
                if (MobRecipeLoader.GeneralMobList.containsKey("fixture:" + name)) {
                    throw new AssertionError("Aborted generation published a recipe: " + name);
                }
            }
            if (MobRecipeLoader.isInGenerationProcess) throw new AssertionError("Generation did not finish");
            if (nightmare != null) {
                if (!MobRecipeLoader.GeneralMobList.containsKey("fixture:after_manametal")) {
                    throw new AssertionError("Generation did not recover after ManaMetal");
                }
                System.out.println("MANAMETAL_GENERATION_BUDGET_PASS");
            }
            System.out.println("GENERATION_BUDGET_INTEGRATION_PASS");
            FMLCommonHandler.instance()
                .exitJava(0, true);
        } catch (Throwable failure) {
            failure.printStackTrace();
            FMLCommonHandler.instance()
                .exitJava(1, true);
        }
    }

    public static class Healthy extends EntityLiving {

        public Healthy(World world) {
            super(world);
            // Enough legitimate construction work to detect a budget leaked from another mob.
            for (int draw = 0; draw < 6000; draw++) world.rand.nextInt(2);
        }

        @Override
        protected void dropFewItems(boolean recentlyHit, int looting) {
            dropItem(Items.diamond, 1);
        }
    }

    public static class BrokenName extends EntityLiving {

        public BrokenName(World world) {
            super(world);
        }

        @Override
        public String getCommandSenderName() {
            throw new IllegalStateException("Fixture cannot resolve its entity name");
        }
    }

    public static class DropLoop extends EntityLiving {

        public DropLoop(World world) {
            super(world);
        }

        @Override
        protected void dropFewItems(boolean recentlyHit, int looting) {
            // Bound the reproduction so a regression fails without exhausting the test JVM.
            for (int draw = 0; draw < 100_000; draw++) {
                if (worldObj.rand.nextInt(2) != 0) break;
            }
        }
    }

    public static class ConstructorLoop extends EntityLiving {

        public ConstructorLoop(World world) {
            super(world);
            for (int draw = 0; draw < 100_000; draw++) {
                if (world.rand.nextInt(2) != 0) break;
            }
        }
    }

    public static class ArmorLoop extends EntityLiving {

        public ArmorLoop(World world) {
            super(world);
        }

        @Override
        protected void addRandomArmor() {
            for (int draw = 0; draw < 100_000; draw++) {
                if (worldObj.rand.nextInt(2) != 0) break;
            }
        }
    }
}
