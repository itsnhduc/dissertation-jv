package dissertation.pricing;

public class SimResult {

    public final int quota;
    public final float ondemandCost;
    public final float hybridCost;
    public final float expectedSaving;
    public final int ondemandInsCount;

    public SimResult(int quota, float ondemandCost, float reservedCost, int ondemandInsCount) {
        this.quota = quota;
        this.ondemandCost = ondemandCost;
        this.hybridCost = reservedCost;
        this.expectedSaving = this.ondemandCost - this.hybridCost;
        this.ondemandInsCount = ondemandInsCount;
    }

    @Override
    public String toString() {
        return "Total cost\n"
                + "    On-demand scheme: \t$" + ondemandCost + "\n"
                + "    Hybrid scheme: \t$" + hybridCost + "\n"
                + "    Expected saving: \t$" + expectedSaving;
    }

    public String toLineString() {
        return quota + "\t"
                + ondemandCost + "\t"
                + hybridCost + "\t"
                + expectedSaving;
    }

}
