package com.kuba6000.mobsinfo.api.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import net.minecraft.util.StatCollector;

public class TranslationHelper {

    public static String translateFormattedFixed(String key, Object... args) {
        String translated = StatCollector.translateToLocal(key);
        List<String> replacements = new ArrayList<>();
        for (Object arg : args) {
            if (arg instanceof Double || arg instanceof Float) {
                if ((double) arg > 0.05d || (double) arg == 0d) replacements.add("%.2f");
                else replacements.add("%.4f");
            } else replacements.add("%s");
        }

        for (String replacement : replacements) {
            translated = translated.replaceFirst(Pattern.quote("%s"), replacement);
        }

        return String.format(translated, args);
    }

}
