package com.kuba6000.mobsinfo.loader.extras;

import java.util.ArrayList;
import java.util.Arrays;

import net.minecraft.item.ItemStack;

import com.kuba6000.mobsinfo.api.MobDrop;
import com.kuba6000.mobsinfo.api.MobRecipe;

import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.items.ItemCrystalEssence;
import thaumcraft.common.lib.research.ScanManager;

public class ThaumicBases implements IExtraLoader {

    @Override
    public void process(String k, ArrayList<MobDrop> drops, MobRecipe recipe) {
        AspectList aspects = ScanManager.generateEntityAspects(recipe.entity);
        if (aspects != null && aspects.size() > 0) {
            Aspect[] aspectsAspects = aspects.getAspects();
            for (int i = 0, aspectsAspectsLength = aspectsAspects.length; i < aspectsAspectsLength; i++) {
                Aspect aspect = aspectsAspects[i];
                int chance = (int) (MobDrop.getChanceBasedOnFromTo(1, 1 + aspects.getAmount(aspect)) * 10000d);
                chance /= 2;
                chance *= (Math.pow(0.5d, i));
                ItemStack stack = new ItemStack(ConfigItems.itemCrystalEssence, 1, 0);
                ((ItemCrystalEssence) ConfigItems.itemCrystalEssence)
                    .setAspects(stack, new AspectList().add(aspect, 1));
                MobDrop drop = new MobDrop(stack, MobDrop.DropType.Normal, chance, null, null, false, false);
                drop.clampChance();
                drop.variableChance = true;
                drop.variableChanceInfo.addAll(
                    Arrays.asList(
                        Translations.CHANCE.get(((double) drop.chance / 100d)),
                        "* " + Translations.DROPS_ONLY_WITH_ENCHANT.get("Vaporising")));
                drop.chance = 0;
                drops.add(drop);
            }
        }
    }
}
