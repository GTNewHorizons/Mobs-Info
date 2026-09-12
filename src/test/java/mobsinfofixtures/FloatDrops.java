package mobsinfofixtures;

import java.util.Random;
import java.util.function.ToIntFunction;

public class FloatDrops implements ToIntFunction<Random> {

    @Override
    public int applyAsInt(Random random) {
        return random.nextFloat() < 0.025f ? 1 : 0;
    }
}
