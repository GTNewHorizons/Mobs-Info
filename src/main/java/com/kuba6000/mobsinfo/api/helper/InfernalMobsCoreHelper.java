package com.kuba6000.mobsinfo.api.helper;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.IMob;

import com.kuba6000.mobsinfo.mixin.late.InfernalMobs.InfernalMobsCoreAccessor;

import atomicstryker.infernalmobs.common.InfernalMobsCore;

// Support for GTNH InfernalMobs version
public class InfernalMobsCoreHelper {

    private static boolean isDimensionBlacklistInitialized = false;
    private static Field dimensionBlackListField = null;

    @SuppressWarnings("unchecked")
    public static List<Integer> getDimensionBlackList(InfernalMobsCore infernalMobsCore) {
        if (!isDimensionBlacklistInitialized) {
            isDimensionBlacklistInitialized = true;
            if (dimensionBlackListField == null) {
                try {
                    dimensionBlackListField = infernalMobsCore.getClass()
                        .getDeclaredField("dimensionBlacklist");
                    dimensionBlackListField.setAccessible(true);
                } catch (NoSuchFieldException ignored) {}
            }
        }
        if (dimensionBlackListField != null) {
            try {
                // noinspection unchecked
                return (ArrayList<Integer>) dimensionBlackListField.get(infernalMobsCore);
            } catch (IllegalAccessException | ClassCastException ignored) {
                dimensionBlackListField = null;
            }
        }
        return Collections.emptyList();
    }

    private static boolean isIsClassAllowedInitialized = false;
    private static Method isClassAllowedMethod = null;

    public static boolean callIsClassAllowed(InfernalMobsCore infernalMobsCore, EntityLivingBase entity) {
        if (!isIsClassAllowedInitialized) {
            isIsClassAllowedInitialized = true;
            if (isClassAllowedMethod == null) {
                try {
                    isClassAllowedMethod = infernalMobsCore.getClass()
                        .getDeclaredMethod("isClassAllowed", EntityLivingBase.class);
                    isClassAllowedMethod.setAccessible(true);
                } catch (NoSuchMethodException ignored) {}
            }
        }
        if (isClassAllowedMethod != null) {
            try {
                return (boolean) isClassAllowedMethod.invoke(infernalMobsCore, entity);
            } catch (InvocationTargetException | IllegalAccessException | ClassCastException ignored) {
                isClassAllowedMethod = null;
            }
        }
        return entity instanceof IMob
            && ((InfernalMobsCoreAccessor) infernalMobsCore).callCheckEntityClassAllowed(entity);
    }

}
