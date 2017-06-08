package dissertation.pricing;

import java.util.concurrent.ThreadLocalRandom;

public class Util {

    public static int rand(int upperExclusive) {
        return rand(0, upperExclusive);
    }

    public static int rand(int lower, int upperExclusive) {
        return ThreadLocalRandom.current().nextInt(lower, upperExclusive);
    }
    
    public static int rand(int[] pool) {
        return pool[ThreadLocalRandom.current().nextInt(0, pool.length)];
    }
}
