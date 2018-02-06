package algorithms;

import PartitionEnumGreedy.BillboardListDuplicator;
import configure.Developer;
import entity.Billboard;
import entity.BillboardSet;
import entity.Route;
import fileIO.MyFileWriter;
import fileIO.finalResult.FinalResultReader;

import java.util.Date;
import java.util.List;

/**
 * Created by marco on 15/06/2017.
 */
public class EnumPhaseTwoThread extends Thread {

    private List<EnumBasedGreedyAlgorithm.GreedyParameter> parameterList;
    public BillboardSet maxInfluenceSet;
    private MyFileWriter fileWriter;
    private List<Billboard> allBillboards;
    private double BUDGET;


    public EnumPhaseTwoThread(Double BUDGET, String name, List<EnumBasedGreedyAlgorithm.GreedyParameter> parameterList, BillboardSet maxInfluenceSet, List<Billboard> allBillboards, MyFileWriter fileWriter) {

        super(name);
        this.BUDGET = BUDGET;
        this.parameterList = parameterList;
        this.maxInfluenceSet = maxInfluenceSet;
        this.allBillboards = allBillboards;
        this.fileWriter = fileWriter;
    }

    @Override
    public void run() {
        Date date1 = new Date();
        System.out.println(getName() + " size " + parameterList.size());
        BillboardListDuplicator duplicator = new BillboardListDuplicator(allBillboards);
        for (int i = 0; i < parameterList.size(); i++) {

            if (i % 10000 == 0)
                System.out.println(getName() + " " + i);

            EnumBasedGreedyAlgorithm.GreedyParameter parameters = parameterList.get(i);
            BillboardSet billboardSet = callGreedy(parameters, duplicator.getBillboards());

            if (billboardSet != null) {
                if (maxInfluenceSet.routeIDs.size() < billboardSet.routeIDs.size())
                    maxInfluenceSet = billboardSet;
            }
        }
        fileWriter.writeToFile(this.getName());
        fileWriter.writeToFile("Influence : " + String.valueOf(maxInfluenceSet.routeIDs.size()) + "\r\n");
        Date date2 = new Date();
        long begin = date1.getTime();
        long end = date2.getTime();
        fileWriter.writeToFile("Total runtime: " + ((end - begin) / 1000d) + "\r\n");
        System.out.println(maxInfluenceSet);
    }


    private BillboardSet callGreedy(EnumBasedGreedyAlgorithm.GreedyParameter parameters, List<Billboard> allBillboards) {

        GreedyAlgorithm greedyAlgorithm = new GreedyAlgorithm();

        Billboard firstBillboard = allBillboards.get(parameters.firstBillboardIndex);
        Billboard secondBillboard = allBillboards.get(parameters.secondBillboardIndex);
        Billboard thirdBillboard = allBillboards.get(parameters.thirdBillboardIndex);

        // remove 3 already picked billboards and update remain billboards influences
        allBillboards.remove(firstBillboard);
        allBillboards.remove(secondBillboard);
        allBillboards.remove(thirdBillboard);

        for (Route route : firstBillboard.routes)
            route.influenced = true;

        for (Route route : secondBillboard.routes)
            route.influenced = true;

        for (Route route : thirdBillboard.routes)
            route.influenced = true;

        for(int i=0;i<allBillboards.size();i++){
            if(allBillboards.get(i).charge>parameters.budgetRemains){
                allBillboards.remove(i);
                i--;
            }
        }

        greedyAlgorithm.updateBillboards(allBillboards);

        BillboardSet billboardSet = greedyAlgorithm.greedy(allBillboards, parameters.budgetRemains);
        billboardSet.add(firstBillboard);
        billboardSet.add(secondBillboard);
        billboardSet.add(thirdBillboard);

        return billboardSet;

    }
}
