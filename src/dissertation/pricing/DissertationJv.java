package dissertation.pricing;

import dissertation.pricing.FileParser.Format;
import dissertation.pricing.Instance.CapacityType;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class DissertationJv {

    public static void main(String[] args) throws FileNotFoundException, ParseException, IllegalAccessException {
        // record start time
        TimeUtil.start();

        // read file
        List<Integer> data = _readFile();
        _testQuota(data, CapacityType.G22XLarge, 6700);

        // output time taken
        System.out.println("");
        TimeUtil.end();
    }
    
    private static List<Integer> _readFile() throws FileNotFoundException, ParseException {
        String region = Config.REGION;
        int month = Config.MONTH;
        String filePath = Config.FILE_PATH;
        Format fileFormat = Config.FILE_FORMAT;
        
        FileContent allRegData = FileParser.read(filePath, month, fileFormat);
        return allRegData.content.get(region);
    }

    private static void _testQuotaRange(
            List<Integer> curRegData, CapacityType capacityType, int minQuota, int maxQuota, int step)
            throws IllegalAccessException, ParseException, FileNotFoundException {
        List<SimResult> results = new ArrayList<>();
        for (int curQ = minQuota; curQ <= maxQuota; curQ += step) {
            results.add(_testQuota(curRegData, capacityType, curQ));
        }

        System.out.println("Quota" + "\t"
                + "On-demand" + "\t"
                + "Reserved" + "\t"
                + "Saving");
        results.forEach((res) -> System.out.println(res.toLineString()));
    }

    private static SimResult _testQuota(
            List<Integer> curRegData, CapacityType capacityType, int quota)
            throws IllegalAccessException, ParseException, FileNotFoundException {
        System.out.println("///////////////////// " + quota + " /////////////////////");
        // gather config
        int deltaTime = Config.DELTA_TIME;
        int[] demandPool = Config.DEMAND_POOL;

        // init allocators
        Allocator ondemandAllocator = new Allocator(capacityType);
        Allocator hybridAllocator = new Allocator(capacityType, quota);

        // 2 minutes/entry
        System.out.println("");
        System.out.println("Processing " + curRegData.size() + " timestamps...");
        for (int i = 0; i < curRegData.size(); i++) {
            if (i >= 5000 && i % 5000 == 0) {
                System.out.println("    " + i + " done");
            }
            // tick
            if (i != 0) {
                ondemandAllocator.tickIns(deltaTime);
                hybridAllocator.tickIns(deltaTime);
            }

            // calculate diff
            int prev = i != 0 ? curRegData.get(i - 1) : 0;
            int cur = curRegData.get(i);
            int diff = cur - prev;

            // if more player
            if (diff > 0) {
                // allocate simulated new demand
                for (int p = 0; p < diff; p++) {
                    int curDemand = Util.rand(demandPool);
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
        }

        // out total ondemand price
        SimResult res = new SimResult(
                quota,
                ondemandAllocator.getCost(),
                hybridAllocator.getCost());

        System.out.println(res.toString());

        return res;
    }

}
