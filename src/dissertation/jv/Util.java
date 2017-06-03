package dissertation.jv;

import java.util.concurrent.ThreadLocalRandom;

public class Util {
    public static int rand(int lower, int upperExclusive) {
        return ThreadLocalRandom.current().nextInt(lower, upperExclusive);
    }
}
