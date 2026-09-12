package com.kuba6000.mobsinfo.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;

import org.junit.Test;

public class RandomSequencerTest {

    @Test
    public void veryRareAlternativesDoNotLosePrecisionBySubtractingFromOne() {
        RandomSequencer random = new RandomSequencer();
        random.newRound();
        double rare = 0;
        do {
            if (random.nextIntCompared(Integer.MAX_VALUE, Integer.MAX_VALUE - 1, RandomSequencer.EQUALITY)
                == Integer.MAX_VALUE - 1) rare += random.chance;
        } while (random.nextRound());
        assertEquals(1d / Integer.MAX_VALUE, rare, 1e-25);
    }

    @Test
    public void integerComparisonsMatchExhaustiveProbabilitiesAtRangeBoundaries() {
        for (int bound : new int[] { 1, 2, 7 }) {
            for (int threshold : new int[] { Integer.MIN_VALUE, -1, 0, 1, 3, 7, Integer.MAX_VALUE }) {
                for (int comparison = RandomSequencer.EQUALITY; comparison
                    <= RandomSequencer.LESS_OR_EQUAL; comparison++) {
                    int matching = 0;
                    for (int value = 0; value < bound; value++) {
                        if (matches(value, threshold, comparison)) matching++;
                    }
                    RandomSequencer random = new RandomSequencer();
                    random.newRound();
                    double probability = 0;
                    double total = 0;
                    do {
                        int value = random.nextIntCompared(bound, threshold, comparison);
                        if (matches(value, threshold, comparison)) probability += random.chance;
                        total += random.chance;
                    } while (random.nextRound());
                    assertEquals((double) matching / bound, probability, 1e-15);
                    assertEquals(1d, total, 1e-15);
                }
            }
        }
    }

    @Test
    public void forcedFloatAndWalkLimitRemainEffectiveForWeightedChoices() {
        RandomSequencer random = new RandomSequencer();
        random.newRound();
        random.forceFloatValue = 0.75f;
        assertEquals(0.75f, random.nextFloatCompared(0.5f, RandomSequencer.LESS_THAN), 0f);
        assertEquals(1d, random.chance, 0d);
        assertFalse(random.nextRound());
        random.newRound();
        random.maxWalkCount = 0;
        assertEquals(0, random.nextIntCompared(100, 0, RandomSequencer.EQUALITY));
        assertEquals(0f, random.nextFloatCompared(0.5f, RandomSequencer.LESS_THAN), 0f);
        assertEquals(1d, random.chance, 0d);
        assertFalse(random.nextRound());
    }

    @Test
    public void weightedChoicesRejectInvalidBounds() {
        RandomSequencer random = new RandomSequencer();
        random.newRound();
        assertThrows(IllegalArgumentException.class, () -> random.nextIntCompared(0, 0, RandomSequencer.EQUALITY));
        assertThrows(IllegalArgumentException.class, () -> random.nextIntCompared(-1, 0, RandomSequencer.EQUALITY));
    }

    private static boolean matches(int value, int threshold, int comparison) {
        if (comparison == RandomSequencer.EQUALITY) return value == threshold;
        if (comparison == RandomSequencer.LESS_THAN) return value < threshold;
        return value <= threshold;
    }

    @Test
    public void floatThresholdsHandleEmptyFullAndSinglePointRanges() {
        float[] thresholds = { Float.NEGATIVE_INFINITY, -1f, -0f, Float.MIN_VALUE, 0.025f, 0.5f, 1f,
            Float.POSITIVE_INFINITY, Float.NaN };
        int[] matchingPoints = { 0, 0, 0, 1, 419431, 8388608, 16777216, 16777216, 0 };
        for (int index = 0; index < thresholds.length; index++) {
            RandomSequencer random = new RandomSequencer();
            random.newRound();
            double probability = 0;
            double total = 0;
            do {
                if (random.nextFloatCompared(thresholds[index], RandomSequencer.LESS_THAN) < thresholds[index])
                    probability += random.chance;
                total += random.chance;
            } while (random.nextRound());
            assertEquals(matchingPoints[index] / 16777216d, probability, 1e-15);
            assertEquals(1d, total, 1e-15);
        }
    }

    @Test
    public void directComparisonsPreserveDropDistributionWithTwoWeightedChoices() {
        RandomSequencer random = new RandomSequencer();
        random.newRound();
        double[] distribution = new double[2];
        int executions = 0;
        do {
            int drop = random.nextIntCompared(100, 0, 0) == 0 ? 1 : 0;
            distribution[drop] += random.chance;
            executions++;
        } while (random.nextRound());
        assertEquals(0.99, distribution[0], 1e-12);
        assertEquals(0.01, distribution[1], 1e-12);
        assertEquals(2, executions);
    }
}
