package dissertation.jv;

import java.util.ArrayList;
import java.util.List;

public class Instance {
    
    public static final int CAPACITY = 4;
    public static final float ONDEMAND_PRICE = 0.772f;
    public static final float RESERVED_PRICE = 0.357f;
    public enum Type { Ondemand, Reserved }
    
    private final Type _type;
    private int _uptime;
    private final List<Integer> _players;

    public Instance(Type type) {
        _type = type;
        _uptime = 0;
        _players= new ArrayList<>();
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
        return _players.isEmpty() && _uptime % 60 == 0;
    }
    
    public int getAvail() {
        return CAPACITY - _players.stream().mapToInt(Integer::intValue).sum();
    }
    
    public float getCost() {
        float unitPrice;
        switch (_type) {
            case Ondemand: unitPrice = ONDEMAND_PRICE; break;
            case Reserved: unitPrice = RESERVED_PRICE; break;
            default: unitPrice = -10000000.0f; // not supposed to happen
        }
        return (_uptime * 1.0f / 60) * unitPrice;
    }
    
    @Override
    public Instance clone() {
        return new Instance(_type, _uptime, _players);
    }
}
