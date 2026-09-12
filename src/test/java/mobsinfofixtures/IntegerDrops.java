package mobsinfofixtures;

import java.util.Random;
import java.util.function.ToIntFunction;

public class IntegerDrops implements ToIntFunction<Random> {

    @Override
    public int applyAsInt(Random random) {
        int drops = 0;
        if (random.nextInt(100) == 0) drops |= 1;
        if (random.nextInt(100) < 7) drops |= 2;
        if (random.nextInt(100) >= 95) drops |= 4;
        return drops;
    }
}
