package dissertation.pricing;

import java.util.ArrayList;
import java.util.List;

public class Instance {

    public enum Type {
        Ondemand, Reserved
    }

    private final Type _type;
    private int _uptime;
    private final List<Integer> _players;

    public Instance(Type type) {
        _type = type;
        _uptime = 0;
        _players = new ArrayList<>();
    }

    public Instance(Type type, int uptime, List<Integer> players) {
        _type = type;
        _uptime = uptime;
        _players = players;
    }

    public void tick(int deltaTime) {
        _uptime += deltaTime;
    }

    public void add(int demand) {
        _players.add(demand);
    }

    public int removeRandom() {
        int randI = Util.rand(0, _players.size());
        return _players.remove(randI);
    }

    public boolean isEmpty() {
        return _players.isEmpty();
    }

    public boolean isReleasable() {
        int cutoffPeriod;
        switch (_type) {
            case Ondemand:
                cutoffPeriod = Config.ONDEMAND_CUTOFF_PERIOD;
                break;
            case Reserved:
                cutoffPeriod = Config.RESERVED_CUTOFF_PERIOD;
                break;
            default:
                return false;
        }
        return _players.isEmpty() && _uptime % cutoffPeriod == 0;
    }

    public int getAvail() {
        return Config.INS_CAPACITY - _players.stream().mapToInt(Integer::intValue).sum();
    }

    public float getCost() {
        float unitPrice;
        int cutoffPeriod;
        switch (_type) {
            case Ondemand:
                unitPrice = Config.ONDEMAND_PRICE;
                cutoffPeriod = Config.ONDEMAND_CUTOFF_PERIOD;
                break;
            case Reserved:
                unitPrice = Config.RESERVED_PRICE;
                cutoffPeriod = Config.RESERVED_CUTOFF_PERIOD;
                break;
            default:
                return -10000000.0f;
        }
        return unitPrice * (_uptime * 1.0f / 60);
    }

    public Type getType() {
        return _type;
    }

    @Override
    public Instance clone() {
        return new Instance(_type, _uptime, _players);
    }
}
