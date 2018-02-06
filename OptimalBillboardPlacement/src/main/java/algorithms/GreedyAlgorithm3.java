package algorithms;

import Knapsack.ClusterBillboard;
import entity.Billboard;
import entity.BillboardSet;
import entity.Route;

import java.util.Collections;
import java.util.List;

/**
 * Created by marco on 06/05/2017.
 */
public class GreedyAlgorithm3 {

    public static boolean lazyForward = true;

    public BillboardSet greedy(List<Billboard> remainingBillboards, double budget, ClusterBillboard cluster,int index) {
        //index is the one which has the biggest influencePerCharge

        BillboardSet pickedBillboards = new BillboardSet();
        //updateBillboardsLazyForward(remainingBillboards, budget);    // 1. reorder by routes/charge
        //Collections.sort(remainingBillboards);
        double budgetRemains = budget;

        Billboard board = remainingBillboards.get(index);
        while (board.charge > budgetRemains) {

            remainingBillboards.remove(0);

            if (remainingBillboards.size() == 0)
                return pickedBillboards;

            board = remainingBillboards.get(0);
        }

        budgetRemains -= board.charge;
        cluster.add(board);
        pickOne(board, pickedBillboards, remainingBillboards, budgetRemains);

        while (budgetRemains > 0) {

            if (remainingBillboards.size() == 0)
                break;

            Billboard firstBoard = remainingBillboards.get(0);

            budgetRemains -= firstBoard.charge;
            cluster.add(firstBoard);
            pickOne(firstBoard, pickedBillboards, remainingBillboards, budgetRemains);

        }
        return pickedBillboards;
    }

    public BillboardSet greedy(List<Billboard> remainingBillboards, double budget, ClusterBillboard cluster) {
        //index is the one which has the biggest influencePerCharge

        BillboardSet pickedBillboards = new BillboardSet();
        //updateBillboardsLazyForward(remainingBillboards, budget);    // 1. reorder by routes/charge
        //Collections.sort(remainingBillboards);
        double budgetRemains = budget;

        Collections.sort(remainingBillboards);
        Billboard board = remainingBillboards.get(0);
        while (board.charge > budgetRemains) {

            remainingBillboards.remove(0);

            if (remainingBillboards.size() == 0)
                return pickedBillboards;

            board = remainingBillboards.get(0);
        }

        budgetRemains -= board.charge;
        cluster.add(board);
        pickOne(board, pickedBillboards, remainingBillboards, budgetRemains);

        while (budgetRemains > 0) {

            if (remainingBillboards.size() == 0)
                break;

            Billboard firstBoard = remainingBillboards.get(0);

            budgetRemains -= firstBoard.charge;
            cluster.add(firstBoard);
            pickOne(firstBoard, pickedBillboards, remainingBillboards, budgetRemains);

        }
        return pickedBillboards;
    }

    public BillboardSet greedy(List<Billboard> remainingBillboards, double budget) {

        BillboardSet pickedBillboards = new BillboardSet();
        //updateBillboardsLazyForward(remainingBillboards, budget);    // 1. reorder by routes/charge
        Collections.sort(remainingBillboards);
        double budgetRemains = budget;

        if(remainingBillboards.size()==0)
            return pickedBillboards;

        Billboard board = remainingBillboards.get(0);
        while (board.charge > budgetRemains) {

            remainingBillboards.remove(0);

            if (remainingBillboards.size() == 0)
                return pickedBillboards;

            board = remainingBillboards.get(0);
        }

        budgetRemains -= board.charge;
        pickOne(board, pickedBillboards, remainingBillboards, budgetRemains);

        while (budgetRemains > 0) {

            if (remainingBillboards.size() == 0)
                break;

            Billboard firstBoard = remainingBillboards.get(0);

            budgetRemains -= firstBoard.charge;
            pickOne(firstBoard, pickedBillboards, remainingBillboards, budgetRemains);

        }
        return pickedBillboards;
    }



    private void pickOne(Billboard billboard, BillboardSet pickedBoards, List<Billboard> remainingBoards, double budgetRemain) {

        pickedBoards.add(billboard);

        if (budgetRemain<=0)
            return;

        remainingBoards.remove(billboard);

        for (Route route : billboard.routes)
            route.influenced = true;

        if (lazyForward)
            updateBillboardsLazyForward(remainingBoards, budgetRemain);
        else
            updateBillboards(remainingBoards);
    }



    // remove overlap influenced routeds
    private void updateBillboardsLazyForward(List<Billboard> billboards, double budgetRemain) {

        for (int i = 0; i < billboards.size(); i++) {

            Billboard currentBillboard = billboards.get(i);

            if (currentBillboard.charge > budgetRemain) {
                billboards.remove(i);
                i--;
                continue;
            }
            currentBillboard.updateInfluence();

            // remove those whose influence is zero
            if (currentBillboard.influence == 0) {
                billboards.remove(i);
                i--;
                continue;
            }

            // lazy-forward prune
            if (i + 1 != billboards.size()) {

                Billboard nextBillboard = billboards.get(i + 1);

                if (currentBillboard.influencePerCharge >= nextBillboard.influencePerCharge) {
                    break; // if goes there then billboard after currentBillboard will not be select in the next round & not get updated
                }
            }

        }
        Collections.sort(billboards);
    }


    public void updateBillboards(List<Billboard> billboards) {

        for (int i = 0; i < billboards.size(); i++) {

            Billboard billboard = billboards.get(i);
            billboard.updateInfluence();

            if (billboard.influence == 0)
                billboards.remove(i);
        }
        Collections.sort(billboards);
    }

    private static class Region {
        public double lon1;
        public double lan1;
        public double lon2;
        public double lan2;

        public Region(double lon1, double lan1, double lon2, double lan2) {
            this.lon1 = lon1;
            this.lan1 = lan1;
            this.lon2 = lon2;
            this.lan2 = lan2;
        }

        public void setRandomRegion(Billboard billboard) {
            double lon = Math.random() * (lon2 - lon1) + lon1;
            billboard.longitude = lon;
            double lan = Math.random() * (lan2 - lan1) + lan1;
            billboard.lantitude = lan;
        }

        public boolean contain(Billboard billboard) {
            if (billboard.lantitude < lan1)
                return false;
            if (billboard.lantitude > lan2)
                return false;
            if (billboard.longitude < lon1)
                return false;
            if (billboard.longitude > lon2)
                return false;
            return true;
        }
    }


}