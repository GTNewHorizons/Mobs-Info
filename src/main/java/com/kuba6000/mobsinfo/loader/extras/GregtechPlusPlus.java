package com.kuba6000.mobsinfo.loader.extras;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.item.ItemStack;

import com.kuba6000.mobsinfo.api.MobDrop;
import com.kuba6000.mobsinfo.api.MobRecipe;

import gtPlusPlus.api.objects.data.AutoMap;
import gtPlusPlus.api.objects.data.Triplet;
import gtPlusPlus.core.material.ELEMENT;

public class GregtechPlusPlus implements IExtraLoader {

    private static boolean initialized = false;
    private static Class<?> EnderDragonDeathHandlerClass = null;
    private static Field EnderDragonDeathHandlerClass_mHEE = null;
    private static Field EnderDragonDeathHandlerClass_mHardcoreDragonClass = null;
    private static Field EnderDragonDeathHandlerClass_mDE = null;
    private static Field EnderDragonDeathHandlerClass_mChaoseDragonClass = null;
    private static Class<?> EntityDeathHandlerClass = null;
    private static Field EntityDeathHandlerClass_mMobDropMap = null;

    private static void init() {
        if (initialized) return;
        initialized = true;
        try {
            EnderDragonDeathHandlerClass = Class.forName("gtPlusPlus.core.handler.events.EnderDragonDeathHandler");
        } catch (ClassNotFoundException ignored) {}
        if (EnderDragonDeathHandlerClass != null) {
            try {
                EnderDragonDeathHandlerClass_mHEE = EnderDragonDeathHandlerClass.getDeclaredField("mHEE");
                EnderDragonDeathHandlerClass_mHEE.setAccessible(true);
                EnderDragonDeathHandlerClass_mHardcoreDragonClass = EnderDragonDeathHandlerClass
                    .getDeclaredField("mHardcoreDragonClass");
                EnderDragonDeathHandlerClass_mHardcoreDragonClass.setAccessible(true);
                EnderDragonDeathHandlerClass_mDE = EnderDragonDeathHandlerClass.getDeclaredField("mDE");
                EnderDragonDeathHandlerClass_mDE.setAccessible(true);
                EnderDragonDeathHandlerClass_mChaoseDragonClass = EnderDragonDeathHandlerClass
                    .getDeclaredField("mChaoseDragonClass");
                EnderDragonDeathHandlerClass_mChaoseDragonClass.setAccessible(true);
            } catch (Exception ignored) {}
        }
        try {
            EntityDeathHandlerClass = Class.forName("gtPlusPlus.core.handler.events.EntityDeathHandler");
        } catch (ClassNotFoundException ignored) {}
        if (EntityDeathHandlerClass != null) {
            try {
                EntityDeathHandlerClass_mMobDropMap = EntityDeathHandlerClass.getDeclaredField("mMobDropMap");
                EntityDeathHandlerClass_mMobDropMap.setAccessible(true);
            } catch (Exception ignored) {}
        }
    }

    @Override
    public void process(String k, ArrayList<MobDrop> drops, MobRecipe recipe) {

        // We are not using mixins accessors here because different versions of GT++ can differ much

        init();

        // gtPlusPlus.core.handler.events.EnderDragonDeathHandler

        if (EnderDragonDeathHandlerClass != null) {

            try {
                if (EnderDragonDeathHandlerClass_mHEE != null && (boolean) EnderDragonDeathHandlerClass_mHEE.get(null)
                    && EnderDragonDeathHandlerClass_mHardcoreDragonClass.get(null) != null
                    && ((Class) EnderDragonDeathHandlerClass_mHardcoreDragonClass.get(null))
                        .isInstance(recipe.entity)) {
                    MobDrop drop = new MobDrop(
                        ELEMENT.STANDALONE.DRAGON_METAL.getNugget(1),
                        MobDrop.DropType.Normal,
                        (int) (MobDrop.getChanceBasedOnFromTo(100, 250) * MobDrop.getChanceBasedOnFromTo(5, 25)
                            * 10000d),
                        null,
                        null,
                        false,
                        false);

                    drop.clampChance();

                    drops.add(drop);
                } else if (EnderDragonDeathHandlerClass_mDE != null
                    && (boolean) EnderDragonDeathHandlerClass_mDE.get(null)
                    && EnderDragonDeathHandlerClass_mChaoseDragonClass.get(null) != null
                    && ((Class) EnderDragonDeathHandlerClass_mChaoseDragonClass.get(null)).isInstance(recipe.entity)) {
                        MobDrop drop = new MobDrop(
                            ELEMENT.STANDALONE.DRAGON_METAL.getIngot(1),
                            MobDrop.DropType.Normal,
                            (int) (MobDrop.getChanceBasedOnFromTo(100, 200) * MobDrop.getChanceBasedOnFromTo(1, 5)
                                * 10000d),
                            null,
                            null,
                            false,
                            false);

                        drop.clampChance();

                        drops.add(drop);
                    } else if (recipe.entity instanceof EntityDragon) {
                        MobDrop drop = new MobDrop(
                            ELEMENT.STANDALONE.DRAGON_METAL.getNugget(1),
                            MobDrop.DropType.Normal,
                            (int) (MobDrop.getChanceBasedOnFromTo(25, 50) * MobDrop.getChanceBasedOnFromTo(1, 10)
                                * 10000d),
                            null,
                            null,
                            false,
                            false);

                        drop.clampChance();

                        drops.add(drop);
                    }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        // gtPlusPlus.core.handler.events.EntityDeathHandler

        if (EntityDeathHandlerClass != null && EntityDeathHandlerClass_mMobDropMap != null) {

            try {

                @SuppressWarnings({ "unchecked", "rawtypes" })
                HashMap<Class, AutoMap<Triplet<ItemStack, Integer, Integer>>> mMobDropMap = (HashMap<Class, AutoMap<Triplet<ItemStack, Integer, Integer>>>) EntityDeathHandlerClass_mMobDropMap
                    .get(null);

                AutoMap<Triplet<ItemStack, Integer, Integer>> dropEntry = mMobDropMap.get(recipe.entity.getClass());

                if (dropEntry != null && !dropEntry.isEmpty()) {
                    for (Triplet<ItemStack, Integer, Integer> data : dropEntry) {
                        ItemStack loot = data.getValue_1();
                        int maxDrop = data.getValue_2();
                        int chance = data.getValue_3();
                        if (loot == null) continue;

                        loot = loot.copy();
                        loot.stackSize = 1;

                        MobDrop drop = new MobDrop(
                            loot,
                            MobDrop.DropType.Normal,
                            (int) (MobDrop.getChanceBasedOnFromTo(1, maxDrop) * 10000d * ((double) chance / 10000d)),
                            null,
                            null,
                            false,
                            false);

                        drop.clampChance();

                        drops.add(drop);
                    }
                }

            } catch (Exception ignored) {}

        }

    }
}
