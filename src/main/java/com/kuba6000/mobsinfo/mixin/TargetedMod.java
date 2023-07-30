package com.kuba6000.mobsinfo.mixin;

import java.nio.file.Path;

import com.google.common.io.Files;

public enum TargetedMod {

    VANILLA("Minecraft", "unused", true),
    INFERNAL_MOBS("InfernalMobs", "InfernalMobs-", true),
    ENDER_IO("EnderIO", "EnderIO", true),
    DRACONIC_EVOLUTION("DraconicEvolution", "Draconic-Evolution-", true),
    DQ_RESPECT("DQMIIINext", "[1.7.10]DQRmod", true),
    CHOCO_CRAFT("chococraft", "ChocoCraftPlus-", true),
    BATTLE_GEAR_2("battlegear2", "battlegear2-", true),

    ;

    public final String modName;
    public final String jarNamePrefixLowercase;
    public final boolean loadInDevelopment;

    TargetedMod(String modName, String jarNamePrefix, boolean loadInDevelopment) {
        this.modName = modName;
        this.jarNamePrefixLowercase = jarNamePrefix.toLowerCase();
        this.loadInDevelopment = loadInDevelopment;
    }

    @SuppressWarnings("UnstableApiUsage")
    public boolean isMatchingJar(Path path) {
        final String pathString = path.toString();
        final String nameLowerCase = Files.getNameWithoutExtension(pathString)
            .toLowerCase();
        final String fileExtension = Files.getFileExtension(pathString);

        return nameLowerCase.startsWith(jarNamePrefixLowercase) && "jar".equals(fileExtension);
    }

    @Override
    public String toString() {
        return "TargetedMod{" + "modName='"
            + modName
            + '\''
            + ", jarNamePrefixLowercase='"
            + jarNamePrefixLowercase
            + '\''
            + '}';
    }
}
