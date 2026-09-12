package com.kuba6000.mobsinfo.loader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import org.junit.Test;

public class DropCollectorTest {

    @Test
    public void rareDamageVariantsKeepTheirProbabilityWhenPathsAreGrouped() {
        Item item = new Item().setMaxDamage(100);
        MobRecipeLoader.dropCollector collector = new MobRecipeLoader.dropCollector();
        MobRecipeLoader.droplist drops = new MobRecipeLoader.droplist();
        collector.addDrop(drops, new ItemStack(item, 1, 10), 0.99);
        collector.addDrop(drops, new ItemStack(item, 1, 90), 0.01);
        Map<Integer, Integer> damage = drops.get(0)
            .getDamageWeights();
        int total = damage.values()
            .stream()
            .mapToInt(Integer::intValue)
            .sum();
        assertEquals(0.99, (double) damage.get(10) / total, 1e-6);
        assertEquals(0.01, (double) damage.get(90) / total, 1e-6);
    }

    @Test
    public void damageDistributionUsesPathWeightsAndItemQuantities() {
        Item item = new Item().setMaxDamage(100);
        MobRecipeLoader.dropCollector collector = new MobRecipeLoader.dropCollector();
        MobRecipeLoader.droplist drops = new MobRecipeLoader.droplist();
        collector.addDrop(drops, new ItemStack(item, 4, 10), 0.5);
        collector.addDrop(drops, new ItemStack(item, 1, 90), 0.5);
        assertEquals(1, drops.size());
        assertTrue(drops.get(0).isDamageRandomized);
        Map<Integer, Integer> damage = drops.get(0)
            .getDamageWeights();
        int total = damage.values()
            .stream()
            .mapToInt(Integer::intValue)
            .sum();
        assertEquals(0.8, (double) damage.get(10) / total, 1e-6);
        assertEquals(0.2, (double) damage.get(90) / total, 1e-6);
        assertEquals(
            25000,
            drops.get(0)
                .getchance(10000));
    }

    @Test
    public void lootingIsComparedByExpectedItemCountRatherThanNumberOfPaths() {
        Item item = new Item();
        MobRecipeLoader.dropCollector collector = new MobRecipeLoader.dropCollector();
        MobRecipeLoader.droplist normal = new MobRecipeLoader.droplist();
        MobRecipeLoader.droplist looting = new MobRecipeLoader.droplist();
        collector.addDrop(normal, new ItemStack(item, 1), 0.1);
        collector.addDrop(normal, new ItemStack(item, 1), 0.1);
        collector.addDrop(looting, new ItemStack(item, 1), 0.3);
        assertEquals(
            2000,
            normal.get(0)
                .getchance(10000));
        assertEquals(
            3000,
            looting.get(0)
                .getchance(10000));
        assertTrue(
            looting.get(0)
                .hasHigherExpectedCountThan(normal.get(0)));
        assertFalse(
            normal.get(0)
                .hasHigherExpectedCountThan(looting.get(0)));
    }
}
