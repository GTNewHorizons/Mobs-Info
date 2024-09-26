package com.kuba6000.mobsinfo.mixin;

import static com.kuba6000.mobsinfo.mixin.TargetedMod.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import cpw.mods.fml.relauncher.FMLLaunchHandler;

public enum Mixin {

    // Minecraft
    EnchantmentHelperMixin("minecraft.EnchantmentHelperMixin", VANILLA),
    EntityAccessor("minecraft.EntityAccessor", VANILLA),
    EntityLivingAccessor("minecraft.EntityLivingAccessor", VANILLA),
    EntityLivingBaseAccessor("minecraft.EntityLivingBaseAccessor", VANILLA),
    EntitySlimeAccessor("minecraft.EntitySlimeAccessor", VANILLA),
    EntityVillagerAccessor("minecraft.EntityVillagerAccessor", VANILLA),
    EventBusAccessor("minecraft.EventBusAccessor", VANILLA),
    RendererLivingEntityAccessor("minecraft.RendererLivingEntityAccessor", VANILLA),
    GuiAccessor("minecraft.GuiAccessor", VANILLA),
    GuiContainerAccessor("minecraft.GuiContainerAccessor", VANILLA),
    ASMEventHandlerAccessor("minecraft.ASMEventHandlerAccessor", VANILLA),
    VillagerRegistryAccessor("minecraft.VillagerRegistryAccessor", VANILLA),

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

    public final String mixinClass;
    public final List<TargetedMod> targetedMods;
    private final Side side;
    private final Phase phase;

    Mixin(String mixinClass, TargetedMod... targetedMods) {
        this.mixinClass = mixinClass;
        this.targetedMods = Arrays.asList(targetedMods);
        this.side = Side.BOTH;
        this.phase = this.targetedMods.size() == 1 && this.targetedMods.contains(VANILLA) ? Phase.EARLY : Phase.LATE;
    }

    public static List<String> getEarlyMixins(Set<String> loadedCoreMods) {
        final List<String> mixins = new ArrayList<>();
        for (Mixin mixin : Mixin.values()) {
            if (mixin.phase == Phase.EARLY) {
                mixins.add(mixin.mixinClass);
            }
        }
        return mixins;
    }

    public static List<String> getLateMixins(Set<String> loadedMods) {
        // NOTE: Any targetmod here needs a modid, not a coremod id
        final List<String> mixins = new ArrayList<>();
        for (Mixin mixin : Mixin.values()) {
            if (mixin.phase == Phase.LATE) {
                if (mixin.shouldLoad(loadedMods)) {
                    mixins.add(mixin.mixinClass);
                }
            }
        }
        return mixins;
    }

    private boolean shouldLoad(Set<String> loadedMods) {
        return shouldLoadSide() && allModsLoaded(loadedMods);
    }

    private boolean shouldLoadSide() {
        return side == Side.BOTH || (side == Side.SERVER && FMLLaunchHandler.side()
            .isServer())
            || (side == Side.CLIENT && FMLLaunchHandler.side()
                .isClient());
    }

    private boolean allModsLoaded(Set<String> loadedMods) {
        if (targetedMods.isEmpty()) return false;
        for (TargetedMod target : targetedMods) {
            if (target == TargetedMod.VANILLA) continue;
            if (target.modId != null && !loadedMods.isEmpty() && !loadedMods.contains(target.modId)) {
                return false;
            }
        }
        return true;
    }

    private enum Side {
        BOTH,
        CLIENT,
        SERVER
    }

    private enum Phase {
        EARLY,
        LATE,
    }
}
