package com.kuba6000.mobsinfo.loader.extras;

import static com.kuba6000.mobsinfo.MobsInfo.MODID;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kuba6000.mobsinfo.api.IChanceModifier;
import com.kuba6000.mobsinfo.api.IMobExtraInfoProvider;
import com.kuba6000.mobsinfo.api.LoaderReference;
import com.kuba6000.mobsinfo.api.MobDrop;
import com.kuba6000.mobsinfo.api.MobRecipe;
import com.kuba6000.mobsinfo.mixin.minecraft.ASMEventHandlerAccessor;
import com.kuba6000.mobsinfo.mixin.minecraft.EventBusAccessor;

import cpw.mods.fml.common.eventhandler.ASMEventHandler;
import cpw.mods.fml.common.eventhandler.IEventListener;

public class ExtraLoader {

    private static final Logger LOG = LogManager.getLogger(MODID + "[Extra Loader]");

    private static boolean initialized = false;
    private static final List<IExtraLoader> loaders = new ArrayList<>();
    private static final List<IMobExtraInfoProvider> APIProviders = new ArrayList<>();

    private static void init() {
        initialized = true;
        LOG.info("Initializing extra loader");
        // FIRST
        loaders.add(new Minecraft());

        HashSet<String> alreadyProvided = new HashSet<>();

        ArrayList<IEventListener> listeners = new ArrayList<>();
        listeners.addAll(
            Arrays.asList(
                new LivingDropsEvent(null, null, null, 0, false, 0).getListenerList()
                    .getListeners(((EventBusAccessor) MinecraftForge.EVENT_BUS).getBusID())));
        listeners.addAll(
            Arrays.asList(
                new LivingDeathEvent(null, null).getListenerList()
                    .getListeners(((EventBusAccessor) MinecraftForge.EVENT_BUS).getBusID())));

        for (IEventListener listener : listeners) {
            if (listener instanceof ASMEventHandler asmEventHandler) {
                IEventListener asmListener = ((ASMEventHandlerAccessor) asmEventHandler).getHandler();
                try {
                    Object instance = asmListener.getClass()
                        .getField("instance")
                        .get(asmListener);
                    Class<?> clazz = instance.getClass();
                    if (IMobExtraInfoProvider.class.isAssignableFrom(clazz)) {
                        APIProviders.add((IMobExtraInfoProvider) instance);
                        alreadyProvided.add(
                            ((ASMEventHandlerAccessor) listener).getOwner()
                                .getModId());
                    }
                } catch (IllegalAccessException | NoSuchFieldException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        LOG.info("Registered {} Extra API providers!", APIProviders.size());

        // Mods
        if (LoaderReference.DraconicEvolution.isLoaded
            && !alreadyProvided.contains(LoaderReference.DraconicEvolution.modID)) loaders.add(new DraconicEvolution());
        if (LoaderReference.TinkersConstruct.isLoaded
            && !alreadyProvided.contains(LoaderReference.TinkersConstruct.modID)) loaders.add(new TinkersConstruct());
        if (LoaderReference.Witchery.isLoaded && !alreadyProvided.contains(LoaderReference.Witchery.modID))
            loaders.add(new Witchery());
        if (LoaderReference.ThaumicHorizons.isLoaded
            && !alreadyProvided.contains(LoaderReference.ThaumicHorizons.modID)) loaders.add(new ThaumicHorizons());
        if (LoaderReference.ThaumicBases.isLoaded && !alreadyProvided.contains(LoaderReference.ThaumicBases.modID))
            loaders.add(new ThaumicBases());
        if (LoaderReference.WirelessCraftingTerminal.isLoaded
            && !alreadyProvided.contains(LoaderReference.WirelessCraftingTerminal.modID))
            loaders.add(new WirelessCraftingTerminal());
        if (LoaderReference.CoFHCore.isLoaded && !alreadyProvided.contains(LoaderReference.CoFHCore.modID))
            loaders.add(new CoFHCore());
        if (LoaderReference.HardcoreEnderExpansion.isLoaded
            && !alreadyProvided.contains(LoaderReference.HardcoreEnderExpansion.modID))
            loaders.add(new HardcoreEnderExpansion());
        if (LoaderReference.Botania.isLoaded && !alreadyProvided.contains(LoaderReference.Botania.modID))
            loaders.add(new Botania());
        if (LoaderReference.HarvestCraft.isLoaded && !alreadyProvided.contains(LoaderReference.HarvestCraft.modID))
            loaders.add(new HarvestCraft());
        if (LoaderReference.OpenBlocks.isLoaded && !alreadyProvided.contains(LoaderReference.OpenBlocks.modID))
            loaders.add(new OpenBlocks());
        if (LoaderReference.BloodArsenal.isLoaded && !alreadyProvided.contains(LoaderReference.BloodArsenal.modID))
            loaders.add(new BloodArsenal());
        if (LoaderReference.BloodMagic.isLoaded && !alreadyProvided.contains(LoaderReference.BloodMagic.modID))
            loaders.add(new BloodMagic());
        if (LoaderReference.Avaritia.isLoaded && !alreadyProvided.contains(LoaderReference.Avaritia.modID))
            loaders.add(new Avaritia());
        if (LoaderReference.ThaumicTinkerer.isLoaded
            && !alreadyProvided.contains(LoaderReference.ThaumicTinkerer.modID)) loaders.add(new ThaumicTinkerer());
        if (LoaderReference.ForbiddenMagic.isLoaded && !alreadyProvided.contains(LoaderReference.ForbiddenMagic.modID))
            loaders.add(new ForbiddenMagic());
        if (LoaderReference.ElectroMagicTools.isLoaded
            && !alreadyProvided.contains(LoaderReference.ElectroMagicTools.modID)) loaders.add(new ElectroMagicTools());
        if (LoaderReference.WitchingGadgets.isLoaded
            && !alreadyProvided.contains(LoaderReference.WitchingGadgets.modID)) loaders.add(new WitchingGadgets());
        if (LoaderReference.Thaumcraft.isLoaded && !alreadyProvided.contains(LoaderReference.Thaumcraft.modID))
            loaders.add(new Thaumcraft());
        if (LoaderReference.Automagy.isLoaded && !alreadyProvided.contains(LoaderReference.Automagy.modID))
            loaders.add(new Automagy());
        if (LoaderReference.GTPlusPlus.isLoaded && !alreadyProvided.contains(LoaderReference.GTPlusPlus.modID))
            loaders.add(new GregtechPlusPlus());
        if (LoaderReference.DQRespect.isLoaded && !alreadyProvided.contains(LoaderReference.DQRespect.modID))
            loaders.add(new DQRespect());
        if (LoaderReference.ChocoCraft.isLoaded && !alreadyProvided.contains(LoaderReference.ChocoCraft.modID))
            loaders.add(new ChocoCraft());
        if (LoaderReference.ExtraUtilities.isLoaded && !alreadyProvided.contains(LoaderReference.ExtraUtilities.modID))
            loaders.add(new ExtraUtilities());
        if (LoaderReference.EtFuturumRequiem.isLoaded
            && !alreadyProvided.contains(LoaderReference.EtFuturumRequiem.modID)) loaders.add(new EtFuturum());
        if (LoaderReference.LycanitesMobs.isLoaded && !alreadyProvided.contains(LoaderReference.LycanitesMobs.modID))
            loaders.add(new LycanitesMobs());
        if (LoaderReference.Reliquarry.isLoaded && !alreadyProvided.contains(LoaderReference.Reliquarry.modID))
            loaders.add(new Reliquarry());

        // LAST
        if (LoaderReference.EditMobDrops.isLoaded && !alreadyProvided.contains(LoaderReference.EditMobDrops.modID))
            loaders.add(new EditMobDrops());
        if (LoaderReference.MineTweaker.isLoaded && !alreadyProvided.contains(LoaderReference.MineTweaker.modID))
            loaders.add(new MineTweaker());
    }

    public static void process(String k, ArrayList<MobDrop> drops, MobRecipe recipe) {
        if (!initialized) init();
        for (IExtraLoader loader : loaders) {
            try {
                loader.process(k, drops, recipe);
            } catch (Exception ex) {
                LOG.error(
                    "There was an error while loading {} modifications on {}",
                    loader.getClass()
                        .getSimpleName(),
                    k);
                ex.printStackTrace();
            }
        }
        for (IMobExtraInfoProvider provider : APIProviders) {
            try {
                provider.provideExtraDropsInformation(k, drops, recipe);
            } catch (Exception ex) {
                LOG.error(
                    "There was an error while loading {} modifications on {}",
                    provider.getClass()
                        .getName(),
                    k);
                ex.printStackTrace();
            }
            for (MobDrop drop : drops) {
                for (IChanceModifier chanceModifier : drop.chanceModifiers) {
                    try {
                        chanceModifier.getClass()
                            .getDeclaredConstructor();
                    } catch (NoSuchMethodException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }
}
