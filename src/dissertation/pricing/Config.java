package dissertation.pricing;

import java.lang.reflect.Field;
import java.util.Calendar;

public class Config {

    //// MISC
    public static final int DELTA_TIME = 2;
    public static final int RESERVED_QUOTA = 6535;
    
    //// FILE
    public static final String FILE_NAME = "Germany_2017-03-31.csv";
    public static final int MONTH = Calendar.APRIL;

    //// INSTANCE
    public static final int INS_CAPACITY = 4;

    // in $/hour
    public static final float ONDEMAND_PRICE = 0.772f;
    public static final float RESERVED_PRICE = 0.357f;

    // in minutes
    public static final int ONDEMAND_CUTOFF_PERIOD = 60;
    public static final int RESERVED_CUTOFF_PERIOD = 43200;

    public static String all() throws IllegalAccessException {
        String res = "";
        Config thisObj = new Config();
        Field[] fields = Config.class.getDeclaredFields();
        for (Field f : fields) {
            res += "    " + f.getName() + ": " + f.get(thisObj) + "\n";
        }
        return res;
    }

}
