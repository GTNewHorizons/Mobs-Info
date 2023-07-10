package com.kuba6000.mobsinfo.loader.extras;

import java.util.ArrayList;
import java.util.Arrays;

import net.minecraft.entity.monster.EntityMagmaCube;
import net.minecraft.entity.monster.EntitySlime;

import com.kuba6000.mobsinfo.api.MobDrop;
import com.kuba6000.mobsinfo.api.MobRecipe;

public class Minecraft implements IExtraLoader {

    @Override
    public void process(String k, ArrayList<MobDrop> drops, MobRecipe recipe) {
        if (recipe.entity.getClass() == EntitySlime.class) {
            drops.get(0).variableChance = true;
            drops.get(0).variableChanceInfo.addAll(
                Arrays.asList(
                    Translations.CHANCE.get((double) drops.get(0).chance / 100d),
                    "* " + Translations.MINECRAFT_SLIME.get()));
        } else if (recipe.entity.getClass() == EntityMagmaCube.class) {
            drops.get(0).variableChance = true;
            drops.get(0).variableChanceInfo.addAll(
                Arrays.asList(
                    Translations.CHANCE.get((double) drops.get(0).chance / 100d),
                    "* " + Translations.MINECRAFT_MAGMA_CUBE.get()));
        }
    }

}
