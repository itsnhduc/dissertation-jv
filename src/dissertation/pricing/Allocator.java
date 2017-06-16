package dissertation.pricing;

import dissertation.pricing.Instance.CapacityType;
import dissertation.pricing.Instance.PricingType;
import dissertation.pricing.Instance.Region;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Allocator {

    public final Instance ondemandProto;
    public final int reservedQuota;
    
    private final HashMap<Integer, List<Instance>> _insMap;
    private float _wrapCost = 0.0f;

    public Allocator(CapacityType capacityType, Region region, int reservedQuota) {
        this.reservedQuota = reservedQuota;
        this.ondemandProto = new Instance(PricingType.Ondemand, capacityType, region);

        // map by available space
        _insMap = new HashMap<>();
        for (int i = 0; i <= ondemandProto.capacity; i++) {
            _insMap.put(i, new ArrayList<>());
        }

        // init reserved ins
        for (int i = 0; i < this.reservedQuota; i++) {
            _insMap.get(ondemandProto.capacity)
                    .add(new Instance(PricingType.Reserved, capacityType, region));
        }
    }

    public Allocator(CapacityType capacityType, Region region) {
        this(capacityType, region, 0);
    }

    private void _updateIns(int key, int insI, Instance ins) {
        if (ins != null) {
            _insMap.get(ins.getAvail()).add(ins);
        }
        if (key != -1 && insI != -1) {
            _insMap.get(key).remove(insI);
        }
    }

    public void allocatePlayer(int demand) {
        // pick instance
        Instance ins = null;
        int insI = -1;
        int key = -1;
        // smallest fit first
        for (int k = demand; k <= ondemandProto.capacity; k++) {
            List<Instance> insMapEntry = _insMap.get(k);
            if (!insMapEntry.isEmpty()) {
                key = k;
                insI = Util.rand(insMapEntry.size());
                ins = insMapEntry.get(insI).clone();
                break;
            }
        }

        // allocate player
        if (ins == null) {
            ins = ondemandProto.clone();
        }
        ins.add(demand);

        // update mapping
        _updateIns(key, insI, ins);
    }

    public void removeRandomPlayer() {
        // pick key
        int key;
        List<Instance> insMapEntry;
        do {
            key = Util.rand(ondemandProto.capacity);
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

    public void tickIns(int deltaTime) {
        _insMap.forEach((key, entry) -> {
            entry.forEach((ins) -> {
                ins.tick(deltaTime);
            });
        });
    }

    public void trimIns() {
        _insMap.forEach((key, entry) -> {
            for (int i = 0; i < entry.size(); i++) {
                Instance ins = entry.get(i);
                if (ins.isReleasable()) {
                    // calculate cost
                    float insCost = ins.getCost();
                    _wrapCost += insCost;

                    // remove
                    _updateIns(key, i, null);
                }
            }
        });
    }

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
    
    public int countIns() {
        int res = 0;
        for (int key : _insMap.keySet()){
            res += _insMap.get(key).size();
        }
        return res;
    }

}
