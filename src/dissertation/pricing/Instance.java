package dissertation.pricing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Instance {

    public enum PricingType {
        Ondemand, Reserved
    }

    public enum CapacityType {
        G22XLarge, G28XLarge
    }

    public final int capacity;
    public final int cutoffPeriod;
    public final float unitPrice;

    private int _uptime;
    private final List<Integer> _players;

    public Instance(PricingType pricingType, CapacityType capacityType, int uptime, List<Integer> players) {
        switch (pricingType) {
            case Ondemand:
                this.cutoffPeriod = Config.ONDEMAND_CUTOFF_PERIOD;
                break;
            case Reserved:
                this.cutoffPeriod = Config.RESERVED_CUTOFF_PERIOD;
                break;
            default:
                this.cutoffPeriod = -1; // not possible
        }
        switch (capacityType) {
            case G22XLarge:
                this.capacity = Config.G22X_INS_CAPACITY;
                break;
            case G28XLarge:
                this.capacity = Config.G28X_INS_CAPACITY;
                break;
            default:
                this.capacity = -1; // not possible
        }
        switch (pricingType) {
            case Ondemand:
                switch (capacityType) {
                    case G22XLarge:
                        this.unitPrice = Config.G22X_GERMANY_ONDEMAND_PRICE;
                        break;
                    case G28XLarge:
                        this.unitPrice = Config.G28X_GERMANY_ONDEMAND_PRICE;
                        break;
                    default:
                        this.unitPrice = -1e10f; // not possible
                }
                break;
            case Reserved:
                switch (capacityType) {
                    case G22XLarge:
                        this.unitPrice = Config.G22X_GERMANY_RESERVED_PRICE;
                        break;
                    case G28XLarge:
                        this.unitPrice = Config.G28X_GERMANY_RESERVED_PRICE;
                        break;
                    default:
                        this.unitPrice = -1e10f; // not possible
                }
                break;
            default:
                this.unitPrice = -1e10f; // not possible
        }
        _uptime = uptime;
        _players = players;
    }

    public Instance(PricingType pricingType, CapacityType capacityType) {
        this(pricingType, capacityType, 0, new ArrayList<>());
    }

    public void tick(int deltaTime) {
        _uptime += deltaTime;
    }

    public void add(int demand) {
        _players.add(demand);
    }

    public int removeRandom() {
        int randI = Util.rand(_players.size());
        return _players.remove(randI);
    }

    public boolean isEmpty() {
        return _players.isEmpty();
    }

    public boolean isReleasable() {
        return _players.isEmpty() && _uptime % this.cutoffPeriod == 0;
    }

    public int getAvail() {
        return this.capacity - _players.stream().mapToInt(Integer::intValue).sum();
    }

    public float getCost() {
        return this.unitPrice * (_uptime * 1.0f / 60);
    }

    private Instance(int capacity, int cutoffPeriod, float unitPrice, int uptime, List<Integer> players) {
        this.capacity = capacity;
        this.cutoffPeriod = cutoffPeriod;
        this.unitPrice = unitPrice;
        _uptime = uptime;
        _players = players;
    }

    public Instance clone() {
        return new Instance(
                this.capacity,
                this.cutoffPeriod,
                this.unitPrice,
                _uptime,
                new ArrayList<>(_players));
    }

    @Override
    public String toString() {
        return "Instance("
                + "capacity = " + capacity
                + ", unitPrice = " + unitPrice + ") @ "
                + "uptime = " + _uptime
                + " & " + Arrays.toString(_players.toArray());
    }
}
