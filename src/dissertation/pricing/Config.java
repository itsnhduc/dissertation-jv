package dissertation.pricing;

import dissertation.pricing.FileParser.Format;
import java.lang.reflect.Field;
import java.util.Calendar;

public class Config {

    //// MISC
    public static final int DELTA_TIME = 2;
//    public static final int RESERVED_QUOTA = 6535;

    //// FILE
    public static final Format FILE_FORMAT = Format.Separated;
    public static final String FILE_PATH = "csv1/Germany_2017-03-31.csv";
    public static final String REGION = "Germany";
    public static final int MONTH = Calendar.APRIL;

    //// INSTANCE
    public static final int[] DEMAND_POOL = {6, 4, 3};
    public static final int G22X_INS_CAPACITY = 12;
    public static final int G28X_INS_CAPACITY = 24;

    // in $/hour
    public static final float G22X_GERMANY_ONDEMAND_PRICE = 0.772f;
    public static final float G22X_GERMANY_RESERVED_PRICE = 0.357f;
    public static final float G28X_GERMANY_ONDEMAND_PRICE = 3.088f;
    public static final float G28X_GERMANY_RESERVED_PRICE = 1.426f;

    // in minutes
    public static final int ONDEMAND_CUTOFF_PERIOD = 60;
    public static final int RESERVED_CUTOFF_PERIOD = 43200;

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
