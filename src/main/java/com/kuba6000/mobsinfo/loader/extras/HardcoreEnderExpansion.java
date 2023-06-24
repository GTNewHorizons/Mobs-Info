package com.kuba6000.mobsinfo.loader.extras;

import java.util.ArrayList;

import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.item.ItemStack;

import com.kuba6000.mobsinfo.api.MobDrop;
import com.kuba6000.mobsinfo.api.MobRecipe;

import chylex.hee.init.ItemList;

public class HardcoreEnderExpansion implements IExtraLoader {

    @Override
    public void process(String k, ArrayList<MobDrop> drops, MobRecipe recipe) {
        if (recipe.entity.getClass() == EntitySilverfish.class) {
            MobDrop drop = new MobDrop(
                new ItemStack(ItemList.silverfish_blood),
                MobDrop.DropType.Normal,
                179,
                null,
                null,
                false,
                false);
            drop.variableChance = true;
            drop.variableChanceInfo
                .add(Translations.CHANCE.get(1.79d) + " (" + Translations.OR_USING.get(7.14d, "golden sword") + ")");
            drops.add(drop);
        }
    }
}
