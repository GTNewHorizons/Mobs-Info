package com.kuba6000.mobsinfo.asm;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Random;
import java.util.function.ToIntFunction;

import net.minecraft.launchwrapper.IClassTransformer;

import org.junit.Test;

import com.kuba6000.mobsinfo.api.RandomSequencer;
import com.kuba6000.mobsinfo.mixin.EarlyMixinLoader;

import mobsinfofixtures.ComparisonFixtures;
import mobsinfofixtures.FloatDrops;
import mobsinfofixtures.IntegerDrops;

public class RandomComparisonTransformerTest {

    @Test
    public void repeatedTransformationDoesNotChangeTheDistributionOrCost() throws Exception {
        IClassTransformer transformer = new RandomComparisonTransformer();
        ToIntFunction<Random> twice = transform(
            IntegerDrops.class,
            (name, transformedName, bytes) -> transformer
                .transform(name, transformedName, transformer.transform(name, transformedName, bytes)));
        Distribution result = enumerate(twice, true, 8);
        assertArrayEquals(enumerate(transform(IntegerDrops.class), true, 8).probabilities, result.probabilities, 0d);
        assertEquals(8, result.executions);
    }

    @Test
    public void startupCompatibilitySwitchKeepsOriginalEnumeration() throws Exception {
        String property = "mobsinfo.disableRandomComparisonTransformer";
        String previous = System.getProperty(property);
        try {
            System.setProperty(property, "true");
            Distribution result = enumerate(transform(FloatDrops.class), true, 2);
            assertEquals(10, result.executions);
            assertEquals(0.1, result.probabilities[1], 1e-12);
        } finally {
            if (previous == null) System.clearProperty(property);
            else System.setProperty(property, previous);
        }
    }

    @Test
    public void coremodRegistersAnExecutableDropOptimization() throws Exception {
        String[] registrations = new EarlyMixinLoader().getASMTransformerClass();
        assertTrue("Coremod must register a transformer", registrations != null && registrations.length > 0);
        IClassTransformer registered = (IClassTransformer) Class.forName(registrations[0])
            .getConstructor()
            .newInstance();
        Distribution result = enumerate(transform(IntegerDrops.class, registered), true, 8);
        assertEquals(8, result.executions);
    }

    @Test
    public void conditionalAndUnrecognizedDropCodeKeepsItsFullDistribution() throws Exception {
        for (Class<?> fixture : new Class<?>[] { ComparisonFixtures.ConditionalDrops.class,
            ComparisonFixtures.ReusedValue.class, ComparisonFixtures.JoinedValue.class,
            ComparisonFixtures.IntBoundaries.class, ComparisonFixtures.CaughtException.class }) {
            ToIntFunction<Random> transformed = transform(fixture);
            Distribution original = enumerate(instantiate(fixture), false, 32);
            Distribution optimized = enumerate(transformed, true, 32);
            assertArrayEquals(fixture.getName(), original.probabilities, optimized.probabilities, 1e-12);
            assertTrue(optimized.executions <= original.executions);
        }
    }

    @Test
    public void ordinaryAndCustomRngsPreserveResultsAndSubsequentRandomState() throws Exception {
        for (Class<?> fixture : new Class<?>[] { IntegerDrops.class, FloatDrops.class,
            ComparisonFixtures.ConditionalDrops.class, ComparisonFixtures.IntBoundaries.class,
            ComparisonFixtures.FloatBoundaries.class }) {
            ToIntFunction<Random> original = instantiate(fixture);
            ToIntFunction<Random> transformed = transform(fixture);
            for (int seed = 0; seed < 128; seed++) {
                Random before = new Random(seed);
                Random after = new Random(seed);
                assertEquals(original.applyAsInt(before), transformed.applyAsInt(after));
                assertEquals(before.nextLong(), after.nextLong());
                before = new CustomRandom(seed);
                after = new CustomRandom(seed);
                assertEquals(original.applyAsInt(before), transformed.applyAsInt(after));
                assertEquals(before.nextLong(), after.nextLong());
            }
        }
    }

    @Test
    public void exceptionsKeepTheirOriginalBehavior() throws Exception {
        ToIntFunction<Random> invalid = transform(ComparisonFixtures.InvalidBound.class);
        assertThrows(IllegalArgumentException.class, () -> invalid.applyAsInt(new Random()));
        RandomSequencer sequencer = new RandomSequencer();
        sequencer.useComparisonWeights = true;
        sequencer.newRound();
        assertThrows(IllegalArgumentException.class, () -> invalid.applyAsInt(sequencer));
        assertThrows(NullPointerException.class, () -> invalid.applyAsInt(null));
        assertEquals(3, transform(ComparisonFixtures.CaughtException.class).applyAsInt(new Random() {

            public int nextInt(int bound) {
                throw new IllegalArgumentException();
            }
        }));
    }

