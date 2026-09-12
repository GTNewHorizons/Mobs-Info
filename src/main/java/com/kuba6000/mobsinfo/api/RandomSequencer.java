package com.kuba6000.mobsinfo.api;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.enchantment.Enchantment;

public class RandomSequencer extends Random {

    public static final int EQUALITY = 0;
    public static final int LESS_THAN = 1;
    public static final int LESS_OR_EQUAL = 2;

    private static final long serialVersionUID = 109358312784613473L;

    // A retry loop may never return to the loader's timeout check.
    private static final int MAX_CALLS_PER_ROUND = 10_000;
    private int callsThisRound;

    public static class GenerationLimitExceededException extends RuntimeException {

        private static final long serialVersionUID = 1L;

        public GenerationLimitExceededException() {
            super("Too many random calls in one generation round");
        }
    }

    private void checkGenerationLimit() {
        if (callsThisRound >= MAX_CALLS_PER_ROUND) {
            nexts.clear();
            throw new GenerationLimitExceededException();
        }
        callsThisRound++;
    }

    private static class nexter {

        private final int bound;
        private int next;
        private int alternative;
        private double firstWeight;
        private double secondWeight;
        private boolean weighted;

        public nexter(int type, int bound) {
            this.next = 0;
            this.bound = bound;
        }

        private boolean getBoolean() {
            return next == 1;
        }

        private int getInt() {
            return weighted ? (next == 0 ? 0 : alternative) : next;
        }

        private float getFloat() {
            return next * 0.1f;
        }

        private boolean next() {
            next++;
            return next >= bound;
        }
    }

    private final ArrayList<nexter> nexts = new ArrayList<>();
    public int walkCounter = 0;
    public double chance;
    public boolean exceptionOnEnchantTry = false;
    public int maxWalkCount = -1;
    public float forceFloatValue = -1.f;

    /** Opt-in for bytecode comparison hooks. Kept across rounds; disabled for legacy API consumers. */
    public boolean useComparisonWeights = false;
    /** Number of weighted comparison evaluations since newRound, for generation diagnostics. */
    public long comparisonCalls = 0;

    /**
     * Enumerates representatives of values with the same comparison result. The returned value must
     * only be used in the specified comparison (or its negation), never as an item count or index.
     */
    public int nextIntCompared(int bound, int threshold, int comparison) {
        if (bound <= 0) throw new IllegalArgumentException("bound must be positive");
        if (exceptionOnEnchantTry) return nextInt(bound);
        checkGenerationLimit();
        if (comparison == EQUALITY) {
            if (threshold < 0 || threshold >= bound) return nextWeighted(bound, bound, 0);
            return threshold == 0 ? nextWeighted(1, bound, 1) : nextWeighted(bound - 1, bound, threshold);
        }
        if (comparison != LESS_THAN && comparison != LESS_OR_EQUAL)
            throw new IllegalArgumentException("Unknown comparison");
        long split = (long) threshold + (comparison == LESS_OR_EQUAL ? 1 : 0);
        split = Math.max(0, Math.min(bound, split));
        return nextWeighted(split == 0 ? bound : split, bound, (int) split);
    }

    /** Same contract as nextIntCompared, using the 2^24 equally likely values of Random.nextFloat. */
    public float nextFloatCompared(float threshold, int comparison) {
        checkGenerationLimit();
        if (forceFloatValue != -1f) return forceFloatValue;
        final int total = 1 << 24;
        double scaled = (double) threshold * total;
        if (comparison == EQUALITY) {
            if (!(scaled >= 0 && scaled < total) || scaled != Math.floor(scaled))
                return nextWeighted(total, total, 0) / (float) total;
            int value = (int) scaled;
            return (value == 0 ? nextWeighted(1, total, 1) : nextWeighted(total - 1, total, value)) / (float) total;
        }
        if (comparison != LESS_THAN && comparison != LESS_OR_EQUAL)
            throw new IllegalArgumentException("Unknown comparison");
        double boundary = comparison == LESS_THAN ? Math.ceil(scaled) : Math.floor(scaled) + 1d;
        int split = (int) Math.max(0d, Math.min(total, boundary));
        return nextWeighted(split == 0 ? total : split, total, split) / (float) total;
    }

    private int nextWeighted(long firstCount, long total, int alternative) {
        comparisonCalls++;
        if (nexts.size() <= walkCounter) {
            if (maxWalkCount == walkCounter) return 0;
            nexter choice = new nexter(0, firstCount == total ? 1 : 2);
            choice.weighted = true;
            choice.alternative = alternative;
            choice.firstWeight = (double) firstCount / total;
            choice.secondWeight = (double) (total - firstCount) / total;
            nexts.add(choice);
        }
        nexter choice = nexts.get(walkCounter++);
        chance *= choice.next == 0 ? choice.firstWeight : choice.secondWeight;
        return choice.getInt();
    }

    @Override
    public int nextInt(int bound) {
        checkGenerationLimit();
        if (exceptionOnEnchantTry && bound == Enchantment.enchantmentsBookList.length) return -1;
        if (nexts.size() <= walkCounter) { // new call
            if (maxWalkCount == walkCounter) {
                return 0;
            }
            nexts.add(new nexter(0, bound));
            walkCounter++;
            chance /= bound;
            return 0;
        }
        chance /= bound;
        return nexts.get(walkCounter++)
            .getInt();
    }

    @Override
    public float nextFloat() {
        checkGenerationLimit();
        if (forceFloatValue != -1f) return forceFloatValue;
        if (nexts.size() <= walkCounter) { // new call
            if (maxWalkCount == walkCounter) {
                return 0f;
            }
            nexts.add(new nexter(2, 10));
            walkCounter++;
            chance /= 10;
            return 0f;
        }
        chance /= 10;
        return nexts.get(walkCounter++)
            .getFloat();
    }

    @Override
    public boolean nextBoolean() {
        checkGenerationLimit();
        if (nexts.size() <= walkCounter) { // new call
            if (maxWalkCount == walkCounter) {
                return false;
            }
            nexts.add(new nexter(1, 2));
            walkCounter++;
            chance /= 2;
            return false;
        }
        chance /= 2;
        return nexts.get(walkCounter++)
            .getBoolean();
    }

    /**
     * Projects the number of leaves if the current path's branching factors applied throughout
     * the tree. Weighted comparisons contribute their representative count, not inverse chance.
     * Conditional RNG calls can make this an overestimate or underestimate of the whole tree.
     * Call before nextRound resets the current path. Large products saturate instead of wrapping.
     */
    public long estimatedPathCount() {
        long paths = 1;
        for (int i = 0; i < walkCounter; i++) {
            int branches = nexts.get(i).bound;
            if (paths > Long.MAX_VALUE / branches) return Long.MAX_VALUE;
            paths *= branches;
        }
        return paths;
    }

    public void newRound() {
        comparisonCalls = 0;
        callsThisRound = 0;
        walkCounter = 0;
        nexts.clear();
        chance = 1d;
        maxWalkCount = -1;
        exceptionOnEnchantTry = false;
        forceFloatValue = -1f;
    }

    public boolean nextRound() {
        callsThisRound = 0;
        walkCounter = 0;
        chance = 1d;
        while (!nexts.isEmpty() && nexts.get(nexts.size() - 1)
            .next()) nexts.remove(nexts.size() - 1);
        return !nexts.isEmpty();
    }
}
