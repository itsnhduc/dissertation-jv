package dissertation.pricing;

import java.io.FileNotFoundException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class DissertationJv {

    public static void main(String[] args) throws FileNotFoundException, ParseException, IllegalAccessException {
        // record start time
        TimeUtil.start();

        _testQuotaRange(6600, 6800, 10);

        // output time taken
        System.out.println("");
        TimeUtil.end();
    }

    private static void _testQuotaRange(int minQuota, int maxQuota, int step)
            throws IllegalAccessException, ParseException, FileNotFoundException {
        List<QuotaTestResult> results = new ArrayList<>();
        for (int curQ = minQuota; curQ <= maxQuota; curQ += step) {
            results.add(_testQuota(curQ));
        }
        System.out.println("Quota" + "\t"
                + "On-demand" + "\t"
                + "Reserved" + "\t"
                + "Saving");
        results.forEach((res) -> {
            System.out.println(res.quota + "\t"
                    + res.ondemandCost + "\t"
                    + res.reservedCost + "\t"
                    + res.expectedSaving);
        });
    }

    private static QuotaTestResult _testQuota(int reservedQuota)
            throws IllegalAccessException, ParseException, FileNotFoundException {
        System.out.println("///////////////////// " + reservedQuota + " /////////////////////");
        // output config
        System.out.println("Config");
        System.out.println(Config.all());

        // init allocators
        Allocator ondemandAllocator = new Allocator();
        Allocator hybridAllocator = new Allocator(reservedQuota);

        // read file
        String fileName = Config.FILE_NAME;
        int zMonth = Config.MONTH;
        System.out.println("Reading from " + fileName + ", month = " + (zMonth + 1));
        List<Integer> aprilData = Util.readPlayerCount(fileName, zMonth);
        System.out.println("    DONE");

        // 2 minutes/entry
        System.out.println("");
        System.out.println("Processing " + aprilData.size() + " timestamps...");
        for (int i = 0; i < aprilData.size(); i++) {
            if (i >= 5000 && i % 5000 == 0) {
                System.out.println("    " + i + " done");
            }
            // tick
            if (i != 0) {
                ondemandAllocator.tickIns(Config.DELTA_TIME);
                hybridAllocator.tickIns(Config.DELTA_TIME);
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
                    int curDemand = Util.rand(1, Config.INS_CAPACITY);
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
        float ondemandCost = ondemandAllocator.getCost();
        float hybridCost = hybridAllocator.getCost();
        float expectedSaving = ondemandCost - hybridCost;
        System.out.println("");
        System.out.println("Total cost");
        System.out.println("    On-demand scheme: $" + ondemandCost);
        System.out.println("    Hybrid scheme: $" + hybridCost);
        System.out.println("    Expected saving: $" + expectedSaving);
        System.out.println("////////////////////////////////////////////////");

        return new QuotaTestResult(reservedQuota, ondemandCost, hybridCost);
    }

}
