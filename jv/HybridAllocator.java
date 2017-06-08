package dissertation.jv;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HybridAllocator implements Allocator {

    private final HashMap<Integer, List<Instance>> _insMap;
    private float _cost = 0.0f;

    public HybridAllocator() {
        _insMap = new HashMap<>();
        // map by available space
        for (int i = 0; i <= Instance.CAPACITY; i++) {
            _insMap.put(i, new ArrayList<>());
        }
    }

    private void _updateIns(int key, int insI, Instance ins) {
        if (ins != null) {
            _insMap.get(ins.getAvail()).add(ins);
        }
        if (key != -1 && insI != -1) {
            _insMap.get(key).remove(insI);
        }
    }
    
    

    @Override
    public void allocatePlayer(int demand) {
        
    }

    @Override
    public void removeRandomPlayer() {
        // pick key
        int key;
        List<Instance> insMapEntry;
        do {
            key = Util.rand(0, Instance.CAPACITY);
            insMapEntry = _insMap.get(key);
        } while (insMapEntry.isEmpty());

        // pick instance
        int insI;
        Instance ins;
        do {
            insI = Util.rand(0, insMapEntry.size());
            ins = insMapEntry.get(insI).clone();
        } while (ins.isEmpty());

        // remove player
        ins.removeRandom();

        // update mapping
        _updateIns(key, insI, ins);
    }

    @Override
    public void tickIns(int deltaTime) {
        _insMap.forEach((key, entry) -> {
            entry.forEach((ins) -> {
                ins.tick(deltaTime);
                _cost += ins.getCost();
            });
        });
    }

    @Override
    public void trimIns() {
        _insMap.forEach((key, entry) -> {
            for (int i = 0; i < entry.size(); i++) {
                Instance ins = entry.get(i);
                if (ins.isReleasable()) {
                    // remove
                    _updateIns(key, i, null);
                }
            }
        });
    }

    @Override
    public float getCost() {
        return _cost;
    }
    
    @Override
    public String toString() {
        return "{ " + _insMap.keySet().stream()
                .map((key) -> "(" + key + ": " + _insMap.get(key).size() + "), ")
                .reduce("", String::concat) + "}";
    }
    
}
