package com.kuba6000.mobsinfo.loader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.village.MerchantRecipe;

import org.junit.BeforeClass;
import org.junit.Test;

import com.kuba6000.mobsinfo.api.RandomSequencer;
import com.kuba6000.mobsinfo.api.VillagerTrade;
import com.kuba6000.mobsinfo.asm.RandomComparisonTransformer;

import cpw.mods.fml.common.registry.TestItemRegistry;
import cpw.mods.fml.common.registry.VillagerRegistry.IVillageTradeHandler;
import mobsinfofixtures.TradeFixtures;

public class VillagerTradeGenerationTest {

    @Test
    public void floatOfferUsesExactProbabilityWithOptimizationAndLegacyGridWithoutIt() throws Exception {
        RandomSequencer random = new RandomSequencer();
        IVillageTradeHandler handler = transform(TradeFixtures.FloatOffer.class);
        var legacy = VillagerTradesLoader.generateTradesForHandler(handler, null, random, 0, -1);
        random.useComparisonWeights = true;
        var weighted = VillagerTradesLoader.generateTradesForHandler(handler, null, random, 0, -1);
        assertEquals(10, legacy.executions);
        assertEquals(2, weighted.executions);
        assertEquals(
            0.1,
            legacy.trades.get(0)
                .getChance(),
            1e-15);
        assertEquals(
            419431d / 16777216d,
            weighted.trades.get(0)
                .getChance(),
            1e-15);
    }

    @Test
    public void weightedChoicesPreserveRandomPricesAndOptionalSecondInputs() throws Exception {
        RandomSequencer random = new RandomSequencer();
        var legacy = VillagerTradesLoader.generateTradesForHandler(new TradeFixtures.Prices(), null, random, 0, -1);
        random.useComparisonWeights = true;
        var weighted = VillagerTradesLoader
            .generateTradesForHandler(transform(TradeFixtures.Prices.class), null, random, 0, -1);
        assertEquals(44, legacy.executions);
        assertEquals(19, weighted.executions);
        assertEquals(2, weighted.trades.size());
        for (VillagerTrade trade : weighted.trades) {
            VillagerTrade original = legacy.trades.stream()
                .filter(t -> t.hasSecondInput() == trade.hasSecondInput())
                .findFirst()
                .get();
            assertEquals(original.getChance(), trade.getChance(), 1e-12);
            assertEquals(0.1, trade.getChance(), 1e-12);
            assertEquals(new HashSet<>(Arrays.asList(1, 2, 3)), trade.getFirstInput().possibleSizes);
            assertEquals(new HashSet<>(Arrays.asList(1, 2)), trade.getOutput().possibleSizes);
            if (trade.hasSecondInput()) {
                assertEquals(1, trade.getSecondInput().stack.getItemDamage());
                assertEquals(new HashSet<>(Arrays.asList(1, 2)), trade.getSecondInput().possibleSizes);
            }
        }
    }

    @Test
    public void handlerFailureDoesNotContaminateTheNextEnumeration() throws Exception {
        RandomSequencer random = new RandomSequencer();
        random.useComparisonWeights = true;
        assertThrows(
            IllegalStateException.class,
            () -> VillagerTradesLoader.generateTradesForHandler((villager, list, rng) -> {
                rng.nextInt(7);
                throw new IllegalStateException("Fixture failure");
            }, null, random, 0, -1));
        assertEquals(0, random.walkCounter);
        assertEquals(1d, random.chance, 0d);
        assertTrue(random.useComparisonWeights);
        var result = VillagerTradesLoader
            .generateTradesForHandler(transform(TradeFixtures.Comparisons.class), null, random, 0, -1);
        assertTrue(result.complete);
        assertEquals(8, result.executions);
        assertEquals(1d, result.enumeratedWeight, 1e-12);
    }

