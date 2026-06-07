package com.kuba6000.mobsinfo.nei;

import java.util.Locale;

import net.minecraft.util.StatCollector;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Enum for UI colors that supports optional resource-pack overrides.
 */
public enum EnumColors {

    TEXT_DEFAULT(0xFF555555),
    TEXT_DANGER(0xFFFF0000),
    TEXT_BOSS(0xFFD68F00),
    TEXT_POSITIVE(0xFF005500),
    SLOT_HIGHLIGHT(0x80FFFFFF),

    ;

    private static final Logger LOG = LogManager.getLogger("mobsinfo[EnumColors]");
    private static final String PREFIX = "mobsinfo.color.override.";
    private final int defaultColor;

    private String unlocalizedName;
    private int cachedColor;
    private boolean hasCachedColor = false;

    EnumColors(int defaultColor) {
        this.defaultColor = defaultColor;
    }

    public int getColor() {
        if (!hasCachedColor) {
            updateCachedColor();
        }
        return cachedColor;
    }

    public void updateCachedColor() {
        String key = getUnlocalized();
        if (!StatCollector.canTranslate(key)) {
            cachedColor = defaultColor;
        } else {
            cachedColor = parseColor(StatCollector.translateToLocal(key), defaultColor);
        }
        hasCachedColor = true;
    }

    public String getUnlocalized() {
        if (unlocalizedName == null) {
            unlocalizedName = PREFIX + name().toLowerCase(Locale.ROOT);
        }
        return unlocalizedName;
    }

    private static int parseColor(String raw, int fallback) {
        if (raw == null) {
            return fallback;
        }

        String value = raw.trim();
        if (value.isEmpty()) {
            return fallback;
        }

        if (value.startsWith("#")) {
            value = value.substring(1);
        } else if (value.startsWith("0x") || value.startsWith("0X")) {
            value = value.substring(2);
        }

        try {
            return Integer.parseUnsignedInt(value, 16);
        } catch (NumberFormatException e) {
            LOG.warn("Invalid color override value '{}'", raw);
            return fallback;
        }
    }
}
