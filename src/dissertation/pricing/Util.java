package dissertation.pricing;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

public class Util {
    public static int rand(int lower, int upperExclusive) {
        return ThreadLocalRandom.current().nextInt(lower, upperExclusive);
    }

    public static List<Integer> readPlayerCount(String filePath, int zeroBasedMonth) 
            throws FileNotFoundException, ParseException {
        List<Integer> playerList = new ArrayList<>();

        Scanner s = new Scanner(new File(filePath));
        SimpleDateFormat parser = new SimpleDateFormat("yy/MM/dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();

        while (s.hasNext()) {
            String[] curLineInfo = s.nextLine().split(",");
            if (!"index".equals(curLineInfo[0])) {
                cal.setTime(parser.parse(curLineInfo[2]));
                boolean isCurMonth = 
                        cal.get(Calendar.MONTH) == zeroBasedMonth;
                boolean isNextMonthStart =
                        cal.get(Calendar.MONTH) == zeroBasedMonth + 1 &&
                        cal.get(Calendar.DATE) == 1 &&
                        cal.get(Calendar.HOUR_OF_DAY) == 0 &&
                        cal.get(Calendar.MINUTE) == 0 &&
                        cal.get(Calendar.SECOND) < 5;
                if (isCurMonth || isNextMonthStart) { // April data
                    playerList.add(Integer.parseInt(curLineInfo[1]));
                }
            }
        }

        return playerList;
    }
}
