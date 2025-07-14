package com.kuba6000.mobsinfo.mixin;

import javax.annotation.Nonnull;

import com.gtnewhorizon.gtnhmixins.builders.ITargetMod;
import com.gtnewhorizon.gtnhmixins.builders.TargetModBuilder;

public enum TargetedMod implements ITargetMod {

    BATTLE_GEAR_2(null, "battlegear2"),
    CHOCO_CRAFT(null, "chococraft"),
    DQ_RESPECT(null, "DQMIIINext"),
    DRACONIC_EVOLUTION(null, "DraconicEvolution"),
    ENDER_IO(null, "EnderIO"),
    FORESTRY(null, "Forestry"),
    HARDCORE_ENDER_EXPANSION(null, "HardcoreEnderExpansion"),
    INFERNAL_MOBS(null, "InfernalMobs");

    private final TargetModBuilder builder;

    TargetedMod(String coreModClass, String modId) {
        this.builder = new TargetModBuilder().setCoreModClass(coreModClass)
            .setModId(modId);
    }

    @Nonnull
    @Override
    public TargetModBuilder getBuilder() {
        return builder;
    }
}
