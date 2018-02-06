package PartitionEnumGreedy;

import Knapsack.ClusterBillboard;
import algorithms.GreedyAlgorithm;
import entity.Billboard;
import entity.Route;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by Lancer on 2017/10/21.
 */
public class GenerateCandidateSubThread2 extends Thread {

    private double budget;
    private Set<Route> routeList;
    private List<Billboard> billboardList;
    private List<Billboard> billboardListRaw;
    private List<GreedyParameter> greedyParameters;
    private ClusterBillboard result;
    private static double costPerformanceThreshold = 0;
    //private SerializeClone serializeClone = new SerializeClone();

    public GenerateCandidateSubThread2(double budget, List<Billboard> billboardList, ClusterBillboard result) {
        this.budget = budget;
        this.billboardListRaw = billboardList;
        this.result = result;

    }

    public GenerateCandidateSubThread2(double budget, List<Billboard> billboardList, ClusterBillboard result, double costPerformanceThreshold) {
        this.budget = budget;
        this.billboardListRaw = billboardList;
        this.result = result;
        this.costPerformanceThreshold = costPerformanceThreshold;

    }

    @Override
    public void run() {
        duplicateBillboard();
        generateRouteList();
        this.greedyParameters = generateGreedyParameters(billboardList, (int) budget);
        generateCandidateK1();
        generateCandidateK2();
        generateCandidateK3();
    }

    private void generateRouteList() {
        routeList = new TreeSet<>();
        for (Billboard billboard : billboardList) {
            routeList.addAll(billboard.routes);
        }
    }

    private void resetRouteList() {
        for (Route route : routeList)
            route.influenced = false;
    }

    private void resetRouteList(Set<Route> routes) {
        for (Route route : routes)
            route.influenced = false;
    }

    private void resetBillboard() {
        for (Billboard billboard : billboardList) {
            billboard.influence = billboard.routes.size();
            billboard.influencePerCharge = billboard.influence / billboard.charge;
        }
    }
    private void resetBillboard(List<Billboard> billboards) {
        for (Billboard billboard : billboards) {
            billboard.influence = billboard.routes.size();
            billboard.influencePerCharge = billboard.influence / billboard.charge;
        }
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
        resetRouteList();
        resetBillboard();
    }

    //generate candidate list while k > 3
    private void generateCandidateK3() {
        int indexMax;
        List<Billboard> billboards;
        ClusterBillboard greedyResult;
        GreedyAlgorithm greedyAlgorithm = new GreedyAlgorithm();
        indexMax = greedyParameters.size();

        for (int n = 0; n < indexMax; n++) {
            billboards = new ArrayList<Billboard>();
            billboards.addAll(billboardList);
            GreedyParameter greedyParameter = greedyParameters.get(n);
            ClusterBillboard candidate = new ClusterBillboard();
            candidate.add(billboards.get(greedyParameter.firstBillboardIndex));//get the enum form all billboards based on the enum parameters
            candidate.add(billboards.get(greedyParameter.secondBillboardIndex));
            candidate.add(billboards.get(greedyParameter.thirdBillboardIndex));

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
                    if (billboards.get(m).influence == 0) {
                        billboards.remove(m);
                        m--;
                        continue;
                    }
                    if (billboards.get(m).influencePerCharge > influencePerCharge) {
                        influencePerCharge = billboards.get(m).influencePerCharge;
                        index = m;
                    }
                }
            }

            if (billboards.size() > 0) {
                // do greedy is there is any billboards doesn't over budget
                greedyResult = new ClusterBillboard();
                //BillboardSet billboardSet = greedyAlgorithm.greedy(allBillboards, i,greddyResult);//get results form greedy
                //greedyAlgorithm.updateBillboardsLazyForward(billboards);
                greedyAlgorithm.greedy(billboards, greedyParameter.budgetRemains, greedyResult, index);//get results form greedy

                if (greedyResult.getBillboardList().size() > 0) {// check if there is any billboard which can be chosen based on this budget
                    candidate.add(greedyResult);
                }
            }
            if (candidate.getInfluence() > result.getInfluence()) {
                candidate.updateInfluence();
                if (candidate.getInfluence() > result.getInfluence()) {//update if it's better
                    result.set(candidate);
                }
            }
            //resetAll();
            resetRouteList();
            resetBillboard();
        }
    }

    private void resetAll() {
        for (Route route : result.getRoutes()) {
            route.influenced = false;
        }
        for (Billboard billboard : result.getBillboardList()) {
            billboard.influence = billboard.routes.size();
            billboard.influencePerCharge = billboard.influence / billboard.charge;
        }
    }

}
