package algorithms;

import entity.Billboard;
import entity.BillboardSet;
import entity.Route;
import fileIO.finalResult.MultipleResultReader;

import java.util.List;

/**
 * Created by marco on 06/05/2017.
 */
public class GreedyAlgorithm2 {

    public static boolean lazyForward = true;

    public static final double BUDGET = 200;



    public static void main(String[] args) {

        GreedyAlgorithm2 greedy = new GreedyAlgorithm2();

        MultipleResultReader multipleResultReader = new MultipleResultReader(1,2);
        List<Billboard> billboards = multipleResultReader.getBillboards();

        BillboardSet pickedSet = greedy.greedy(billboards, BUDGET);


        System.out.println(pickedSet);
    }

    public BillboardSet greedy(List<Billboard> remainingBillboards, double budget) {

        BillboardSet pickedBillboards = new BillboardSet();
        //updateBillboardsLazyForward(remainingBillboards, budget);    // 1. reorder by routes/charge
        sort(remainingBillboards);
        double budgetRemains = budget;

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

    private void sort(List<Billboard> billboards){
        for(int i=0; i<billboards.size()-1;i++){
            int max = i;
            for(int j=i+1; j<billboards.size();j++){
                if(billboards.get(j).influence>billboards.get(max).influence)
                    max = j;
            }
            Billboard tem = billboards.get(i);
            billboards.set(i,billboards.get(max));
            billboards.set(max,tem);
        }
    }



    private void pickOne(Billboard billboard, BillboardSet pickedBoards, List<Billboard> remainingBoards, double budgetRemain) {

        pickedBoards.add(billboard);

        if (budgetRemain==0)
            return;

        remainingBoards.remove(billboard);

        for (Route route : billboard.routes)
            route.influenced = true;

        updateBillboards(remainingBoards,budgetRemain);
    }

    public void updateBillboards(List<Billboard> billboards, double budgetRemain) {

        for (int i = 0; i < billboards.size(); i++) {

            if(billboards.get(i).charge>budgetRemain){
                billboards.remove(i);
                i--;
                continue;
            }

            Billboard billboard = billboards.get(i);
            billboard.updateInfluence();

            if (billboard.influence == 0){
                billboards.remove(i);
                i--;
            }

        }
        sort(billboards);
    }

}