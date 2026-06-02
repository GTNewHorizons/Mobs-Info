package com.kuba6000.mobsinfo.nei;

import com.gtnewhorizon.gtnhlib.color.ColorResource;

public class ColorUtils {

    private static final ColorResource.Factory color = new ColorResource.Factory("mobsinfo");

    public static final ColorResource
    // spotless:off

        textDefault     = color.argb("textDefault",     "0xFF555555"),
        textDanger      = color.argb("textDanger",      "0xFFFF0000"),
        textBoss        = color.argb("textBoss",        "0xFFD68F00"),
        textPositive    = color.argb("textPositive",    "0xFF005500"),
        slotHighlight   = color.argb("slotHighlight",   "0x80FFFFFF");
    // spotless:on
}
