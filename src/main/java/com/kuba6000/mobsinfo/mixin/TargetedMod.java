package com.kuba6000.mobsinfo.mixin;

import cpw.mods.fml.common.Mod;

public enum TargetedMod {

    BATTLE_GEAR_2("Mine & Blade Battlegear 2", null, "battlegear2"),
    CHOCO_CRAFT("ChocoCraft Plus", null, "chococraft"),
    DQ_RESPECT("DQRespect", null, "DQMIIINext"),
    DRACONIC_EVOLUTION("Draconic Evolution", null, "DraconicEvolution"),
    ENDER_IO("Ender IO", null, "EnderIO"),
    FORESTRY("Forestry", null, "Forestry"),
    HARDCORE_ENDER_EXPANSION("Hardcore Ender Expansion", null, "HardcoreEnderExpansion"),
    INFERNAL_MOBS("Infernal Mobs", null, "InfernalMobs"),
    VANILLA("Minecraft", null);

    /** The "name" in the {@link Mod @Mod} annotation */
    public final String modName;
    /** Class that implements the IFMLLoadingPlugin interface */
    public final String coreModClass;
    /** The "modid" in the {@link Mod @Mod} annotation */
    public final String modId;

    TargetedMod(String modName, String coreModClass) {
        this(modName, coreModClass, null);
    }

    TargetedMod(String modName, String coreModClass, String modId) {
        this.modName = modName;
        this.coreModClass = coreModClass;
        this.modId = modId;
    }

    @Override
    public String toString() {
        return "TargetedMod{modName='" + modName + "', coreModClass='" + coreModClass + "', modId='" + modId + "'}";
    }
}
