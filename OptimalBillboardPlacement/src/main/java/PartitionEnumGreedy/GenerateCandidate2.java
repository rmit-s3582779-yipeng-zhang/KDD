package PartitionEnumGreedy;

import Knapsack.ClusterBillboard;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import algorithms.GreedyAlgorithm;
import entity.*;
import fileIO.Serialize;
import partition.PartitionAlgorithm;
import Knapsack.*;
import fileIO.finalResult.MultipleResultReader;

import java.io.FileInputStream;
import java.io.ObjectInputStream;

/**
 * Created by Lancer on 2017/7/7.
 */
public class GenerateCandidate2 {

    private double budget;
    private List<Billboard> billboardsRaw = new ArrayList<Billboard>();
    private ArrayList<ClusterBillboard> lastFinalCandidateRow;
    private ArrayList<Double> UpperBoundRow = new ArrayList<Double>();
    private ArrayList<ArrayList<ClusterBillboard>> finalCandidate = new ArrayList<ArrayList<ClusterBillboard>>();
    private List<List<Billboard>> billboardLists;
    private ArrayList<ArrayList<Double>> upperBoundMatrix;
    private static int chargeDensity = 5;
    private static double upbound = 1.4;
    private static double costPerformanceThreshold = 0;
    private static int trajectoryNumber = 2;
    private static int clusterNumber = 10;

    public static void main(String[] args) {
        double BUDGET = 100;

        String clusterName = "Cluster" + clusterNumber + "_" + (int) BUDGET + "_" + chargeDensity + "_" + trajectoryNumber + "_" + upbound + ".cluster";
        String upperBoundName = "Cluster" + clusterNumber + "_" + (int) BUDGET + "_" + chargeDensity + "_" + trajectoryNumber + "_" + upbound + ".UB";
        ArrayList<ArrayList<ClusterBillboard>> candidates;
        Serialize serialize = new Serialize();
        GenerateCandidate2 generateCandidate = new GenerateCandidate2();
        generateCandidate.budget = BUDGET;
        candidates = serialize.deserializeMatrix(clusterName);//read candidates from file
        if (candidates == null) {
            //PartitionAlgorithm partitionAlgorithm = new PartitionAlgorithm(clusterNumber, 1, trajectoryNumber);
            //generateCandidate.billboardLists = partitionAlgorithm.getClusters();
            generateCandidate.billboardLists = generateCandidate.getPartition();

            generateCandidate.billboardLists = generateCandidate.orderBillboardList();
            //generateCandidate.findCostPerformanceThreshold();
            generateCandidate.upperBoundMatrix = generateCandidate.generateUpperBound();

            Date date1 = new Date();
            candidates = generateCandidate.getCandidate();
            Date date2 = new Date();
            long begin = date1.getTime();
            long end = date2.getTime();
            System.out.println("runtime: " + ((end - begin) / 1000d));
            serialize.serializeMatrix(candidates, clusterName);
            serialize.serializeUpperBoundMatrix(generateCandidate.upperBoundMatrix, upperBoundName);
        }

        printCandidate(candidates);
        //Knapsack5 knapsack = new Knapsack5(candidates,upperBoundMatrix, chargeDensity);
        Knapsack4 knapsack = new Knapsack4(candidates, chargeDensity);
        ArrayList<ClusterBillboard> result = knapsack.knapsack(BUDGET);
        //MultipleResultReader multipleResultReader = new MultipleResultReader(1, trajectoryNumber);
        //ArrayList<BillboardSet> greedyResult = generateCandidate.getGreedyList(multipleResultReader.getBillboards(), BUDGET);
        ArrayList<BillboardSet> greedyResult = generateCandidate.getGreedyList(generateCandidate.combineCluster(), BUDGET);
        generateCandidate.printGreedyResults(greedyResult);
        generateCandidate.compareResult(greedyResult, result);
    }

    private List<Billboard> combineCluster() {
        List<Billboard> billboardList = new ArrayList<Billboard>();
        for (List<Billboard> billboards : getPartition()) {
            billboardList.addAll(billboards);
        }
        return billboardList;
    }

