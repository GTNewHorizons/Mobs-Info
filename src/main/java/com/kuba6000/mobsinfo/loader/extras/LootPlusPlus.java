package com.kuba6000.mobsinfo.loader.extras;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.item.ItemRecord;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.kuba6000.mobsinfo.api.IChanceModifier;
import com.kuba6000.mobsinfo.api.MobDrop;
import com.kuba6000.mobsinfo.api.MobRecipe;
import com.kuba6000.mobsinfo.mixin.early.minecraft.EntityLivingBaseAccessor;
import com.tmtravlr.lootplusplus.LootPPHelper;

public class LootPlusPlus implements IExtraLoader {

    @Override
    public void process(String name, ArrayList<MobDrop> drops, MobRecipe recipe) {
        // LootPPEventHandler.onLivingDrops, Loot++ 0.24. Read live tables: these load at server startup.
        String registeredName = (String) EntityList.classToStringMapping.get(recipe.entity.getClass());
        if (registeredName == null) registeredName = name;
        Set<ItemStack> removals = LootPPHelper.entityDropRemovals.get(registeredName);
        if (removals != null) drops.removeIf(
            drop -> removals.stream()
                .anyMatch(rule -> matches(rule, drop.stack)));
        if (recipe.entity instanceof EntityCreeper) processRecords(drops);
        List<LootPPHelper.EntityDropInfo> groups = LootPPHelper.entityDropAdditions.get(registeredName);
        if (groups != null) for (LootPPHelper.EntityDropInfo group : groups) addGroup(drops, group);
    }

    private static void processRecords(ArrayList<MobDrop> drops) {
        if (LootPPHelper.creepersDropRecords && !LootPPHelper.creepersDropAllRecords) return;
        if (!LootPPHelper.creepersDropRecords || LootPPHelper.allRecords.isEmpty()) return;
        // Natural records are emitted after LivingDropsEvent, then replaced in the server tick handler.
        // They are not part of our base dropFewItems generation. Do not erase ordinary item drops here.
        for (ItemRecord record : LootPPHelper.allRecords) {
            MobDrop drop = MobDrop.create(record)
                .withChance(1d / LootPPHelper.allRecords.size());
            condition(drop, new SkeletonKill());
            drops.add(drop);
        }
    }

    public static class SkeletonKill implements IChanceModifier {

        @Override
        public String getDescription() {
            return Translations.LOOTPLUSPLUS_SKELETON_KILL.get();
        }

        @Override
        public double apply(double chance, @Nonnull World world, @Nonnull List<ItemStack> drops, Entity attacker,
            EntityLiving victim) {
            return attacker instanceof EntitySkeleton ? chance : 0;
        }
    }

    private static void addGroup(ArrayList<MobDrop> drops, LootPPHelper.EntityDropInfo group) {
        if (group == null || group.dropList == null || group.dropList.isEmpty()) return;
        double[] probabilities = selectionProbabilities(group.dropList);
        for (int i = 0; i < group.dropList.size(); i++) {
            LootPPHelper.DropInfo entry = group.dropList.get(i);
            // Spawned entities are not item drops, but their weights still participate in selection.
            if (entry.entityTag != null || entry.stack == null
                || entry.stack.getItem() == null
                || probabilities[i] == 0) continue;
            double amount = (entry.min + (double) Math.max(entry.min, entry.max)) / 2d;
            if (amount < 0 || (amount == 0 && !group.increasedByLooting)) continue;
            ItemStack stack = entry.stack.copy();
            stack.stackSize = 1;
            MobDrop drop = MobDrop.create(stack)
                .withChance(rollChance(group.chance) * probabilities[i] * amount);
            drop.clampChance();
            if (group.increasedByLooting) drop.withLooting();
            if (group.playerKill) condition(drop, new PlayerCredit());
            drops.add(drop);
        }
    }

    private static void condition(MobDrop drop, IChanceModifier modifier) {
        if (!drop.variableChance) drop.withChanceModifiers(new IChanceModifier.NormalChance(drop.chance / 100d));
        drop.withChanceModifiers(modifier);
    }

    public static class PlayerCredit implements IChanceModifier {

        @Override
        public String getDescription() {
            return Translations.LOOTPLUSPLUS_PLAYER_CREDIT.get();
        }

        @Override
        public double apply(double chance, @Nonnull World world, @Nonnull List<ItemStack> drops, Entity attacker,
            EntityLiving victim) {
            return victim != null && ((EntityLivingBaseAccessor) victim).getRecentlyHit() > 0 ? chance : 0;
        }
    }

    private static double rollChance(float threshold) {
        // Loot++ uses nextFloat() <= threshold, including its discrete endpoint at zero.
        if (Float.isNaN(threshold) || threshold >= 1) return 1;
        if (threshold < 0) return 0;
        return (Math.floor(threshold * 16777216d) + 1d) / 16777216d;
    }

    private static double[] selectionProbabilities(List<LootPPHelper.DropInfo> entries) {
        int total = 0;
        for (LootPPHelper.DropInfo entry : entries) total += entry.weight;
        int bound = Math.max(1, total);
        int cumulative = 0;
        int covered = 0;
        double[] probabilities = new double[entries.size()];
        for (int i = 0; i < entries.size(); i++) {
            cumulative += entries.get(i).weight;
            int upper = Math.max(covered, Math.min(bound, cumulative));
            probabilities[i] = (upper - covered) / (double) bound;
            covered = upper;
        }
        // The original handler falls back to the first entry for uncovered rolls (invalid weights).
        probabilities[0] += (bound - covered) / (double) bound;
        return probabilities;
    }

    private static boolean matches(ItemStack rule, ItemStack stack) {
        return rule != null && stack != null
            && rule.getItem() == stack.getItem()
            && (rule.getItemDamage() == 32767 || rule.getItemDamage() == stack.getItemDamage())
            && (rule.stackTagCompound == null || rule.stackTagCompound.hasNoTags()
                || rule.stackTagCompound.equals(stack.stackTagCompound));
    }
}