    @Test
    public void reusedStacksRetainEveryObservedQuantity() throws Exception {
        RandomSequencer random = new RandomSequencer();
        random.useComparisonWeights = true;
        var result = VillagerTradesLoader
            .generateTradesForHandler(transform(TradeFixtures.MutableOffer.class), null, random, 0, -1);
        assertTrue(result.complete);
        assertEquals(6, result.executions);
        assertEquals(1, result.trades.size());
        VillagerTrade trade = result.trades.get(0);
        assertEquals(new HashSet<>(Arrays.asList(1, 2, 3)), trade.getFirstInput().possibleSizes);
        assertEquals(new HashSet<>(Arrays.asList(1, 2)), trade.getOutput().possibleSizes);
        assertEquals(1d, trade.getChance(), 1e-12);
    }

    @Test
    public void reusedNbtKeepsDistinctOffersAndDoesNotMutateHandlerEnchantments() {
        ItemStack input = new ItemStack(TradeFixtures.INPUT);
        ItemStack output = new ItemStack(TradeFixtures.OUTPUT);
        input.setTagCompound(new NBTTagCompound());
        output.setTagCompound(new NBTTagCompound());
        output.stackTagCompound.setInteger(MobRecipeLoader.randomEnchantmentDetectedString, 12);
        output.stackTagCompound.setString("ench", "handler-owned enchantment");
        var result = VillagerTradesLoader.generateTradesForHandler((villager, list, rng) -> {
            // Revisit each NBT identity at two prices, mutating the same compound in place.
            input.stackSize = rng.nextInt(2) + 1;
            input.stackTagCompound.setInteger("variant", rng.nextInt(2));
            list.add(new MerchantRecipe(input, output));
        }, null, new RandomSequencer(), 0, -1);
        assertTrue(result.complete);
        assertEquals(4, result.executions);
        assertEquals(2, result.trades.size());
        Set<Integer> variants = new HashSet<>();
        for (VillagerTrade trade : result.trades) {
            variants.add(trade.getFirstInput().stack.stackTagCompound.getInteger("variant"));
            assertEquals(new HashSet<>(Arrays.asList(1, 2)), trade.getFirstInput().possibleSizes);
            assertEquals(0.5, trade.getChance(), 1e-12);
            assertEquals(Integer.valueOf(12), trade.getOutput().enchantability);
            assertFalse(trade.getOutput().stack.stackTagCompound.hasKey("ench"));
        }
        assertEquals(new HashSet<>(Arrays.asList(0, 1)), variants);
        assertEquals("handler-owned enchantment", output.stackTagCompound.getString("ench"));
        assertEquals(12, output.stackTagCompound.getInteger(MobRecipeLoader.randomEnchantmentDetectedString));
    }

    @Test
    public void excessiveEstimatedWorkStopsAfterTheFirstPathIncludingHugeTrees() {
        for (int[] bounds : new int[][] { { 101, 103 }, { 65536, 65536, 65536, 65536 } }) {
            var result = VillagerTradesLoader.generateTradesForHandler((villager, list, rng) -> {
                for (int bound : bounds) rng.nextInt(bound);
                list.add(new MerchantRecipe(new ItemStack(TradeFixtures.INPUT), new ItemStack(TradeFixtures.OUTPUT)));
            }, null, new RandomSequencer(), 10_000, -1);
            assertFalse(result.complete);
            assertEquals(1, result.executions);
            assertEquals(1, result.trades.size());
            assertTrue(result.enumeratedWeight > 0);
            assertTrue(result.enumeratedWeight < 0.0001);
            assertEquals(
                result.enumeratedWeight,
                result.trades.get(0)
                    .getChance(),
                0d);
        }
    }

    @Test
    public void rareWeightedOffersFitTheWorkLimitAndRetainTheirProbability() throws Exception {
        RandomSequencer random = new RandomSequencer();
        random.useComparisonWeights = true;
        var result = VillagerTradesLoader
            .generateTradesForHandler(transform(TradeFixtures.VeryRareOffer.class), null, random, 2, -1);
        assertTrue(result.complete);
        assertEquals(2, result.executions);
        assertEquals(2, result.estimatedPaths);
        assertEquals(
            1e-8,
            result.trades.get(0)
                .getChance(),
            1e-20);
        assertEquals(1d, result.enumeratedWeight, 1e-15);
    }

