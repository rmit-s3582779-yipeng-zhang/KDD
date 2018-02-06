package test;

/**
 * Created by Lancer on 2017/7/26.
 */
public class TestParameter {
    public double budget;
    public int trajectoryNumber;
    public int multipleBillboardNumber;
    public double pow;
    public int removeHighest;
    public boolean ifAddBillboardRandom;
    public boolean ifAdjustCostRandom;
    public boolean ifUpperBound;
    public double reta;


    public TestParameter(double budget, int trajectoryNumber, int multipleBillboardNumber, double pow, int removeHighest, boolean ifAddBillboardRandom, boolean ifAdjustCostRandom, boolean ifUpperBound, double reta) {
        this.budget = budget;
        this.trajectoryNumber = trajectoryNumber;
        this.multipleBillboardNumber = multipleBillboardNumber;
        this.pow = pow;
        this.removeHighest = removeHighest;
        this.ifAddBillboardRandom = ifAddBillboardRandom;
        this.ifAdjustCostRandom = ifAdjustCostRandom;
        this.ifUpperBound = ifUpperBound;
        this.reta = reta;
    }
}