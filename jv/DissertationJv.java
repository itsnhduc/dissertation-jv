package dissertation.jv;

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
    public static int PREDICTED_VALUE = 13070;

    public static void main(String[] args) throws FileNotFoundException, ParseException {
        // record start time
        TimeUtil.start();
        
        // init & read file
        Allocator ondemandAllocator = new OndemandAllocator();
        Allocator hybridAllocator = new HybridAllocator();
        List<Integer> aprilData = readPlayerCount(Calendar.APRIL);
        
        // 2 minutes/entry
        System.out.println("Processing " + aprilData.size() + " lines of data...");
        for (int i = 0; i < aprilData.size(); i++) {
            if (i >= 5000 && i % 5000 == 0) System.out.println(i + " done");
            // tick
            if (i != 0) ondemandAllocator.tickIns(DELTA_TIME);
            
            // calculate diff
            int prev = i != 0 ? aprilData.get(i - 1) : 0;
            int cur = aprilData.get(i);
            
            // >> ondemand scheme
            int diff = cur - prev;
            
//            System.out.println("");
//            System.out.println("alctr = " + ondemandAllocator.toString());
//            System.out.println("diff = " + diff);
            
            // if more player
            if (diff > 0) {
                // allocate simulated new demand
                for (int p = 0; p < diff; p++) {
                    int curDemand = Util.rand(1, Instance.CAPACITY);
                    ondemandAllocator.allocatePlayer(curDemand);
                }
            } else {
                // if less player
                if (diff < 0) {
                    // release random
                    for (int p = 0; p < -diff; p++) {
                        ondemandAllocator.removeRandomPlayer();
                    }
                }
                
                // trim releasable instances
                ondemandAllocator.trimIns();
            }
            
            // >> hybrid scheme
            // wip
        }
        
        // out total ondemand price
        System.out.println("On-demand scheme total cost = " + ondemandAllocator.getCost());
        
        // output time taken
        TimeUtil.end();
    }
    
    public static List<Integer> readPlayerCount(int zeroBasedMonth) throws FileNotFoundException, ParseException {
        List<Integer> playerList = new ArrayList<>();
        
        Scanner s = new Scanner(new File("Germany_2017-03-31.csv"));
        SimpleDateFormat parser = new SimpleDateFormat("yy/MM/dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        
        while (s.hasNext()) {
            String[] curLineInfo = s.nextLine().split(",");
            if (!"index".equals(curLineInfo[0])) {
                cal.setTime(parser.parse(curLineInfo[2]));
                if (cal.get(Calendar.MONTH) == zeroBasedMonth) { // April data
                    playerList.add(Integer.parseInt(curLineInfo[1]));
                }
            }
        }
        
        return playerList;
    }
    
}
