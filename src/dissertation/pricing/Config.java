package dissertation.pricing;

import dissertation.pricing.FileParser.Format;
import dissertation.pricing.Instance.Region;
import java.lang.reflect.Field;
import java.util.Calendar;

public class Config {

    public static class Simulation {

        public static final int DELTA_TIME = 2;
        public static final int PREDICTED_VALUE = 12142;

    }

    public static class File {

        //// FILE
        public static final Format FORMAT = Format.Combined;
        public static final String PATH = "csv1/Germany_2017-03-31.csv";
        public static final String REGION = Region.Germany.name();
        public static final int MONTH = Calendar.APRIL;

    }

    public static class Instance {

        public static final int[] DEMAND_POOL = {6, 4, 3};
        public static final int ONDEMAND_CUTOFF_PERIOD = 60;
        public static final int RESERVED_CUTOFF_PERIOD = 43200;

        public static class G22XLarge {

            public static final int INS_CAPACITY = 12;

            public static class Ondemand {

                public static final float GERMANY_PRICE = 0.772f;
                public static final float USA_PRICE = 0.702f;
                public static final float UK_PRICE = 0.702f;
            }

            public static class Reserved {

                public static final float GERMANY_PRICE = 0.357f;
                public static final float USA_PRICE = 0.325f;
                public static final float UK_PRICE = 0.325f;
            }
        }

        public static class G28XLarge {

            public static final int INS_CAPACITY = 24;

            public static class Ondemand {

                public static final float GERMANY_PRICE = 3.088f;
                public static final float USA_PRICE = 2.808f;
                public static final float UK_PRICE = 2.808f;
            }

            public static class Reserved {

                public static final float GERMANY_PRICE = 1.426f;
                public static final float USA_PRICE = 1.298f;
                public static final float UK_PRICE = 1.298f;
            }
        }

    }

    //// MISC
    //// INSTANCE
    public static String propToString() throws IllegalAccessException {
        String res = "";
        Config thisObj = new Config();
        Field[] fields = Config.class.getDeclaredFields();
        for (Field f : fields) {
            res += "    " + f.getName() + ": " + f.get(thisObj) + "\n";
        }
        return res;
    }

}
