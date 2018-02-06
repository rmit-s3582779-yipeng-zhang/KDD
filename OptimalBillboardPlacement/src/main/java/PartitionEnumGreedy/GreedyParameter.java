package PartitionEnumGreedy;

/**
 * Created by Lancer on 2017/7/22.
 */
public class GreedyParameter {

    public double budgetRemains;

    public int firstBillboardIndex;

    public int secondBillboardIndex;

    public int thirdBillboardIndex;


    @Override
    public String toString() {

        String result = "";
        result += "budgetRemains : " + budgetRemains + "\n";
        result += "firstBillboardIndex : " + firstBillboardIndex + "\n";
        result += "secondBillboardIndex : " + secondBillboardIndex + "\n";
        result += "thirdBillboardIndex : " + thirdBillboardIndex + "\n";

        return result;
    }

}