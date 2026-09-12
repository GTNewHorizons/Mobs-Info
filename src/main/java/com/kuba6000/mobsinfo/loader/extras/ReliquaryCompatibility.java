package com.kuba6000.mobsinfo.loader.extras;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/** Selects the drop API without initializing optional Reliquary classes. */
public enum ReliquaryCompatibility {

    LEGACY,
    MODERN,
    UNSUPPORTED;

    public static ReliquaryCompatibility detect(String version, ClassLoader loader) {
        try {
            Class<?> recipes = Class.forName("xreliquary.init.XRRecipes", false, loader);
            Class<?> events = Class.forName("xreliquary.event.CommonEventHandler", false, loader);
            if (isStaticMethod(recipes.getMethod("ingredient", int.class), "net.minecraft.item.ItemStack")
                && isStaticMethod(recipes.getMethod("getItem", String.class), "net.minecraft.item.Item")
                && events.getMethod("getBaseDrop", String.class)
                    .getReturnType() == float.class
                && events.getMethod("getLootingDrop", String.class)
                    .getReturnType() == float.class) {
                return MODERN;
            }
        } catch (ClassNotFoundException | NoSuchMethodException | LinkageError e) {
            // Stable 1.2 predates this API; beta 1.2.257 reports the same version but has it.
        }
        if ("1.2".equals(version) && loader.getResource("xreliquary/items/ItemSquidBeak.class") != null) {
            return LEGACY;
        }
        return UNSUPPORTED;
    }

    private static boolean isStaticMethod(Method method, String returnType) {
        return Modifier.isStatic(method.getModifiers()) && method.getReturnType()
            .getName()
            .equals(returnType);
    }
}
