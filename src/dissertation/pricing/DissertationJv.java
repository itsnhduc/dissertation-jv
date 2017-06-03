package dissertation.pricing;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Scanner;

public class DissertationJv {

    public static int DELTA_TIME = 2;
    public static int PREDICTED_VALUE = 4000;

    public static void main(String[] args) throws FileNotFoundException, ParseException {
        // record start time
        TimeUtil.start();

        // init allocators
        Allocator ondemandAllocator = new Allocator();
        Allocator hybridAllocator = new Allocator(PREDICTED_VALUE);
        
        // read file
        String fileName = "Germany_2017-03-31.csv";
        int zeroBasedMonth = Calendar.APRIL;
        System.out.println("Reading from " + fileName + ", month = " + (zeroBasedMonth + 1));
        List<Integer> aprilData = readPlayerCount(fileName, zeroBasedMonth);
        System.out.println("    DONE");

        // 2 minutes/entry
        System.out.println("");
        System.out.println("Processing " + aprilData.size() + " time instances...");
        for (int i = 0; i < aprilData.size(); i++) {
            if (i >= 5000 && i % 5000 == 0) {
                System.out.println("    " + i + " done");
            }
            // tick
            if (i != 0) {
                ondemandAllocator.tickIns(DELTA_TIME);
                hybridAllocator.tickIns(DELTA_TIME);
            }

            // calculate diff
            int prev = i != 0 ? aprilData.get(i - 1) : 0;
            int cur = aprilData.get(i);

            // >> ondemand scheme
            int diff = cur - prev;

            // if more player
            if (diff > 0) {
                // allocate simulated new demand
                for (int p = 0; p < diff; p++) {
                    int curDemand = Util.rand(1, Instance.CAPACITY);
                    ondemandAllocator.allocatePlayer(curDemand);
                    hybridAllocator.allocatePlayer(curDemand);

                }
            } else {
                // if less player
                if (diff < 0) {
                    // release random
                    for (int p = 0; p < -diff; p++) {
                        ondemandAllocator.removeRandomPlayer();
                        hybridAllocator.removeRandomPlayer();
                    }
                }

                // trim releasable instances
                ondemandAllocator.trimIns();
                hybridAllocator.trimIns();
            }

//            System.out.println("diff = " + diff);            
//            System.out.println("on-demand: " + ondemandAllocator.toString());
//            System.out.println("hybrid: " + hybridAllocator.toString());
        }
        System.out.println("    DONE");

        // out total ondemand price
        System.out.println("");
        System.out.println("Total cost");
        System.out.println("    On-demand scheme: $" + ondemandAllocator.getCost());
        System.out.println("    Hybrid scheme: $" + hybridAllocator.getCost());
        float costDiff = ondemandAllocator.getCost() - hybridAllocator.getCost();
        System.out.println("    Expected saving: $" + costDiff);

        // output time taken
        System.out.println("");
        TimeUtil.end();
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
