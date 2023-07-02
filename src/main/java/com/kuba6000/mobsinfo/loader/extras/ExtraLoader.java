package com.kuba6000.mobsinfo.loader.extras;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kuba6000.mobsinfo.Tags;
import com.kuba6000.mobsinfo.api.LoaderReference;
import com.kuba6000.mobsinfo.api.MobDrop;
import com.kuba6000.mobsinfo.api.MobRecipe;

public class ExtraLoader {

    private static final Logger LOG = LogManager.getLogger(Tags.MODID + "[Extra Loader]");

    private static boolean initialized = false;
    private static final List<IExtraLoader> loaders = new ArrayList<>();

    private static void init() {
        initialized = true;
        if (LoaderReference.DraconicEvolution) loaders.add(new DraconicEvolution());
        if (LoaderReference.TinkersConstruct) loaders.add(new TinkersConstruct());
        if (LoaderReference.Witchery) loaders.add(new Witchery());
        if (LoaderReference.ThaumicHorizons) loaders.add(new ThaumicHorizons());
        if (LoaderReference.ThaumicBases) loaders.add(new ThaumicBases());
        if (LoaderReference.WirelessCraftingTerminal) loaders.add(new WirelessCraftingTerminal());
        if (LoaderReference.CoFHCore) loaders.add(new CoFHCore());
        if (LoaderReference.HardcoreEnderExpansion) loaders.add(new HardcoreEnderExpansion());
        if (LoaderReference.Botania) loaders.add(new Botania());
        if (LoaderReference.HarvestCraft) loaders.add(new HarvestCraft());
        if (LoaderReference.OpenBlocks) loaders.add(new OpenBlocks());
        if (LoaderReference.BloodArsenal) loaders.add(new BloodArsenal());
        if (LoaderReference.BloodMagic) loaders.add(new BloodMagic());
        if (LoaderReference.Avaritia) loaders.add(new Avaritia());
        if (LoaderReference.ThaumicTinkerer) loaders.add(new ThaumicTinkerer());
        if (LoaderReference.ForbiddenMagic) loaders.add(new ForbiddenMagic());
        if (LoaderReference.ElectroMagicTools) loaders.add(new ElectroMagicTools());
        if (LoaderReference.WitchingGadgets) loaders.add(new WitchingGadgets());
        if (LoaderReference.Thaumcraft) loaders.add(new Thaumcraft());
        if (LoaderReference.Automagy) loaders.add(new Automagy());
        if (LoaderReference.GTPlusPlus) loaders.add(new GregtechPlusPlus());

        // LAST
        if (LoaderReference.MineTweaker) loaders.add(new MineTweaker());
    }

    public static void process(String k, ArrayList<MobDrop> drops, MobRecipe recipe) {
        if (!initialized) init();
        for (IExtraLoader loader : loaders) {
            try {
                loader.process(k, drops, recipe);
            } catch (Exception ex) {
                LOG.error(
                    "There was an error while loading " + loader.getClass()
                        .getSimpleName());
                ex.printStackTrace();
            }
        }
    }
}
