package mobsinfofixtures;

import java.util.Random;
import java.util.function.ToIntFunction;

public final class ComparisonFixtures {

    public static class ConditionalDrops implements ToIntFunction<Random> {

        public int applyAsInt(Random random) {
            if (random.nextInt(4) == 0) return random.nextInt(3) + 1;
            return random.nextInt(5) != 2 ? 6 : 0;
        }
    }

    public static class ReusedValue implements ToIntFunction<Random> {

        public int applyAsInt(Random random) {
            int count = random.nextInt(12);
            return count < 5 ? count + 1 : 0;
        }
    }

    public static class JoinedValue implements ToIntFunction<Random> {

        public int applyAsInt(Random random) {
            return (random.nextBoolean() ? random.nextInt(6) : 1) == 0 ? 1 : 0;
        }
    }

    public static class IntBoundaries implements ToIntFunction<Random> {

        public int applyAsInt(Random random) {
            int drops = 0;
            if (random.nextInt(4) <= 1) drops |= 1;
            if (random.nextInt(4) > 2) drops |= 2;
            if (random.nextInt(4) != 3) drops |= 4;
            if (random.nextInt(1) < Integer.MIN_VALUE) drops |= 8;
            if (random.nextInt(1) > Integer.MAX_VALUE) drops |= 16;
            return drops;
        }
    }

    public static class FloatBoundaries implements ToIntFunction<Random> {

        public int applyAsInt(Random random) {
            int drops = 0;
            if (random.nextFloat() == 0f) drops |= 1;
            if (random.nextFloat() <= 0.5f) drops |= 2;
            if (random.nextFloat() > 0.5f) drops |= 4;
            return drops;
        }
    }

    public static class InvalidBound implements ToIntFunction<Random> {

        public int applyAsInt(Random random) {
            return random.nextInt(0) == 0 ? 1 : 0;
        }
    }

    public static class CaughtException implements ToIntFunction<Random> {

        public int applyAsInt(Random random) {
            try {
                return random.nextInt(10) < 2 ? 1 : 0;
            } catch (IllegalArgumentException expected) {
                return 3;
            }
        }
    }
}
