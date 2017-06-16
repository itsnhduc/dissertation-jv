package dissertation.pricing;

import dissertation.pricing.FileParser.Format;
import dissertation.pricing.Instance.CapacityType;
import dissertation.pricing.Instance.Region;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DissertationJv {

    public static void main(String[] args) throws FileNotFoundException, ParseException, IllegalAccessException {
        // record start time
        TimeUtil.start();

        // convert
        String reg = Config.File.REGION;
        int month = Config.File.MONTH;
        String filePath = Config.File.PATH;
        Format fileFormat = Config.File.FORMAT;

        // read file
        FileContent data = _readFile(month - 1, filePath, fileFormat);
        List<Integer> germanyData = data.content.get(Region.Germany.name());
        List<Integer> ukData = data.content.get(Region.UK.name());
        List<Integer> usaData = data.content.get(Region.USA.name());

//        int aveGer = germanyData.stream().reduce(0, Integer::sum) / germanyData.size();
//        int aveUK = ukData.stream().reduce(0, Integer::sum) / ukData.size();
//        int aveUSA = usaData.stream().reduce(0, Integer::sum) / usaData.size();

        int aveGer = 11004;
        int aveUK = 3849;
        int aveUSA = 20931;

//        System.out.println("aveGer = " + aveGer);
//        System.out.println("aveUK = " + aveUK);
//        System.out.println("aveUSA = " + aveUSA);

        int quotaGer22 = _playerCountToInsCount(CapacityType.G22XLarge, Region.Germany, aveGer);
        int quotaGer28 = _playerCountToInsCount(CapacityType.G28XLarge, Region.Germany, aveGer);
        int quotaUK22 = _playerCountToInsCount(CapacityType.G22XLarge, Region.UK, aveUK);
        int quotaUK28 = _playerCountToInsCount(CapacityType.G28XLarge, Region.UK, aveUK);
        int quotaUSA22 = _playerCountToInsCount(CapacityType.G22XLarge, Region.USA, aveUSA);
        int quotaUSA28 = _playerCountToInsCount(CapacityType.G28XLarge, Region.USA, aveUSA);
        _testQuota(germanyData, CapacityType.G22XLarge, Region.Germany, quotaGer22);
        _testQuota(germanyData, CapacityType.G28XLarge, Region.Germany, quotaGer28);
        _testQuota(ukData, CapacityType.G22XLarge, Region.UK, quotaUK22);
        _testQuota(ukData, CapacityType.G28XLarge, Region.UK, quotaUK28);
        _testQuota(usaData, CapacityType.G22XLarge, Region.USA, quotaUSA22);
        _testQuota(usaData, CapacityType.G28XLarge, Region.USA, quotaUSA28);
//        _testQuota(germanyData, CapacityType.G22XLarge, Region.Germany, Collections.max(germanyData));
//        _testQuota(germanyData, CapacityType.G28XLarge, Region.Germany, Collections.max(germanyData));
//        _testQuota(ukData, CapacityType.G22XLarge, Region.UK, Collections.max(ukData));
//        _testQuota(ukData, CapacityType.G28XLarge, Region.UK, Collections.max(ukData));
//        _testQuota(usaData, CapacityType.G22XLarge, Region.USA, Collections.max(usaData));
//        _testQuota(usaData, CapacityType.G28XLarge, Region.USA, Collections.max(usaData));
//        _testQuotaRange(germanyData, CapacityType.G22XLarge, Region.Germany, 5000, 6000, 100);
//        _testQuotaRange(germanyData, CapacityType.G28XLarge, Region.Germany, 2000, 3000, 100);
//        _testQuotaRange(ukData, CapacityType.G22XLarge, Region.UK, 1500, 2500, 100);
//        _testQuotaRange(ukData, CapacityType.G28XLarge, Region.UK, 500, 1500, 100);
//        _testQuotaRange(usaData, CapacityType.G22XLarge, Region.USA, 9000, 10000, 100);
//        _testQuotaRange(usaData, CapacityType.G28XLarge, Region.USA, 4000, 5000, 100);
//        _testQuota(germanyData, CapacityType.G22XLarge, Region.Germany, 5087);
//        _testQuota(germanyData, CapacityType.G28XLarge, Region.Germany, 2452);
//        _testQuota(ukData, CapacityType.G22XLarge, Region.UK, 1597);
//        _testQuota(ukData, CapacityType.G28XLarge, Region.UK, 770);
//        _testQuota(usaData, CapacityType.G22XLarge, Region.USA, 9980);
//        _testQuota(usaData, CapacityType.G28XLarge, Region.USA, 4811);

        // output time taken
        System.out.println("");
        TimeUtil.end();
    }

    private static FileContent _readFile(int month, String filePath, Format fileFormat)
            throws FileNotFoundException, ParseException {
        return FileParser.read(filePath, month, fileFormat);
    }

    private static int _playerCountToInsCount(CapacityType capacityType, Region reg, int playerCount)
            throws IllegalAccessException, FileNotFoundException, ParseException {
        List<Integer> mockData = new ArrayList<>();
        mockData.add(playerCount);
        SimResult result = _testQuota(mockData, capacityType, reg, playerCount);
        return result.ondemandInsCount;
    }

    private static void _testQuotaRange(
            List<Integer> curRegData, CapacityType capacityType, Region reg, int minQuota, int maxQuota, int step)
            throws IllegalAccessException, ParseException, FileNotFoundException {
        List<SimResult> results = new ArrayList<>();
        for (int curQ = minQuota; curQ <= maxQuota; curQ += step) {
            results.add(_testQuota(curRegData, capacityType, reg, curQ));
        }

        System.out.println("Quota" + "\t"
                + "On-demand" + "\t"
                + "Reserved" + "\t"
                + "Saving");
        results.forEach((res) -> System.out.println(res.toLineString()));
    }

    private static SimResult _testQuota(
            List<Integer> curRegData, CapacityType capacityType, Region reg, int quota)
            throws IllegalAccessException, ParseException, FileNotFoundException {
        System.out.println("///////////////////// " + reg + " @ " + quota + " /////////////////////");
        // gather config
        int deltaTime = Config.Simulation.DELTA_TIME;
        int[] demandPool = Config.Instance.DEMAND_POOL;

        // init allocators
        Allocator ondemandAllocator = new Allocator(capacityType, reg);
        Allocator hybridAllocator = new Allocator(capacityType, reg, quota);

        // 2 minutes/entry
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
                hybridAllocator.getCost(),
                ondemandAllocator.countIns());

        System.out.println(res.toString());

        return res;
    }

}
