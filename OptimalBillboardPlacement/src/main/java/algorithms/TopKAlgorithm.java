
package algorithms;

import entity.Billboard;
import entity.BillboardSet;
import fileIO.finalResult.MultipleResultReader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by marco on 21/07/2017.
 */
public class TopKAlgorithm {

    public static List<Double> costList = new ArrayList<>();

    public static final double BUDGET = 300;



    public static void main(String[] args) {


        MultipleResultReader multipleResultReader = new MultipleResultReader(1, 5);
        List<Billboard> billboards = multipleResultReader.getBillboards();

        for (int i = 0; i < billboards.size(); i++)
            costList.add(billboards.get(i).charge);

        TopKAlgorithm topKAlgorithm = new TopKAlgorithm();
        BillboardSet billboardSet = topKAlgorithm.topK(billboards, BUDGET);

        System.out.println(billboardSet);

    }


    public BillboardSet topK(List<Billboard> billboards, double budget) {

        BillboardSet billboardSet = new BillboardSet();
        double budgetRemains = budget;

        Collections.sort(billboards);

        int numberOfBillboard = billboards.size();

        for (int i = 0; i < numberOfBillboard; i++) {

            Billboard firstBoard = billboards.get(0);

            if (firstBoard.charge < budgetRemains) {

                billboardSet.add(firstBoard);
                budgetRemains -= firstBoard.charge;
            }
            billboards.remove(0);
        }

        return billboardSet;
    }

}