    @Test
    public void unmodifiedSequencerConsumersRetainLegacyEnumeration() throws Exception {
        Distribution result = enumerate(transform(FloatDrops.class), false, 2);
        assertEquals(10, result.executions);
        assertEquals(0.1, result.probabilities[1], 1e-12);
    }

    @Test
    public void floatEqualityAndInclusiveBoundariesKeepOneGridPointsWeight() throws Exception {
        Distribution result = enumerate(transform(ComparisonFixtures.FloatBoundaries.class), true, 8);
        double[] marginals = new double[3];
        for (int outcome = 0; outcome < 8; outcome++) {
            for (int bit = 0; bit < 3; bit++) {
                if ((outcome & (1 << bit)) != 0) marginals[bit] += result.probabilities[outcome];
            }
        }
        assertArrayEquals(
            new double[] { 1d / 16777216d, 0.5d + 1d / 16777216d, 0.5d - 1d / 16777216d },
            marginals,
            1e-15);
        assertEquals(8, result.executions);
    }

    @Test
    public void floatDropThresholdUsesTheJavaRandomGridInsteadOfTenPercentBuckets() throws Exception {
        Distribution optimized = enumerate(transform(FloatDrops.class), true, 2);
        // Exactly 419431 of the 2^24 possible nextFloat values are below 0.025f.
        assertEquals(419431d / 16777216d, optimized.probabilities[1], 1e-15);
        assertEquals(1d, optimized.probabilities[0] + optimized.probabilities[1], 1e-15);
        assertEquals(2, optimized.executions);
    }

    @Test
    public void threeDropChecksKeepTheirJointDistributionWithEightExecutions() throws Exception {
        Distribution exhaustive = enumerate(new IntegerDrops(), false, 8);
        Distribution optimized = enumerate(transform(IntegerDrops.class), true, 8);
        assertArrayEquals(exhaustive.probabilities, optimized.probabilities, 1e-9);
        assertEquals(1_000_000, exhaustive.executions);
        assertEquals(8, optimized.executions);
    }

    static Distribution enumerate(ToIntFunction<Random> drops, boolean optimized, int outcomes) {
        RandomSequencer random = new RandomSequencer();
        random.useComparisonWeights = optimized;
        random.newRound();
        Distribution result = new Distribution(outcomes);
        do {
            int outcome = drops.applyAsInt(random);
            result.probabilities[outcome] += random.chance;
            result.executions++;
            assertTrue("Enumeration must terminate within the fixture budget", result.executions <= 1_000_000);
        } while (random.nextRound());
        return result;
    }

    static ToIntFunction<Random> transform(Class<?> fixture) throws Exception {
        return transform(fixture, new RandomComparisonTransformer());
    }

    @SuppressWarnings("unchecked")
    static ToIntFunction<Random> transform(Class<?> fixture, IClassTransformer transformer) throws Exception {
        byte[] bytes;
        try (InputStream input = fixture.getResourceAsStream(
            "/" + fixture.getName()
                .replace('.', '/') + ".class")) {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int length;
            while ((length = input.read(buffer)) != -1) output.write(buffer, 0, length);
            bytes = output.toByteArray();
        }
        byte[] transformed = transformer.transform(fixture.getName(), fixture.getName(), bytes);
        Class<?> type = new ClassLoader(fixture.getClassLoader()) {

            Class<?> loadFixture() {
                return defineClass(fixture.getName(), transformed, 0, transformed.length);
            }
        }.loadFixture();
        return (ToIntFunction<Random>) type.getConstructor()
            .newInstance();
    }

    static class Distribution {

        final double[] probabilities;
        int executions;

        Distribution(int outcomes) {
            probabilities = new double[outcomes];
        }
    }

    @SuppressWarnings("unchecked")
    private static ToIntFunction<Random> instantiate(Class<?> fixture) throws Exception {
        return (ToIntFunction<Random>) fixture.getConstructor()
            .newInstance();
    }

    private static class CustomRandom extends Random {

        CustomRandom(long seed) {
            super(seed);
        }

        @Override
        public int nextInt(int bound) {
            return bound - 1 - super.nextInt(bound);
        }

        @Override
        public float nextFloat() {
            return super.nextFloat() / 2f;
        }
    }
}
