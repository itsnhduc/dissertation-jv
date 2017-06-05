package dissertation.pricing;

public class QuotaTestResult {

    public final int quota;
    public final float ondemandCost;
    public final float reservedCost;
    public final float expectedSaving;

    public QuotaTestResult(int quota, float ondemandCost, float reservedCost) {
        this.quota = quota;
        this.ondemandCost = ondemandCost;
        this.reservedCost = reservedCost;
        this.expectedSaving = this.ondemandCost - this.reservedCost;
    }

}
