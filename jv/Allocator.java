package dissertation.jv;

public interface Allocator {
    public void allocatePlayer(int demand);
    public void removeRandomPlayer();
    public void tickIns(int deltaTime);
    public void trimIns();
    public float getCost();
}
