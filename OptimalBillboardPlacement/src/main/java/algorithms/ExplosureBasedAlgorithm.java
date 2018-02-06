package algorithms;

import entity.Billboard;
import entity.BillboardSet;
import fileIO.finalResult.FinalResultReader;

import java.util.List;

/**
 * Created by marco on 04/06/2017.
 */
public class ExplosureBasedAlgorithm {

    // TODO : remember weeklyImpression = charge for now

    public static final int BUDGET = 1000;


    public static void main(String[] args) {


        ExplosureBasedAlgorithm explosureBasedAlgorithm = new ExplosureBasedAlgorithm();

        FinalResultReader finalResultReader = new FinalResultReader();
        List<Billboard> billboards = finalResultReader.getBillboards();
        BillboardSet pickedSet = explosureBasedAlgorithm.getMaxInfluence(billboards, BUDGET);

        for (String billboardID : pickedSet.billboards)
            System.out.print(billboardID + ", ");
        System.out.println("\ninfluence: " + pickedSet.routeIDs.size());
    }


    public BillboardSet getMaxInfluence(List<Billboard> billboards, int budgetRemains) {

        BillboardSet billboardSet = new BillboardSet();

        while(budgetRemains > 0 && !billboards.isEmpty()) {

            double maxExplosure = 0;
            int maxExplosureBoardIndex = -1;

            for (int i = 0; i < billboards.size(); i++) {

                Billboard billboard = billboards.get(i);
                double billboardExplosure = billboard.charge;

                if (maxExplosure < billboardExplosure) {
                    maxExplosure = billboardExplosure;
                    maxExplosureBoardIndex = i;
                }
            }

            // pick up one billboard
            Billboard pickedBoard = billboards.get(maxExplosureBoardIndex);

            if (pickedBoard.charge < budgetRemains) {

                budgetRemains -= pickedBoard.charge;
                billboardSet.add(pickedBoard);
                billboards.remove(maxExplosureBoardIndex);

            } else {

                // can not afford this billboard, remove it so that we can find if any remaining billboards can be picked
                billboards.remove(maxExplosureBoardIndex);
                break;
            }
        }
        return billboardSet;
    }

}
