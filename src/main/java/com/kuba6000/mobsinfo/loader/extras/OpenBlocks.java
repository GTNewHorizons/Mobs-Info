package com.kuba6000.mobsinfo.loader.extras;

import java.util.ArrayList;

import com.kuba6000.mobsinfo.api.MobDrop;
import com.kuba6000.mobsinfo.api.MobRecipe;

import openblocks.common.TrophyHandler;

public class OpenBlocks implements IExtraLoader {

    @Override
    public void process(String k, ArrayList<MobDrop> drops, MobRecipe recipe) {
        TrophyHandler.Trophy trophy = TrophyHandler.Trophy.TYPES.get(k);
        if (trophy != null) {
            MobDrop drop = new MobDrop(trophy.getItemStack(), MobDrop.DropType.Normal, 0, null, null, false, false);
            drop.variableChance = true;
            drop.variableChanceInfo.add(Translations.OPEN_BLOCKS_SMALL_CHANCE.get());
            drops.add(drop);
        }
    }
}
