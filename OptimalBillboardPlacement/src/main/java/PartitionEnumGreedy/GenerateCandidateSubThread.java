package PartitionEnumGreedy;

import Knapsack.ClusterBillboard;
import algorithms.GreedyAlgorithm;
import entity.Billboard;
import entity.Route;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lancer on 2017/7/22.
 */
public class GenerateCandidateSubThread extends Thread {

    private double budget;
    private List<Billboard> billboardList;
    private List<Billboard> billboardListRaw;
    private List<GreedyParameter> greedyParameters;
    private ClusterBillboard result;
    private static double costPerformanceThreshold = 0;
    //private SerializeClone serializeClone = new SerializeClone();

    public GenerateCandidateSubThread(double budget, List<Billboard> billboardList, ClusterBillboard result) {
        this.budget = budget;
        this.billboardListRaw = billboardList;
        this.result = result;

    }

    public GenerateCandidateSubThread(double budget, List<Billboard> billboardList, ClusterBillboard result, double costPerformanceThreshold) {
        this.budget = budget;
        this.billboardListRaw = billboardList;
        this.result = result;
        this.costPerformanceThreshold = costPerformanceThreshold;

    }

    @Override
    public void run() {
        duplicateBillboard();
        this.greedyParameters = generateGreedyParameters(billboardList, (int) budget);
        generateCandidateK1();
        generateCandidateK2();
        generateCandidateK3();
    }

    private void duplicateBillboard() {
        //serializeClone.clone(this.billboardListRaw);
        //this.billboardList=serializeClone.getClone();
        BillboardListDuplicator duplicater = new BillboardListDuplicator(this.billboardListRaw);
        this.billboardList = duplicater.getBillboards(budget);//get billboards that charge is smaller than budget
    }

    private List<GreedyParameter> generateGreedyParameters(List<Billboard> billboardList, int budget) {
        List<GreedyParameter> parameterList = new ArrayList<>();
        for (int i = 0; i < billboardList.size(); i++) {
            if (billboardList.get(i).influencePerCharge < costPerformanceThreshold)
                continue;
            for (int j = i + 1; j < billboardList.size(); j++) {
                if (billboardList.get(j).influencePerCharge < costPerformanceThreshold)
                    continue;
                for (int k = j + 1; k < billboardList.size(); k++) {
                    if (billboardList.get(k).influencePerCharge < costPerformanceThreshold)
                        continue;
                    GreedyParameter parameter = new GreedyParameter();
                    parameter.firstBillboardIndex = i;
                    parameter.secondBillboardIndex = j;
                    parameter.thirdBillboardIndex = k;
                    parameter.budgetRemains = budget - billboardList.get(i).charge - billboardList.get(j).charge
                            - billboardList.get(k).charge;
                    if (parameter.budgetRemains >= 0)
                        parameterList.add(parameter);
                }
            }
        }
        return parameterList;
    }


    //generate candidate list while k = 1
    private void generateCandidateK1() {
        int indexMax;
        double influence = 0;
        Billboard billboard = new Billboard();
        indexMax = billboardList.size();
        for (int n = 0; n < indexMax; n++) {
            if (billboardList.get(n).influence > influence) {
                billboard = billboardList.get(n);
                influence = billboard.influence;
            }
        }
        if (influence == 0)
            result.add(new ClusterBillboard());//cannot find any one meeting the condition
        else
            result.set(new ClusterBillboard(billboard));
    }

