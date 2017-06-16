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
    
    public enum Region {
        USA, UK, Germany
    }

    public final int capacity;
    public final int cutoffPeriod;
    public final float unitPrice;

    private int _uptime;
    private final List<Integer> _players;

    public Instance(PricingType pricingType, CapacityType capacityType, Region region, int uptime, List<Integer> players) {
        switch (pricingType) {
            case Ondemand:
                this.cutoffPeriod = Config.Instance.ONDEMAND_CUTOFF_PERIOD;
                break;
            case Reserved:
                this.cutoffPeriod = Config.Instance.RESERVED_CUTOFF_PERIOD;
                break;
            default:
                this.cutoffPeriod = -1; // not possible
        }
        switch (capacityType) {
            case G22XLarge:
                this.capacity = Config.Instance.G22XLarge.INS_CAPACITY;
                break;
            case G28XLarge:
                this.capacity = Config.Instance.G28XLarge.INS_CAPACITY;
                break;
            default:
                this.capacity = -1; // not possible
        }
        switch (pricingType) {
            case Ondemand:
                switch (capacityType) {
                    case G22XLarge:
                        switch (region) {
                            case USA:
                                this.unitPrice = Config.Instance.G22XLarge.Ondemand.USA_PRICE;
                                break;
                            case UK:
                                this.unitPrice = Config.Instance.G22XLarge.Ondemand.UK_PRICE;
                                break;
                            case Germany:
                                this.unitPrice = Config.Instance.G22XLarge.Ondemand.GERMANY_PRICE;
                                break;
                            default:
                                this.unitPrice = -1e10f; // not possible
                        }
                        break;
                    case G28XLarge:
                        switch (region) {
                            case USA:
                                this.unitPrice = Config.Instance.G28XLarge.Ondemand.USA_PRICE;
                                break;
                            case UK:
                                this.unitPrice = Config.Instance.G28XLarge.Ondemand.UK_PRICE;
                                break;
                            case Germany:
                                this.unitPrice = Config.Instance.G28XLarge.Ondemand.GERMANY_PRICE;
                                break;
                            default:
                                this.unitPrice = -1e10f; // not possible
                        }
                        break;
                    default:
                        this.unitPrice = -1e10f; // not possible
                }
                break;
            case Reserved:
                switch (capacityType) {
                    case G22XLarge:
                        switch (region) {
                            case USA:
                                this.unitPrice = Config.Instance.G22XLarge.Reserved.USA_PRICE;
                                break;
                            case UK:
                                this.unitPrice = Config.Instance.G22XLarge.Reserved.UK_PRICE;
                                break;
                            case Germany:
                                this.unitPrice = Config.Instance.G22XLarge.Reserved.GERMANY_PRICE;
                                break;
                            default:
                                this.unitPrice = -1e10f; // not possible
                        }
                        break;
                    case G28XLarge:
                        switch (region) {
                            case USA:
                                this.unitPrice = Config.Instance.G28XLarge.Reserved.USA_PRICE;
                                break;
                            case UK:
                                this.unitPrice = Config.Instance.G28XLarge.Reserved.UK_PRICE;
                                break;
                            case Germany:
                                this.unitPrice = Config.Instance.G28XLarge.Reserved.GERMANY_PRICE;
                                break;
                            default:
                                this.unitPrice = -1e10f; // not possible
                        }
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

    public Instance(PricingType pricingType, CapacityType capacityType, Region region) {
        this(pricingType, capacityType, region, 0, new ArrayList<>());
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
