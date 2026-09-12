package com.kuba6000.mobsinfo.loader.extras;

import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kuba6000.mobsinfo.api.MobDrop;
import com.kuba6000.mobsinfo.api.MobRecipe;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;

public class Reliquarry implements IExtraLoader {

    private static final Logger LOG = LogManager.getLogger("mobsinfo[Reliquary]");
    private final IExtraLoader delegate;

    public Reliquarry() {
        this(installedVersion(), Reliquarry.class.getClassLoader());
    }

    public Reliquarry(String version, ClassLoader classLoader) {
        ReliquaryCompatibility compatibility = ReliquaryCompatibility.detect(version, classLoader);
        IExtraLoader selected = null;
        try {
            if (compatibility == ReliquaryCompatibility.MODERN) selected = new ReliquaryModern();
            else if (compatibility == ReliquaryCompatibility.LEGACY) selected = new ReliquaryLegacy();
        } catch (LinkageError e) {
            LOG.warn("Cannot initialize Reliquary {} drop support; skipping integration", version, e);
        }
        delegate = selected;
        if (delegate == null) {
            LOG.warn("Unsupported Reliquary version/API: {} ({}); skipping extra drops", version, compatibility);
        } else {
            LOG.info("Reliquary {}: using {} drop support", version, compatibility);
        }
    }

    private static String installedVersion() {
        ModContainer mod = Loader.instance()
            .getIndexedModList()
            .get("xreliquary");
        return mod == null ? "unknown" : mod.getVersion();
    }

    @Override
    public void process(String k, ArrayList<MobDrop> drops, MobRecipe recipe) {
        if (delegate != null) delegate.process(k, drops, recipe);
    }
}
