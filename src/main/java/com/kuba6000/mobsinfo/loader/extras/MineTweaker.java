package com.kuba6000.mobsinfo.loader.extras;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import net.minecraft.item.ItemStack;

import com.kuba6000.mobsinfo.api.MobDrop;
import com.kuba6000.mobsinfo.api.MobRecipe;

import minetweaker.MineTweakerAPI;
import minetweaker.api.entity.IEntityDefinition;
import minetweaker.api.item.IItemStack;
import minetweaker.mc1710.item.MCItemStack;
import stanhebben.zenscript.value.IntRange;

public class MineTweaker implements IExtraLoader {

    @Override
    public void process(String k, ArrayList<MobDrop> drops, MobRecipe recipe) {

        try {
            MineTweakerAPI.game.getClass()
                .getMethod("getEntity", String.class);
        } catch (Exception ignored) // not supported
        {
            return;
        }

        IEntityDefinition ie = MineTweakerAPI.game.getEntity(k);
        if (ie != null) {
            for (Map.Entry<IItemStack, IntRange> entry : ie.getDropsToAdd()
                .entrySet()) {
                IntRange r = entry.getValue();
                // Get average chance
                double chance;
                if (r.getFrom() == 0 && r.getTo() == 0) chance = 1d;
                else {
                    double a = r.getFrom();
                    double b = r.getTo();
                    chance = ((b * b) + b - (a * a) + a) / (2 * (b - a + 1));
                }
                ItemStack stack = ((ItemStack) entry.getKey()
                    .getInternal()).copy();
                MobDrop drop = new MobDrop(
                    stack,
                    MobDrop.DropType.Normal,
                    (int) (chance * 10000),
                    null,
                    null,
                    false,
                    false);
                drop.clampChance();
                drops.add(drop);
            }
            for (Map.Entry<IItemStack, IntRange> entry : ie.getDropsToAddPlayerOnly()
                .entrySet()) {
                IntRange r = entry.getValue();
                // Get average chance
                double chance;
                if (r.getFrom() == 0 && r.getTo() == 0) chance = 1d;
                else {
                    double a = r.getFrom();
                    double b = r.getTo();
                    chance = ((b * b) + b - (a * a) + a) / (2 * (b - a + 1));
                }
                ItemStack stack = ((ItemStack) entry.getKey()
                    .getInternal()).copy();
                MobDrop drop = new MobDrop(
                    stack,
                    MobDrop.DropType.Normal,
                    (int) (chance * 10000),
                    null,
                    null,
                    false,
                    true);
                drop.clampChance();
                drops.add(drop);
            }
            for (IItemStack istack : ie.getDropsToRemove()) {
                List<MobDrop> toRemove = drops.stream()
                    .filter(d -> istack.matches(new MCItemStack(d.stack)))
                    .collect(Collectors.toList());
                drops.removeAll(toRemove);
            }
        }
    }
}
