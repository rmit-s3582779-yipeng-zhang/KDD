package PartitionEnumGreedy;

import Knapsack.ClusterBillboard;
import entity.Billboard;
import entity.BillboardSet;
import entity.Route;
import fileIO.finalResult.MultipleResultReader;

import java.util.*;


public class GreedyAlgorithmForced {

    public static boolean lazyForward = true;

    private static double BUDGET = 500;

    public static void main(String[] args) {

        GreedyAlgorithmForced greedy = new GreedyAlgorithmForced();

        MultipleResultReader multipleResultReader = new MultipleResultReader(1, 2);
        List<Billboard> billboards = multipleResultReader.getBillboards();

        //BillboardSet pickedSet = greedy.greedy(billboards, BUDGET);
        double influence = greedy.greedy2(billboards, BUDGET);

        System.out.println(influence);
    }

    public double greedy2(List<Billboard> remainingBillboards, double budget) {
        double influence = 0;
        BillboardSet pickedBillboards = new BillboardSet();
        //updateBillboardsLazyForward(remainingBillboards, budget);    // 1. reorder by routes/charge
        Collections.sort(remainingBillboards);
        double budgetRemains = budget;

        Billboard board = remainingBillboards.get(0);
        while (board.charge > budgetRemains) {

            remainingBillboards.remove(0);

            if (remainingBillboards.size() == 0)
                return 0;

            board = remainingBillboards.get(0);
        }

        budgetRemains -= board.charge;
        influence += board.influence;

        if (budgetRemains > 0) {
            influence += budgetRemains / board.charge * board.influence;
        }
        return influence;
    }

    public double greedy(List<Billboard> remainingBillboards, double budget) {
        double influence = 0;
        BillboardSet pickedBillboards = new BillboardSet();
        //updateBillboardsLazyForward(remainingBillboards, budget);    // 1. reorder by routes/charge
        Collections.sort(remainingBillboards);
        double budgetRemains = budget;

        Billboard board = remainingBillboards.get(0);
        while (board.charge > budgetRemains) {

            remainingBillboards.remove(0);

            if (remainingBillboards.size() == 0)
                return 0;

            board = remainingBillboards.get(0);
        }

        budgetRemains -= board.charge;
        influence += board.influence;
        pickOne(board, pickedBillboards, remainingBillboards, budgetRemains);

        Billboard firstBoard = new Billboard();
        while (budgetRemains > 0) {

            if (remainingBillboards.size() == 0)
                break;

            firstBoard = remainingBillboards.get(0);

            budgetRemains -= firstBoard.charge;
            influence += firstBoard.influence;
            pickOne(firstBoard, pickedBillboards, remainingBillboards, budgetRemains);

        }
        if (budgetRemains < 0) {
            budgetRemains += firstBoard.charge;
            influence -= firstBoard.influence;
            influence += budgetRemains / firstBoard.charge * firstBoard.influence;
        }
        return influence;
    }


    private void pickOne(Billboard billboard, BillboardSet pickedBoards, List<Billboard> remainingBoards, double budgetRemain) {

        pickedBoards.add(billboard);

        if (budgetRemain == 0)
            return;

        remainingBoards.remove(billboard);

        for (Route route : billboard.routes)
            route.influenced = true;

        if (lazyForward)
            updateBillboardsLazyForward(remainingBoards);
        else
            updateBillboards(remainingBoards);
    }


    // remove overlap influenced routeds
    private void updateBillboardsLazyForward(List<Billboard> billboards) {

        for (int i = 0; i < billboards.size(); i++) {

            Billboard currentBillboard = billboards.get(i);

            currentBillboard.updateInfluence();

            // lazy-forward prune
            if (i + 1 != billboards.size()) {

                Billboard nextBillboard = billboards.get(i + 1);

                if (currentBillboard.influencePerCharge >= nextBillboard.influencePerCharge) {
                    break; // if goes there then billboard after currentBillboard will not be select in the next round & not get updated
                }
            }

            // remove those whose influence is zero
            if (currentBillboard.influence == 0) {
                billboards.remove(i);
                i--;
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

}