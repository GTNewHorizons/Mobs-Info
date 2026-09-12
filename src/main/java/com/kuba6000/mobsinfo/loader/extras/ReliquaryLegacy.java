package com.kuba6000.mobsinfo.loader.extras;

import java.util.ArrayList;

import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.item.Item;

import com.kuba6000.mobsinfo.api.MobDrop;
import com.kuba6000.mobsinfo.api.MobRecipe;

import cpw.mods.fml.common.registry.GameRegistry;

/** Stable Reliquary 1.2, before the configurable mob_ingredient system. */
final class ReliquaryLegacy implements IExtraLoader {

    @Override
    public void process(String k, ArrayList<MobDrop> drops, MobRecipe recipe) {
        if (recipe.entity instanceof EntitySquid) {
            Item item = GameRegistry.findItem("xreliquary", "squid_beak");
            if (item == null) return;
            // Original 1.2: 0.04 + 0.07 * (lootingLevel + 1).
            drops.add(
                MobDrop.create(item)
                    .withChance(0.11d)
                    .withLooting());
        } else if (recipe.entity instanceof EntityWitch) {
            Item item = GameRegistry.findItem("xreliquary", "witch_hat");
            if (item == null) return;
            // Original 1.2 uses nextBoolean(), with no Looting adjustment.
            drops.add(
                MobDrop.create(item)
                    .withChance(0.5d));
        }
    }

}