    //generate candidate list while k = 2
    private void generateCandidateK2() {
        int indexMax;
        double influence = 0;
        int billboardSize = billboardList.size();
        ArrayList<ClusterBillboard> candidates = new ArrayList<ClusterBillboard>(billboardSize * billboardSize);
        ClusterBillboard clusterBillboard;

        for (int i = 0; i < billboardSize; i++) {
            for (int n = i + 1; n < billboardSize; n++) {
                if ((billboardList.get(i).charge + billboardList.get(n).charge) > budget)
                    continue;
                clusterBillboard = new ClusterBillboard(billboardList.get(i));
                clusterBillboard.add(billboardList.get(n));
                candidates.add(clusterBillboard);
            }
        }
        indexMax = candidates.size();
        clusterBillboard = new ClusterBillboard();
        for (int n = 0; n < indexMax; n++) {
            if (candidates.get(n).getInfluence() > influence) {
                candidates.get(n).updateInfluence();
                if (candidates.get(n).getInfluence() > influence) {
                    clusterBillboard = candidates.get(n);
                    influence = clusterBillboard.getInfluence();
                }
            }
        }
        if (influence > result.getInfluence())
            result.set(clusterBillboard);
    }

    //generate candidate list while k > 3
    private void generateCandidateK3() {
        int indexMax;
        List<Billboard> billboards;
        ClusterBillboard greedyResult;
        GreedyAlgorithm greedyAlgorithm = new GreedyAlgorithm();
        indexMax = greedyParameters.size();

        for (int n = 0; n < indexMax; n++) {
            //billboards = serializeClone.getClone();
            billboards = getBillboards();
            GreedyParameter greedyParameter = greedyParameters.get(n);
            ClusterBillboard candidate = new ClusterBillboard();
            candidate.add(billboards.get(greedyParameter.firstBillboardIndex));//get the enum form all billboards based on the enum parameters
            candidate.add(billboards.get(greedyParameter.secondBillboardIndex));
            candidate.add(billboards.get(greedyParameter.thirdBillboardIndex));

            //billboards = getBillboards();//fetch billboards from this cluster
            //delete 3 enum from candidate billboards
            billboards.remove(greedyParameter.thirdBillboardIndex);
            billboards.remove(greedyParameter.secondBillboardIndex);
            billboards.remove(greedyParameter.firstBillboardIndex);

            //remove all billboards which is over budget
            for (int m = 0; m < billboards.size(); m++) {
                if (billboards.get(m).charge > greedyParameter.budgetRemains) {
                    billboards.remove(m);
                    m--;
                }
            }

            if (billboards.size() == 0)
                continue;

            //update influencePerCharge LazyForward
            double influencePerCharge = 0;
            int index = 0; //index of billboard which has the biggest influencePerCharge
            for (int m = 0; m < billboards.size(); m++) {
                if (billboards.get(m).influencePerCharge > influencePerCharge) {
                    billboards.get(m).updateInfluence();
                    if (billboards.get(m).influencePerCharge > influencePerCharge) {
                        influencePerCharge = billboards.get(m).influencePerCharge;
                        index = m;
                    }
                }
            }

            // do greedy is there is any billboards doesn't over budget
            greedyResult = new ClusterBillboard();
            //BillboardSet billboardSet = greedyAlgorithm.greedy(allBillboards, i,greddyResult);//get results form greedy
            //greedyAlgorithm.updateBillboardsLazyForward(billboards);
            greedyAlgorithm.greedy(billboards, greedyParameter.budgetRemains, greedyResult,index);//get results form greedy

            if (greedyResult.getBillboardList().size() > 0) {// check if there is any billboard which can be chosen based on this budget
                candidate.add(greedyResult);
            }
            candidate.updateInfluence();
            if (candidate.getInfluence() > result.getInfluence()) {//update if it's better
                result.set(candidate);
            }
        }
    }

    public List<Billboard> getBillboards() {
        try {
            List<Billboard> billboards = new ArrayList<Billboard>();
            for (Billboard billboard : billboardList) {
                billboards.add((Billboard) billboard.clone());
            }
            return billboards;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }

    private List<Billboard> getBillboard(List<Billboard> billboards) {
        List<Billboard> newBillboards = new ArrayList<Billboard>(billboards.size());
        for (Billboard billboard : billboards) {
            for (Route route : billboard.routes) {
                route.influenced = false;
            }
            billboard.influence = billboard.routes.size();
            newBillboards.add(billboard);
        }
        return newBillboards;
    }

}