    @Test
    public void expensiveLaterBranchIsRejectedAsSoonAsItIsObserved() {
        var result = VillagerTradesLoader.generateTradesForHandler((villager, list, rng) -> {
            if (rng.nextBoolean()) {
                rng.nextInt(101);
                rng.nextInt(103);
            }
        }, null, new RandomSequencer(), 10_000, -1);
        assertFalse(result.complete);
        assertEquals(2, result.executions);
        assertEquals(20806, result.estimatedPaths);
    }

    @Test
    public void budgetsMarkPartialResultsWithoutRenormalizingThem() {
        RandomSequencer random = new RandomSequencer();
        var limited = VillagerTradesLoader
            .generateTradesForHandler(new TradeFixtures.Comparisons(), null, random, 1, -1);
        assertFalse(limited.complete);
        assertEquals(1, limited.executions);
        assertEquals(1d / 8000d, limited.enumeratedWeight, 1e-15);
        assertEquals(
            1d / 8000d,
            limited.trades.get(0)
                .getChance(),
            1e-15);
        var timedOut = VillagerTradesLoader
            .generateTradesForHandler(new TradeFixtures.Comparisons(), null, random, 0, 0);
        assertFalse(timedOut.complete);
        assertEquals(1, timedOut.executions);
        var finished = VillagerTradesLoader.generateTradesForHandler((villager, list, rng) -> {}, null, random, 1, 0);
        assertTrue(finished.complete);
        assertEquals(1d, finished.enumeratedWeight, 0d);
    }

    @BeforeClass
    public static void registerFixtureItems() {
        TestItemRegistry.register(30001, "mobsinfo_test:trade_input", TradeFixtures.INPUT);
        TestItemRegistry.register(30002, "mobsinfo_test:trade_output", TradeFixtures.OUTPUT);
    }

    @Test
    public void weightedHandlerGenerationPreservesOfferProbabilities() throws Exception {
        RandomSequencer legacy = new RandomSequencer();
        RandomSequencer weighted = new RandomSequencer();
        weighted.useComparisonWeights = true;
        var before = VillagerTradesLoader
            .generateTradesForHandler(new TradeFixtures.Comparisons(), null, legacy, 0, -1);
        var after = VillagerTradesLoader
            .generateTradesForHandler(transform(TradeFixtures.Comparisons.class), null, weighted, 0, -1);
        assertTrue(before.complete);
        assertTrue(after.complete);
        assertEquals(8000, before.executions);
        assertEquals(8, after.executions);
        Map<Integer, Double> original = probabilities(before.trades);
        Map<Integer, Double> optimized = probabilities(after.trades);
        assertEquals(original.keySet(), optimized.keySet());
        for (int meta : original.keySet()) assertEquals(original.get(meta), optimized.get(meta), 1e-12);
        assertEquals(0.05, optimized.get(0), 1e-12);
        assertEquals(0.15, optimized.get(1), 1e-12);
        assertEquals(0.1, optimized.get(2), 1e-12);
    }

    private static Map<Integer, Double> probabilities(Iterable<VillagerTrade> trades) {
        Map<Integer, Double> result = new TreeMap<>();
        for (VillagerTrade trade : trades) result.put(trade.getOutput().stack.getItemDamage(), trade.getChance());
        return result;
    }

    private static IVillageTradeHandler transform(Class<?> fixture) throws Exception {
        byte[] bytes;
        try (InputStream input = fixture.getResourceAsStream(
            "/" + fixture.getName()
                .replace('.', '/') + ".class")) {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int count;
            while ((count = input.read(buffer)) != -1) output.write(buffer, 0, count);
            bytes = output.toByteArray();
        }
        byte[] transformed = new RandomComparisonTransformer().transform(fixture.getName(), fixture.getName(), bytes);
        Class<?> type = new ClassLoader(fixture.getClassLoader()) {

            Class<?> loadFixture() {
                return defineClass(fixture.getName(), transformed, 0, transformed.length);
            }
        }.loadFixture();
        return (IVillageTradeHandler) type.getConstructor()
            .newInstance();
    }
}
