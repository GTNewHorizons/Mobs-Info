package com.kuba6000.mobsinfo.api;

import java.util.Random;

/** Runtime bridge for comparisons recognized by the coremod. Ordinary RNGs keep virtual dispatch. */
public final class RandomComparison {

    private RandomComparison() {}

    public static int nextInt(Random random, int bound, int threshold, int comparison) {
        if (random != null && random.getClass() == RandomSequencer.class) {
            RandomSequencer sequencer = (RandomSequencer) random;
            if (sequencer.useComparisonWeights) return sequencer.nextIntCompared(bound, threshold, comparison);
        }
        return random.nextInt(bound);
    }

    public static float nextFloat(Random random, float threshold, int comparison) {
        if (random != null && random.getClass() == RandomSequencer.class) {
            RandomSequencer sequencer = (RandomSequencer) random;
            if (sequencer.useComparisonWeights) return sequencer.nextFloatCompared(threshold, comparison);
        }
        return random.nextFloat();
    }
}
