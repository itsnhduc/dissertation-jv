package dissertation.jv;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OndemandAllocator implements Allocator {

    private final HashMap<Integer, List<Instance>> _insMap;
    private float _wrapCost = 0.0f;

    public OndemandAllocator() {
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
        // pick instance
        Instance ins = null;
        int insI = -1;
        int key = -1;
        // smallest fit first
        for (int k = demand; k < Instance.CAPACITY; k++) {
            List<Instance> insMapEntry = _insMap.get(k);
            if (!insMapEntry.isEmpty()) {
                key = k;
                insI = Util.rand(0, insMapEntry.size());
                ins = insMapEntry.get(insI).clone();
                break;
            }
        }

        // allocate player
        if (ins == null) {
            ins = new Instance(Instance.Type.Ondemand);
        }
        ins.add(demand);

        // update mapping
        _updateIns(key, insI, ins);
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
            });
        });
    }

    @Override
    public void trimIns() {
        _insMap.forEach((key, entry) -> {
            for (int i = 0; i < entry.size(); i++) {
                Instance ins = entry.get(i);
                if (ins.isReleasable()) {
//                    // calculate cost
                    float insCost = ins.getCost();
                    _wrapCost += insCost;

                    // remove
                    _updateIns(key, i, null);
                }
            }
        });
    }
    
    @Override
    public float getCost() {
        float unfinishedCost = 0.0f;
        for (int key : _insMap.keySet()) {
            for (Instance ins : _insMap.get(key)) {
               unfinishedCost += ins.getCost();
            }
        }
        return _wrapCost + unfinishedCost;
    }

    @Override
    public String toString() {
        return "{ " + _insMap.keySet().stream()
                .map((key) -> "(" + key + ": " + _insMap.get(key).size() + "), ")
                .reduce("", String::concat) + "}";
    }

}
