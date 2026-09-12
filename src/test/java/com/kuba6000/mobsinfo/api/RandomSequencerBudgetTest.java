package com.kuba6000.mobsinfo.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.kuba6000.mobsinfo.api.RandomSequencer.GenerationLimitExceededException;

public class RandomSequencerBudgetTest {

    @Test
    public void weightedComparisonsCannotBypassTheGenerationBudget() {
        RandomSequencer random = new RandomSequencer();
        random.newRound();
        assertThrows(
            GenerationLimitExceededException.class,
            () -> {
                for (int attempt = 0; attempt < 100_000; attempt++)
                    random.nextIntCompared(100, 0, RandomSequencer.EQUALITY);
            });
        random.newRound();
        assertThrows(
            GenerationLimitExceededException.class,
            () -> {
                for (int attempt = 0; attempt < 100_000; attempt++)
                    random.nextFloatCompared(0.5f, RandomSequencer.LESS_THAN);
            });
        random.newRound();
        random.forceFloatValue = 0f;
        assertThrows(
            GenerationLimitExceededException.class,
            () -> {
                for (int attempt = 0; attempt < 100_000; attempt++)
                    random.nextFloatCompared(0.5f, RandomSequencer.LESS_THAN);
            });
        random.newRound();
        random.maxWalkCount = 0;
        assertThrows(
            GenerationLimitExceededException.class,
            () -> {
                for (int attempt = 0; attempt < 100_000; attempt++)
                    random.nextIntCompared(100, 0, RandomSequencer.EQUALITY);
            });
    }

    @Test
    public void forcedResultsCannotBypassTheGenerationBudget() {
        RandomSequencer random = new RandomSequencer();
        random.newRound();
        random.forceFloatValue = 0f;
        assertThrows(
            GenerationLimitExceededException.class,
            () -> { for (int attempt = 0; attempt < 100_000; attempt++) random.nextFloat(); });
        random.newRound();
        random.maxWalkCount = 0;
        assertThrows(
            GenerationLimitExceededException.class,
            () -> { for (int attempt = 0; attempt < 100_000; attempt++) random.nextBoolean(); });
        random.newRound();
        random.maxWalkCount = 0;
        assertThrows(
            GenerationLimitExceededException.class,
            () -> { for (int attempt = 0; attempt < 100_000; attempt++) random.nextInt(2); });
    }

    @Test
    public void repeatedRejectedDrawsAreAbortedAndTheSequencerCanBeReused() {
        RandomSequencer random = new RandomSequencer();
        random.newRound();
        // A bounded reproduction of a mod retrying until it draws a nonzero item index.
        assertThrows(GenerationLimitExceededException.class, () -> {
            for (int attempt = 0; attempt < 100_000; attempt++) {
                if (random.nextInt(2) != 0) break;
            }
        });
        random.newRound();
        assertEquals(0, random.nextInt(2));
        assertEquals(0.5d, random.chance, 0d);
        assertTrue(random.nextRound());
        assertEquals(1, random.nextInt(2));
        assertEquals(0.5d, random.chance, 0d);
    }
}