    private List<List<Billboard>> getPartition() {
        try {
            String root = "G:\\JAVA\\Optimal-Billboard-Placement\\cluster\\";
            FileInputStream fis = new FileInputStream(root + trajectoryNumber + "w-trip-" + clusterNumber + "-Clusters.cluster");
            ObjectInputStream ois = new ObjectInputStream(fis);
            Object obj = ois.readObject();

            Clusters clusters = (Clusters) obj;

            List<List<Billboard>> clu = clusters.clusters;
            ois.close();

            List<List<Billboard>> clu2 = new ArrayList<List<Billboard>>(clu.size());
            for (List<Billboard> billboards : clu) {
                List<Billboard> newRow = new ArrayList<Billboard>();
                for (Billboard billboard : billboards) {
                    if (billboard.routes.size() > 0) {
                        //billboard.charge = Math.pow(billboard.influence, (2.0 / 3.0));
                        //billboard.charge = Math.random() * 100;
                    }
                    billboard.charge = (int) (Math.round(billboard.charge / 5)) * 5;
                    if (billboard.charge == 0)
                        billboard.charge = 5;
                    newRow.add(billboard);
                }
                if (newRow.size() > 0)
                    clu2.add(newRow);
            }

            return clu2;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void findCostPerformanceThreshold() {
        List<Billboard> allBillboard = new ArrayList<Billboard>(1000);
        for (List<Billboard> list : billboardLists) {
            for (Billboard billboard : list) {
                allBillboard.add(billboard);
            }
        }
        Collections.sort(allBillboard);
        costPerformanceThreshold = allBillboard.get((int) (allBillboard.size() * 0.9)).influencePerCharge;
    }

    private List<List<Billboard>> orderBillboardList() {
        int listSize = billboardLists.size();
        List<List<Billboard>> newbillboardLists = new ArrayList<List<Billboard>>(listSize);
        double[] costPerformance = new double[listSize];
        double cost, influence;
        for (int i = 0; i < listSize; i++) {
            cost = 0;
            influence = 0;
            List<Billboard> billboardRow = billboardLists.get(i);
            for (Billboard billboard : billboardRow) {
                cost += billboard.charge;
                influence += billboard.routes.size();
            }
            costPerformance[i] = influence / cost;
        }
        double maxInfluence;
        int index = 0;
        for (int i = 0; i < listSize; i++) {
            maxInfluence = 0;
            for (int n = 0; n < listSize; n++) {
                if (costPerformance[n] > maxInfluence) {
                    maxInfluence = costPerformance[n];
                    index = n;
                }
            }
            newbillboardLists.add(billboardLists.get(index));
            costPerformance[index] = 0;

        }
        return newbillboardLists;
    }

    private ArrayList<ArrayList<ClusterBillboard>> getCandidate() {
        int i = 0;
        for (List<Billboard> billboardList : billboardLists) {
            if (i == 1)
                System.out.print("");
            Date date1 = new Date();
            UpperBoundRow = upperBoundMatrix.get(i);
            billboardsRaw = billboardList;
            ArrayList<ClusterBillboard> candidate = combineCandidate();
            lastFinalCandidateRow = candidate;
            finalCandidate.add(candidate);
            System.out.println("");
            System.out.print("Finish " + i++);
            Date date2 = new Date();
            long begin = date1.getTime();
            long end = date2.getTime();
            System.out.println("   runtime: " + ((end - begin) / 1000d));
        }
        return finalCandidate;
    }

    private ArrayList<ClusterBillboard> combineCandidate() {

        ArrayList<ClusterBillboard> candidates = generateCandidateK1();
        candidates = generateCandidateK2(candidates);
        candidates = generateCandidateK3(candidates);
        return candidates;
    }


    public class GreedyParameter {

        private double budgetRemains;

        private int firstBillboardIndex;

        private int secondBillboardIndex;

        private int thirdBillboardIndex;


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

    private List<GreedyParameter> generateGreedyParameters() {
        List<GreedyParameter> parameterList = new ArrayList<>();
        for (int i = 0; i < billboardsRaw.size(); i++) {
            if (billboardsRaw.get(i).influencePerCharge < costPerformanceThreshold)
                continue;
            for (int j = i + 1; j < billboardsRaw.size(); j++) {
                if (billboardsRaw.get(j).influencePerCharge < costPerformanceThreshold)
                    continue;
                for (int k = j + 1; k < billboardsRaw.size(); k++) {
                    if (billboardsRaw.get(k).influencePerCharge < costPerformanceThreshold)
                        continue;
                    GreedyParameter parameter = new GreedyParameter();
                    parameter.firstBillboardIndex = i;
                    parameter.secondBillboardIndex = j;
                    parameter.thirdBillboardIndex = k;
                    parameter.budgetRemains = budget - billboardsRaw.get(i).charge - billboardsRaw.get(j).charge
                            - billboardsRaw.get(k).charge;
                    if (parameter.budgetRemains >= 0)
                        parameterList.add(parameter);
                }
            }
        }
        return parameterList;
    }

    //generate candidate list while k = 1
    private ArrayList<ClusterBillboard> generateCandidateK1() {
        int indexMax;
        double influence;
        ArrayList<ClusterBillboard> candidates = new ArrayList<ClusterBillboard>((int) budget + 1);
        List<Billboard> billboards;
        billboards = getBillboards();
        candidates.add(new ClusterBillboard());
        Billboard billboard = new Billboard();
        indexMax = billboards.size();
        for (int i = 1; i <= budget; i++) {
            influence = 0;
            for (int n = 0; n < indexMax; n++) {
                if (billboards.get(n).charge <= i) {
                    if (billboards.get(n).influence > influence) {
                        billboard = billboards.get(n);
                        influence = billboard.influence;
                    }
                }
            }
            if (influence == 0)
                candidates.add(new ClusterBillboard());//cannot find any one meeting the condition
            else
                candidates.add(new ClusterBillboard(billboard));
        }
        return candidates;
    }

    //generate candidate list while k = 2
    private ArrayList<ClusterBillboard> generateCandidateK2(ArrayList<ClusterBillboard> results) {
        int indexMax;
        double influence;
        List<Billboard> billboards;
        billboards = getBillboards();
        int billboardSize = billboards.size();
        ArrayList<ClusterBillboard> candidates = new ArrayList<ClusterBillboard>(billboardSize * billboardSize);
        ClusterBillboard clusterBillboard;

        for (int i = 0; i < billboardSize; i++) {
            for (int n = i + 1; n < billboardSize; n++) {
                clusterBillboard = new ClusterBillboard(billboards.get(i));
                clusterBillboard.add(billboards.get(n));
                candidates.add(clusterBillboard);
            }
        }
        indexMax = candidates.size();
        clusterBillboard = new ClusterBillboard();
        for (int i = chargeDensity; i <= budget; i += chargeDensity) {
            influence = 0;
            for (int n = 0; n < indexMax; n++) {
                if (candidates.get(n).getCharge() <= i) {
                    candidates.get(n).updateInfluence();
                    if (candidates.get(n).getInfluence() > influence) {
                        clusterBillboard = candidates.get(n);
                        influence = clusterBillboard.getInfluence();
                    }
                }
            }
            if (influence > results.get(i).getInfluence())
                results.get(i).set(clusterBillboard);
        }
        return results;
    }

    //generate candidate list while k > 3
    private ArrayList<ClusterBillboard> generateCandidateK3(ArrayList<ClusterBillboard> results) {
        int indexMax;
        boolean ifPass;
        boolean[] jumpList = new boolean[(int) budget + 1];
        List<Billboard> billboards;
        ClusterBillboard greedyResult = new ClusterBillboard();
        GreedyAlgorithm greedyAlgorithm = new GreedyAlgorithm();
        List<GreedyParameter> greedyParameters = generateGreedyParameters();//get the enum parameters
        indexMax = greedyParameters.size();

        System.out.println("indexMax : " + indexMax);
        for (int n = 0; n < indexMax; n++) {
            if (n % 1000 == 0)
                System.out.print(n / 1000 + " , ");

            billboards = getBillboards();
            GreedyParameter greedyParameter = greedyParameters.get(n);
            ClusterBillboard candidate = new ClusterBillboard();
            candidate.add(billboards.get(greedyParameter.firstBillboardIndex));//get the enum form all billboards based on the enum parameters
            candidate.add(billboards.get(greedyParameter.secondBillboardIndex));
            candidate.add(billboards.get(greedyParameter.thirdBillboardIndex));

            int cost = (int) Math.ceil(candidate.getCharge());

            candidate.updateInfluence();//update the candidates influence
            //cost = candidate.cost (k=3)
            if (candidate.getInfluence() > results.get(cost).getInfluence()) {//update if these three enum are  better
                results.get(cost).set(candidate);
            }
            //cost = candidate.cost+1 ~  budget (k>3)
            for (int i = chargeDensity; i <= budget - cost; i += chargeDensity) {
                //bound based selection
                /*
                if (jumpList[i] == true)
                    continue;

                if (lastFinalCandidateRow != null) {
                    ifPass = true;
                    for (int p = chargeDensity; p <= i; p += chargeDensity) {
                        if ((lastFinalCandidateRow.get(i - p).getInfluence() + UpperBoundRow.get(p)) < lastFinalCandidateRow.get(i).getInfluence()) {
                            ifPass = true;
                            jumpList[i] = true;
                            break;
                        }
                    }
                    if (ifPass) {
                        results.get(i + cost).set(lastFinalCandidateRow.get(i + cost));
                        continue;
                    }
                }
                */

                billboards = getBillboards();//fetch billboards from this cluster
                //delete 3 enum from candidate billboards
                billboards.remove(greedyParameter.thirdBillboardIndex);
                billboards.remove(greedyParameter.secondBillboardIndex);
                billboards.remove(greedyParameter.firstBillboardIndex);

                //remove all billboards which is over budget
                for (int m = 0; m < billboards.size(); m++) {
                    if (billboards.get(m).charge > i) {
                        billboards.remove(m);
                        m--;
                    }
                }
                if (billboards.size() == 0)
                    continue;
                // do greedy is there is any billboards doesn't over budget
                greedyResult = new ClusterBillboard();
                //BillboardSet billboardSet = greedyAlgorithm.greedy(allBillboards, i,greddyResult);//get results form greedy
                //greedyAlgorithm.updateBillboardsLazyForward(billboards);
                greedyAlgorithm.greedy(billboards, i, greedyResult);//get results form greedy


                if (greedyResult.getBillboardList().size() > 0) {// check if there is any billboard which can be chosen based on this budget
                    ClusterBillboard temporaryCluster = new ClusterBillboard();
                    temporaryCluster.add(candidate);
                    temporaryCluster.add(greedyResult);
                    temporaryCluster.updateInfluence();
                    if (temporaryCluster.getInfluence() > results.get(i + cost).getInfluence()) {//update if it's better
                        if ((i + cost) == 50)
                            System.out.print("");
                        results.get(i + cost).set(temporaryCluster);
                    }
                }
            }
        }
        return results;
    }

    public List<Billboard> getBillboards() {
        try {
            List<Billboard> billboards = new ArrayList<Billboard>();
            for (Billboard billboard : billboardsRaw) {
                billboard.resetBillboard();
                billboards.add((Billboard) billboard.clone());
            }
            return billboards;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }

    private ArrayList<ArrayList<Double>> generateUpperBound() {
        ArrayList<ArrayList<Double>> upperBoundMatrix;
        //GreedyAlgorithmForced greedyAlgorithmForced = new GreedyAlgorithmForced();
        GreedyAlgorithm greedyAlgorithm = new GreedyAlgorithm();
        upperBoundMatrix = new ArrayList<ArrayList<Double>>(billboardLists.size() + 1);
        ArrayList<Double> upperBoundRow;
        for (int i = 0; i <= billboardLists.size(); i++) {
            upperBoundRow = new ArrayList<Double>((int) budget + 1);
            for (int n = 0; n <= budget; n++) {
                if (i == 0 || n == 0) {
                    upperBoundRow.add(0.0);
                } else {
                    List<Billboard> newBillboards = getBillboard(billboardLists.get(i - 1));
                    BillboardSet billboardSet = greedyAlgorithm.greedy(newBillboards, n);
                    upperBoundRow.add(((double) billboardSet.routeIDs.size()) * upbound);
                    //upperBoundRow.add(greedyAlgorithmForced.greedy(newBillboards,n));
                }
            }
            upperBoundMatrix.add(upperBoundRow);
        }
        return upperBoundMatrix;
    }

    private ArrayList<BillboardSet> getGreedyList(List<Billboard> remainingBillboards, double budget) {
        ArrayList<BillboardSet> greedyResult = new ArrayList<BillboardSet>((int) budget + 1);
        GreedyAlgorithm greedyAlgorithm = new GreedyAlgorithm();
        List<Billboard> temporalBillboards;
        for (int i = 0; i <= budget; i = i + 1) {
            temporalBillboards = getBillboard(remainingBillboards);
            greedyResult.add(greedyAlgorithm.greedy(temporalBillboards, i));
        }
        return greedyResult;
    }

    private void compareResult(ArrayList<BillboardSet> greedyResult, ArrayList<ClusterBillboard> result) {
        System.out.println("Compare result:");
        double deviation;
        for (int i = 0; i <= budget; i = i + chargeDensity) {
            deviation = result.get(i).getInfluence() - greedyResult.get(i).routeIDs.size();
            //if(deviation<0)
            System.out.print("B" + i + "(" + deviation + "," + ((int) (10000 * deviation / greedyResult.get(i).routeIDs.size())) / 100.0 + "%)");
        }

    }

    private void printGreedyResults(ArrayList<BillboardSet> greedyResult) {
        try {
            System.out.println("Greedy result:");
            for (int i = 0; i <= budget; i += (20 * chargeDensity)) {
                for (int j = i; j < i + (20 * chargeDensity); j = j + chargeDensity) {
                    System.out.print("B" + (j) + "(" + greedyResult.get((j)).routeIDs.size() + ")");
                }
                System.out.println();
            }
            System.out.println();
        } catch (Exception e) {

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

    public static void print(String message, int length) {
        message = String.format("%1$-" + length + "s", message);
        System.out.print(message);
    }

    public static void println(String message, int length) {
        message = String.format("%1$-" + length + "s", message);
        System.out.println(message);
    }

    private static void printCandidate(ArrayList<ArrayList<ClusterBillboard>> candidates) {
        Integer[] length = new Integer[candidates.get(0).size()];
        for (int i = 0; i < candidates.get(0).size(); i++) {
            length[i] = 0;
        }
        for (int i = 0; i < candidates.size(); i++) {
            ArrayList<ClusterBillboard> candidateRow = candidates.get(i);
            for (int n = 0; n < candidateRow.size(); n++) {
                ClusterBillboard cluster = candidateRow.get(n);
                if (length[n] < cluster.getBillboardList().size()) {
                    length[n] = cluster.getBillboardList().size();
                }
            }
        }

        for (int i = 0; i < candidates.size(); i++) {
            ArrayList<ClusterBillboard> candidateRow = candidates.get(i);
            System.out.print("ROW " + i + " : ");
            for (int n = chargeDensity; n < candidateRow.size(); n += chargeDensity) {
                ClusterBillboard cluster = candidateRow.get(n);
                cluster.updateInfluence();
                System.out.print(" B" + n + "{");
                int billboardnumber = cluster.getBillboardList().size();
                for (Billboard billboard : cluster.getBillboardList()) {
                    String message = billboard.panelID + "|";
                    print(message, 8);
                }
                for (int m = 0; m < (length[n] - billboardnumber); m++) {
                    print("", 8);
                }
                System.out.print("}");
            }
            System.out.println();
        }

        for (int i = 0; i < candidates.size(); i++) {
            ArrayList<ClusterBillboard> candidateRow = candidates.get(i);
            System.out.print("ROW " + i + " : ");
            for (int n = chargeDensity; n < candidateRow.size(); n += chargeDensity) {
                ClusterBillboard cluster = candidateRow.get(n);
                cluster.updateInfluence();
                System.out.print(" B" + n + "{");
                int billboardNumber = cluster.getBillboardList().size();
                int tripNumber = 0;
                //for (Billboard billboard : cluster.getBillboard()) {
                //    tripNumber += billboard.routes.size();
                //}
                tripNumber = cluster.getInfluence();
                print(tripNumber + "}", 5);
            }
            System.out.println();
        }
    }
}