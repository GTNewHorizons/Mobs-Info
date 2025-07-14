package com.kuba6000.mobsinfo.mixin;

import static com.kuba6000.mobsinfo.mixin.TargetedMod.*;

import javax.annotation.Nonnull;

import com.gtnewhorizon.gtnhmixins.builders.IMixins;
import com.gtnewhorizon.gtnhmixins.builders.MixinBuilder;

public enum Mixin implements IMixins {

    // Minecraft
    MINECRAFT(new MixinBuilder()
        .addCommonMixins(
            "minecraft.EnchantmentHelperMixin",
            "minecraft.EntityAccessor",
            "minecraft.EntityLivingAccessor",
            "minecraft.EntityLivingBaseAccessor",
            "minecraft.EntitySlimeAccessor",
            "minecraft.EntityVillagerAccessor",
            "minecraft.EventBusAccessor",
            "minecraft.ASMEventHandlerAccessor",
            "minecraft.VillagerRegistryAccessor")
        .addClientMixins(
            "minecraft.RendererLivingEntityAccessor",
            "minecraft.GuiAccessor",
            "minecraft.GuiContainerAccessor")
        .setPhase(Phase.EARLY)),

    // Infernal Mobs
    InfernalMobsCoreAccessor("InfernalMobs.InfernalMobsCoreAccessor", INFERNAL_MOBS),

    // Ender IO
    ItemSoulVesselAccessor("EnderIO.ItemSoulVesselAccessor", ENDER_IO),
    BlockPoweredSpawnerAccessor("EnderIO.BlockPoweredSpawnerAccessor", ENDER_IO),

    // Draconic Evolution
    MinecraftForgeEventHandlerAccessor("DraconicEvolution.MinecraftForgeEventHandlerAccessor", DRACONIC_EVOLUTION),
    RenderMobSoulMixin("DraconicEvolution.RenderMobSoulMixin", DRACONIC_EVOLUTION, BATTLE_GEAR_2),

    // DQRespect
    FuncCalcMobParamMixin("DQRespect.FuncCalcMobParamMixin", DQ_RESPECT),
    DqmEntitySweetbagMixin("DQRespect.DqmEntitySweetbagMixin", DQ_RESPECT),

    // ChocoCraft
    EntityAnimalChocoboMixin("ChocoCraft.EntityAnimalChocoboMixin", CHOCO_CRAFT),

    // Hardcore-Ender-Expansion
    EntityMobLouseMixin("HardcoreEnderExpansion.EntityMobLouseMixin", HARDCORE_ENDER_EXPANSION),
    EntityMobHomelandEndermanMixin("HardcoreEnderExpansion.EntityMobHomelandEndermanMixin", HARDCORE_ENDER_EXPANSION),

    // Forestry
    EntityButterflyMixin("Forestry.EntityButterflyMixin", FORESTRY),

    ;

    private final MixinBuilder builder;

    Mixin(MixinBuilder builder) {
        this.builder = builder;
    }

    Mixin(String mixinClass, TargetedMod... targetedMods) {
        this.builder = new MixinBuilder().addCommonMixins(mixinClass)
            .setPhase(Phase.LATE);
        if (targetedMods != null) {
            for (TargetedMod mod : targetedMods) {
                this.builder.addRequiredMod(mod);
            }
        }
    }

    @Nonnull
    @Override
    public MixinBuilder getBuilder() {
        return this.builder;
    }
}
