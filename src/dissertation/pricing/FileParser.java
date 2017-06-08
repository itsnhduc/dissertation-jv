package dissertation.pricing;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Scanner;

public class FileParser {

    public enum Format {
        Separated, Combined
    }

    public static FileContent read(String filePath, int zeroBasedMonth, Format format)
            throws FileNotFoundException, ParseException {
        switch (format) {
            case Separated:
                return _readSeparate(filePath, zeroBasedMonth);
            case Combined:
                return _readCombined(zeroBasedMonth);
            default:
                return new FileContent();
        }
    }

    private static FileContent _readSeparate(String filePath, int zeroBasedMonth)
            throws FileNotFoundException, ParseException {
        FileContent res = new FileContent();
        List<Integer> playerList = new ArrayList<>();

        Scanner s = new Scanner(new File(filePath));
        SimpleDateFormat parser = new SimpleDateFormat("yy/MM/dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();

        while (s.hasNext()) {
            // index, player count, timestamp
            String[] curLineInfo = s.nextLine().split(",");
            if (!"index".equals(curLineInfo[0])) {
                cal.setTime(parser.parse(curLineInfo[2]));
                boolean isCurMonth
                        = cal.get(Calendar.MONTH) == zeroBasedMonth;
                boolean isNextMonthStart
                        = cal.get(Calendar.MONTH) == zeroBasedMonth + 1
                        && cal.get(Calendar.DATE) == 1
                        && cal.get(Calendar.HOUR_OF_DAY) == 0
                        && cal.get(Calendar.MINUTE) == 0
                        && cal.get(Calendar.SECOND) < 5;
                if (isCurMonth || isNextMonthStart) { // April data
                    playerList.add(Integer.parseInt(curLineInfo[1]));
                }
            }
        }

        String[] pathInfo = filePath.split("/");
        String region = pathInfo[pathInfo.length - 1].split("_")[0];
        res.content.put(region, playerList);

        return res;
    }

    private static FileContent _readCombined(int zeroBasedMonth)
            throws FileNotFoundException, ParseException {

        // format: csv2/yyyy-MM-dd-totalRecord
        FileContent res = new FileContent();
        int maxDay = new GregorianCalendar(2017, zeroBasedMonth, 1)
                .getActualMaximum(Calendar.DATE);

        for (int date = 1; date <= maxDay; date++) {
            String path = "csv2/2017-"
                    + String.format("%02d", zeroBasedMonth + 1) + "-"
                    + String.format("%02d", date) + "-totalRecord.csv";
            Scanner s = new Scanner(new File(path));
            
            while (s.hasNext()) {
                // region, player count, timestamp
                String[] lineInfo = s.nextLine().split(",");
                if (!res.content.containsKey(lineInfo[0])) {
                    res.content.put(lineInfo[0], new ArrayList<>());
                }
                res.content.get(lineInfo[0]).add(Integer.parseInt(lineInfo[1]));
            }
        }
        
        return res;
    }
}
