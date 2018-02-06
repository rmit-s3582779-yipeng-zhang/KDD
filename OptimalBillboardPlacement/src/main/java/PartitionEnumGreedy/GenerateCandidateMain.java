package PartitionEnumGreedy;

import Analysis.BillboardAnalysis;
import Knapsack.ClusterBillboard;
import algorithms.EnumBasedGreedyAlgorithm;
import algorithms.GreedyAlgorithm2;
import configure.Developer;
import algorithms.GreedyAlgorithm;
import entity.Billboard;
import entity.BillboardSet;
import entity.Clusters;
import entity.Route;
import fileIO.MyFileWriter;
import fileIO.Serialize;
import fileIO.finalResult.ReadLocation;
import test.TestParameter;
import Annealing.Annealing;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.*;

/**
 * Created by Lancer on 2017/7/7.
 */
public class GenerateCandidateMain {

    private double budget;
    private List<Billboard> billboardsRaw = new ArrayList<>();
    private List<List<Billboard>> billboardListsRaw;
    private List<List<Billboard>> billboardLists;
    private ArrayList<ArrayList<Double>> upperBoundMatrix;
    private ArrayList<Double> costList = new ArrayList<>();


    private static double reta = 0.0;
    private static double theta = 0.05;
    private static int threadNumber = 2;
    private static int chargeDensity = 1;
    private static double costPerformanceThreshold = 1;
    private static ArrayList<Region> regions = new ArrayList<Region>();
    private static double costCoefficient = 10;
    private static int miniBillboardNumber = 70;
    private static double adjustCostExtent = 0.3;
    private static int multipleBillboardNumber = 1;
    private static int removeHighest = 0;

    private static int trajectoryNumber = 4;
    private static int clusterNumber = 10;
    private static double pow = 1.0;
    private static double costPerformanceThresholdPer;
    private static boolean readNYC = true;
    private static boolean readLA = false;
    private static boolean ifUpperBound = true;
    private static boolean costFixed = false;// use fixed cost
    private static boolean randomCost = false;// generate cost randomly
    private static boolean removeBestBilboard = false;
    private static boolean ifCombineCluster = false;
    private static boolean ifPrintLocation = false; // print billboard location
    private static boolean ifShowOverlap = false;
    private static boolean ifAddBillboardRandom = true;//add billboards randomly
    private static boolean ifAdjustCostRandom = false;
    private static boolean testModle = true;
    private static boolean testModleDetial = false;
    //private MyFileWriter fileWriter = new MyFileWriter("./Result.txt");
    private MyFileWriter fileWriter;

    Scanner scan = new Scanner(System.in);


    public static void main(String[] args) {
        if (readNYC) {
            regions.add(new Region(-73.9859676361084, 40.780021490225984, -73.92365455627441, 40.809521739444506));
            regions.add(new Region(-73.94906044006348, 40.83342421616831, -73.78211975097656, 40.88860081193034));
            regions.add(new Region(-74.00287628173828, 40.57067539946112, -73.8888931274414, 40.612388698663665));
            regions.add(new Region(-74.0174674987793, 40.668399962792876, -73.97283554077148, 40.701984159668676));
            regions.add(new Region(-73.96116256713867, 40.65016889724004, -73.89593124389648, 40.67829474034605));
            regions.add(new Region(-73.88116836547852, 40.71213418976526, -73.8310432434082, 40.75323899431278));
            regions.add(new Region(-73.81692409515381, 40.69267860646093, -73.7653398513794, 40.71076792966806));
        }
        if (readLA) {
            regions.add(new Region(-118.360259, 34.091668, -118.291045, 34.153027));
            regions.add(new Region(-118.250171, 34.088509, -118.187149, 34.135355));
            regions.add(new Region(-118.498490, 34.051749, -118.376008, 34.094706));
            regions.add(new Region(-118.272509, 34.035693, -118.195688, 34.075163));
            regions.add(new Region(-118.457932, 33.991406, -118.402277, 34.031634));
            regions.add(new Region(-118.360544, 34.001268, -118.308283, 34.050322));
            regions.add(new Region(-118.262947, 33.977252, -118.178573, 34.016404));
            regions.add(new Region(-118.436692, 33.906554, -118.352747, 33.968414));
            regions.add(new Region(-118.295243, 33.855249, -118.155854, 33.953841));
        }

        ArrayList<TestParameter> testParameter = new ArrayList<>();
        //testParameter.add(new TestParameter(150, 4, 1, 1, 0, true, true, true, 0.0));
        testParameter.add(new TestParameter(10, 4, 1, 1, 0, true, false, true, 0.05));
        //testParameter.add(new TestParameter(150, 8, 1, 1, 0, true, false, true, 0.05));
        //testParameter.add(new TestParameter(150, 12, 1, 1, 0, true, false, true, 0.05));
        //testParameter.add(new TestParameter(150, 16, 1, 1, 0, true, false, true, 0.05));
        //testParameter.add(new TestParameter(150, 20, 1, 1, 0, true, false, true, 0.05));


        GenerateCandidateMain generateCandidate = new GenerateCandidateMain();
        generateCandidate.test(testParameter);
    }

    public void test(ArrayList<TestParameter> testParameters) {

        //String root = GenerateCandidateMain.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        String root = System.getProperty("user.dir");
        if (Developer.SYSTEM.equals("Win"))
            fileWriter = new MyFileWriter(root + "\\ResultNew2.txt");
        else if (Developer.SYSTEM.equals("Linux"))
            fileWriter = new MyFileWriter(root + "/ResultNew2.txt");
        if (testModle)
            System.out.println(root + "/ResultNew2.txt");
        for (TestParameter testParameter : testParameters) {
            try {
                budget = testParameter.budget;
                trajectoryNumber = testParameter.trajectoryNumber;
                multipleBillboardNumber = testParameter.multipleBillboardNumber;
                removeHighest = testParameter.removeHighest;
                ifAddBillboardRandom = testParameter.ifAddBillboardRandom;
                ifUpperBound = testParameter.ifUpperBound;
                pow = testParameter.pow;
                reta = testParameter.reta;

                System.out.println("Budget " + budget + "," + " Trajectory " + trajectoryNumber + ", BillboardNumber " + multipleBillboardNumber * 500 + ", pow " + pow + ", THETA " + reta);
                fileWriter.writeToFile("Budget " + budget + "," + " Trajectory " + trajectoryNumber + ", BillboardNumber " + multipleBillboardNumber * 500 + ", pow " + pow + ", Remove " + removeHighest + ", AddBillboard(" + miniBillboardNumber + ") "
                        + ifAddBillboardRandom + "\r\n");

                String clusterName = "BillboardNumber" + multipleBillboardNumber * 500 + "_" + (int) budget + "_" + chargeDensity + "_" + trajectoryNumber + "_" + pow + ".cluster";
                String upperBoundName = "BillboardNumber" + multipleBillboardNumber * 500 + "_" + (int) budget + "_" + chargeDensity + "_" + trajectoryNumber + "_" + pow + ".UB";
                ArrayList<ArrayList<ClusterBillboard>> result;
                Serialize serialize = new Serialize();
                String city = null;
                if (readNYC)
                    city = "NYC";
                else if (readLA)
                    city = "LA";
                String billboardFileName = "T" + trajectoryNumber + "B" + multipleBillboardNumber * 500 + "C" + (int) costCoefficient + city;
                try {
                    billboardLists = serialize.deserializeBillboard(billboardFileName);
                    if (billboardLists != null)
                        System.out.println("Read data from " + billboardFileName);
                    else
                        System.out.println("Generate billboards Now");
                } catch (Exception e) {
                    System.out.println("Can not read data!");
                }

                if (true) {

                    if (billboardLists == null) {
                        //PartitionAlgorithm partitionAlgorithm = new PartitionAlgorithm(clusterNumber, 1, trajectoryNumber);
                        //generateCandidate.billboardLists = partitionAlgorithm.getClusters();
                        billboardLists = new ArrayList<List<Billboard>>();
                        billboardListsRaw = new ArrayList<List<Billboard>>();
                        if (readLA)
                            billboardListsRaw.addAll(getPartition(ifCombineCluster, "LA"));
                        if (readNYC)
                            billboardListsRaw.addAll(getPartition(ifCombineCluster, "NYC"));
                        //BillboardAnalysis billboardAnalysis=new BillboardAnalysis();
                        //billboardAnalysis.influenceDistribution(billboardListsRaw);

                        if (removeHighest > 0)
                            removeHighest(billboardListsRaw);

                        billboardListsRaw = setCluster(regions, billboardListsRaw);

                        billboardListsRaw = orderBillboardList(billboardListsRaw);

                        billboardLists.addAll(billboardListsRaw);

                        for (List<Billboard> billboards : billboardListsRaw) {
                            BillboardListDuplicator duplicator = new BillboardListDuplicator(billboards);
                            for (int i = 1; i < multipleBillboardNumber; i++) {
                                billboardLists.add(duplicator.getBillboards(i));
                            }
                        }

                        if (randomCost) {
                            String fileName = "random billboards/Billboads_Trajectory_" + trajectoryNumber + "_Cluster_" + clusterNumber + ".billboard";
                            List<List<Billboard>> bestBillboards = serialize.deserializeBillboard(fileName);
                            getBestBillboard(bestBillboards);
                        }

                        if (ifAdjustCostRandom)
                            adjustCostRandom();

                        serialize.serialize(billboardLists, billboardFileName);
                    }

                    billboardLists = orderBillboardList(billboardLists);

                    if (ifShowOverlap)
                        showClusterOverlap(billboardLists);

                    if (costPerformanceThreshold < 1)
                        findCostPerformanceThreshold();

                    upperBoundMatrix = generateUpperBound();
                    //generateCandidate.printUpperBound();

                    Date date1 = new Date();
                    sortBillboardDesc();
                    result = knapsack();
                    Date date2 = new Date();
                    long begin = date1.getTime();
                    long end = date2.getTime();
                    System.out.println("Total runtime: " + ((end - begin) / 1000d));
                    fileWriter.writeToFile("Total runtime: " + ((end - begin) / 1000d) + "\r\n");
                    //serialize.serializeMatrix(result, clusterName);
                    //serialize.serializeUpperBoundMatrix(upperBoundMatrix, upperBoundName);
                    //serialize.serialize(billboardLists, "random billboards/Billboads_Trajectory_"+trajectoryNumber+"_Cluster_"+clusterNumber+".billboard");
                }
                //Greedy
                ArrayList<BillboardSet> greedyResult = getGreedyList(combineCluster(), budget);
                //Annealing
                //ArrayList<ClusterBillboard> annealResult = getAnneal(combineCluster(), budget);
                //Enum
                //EnumBasedGreedyAlgorithm enumBGA = new EnumBasedGreedyAlgorithm(combineCluster(), budget);
                //enumBGA.getPhaseTwoMaxInfluence();

                ArrayList<ClusterBillboard> resultRow = result.get(result.size() - 1);
                rewriteByRaw(resultRow);
                printPartitionResults(resultRow);
                printGreedyResults(greedyResult);
                compareResult(greedyResult, resultRow);
                //printBillboard(resultRow.get(resultRow.size()-1));
                if (ifPrintLocation)
                    printLocation(greedyResult, resultRow);

                printBillboard(resultRow.get(resultRow.size()-1));


            } catch (Exception e) {
            }
        }
    }

    private void removeHighest(List<List<Billboard>> billboardLists) {
        List<Billboard> sortedList = new ArrayList<Billboard>(billboardLists.size());
        List<Billboard> removeList = new ArrayList<Billboard>(removeHighest);
        for (int i = 0; i < billboardLists.size(); i++) {
            List<Billboard> billboards = billboardLists.get(i);
            for (int j = 0; j < billboards.size(); j++) {
                sortedList.add(billboards.get(j));
            }
        }

        for (int i = 0; i < sortedList.size() - 1; i++) {
            int max = i;
            for (int j = i + 1; j < sortedList.size(); j++) {
                if (sortedList.get(j).influence > sortedList.get(max).influence)
                    max = j;
            }
            Billboard tem = sortedList.get(i);
            sortedList.set(i, sortedList.get(max));
            sortedList.set(max, tem);
        }

        for (int i = 0; i < removeHighest && i < sortedList.size(); i++) {
            removeList.add(sortedList.get(i));
        }

        for (int i = 0; i < billboardLists.size(); i++) {
            List<Billboard> billboards = billboardLists.get(i);
            for (int j = 0; j < billboards.size(); j++) {
                for (int n = 0; n < removeList.size(); n++) {
                    if (billboards.get(j) == removeList.get(n)) {
                        billboards.remove(j);
                        j--;
                        removeList.remove(n);
                        break;
                    }
                }
            }
        }

    }

    private void sortBillboard(List<Billboard> remainingBillboards) { // order by influencePerCharge

        for (int i = 0; i < remainingBillboards.size(); i++)
            remainingBillboards.get(i).resetBillboard();

        for (int i = 0; i < remainingBillboards.size() - 1; i++) {
            int min = i;
            for (int j = i + 1; j < remainingBillboards.size(); j++) {
                if (remainingBillboards.get(j).influencePerCharge < remainingBillboards.get(min).influencePerCharge) {
                    min = j;
                    continue;
                }
                if (remainingBillboards.get(j).influence < remainingBillboards.get(min).influence) {
                    min = j;
                    continue;
                }
            }
            Billboard tem = remainingBillboards.get(i);
            remainingBillboards.set(i, remainingBillboards.get(min));
            remainingBillboards.set(min, tem);
        }
    }

    private void sortBillboardDesc() { // order by influencePerCharge desc
        for (List<Billboard> billboards : billboardLists) {

            for (int i = 0; i < billboards.size(); i++)
                billboards.get(i).resetBillboard();

            for (int i = 0; i < billboards.size() - 1; i++) {
                int max = i;
                for (int j = i + 1; j < billboards.size(); j++) {
                    if (billboards.get(j).influencePerCharge > billboards.get(max).influencePerCharge) {
                        max = j;
                        continue;
                    }
                    if (billboards.get(j).influence > billboards.get(max).influence) {
                        max = j;
                        continue;
                    }
                }
                Billboard tem = billboards.get(i);
                billboards.set(i, billboards.get(max));
                billboards.set(max, tem);
            }

        }
    }

    private void showClusterOverlap(List<List<Billboard>> billboardLists) {
        Clusters clusters = new Clusters(billboardLists);

        for (int i = 0; i < billboardLists.size(); ++i) {
            int influence = clusters.getInfluence(i);
            System.out.println("cluster" + i + ", Billboard: " + billboardLists.get(i).size() + ", Influence: " + influence);
        }

        for (int i = 0; i < billboardLists.size(); ++i) {
            for (int j = i + 1; j < billboardLists.size(); ++j) {
                int overlap = clusters.calculateOverlapBetween(i, j);
                System.out.println("Overlap(" + i + "," + j + ") : " + overlap);
            }
        }
    }

    private void adjustCostRandom() {
        int index = 0;
        for (List<Billboard> billboards : billboardLists) {
            index++;
            if (index <= (2 + multipleBillboardNumber))
                continue;
            //from 1-adjust to 1+adjust
            double costCoefficient = (Math.random()) * adjustCostExtent;
            for (Billboard billboard : billboards) {
                double adjustCost = billboard.charge * costCoefficient;
                if (adjustCost < Math.ceil(costCoefficient * 10.0))
                    adjustCost = Math.ceil(costCoefficient * 10.0);
                billboard.charge += Math.ceil(adjustCost);
                billboard.influencePerCharge = billboard.influence / billboard.charge;
            }
        }
    }

    private List<Billboard> addRandomTrajctory(List<Billboard> billboards) {
        Set<Route> routes = new HashSet<Route>();
        List<Route> routeList = new ArrayList<Route>(routes.size());
        int maxTry = 10;//some billboars could be hard to add traj, so try others.

        for (int i = 0; i < billboards.size(); i++) {
            routes.addAll(billboards.get(i).routes);
        }

        Iterator iter = routes.iterator();
        while (iter.hasNext()) {
            routeList.add((Route) iter.next());
        }

        int addInfluence = (int) (routes.size() * reta);

        while (addInfluence > 0) {
            nextBillboard:
            while (true) {
                int billboardIndex = (int) Math.round(Math.random() * (billboards.size() - 1));
                Billboard billboard = billboards.get(billboardIndex);
                for (int i = 0; i < maxTry; i++) {
                    int trajIndex = (int) (Math.random() * (routeList.size() - 1));
                    Route route = routeList.get(trajIndex);
                    if (!billboard.routes.contains(route)) {
                        billboard.routes.add(route);
                        billboard.influence++;
                        billboard.charge = Math.pow(billboard.influence, pow);
                        billboard.charge = billboard.charge / costCoefficient;
                        billboard.charge = (int) (Math.round(billboard.charge / chargeDensity)) * chargeDensity;
                        if (billboard.charge == 0 && billboard.routes.size() > 0)
                            billboard.charge = chargeDensity;
                        billboard.influencePerCharge = billboard.influence / billboard.charge;
                        break nextBillboard;
                    }
                }
                break nextBillboard;
            }
            addInfluence--;
        }

        return billboards;
    }

    private List<Billboard> addRandomBillboard(List<Billboard> billboards, Region region) {

        Set<Route> routes = new HashSet<Route>();
        List<Route> routeList = new ArrayList<Route>(routes.size());

        int totalInfluence = 0;

        for (int i = 0; i < billboards.size(); i++) {
            routes.addAll(billboards.get(i).routes);
            totalInfluence += billboards.get(i).routes.size();
        }

        Iterator iter = routes.iterator();
        while (iter.hasNext()) {
            routeList.add((Route) iter.next());
        }

        int meanInfluence = totalInfluence / billboards.size();

        List<Billboard> newBillboards = new ArrayList<Billboard>(miniBillboardNumber);
        newBillboards.addAll(billboards);

        for (int i = 0; i < (miniBillboardNumber - billboards.size()); i++) {
            routes = new HashSet<Route>();
            Billboard billboard = new Billboard();
            region.setRandomRegion(billboard);
            int routeSize = (int) ((double) meanInfluence * Math.random() * Math.random()) + 1;
            for (int n = 0; n < routeSize; n++) {
                int index = (int) (Math.random() * ((double) (routes.size() - 1)));
                routes.add(routeList.get(index));
            }
            billboard.panelID = String.valueOf((int) (Math.random() * 10000000));
            billboard.routes.addAll(routes);
            billboard.influence = billboard.routes.size();
            //add cost
            if (randomCost)
                billboard.charge = Math.random() * 100;
            else {
                if (billboard.routes.size() > 0)
                    billboard.charge = Math.pow(billboard.influence, pow);
                //billboard.charge=billboard.routes.size()/5;
            }
            billboard.charge = billboard.charge / costCoefficient;
            billboard.charge = (int) (Math.round(billboard.charge / chargeDensity)) * chargeDensity;

            if (billboard.charge == 0 && billboard.routes.size() > 0)
                billboard.charge = chargeDensity;
            billboard.influencePerCharge = billboard.influence / billboard.charge;


            newBillboards.add(billboard);
        }
        return newBillboards;
    }

    private List<List<Billboard>> setCluster(ArrayList<Region> regions, List<List<Billboard>> billboardLists) {
        List<List<Billboard>> newBillboardList = new ArrayList<>();
        for (int i = 0; i < regions.size(); i++) {
            newBillboardList.add(new ArrayList<Billboard>());
        }

        for (List<Billboard> billboards : billboardLists) {
            for (Billboard billboard : billboards) {
                for (int i = 0; i < regions.size(); i++) {
                    if (regions.get(i).contain(billboard)) {
                        newBillboardList.get(i).add(billboard);
                        break;
                    }
                }
            }
        }
        billboardLists = newBillboardList;

        if (ifAddBillboardRandom) {
            for (int i = 0; i < billboardLists.size(); i++) {
                if (billboardLists.get(i).size() < miniBillboardNumber)
                    billboardLists.set(i, addRandomBillboard(billboardLists.get(i), regions.get(i)));
            }
        }

        return billboardLists;
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

    private void printLocation(ArrayList<BillboardSet> greedyResult, ArrayList<ClusterBillboard> partitionResult) {
        int index;
        ArrayList<ClusterBillboard> results;
        try {
            System.out.println("0 exit, 1 greedy, 2 partition");
            index = scan.nextInt();
            if (index == 1) {
                results = new ArrayList<ClusterBillboard>(greedyResult.size());
                for (int i = 0; i < greedyResult.size(); i++) {
                    ClusterBillboard cluster = new ClusterBillboard();
                    for (String billboardInf : greedyResult.get(i).billboards) {
                        String billboardID = billboardInf.split("~")[0];
                        for (Billboard billboard : billboardsRaw) {
                            if (billboard.panelID.equals(billboardID)) {
                                cluster.add(billboard);
                                break;
                            }
                        }
                    }
                    results.add(cluster);
                }
            } else if (index == 2)
                results = partitionResult;
            else return;

            System.out.println("input budget (entry 0 to exit):");
            index = scan.nextInt();
            while (index > 0) {
                if (index < 0 || index > results.size()) {
                    System.out.println("out of budget");
                    index = scan.nextInt();
                    continue;
                }

                for (Billboard billboard : results.get(index).getBillboardList()) {
                    billboard.resetBillboard();
                    System.out.println(billboard.panelID + "(" + billboard.influence + "(:" + billboard.longitude + "," + billboard.lantitude);
                }

                System.out.println("input budget (entry 0 to exit):");
                index = scan.nextInt();
            }
        } catch (Exception e) {
            System.out.println("illegal input!");
        }
    }


    private void getBestBillboard(List<List<Billboard>> bestBillboards) {
        for (List<Billboard> billboards1 : billboardLists) {
            for (int i = 0; i < billboards1.size(); i++) {
                nextOne:
                for (List<Billboard> billboards2 : bestBillboards) {
                    for (int n = 0; n < billboards2.size(); n++) {
                        if (billboards1.get(i).panelID.equals(billboards2.get(n).panelID)) {
                            billboards1.set(i, billboards2.get(n));
                            break nextOne;
                        }
                    }
                }
            }
        }
    }

    private void printBillboard(ClusterBillboard cluster) {
        System.out.println("billboards of result:");
        double budget = 0;
        for (Billboard billboard : cluster.getBillboardList()) {
            budget+=billboard.charge;
            System.out.print(print(billboard.panelID, 10));
            System.out.print(print("B" + billboard.charge, 10));
            System.out.print(print("I" + billboard.influence, 10));
            System.out.println(println("P" + billboard.influencePerCharge, 10));

        }
        System.out.println("Total cost " + budget);
    }

    private void rewriteByRaw(ArrayList<ClusterBillboard> resultRow) {
        try {
            for (ClusterBillboard cluster : resultRow) {
                for (int i = 0; i < cluster.getBillboardList().size(); i++) {
                    nextOne:
                    for (List<Billboard> billboards : billboardLists) {
                        if (billboards.size() == 0)
                            continue;
                        for (int n = 0; n < billboards.size(); n++) {
                            if (cluster.getBillboardList().get(i).panelID.equals(billboards.get(n).panelID)) {
                                cluster.getBillboardList().set(i, billboards.get(n));
                                break nextOne;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (ClusterBillboard cluster : resultRow) {
            cluster.updateInfluence();
        }
    }

    public ArrayList<ArrayList<ClusterBillboard>> knapsack() {
        ArrayList<ArrayList<ClusterBillboard>> matrixResult = new ArrayList<>(clusterNumber + 1);
        ArrayList<Double> upperBoundCandidate;
        ArrayList<ArrayList<ClusterBillboard>> finalCandidate = new ArrayList<>();
        ArrayList<ClusterBillboard> candidate;
        ArrayList<ClusterBillboard> newMatrixRow = new ArrayList<>((int) budget);//to be generated
        ArrayList<ClusterBillboard> lastMatrixRow;// already have been generated
        List<Billboard> billboards;

        double totalBudget = 0;
        for (List<Billboard> billboards2 : billboardLists) {
            for (Billboard billboard : billboards2) {
                totalBudget += billboard.charge;
            }
        }

        System.out.println("Total Cost : " + totalBudget);
        fileWriter.writeToFile("Total Cost : " + totalBudget + "\r\n");

        for (int i = 0; i <= budget; i++) {
            newMatrixRow.add(new ClusterBillboard());
        }
        matrixResult.add(newMatrixRow);

        for (int i = 1; i <= billboardLists.size(); i++) {
            billboards = billboardLists.get(i - 1);
            newMatrixRow = new ArrayList<>((int) budget + 1);
            lastMatrixRow = matrixResult.get(i - 1);
            upperBoundCandidate = upperBoundMatrix.get(i);

            for (int m = 0; m <= budget; m++) {
                newMatrixRow.add(new ClusterBillboard());//initial new Row
            }

            int indexQ = (int) budget;
            double influence;

            Boolean calculate[] = new Boolean[(int) (budget + 1)];

            if (ifUpperBound) {

                for (int n = 0; n < calculate.length; n++) {
                    calculate[n] = false;
                }
                for (int b = (int) budget; b >= chargeDensity; b -= chargeDensity) {// budget
                    //upper bound, check weather it's need to be calculated
                    for (int q = chargeDensity; q <= b; q += chargeDensity) {
                        if (calculate[q] == true)
                            continue;
                        influence = lastMatrixRow.get(b - q).getInfluence() + upperBoundCandidate.get(q);
                        if (influence > lastMatrixRow.get(b).getInfluence()) {
                            calculate[q] = true;
                        }
                    }
                }
                //System.out.println();
            } else {
                for (int n = 0; n < calculate.length; n++) {
                    calculate[n] = true;
                }
            }
            if (testModle) {
                System.out.println("Cluster " + i + ":");
            }
            candidate = getCandidate(billboards, calculate);
            finalCandidate.add(candidate);

            for (int b = chargeDensity; b <= budget; b += chargeDensity) {// budget
                indexQ = -1;
                //do calculate
                double maxInfluence = 0;
                for (int q = chargeDensity; q <= b; q += chargeDensity) {
                    influence = lastMatrixRow.get(b - q).getInfluence() + candidate.get(q).getInfluence();
                    if ((influence > lastMatrixRow.get(b).getInfluence() && (influence > maxInfluence))) {
                        maxInfluence = influence;
                        indexQ = q;
                    }
                }
                if (indexQ >= 0) {
                    newMatrixRow.get(b).add(lastMatrixRow.get((int) (b - indexQ)));
                    newMatrixRow.get(b).add(candidate.get(indexQ));
                    //newMatrixRow.get(b).updateInfluence();
                } else {
                    newMatrixRow.get(b).add(lastMatrixRow.get(b));
                }
            }
            matrixResult.add(newMatrixRow);
        }

        //return the last column of the last row
        if (testModleDetial) {
            System.out.println("Result");
            printCandidate(finalCandidate);
            printCandidate(matrixResult);
        }
        return matrixResult;
    }

    private ArrayList<ClusterBillboard> getCandidate(List<Billboard> billboards, Boolean calculate[]) {
        int maxBudget = calculate.length - 1;
        ArrayList<Thread> threadPool = new ArrayList<>((int) budget);
        ArrayList<ClusterBillboard> result = new ArrayList<>((int) budget + 1);
        GreedyAlgorithm greedyAlgorithm = new GreedyAlgorithm();
        int upperBoundInfluence = greedyAlgorithm.greedy(getBillboard(billboards), 100000000).routeIDs.size();
        int repeatI = -1;
        double maxCharge = 0;
        for (int i = 0; i <= budget; i++) {
            result.add(new ClusterBillboard());
        }

        for (Billboard billboard : billboards) {
            billboard.resetBillboard();
            maxCharge += billboard.charge;
        }
        if (testModle)
            System.out.println("Max charge : " + maxCharge);


        for (int i = chargeDensity; i <= maxBudget; i += chargeDensity) {
            if (i > maxCharge)
                break;
            if (calculate[i] == false)
                continue;
            Thread newThread = new GenerateCandidateSubThread2(i, billboards, result.get(i), costPerformanceThresholdPer);
            newThread.setName(String.valueOf(i));
            threadPool.add(newThread);
        }

        try {
            if (testModle)
                System.out.println("Calcuate Total Number : " + threadPool.size());
            Date date1 = new Date();
            for (int i = 0; i < threadNumber && i < threadPool.size(); i++) {
                threadPool.get(i).start();
            }
            while (threadPool.size() > 0) {
                for (int i = 0; i < threadNumber && i < threadPool.size(); i++) {
                    if (threadPool.get(i) != null && !(threadPool.get(i).isAlive())) {
                        int threadName = Integer.valueOf(threadPool.get(i).getName());
                        if (testModle)
                            System.out.print(threadName + ",");
                        threadPool.remove(i);
                        if (result.get((threadName) * chargeDensity).getInfluence() >= upperBoundInfluence) {
                            repeatI = (threadName) * chargeDensity;
                            System.out.print("pruning from " + repeatI + ",");
                            for (int n = i; n < threadPool.size(); n++) {
                                if (threadPool.get(n).isAlive())
                                    threadPool.get(n).interrupt();
                                threadPool.remove(n);
                            }
                            break;
                        }
                        if (threadPool.size() <= threadNumber - 1)
                            break;
                        if (threadPool.get(threadNumber - 1) != null) {
                            threadPool.get(threadNumber - 1).start();
                        }
                    }
                }
            }
            if (repeatI > 0) {
                for (int i = repeatI; i < maxBudget; i += chargeDensity) {
                    result.get(i + chargeDensity).set(result.get(i));
                }
            }
            /*
            for (int i = 0; i < threadPool.size(); i += threadNumber) {
                for (int t = i; t < i + threadNumber && t < threadPool.size(); t++) {
                    threadPool.get(t).start();
                }
                for (int t = i; t < i + threadNumber && t < threadPool.size(); t++) {
                    threadPool.get(i).join();
                }
                if (testModle)
                    System.out.print(i + threadNumber + ",");
                if ((i + threadNumber) < threadPool.size()) {
                    if (result.get((i + threadNumber) * chargeDensity).getInfluence() >= upperBoundInfluence) {
                        repeatI = (i + threadNumber) * chargeDensity;
                        break;
                    }
                }
            }
            if (repeatI > 0) {
                for (int i = repeatI; i < maxBudget; i += chargeDensity) {
                    result.get(i + chargeDensity).set(result.get(i));
                }
            }
            */

            Date date2 = new Date();
            long begin = date1.getTime();
            long end = date2.getTime();
            if (testModle) {
                System.out.println();
                System.out.println("runtime: " + ((end - begin) / 1000d));
            }
        } catch (Exception e) {
            System.out.println("Tread Error!");
        }
        return result;
    }

    private List<Billboard> combineCluster() {
        List<Billboard> billboardList = new ArrayList<>();
        for (List<Billboard> billboards : billboardLists) {
            billboardList.addAll(billboards);
        }
        for (Billboard billboard : billboardList) {
            billboard.resetBillboard();
        }
        return billboardList;
    }

    private void getFixedCost() {
        try {
            String root = System.getProperty("user.dir");
            //String root = GenerateCandidateMain.class.getProtectionDomain().getCodeSource().getLocation().getPath();
            FileInputStream fis = null;
            if (Developer.SYSTEM.equals("Mac")) {
                fis = new FileInputStream("./cluster/t2w-trip-" + clusterNumber + "-Clusters.cluster");
            } else if (Developer.SYSTEM.equals("Win")) {
                fis = new FileInputStream(root + "\\cluster\\2w-trip-" + clusterNumber + "-Clusters.cluster");
            } else if (Developer.SYSTEM.equals("Linux")) {
                fis = new FileInputStream(root + "/cluster/2w-trip-" + clusterNumber + "-Clusters.cluster");
            }

            ObjectInputStream ois = new ObjectInputStream(fis);
            Object obj = ois.readObject();

            Clusters clusters = (Clusters) obj;

            List<List<Billboard>> clu = clusters.clusters;
            ois.close();

            for (List<Billboard> billboards : clu) {
                for (Billboard billboard : billboards) {
                    if (billboard.routes.size() > 0) {
                        if (randomCost)
                            billboard.charge = Math.random() * 50;
                        else
                            billboard.charge = Math.pow(billboard.influence, pow);
                    }
                    billboard.charge = (int) (Math.floor(billboard.charge / chargeDensity)) * chargeDensity;

                    if (billboard.charge == 0)
                        billboard.charge = 1;
                    costList.add(billboard.charge);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<List<Billboard>> removeBillboard(List<List<Billboard>> clu) {
        double maxInfluence = 0;
        int indexI = 0, indexN = 0;
        for (int i = 0; i < clu.size(); i++) {
            for (int n = 0; n < clu.get(i).size(); n++) {
                if (clu.get(i).get(n).influence > maxInfluence) {
                    maxInfluence = clu.get(i).get(n).influence;
                    indexI = i;
                    indexN = n;
                }
            }
        }
        clu.get(indexI).remove(indexN);
        return clu;
    }

    public List<List<Billboard>> getPartition(boolean combine, String city) {
        String clusterName = "null";
        ReadLocation readLocation = null;
        int clusterNumber = 0;
        if (city.equals("LA")) {
            clusterName = "cluster-la";
            readLocation = new ReadLocation(-118.642000, 33.717900, -118.145000, 34.330962);
            clusterNumber = 25;
        } else if (city.equals("NYC")) {
            clusterName = "cluster-nyc";
            readLocation = new ReadLocation(-74.260948, 40.485284, -73.688285, 40.920459);
            clusterNumber = 10;
        }

        if (costFixed)
            getFixedCost();

        try {
            String root = System.getProperty("user.dir");
            //String root = GenerateCandidateMain.class.getProtectionDomain().getCodeSource().getLocation().getPath();
            FileInputStream fis = null;
            if (Developer.SYSTEM.equals("Mac")) {
                fis = new FileInputStream("./" + clusterName + "/" + trajectoryNumber + "w-trip-" + clusterNumber + "-Clusters.cluster");
            } else if (Developer.SYSTEM.equals("Win")) {
                fis = new FileInputStream(root + "\\" + clusterName + "\\" + trajectoryNumber + "w-trip-" + clusterNumber + "-Clusters.cluster");
            } else if (Developer.SYSTEM.equals("Linux")) {
                fis = new FileInputStream(root + "/" + clusterName + "/" + trajectoryNumber + "w-trip-" + clusterNumber + "-Clusters.cluster");
            }

            ObjectInputStream ois = new ObjectInputStream(fis);
            Object obj = ois.readObject();

            Clusters clusters = (Clusters) obj;

            if (combine) {
                CombineCluster combineCluster = new CombineCluster(clusters, theta);
                clusters = combineCluster.getClusters();
            }

            List<List<Billboard>> clu = clusters.clusters;
            ois.close();

            if (removeBestBilboard) {
                clu = removeBillboard(clu);
            }

            List<List<Billboard>> clu2 = new ArrayList<>(clu.size());
            int index = 0;
            for (List<Billboard> billboards : clu) {
                List<Billboard> newRow = new ArrayList<>();
                for (Billboard billboard : billboards) {
                    if (costFixed)
                        billboard.charge = costList.get(index++);
                    else {
                        if (randomCost)
                            billboard.charge = Math.random() * 100;
                        else {
                            if (billboard.routes.size() > 0)
                                billboard.charge = Math.pow(billboard.influence, pow);
                            //billboard.charge=billboard.routes.size()/5;
                        }
                        billboard.charge = billboard.charge / costCoefficient;
                        billboard.charge = (int) (Math.round(billboard.charge / chargeDensity)) * chargeDensity;

                        if (billboard.charge == 0 && billboard.routes.size() > 0)
                            billboard.charge = chargeDensity;
                        billboard.influencePerCharge = billboard.influence / billboard.charge;
                    }
                    newRow.add(billboard);
                    billboardsRaw.add(billboard);
                }
                readLocation.setLocation(newRow);

                if (reta > 0.0)
                    addRandomTrajctory(billboardsRaw);

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
        List<Billboard> allBillboard = new ArrayList<>(1000);
        for (List<Billboard> list : billboardLists) {
            for (Billboard billboard : list) {
                billboard.resetBillboard();
                allBillboard.add(billboard);
            }
        }
        Collections.sort(allBillboard);
        int index = (int) (allBillboard.size() * 0.8);
        Billboard a = allBillboard.get((int) (allBillboard.size() * 0.8));
        costPerformanceThresholdPer = allBillboard.get((int) (allBillboard.size() * costPerformanceThreshold)).influencePerCharge;
    }

    private List<List<Billboard>> orderBillboardList(List<List<Billboard>> billboardLists) {
        int listSize = billboardLists.size();
        List<List<Billboard>> newBillboardLists = new ArrayList<>(listSize);
        ClusterBillboard clusterBillboard;
        double[] costPerformance = new double[listSize];
        for (int i = 0; i < listSize; i++) {
            List<Billboard> billboardRow = billboardLists.get(i);
            clusterBillboard = new ClusterBillboard();
            for (Billboard billboard : billboardRow) {
                clusterBillboard.add(billboard);
            }
            clusterBillboard.updateInfluence();
            costPerformance[i] = clusterBillboard.getInfluencePerCharge();
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
            for(int n=0;n<billboardLists.get(index).size();n++){
                billboardLists.get(index).get(n).resetBillboard();
            }
            newBillboardLists.add(billboardLists.get(index));
            costPerformance[index] = 0;

        }
        return newBillboardLists;
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
        GreedyAlgorithmForced greedyAlgorithm = new GreedyAlgorithmForced();
        upperBoundMatrix = new ArrayList<>(billboardLists.size() + 1);
        ArrayList<Double> upperBoundRow;
        double upperBound = 0;
        double maxUpperBound = 0;
        int indexI = -1;
        for (int i = 0; i <= billboardLists.size(); i++) {
            if (i > 0) {
                maxUpperBound = greedyAlgorithm.greedy(getBillboard(billboardLists.get(i - 1)), 100000000);
                indexI = -1;
            }
            upperBoundRow = new ArrayList<>((int) budget + 1);
            for (int n = 0; n <= budget; n++) {
                if (i == 0 || n == 0) {
                    upperBoundRow.add(0.0);
                } else {
                    List<Billboard> newBillboards = getBillboard(billboardLists.get(i - 1));
                    upperBound = greedyAlgorithm.greedy(newBillboards, n);
                    upperBoundRow.add(upperBound);
                    if (upperBound >= maxUpperBound) {
                        indexI = n;
                        break;
                    }
                }
            }
            if (indexI > 0) {
                for (int n = indexI; n < budget; n++) {
                    upperBoundRow.add(upperBoundRow.get(n));
                }
            }
            upperBoundMatrix.add(upperBoundRow);
        }
        return upperBoundMatrix;
    }

    private void printUpperBound() {
        for (int i = 0; i <= billboardLists.size(); i++) {
            System.out.print("Row " + i + " : ");
            for (int n = 0; n <= budget; n++) {
                if (i > 0 & n > 0)
                    System.out.print("B" + n + "(" + upperBoundMatrix.get(i).get(n) + ")");
            }
            System.out.println();
        }
    }

    private ArrayList<ClusterBillboard> getAnneal(List<Billboard> remainingBillboards, double budget) {
        ArrayList<ClusterBillboard> results = new ArrayList<>();
        Annealing annealing = new Annealing(remainingBillboards, budget);
        return annealing.getResult();
    }

    private ArrayList<BillboardSet> getGreedyList(List<Billboard> remainingBillboards, double budget) {
        ArrayList<BillboardSet> greedyResult = new ArrayList<>((int) budget + 1);
        GreedyAlgorithm greedyAlgorithm = new GreedyAlgorithm();
        List<Billboard> temporalBillboards;
        for (int i = 0; i <= budget; i = i + 1) {
            //sortBillboard(remainingBillboards);
            temporalBillboards = getBillboard(remainingBillboards);
            greedyResult.add(greedyAlgorithm.greedy(temporalBillboards, i));
        }
        temporalBillboards = getBillboard(remainingBillboards);
        BillboardSet allBillboard = greedyAlgorithm.greedy(temporalBillboards, 1000000000);
        System.out.println("Total influences of all billboards : " + allBillboard.routeIDs.size());
        fileWriter.writeToFile("Total influences of all billboards : " + allBillboard.routeIDs.size() + "\r\n");
        return greedyResult;
    }

    private void compareResult(ArrayList<BillboardSet> greedyResult, ArrayList<ClusterBillboard> result) {
        System.out.println("Compare result:");
        fileWriter.writeToFile("Compare result:\r\n");
        double deviation;
        for (int i = 0; i <= budget; i = i + chargeDensity * 10) {
            deviation = result.get(i).getInfluence() - greedyResult.get(i).routeIDs.size();
            System.out.print("B" + i + "(" + deviation + "," + ((int) (10000 * deviation / greedyResult.get(i).routeIDs.size())) / 100.0 + "%)");
            fileWriter.writeToFile("B" + i + "(" + deviation + "," + ((int) (10000 * deviation / greedyResult.get(i).routeIDs.size())) / 100.0 + "%)");
        }
        System.out.println();
        fileWriter.writeToFile("\r\n");
        System.out.println("****************************************************************************");
        fileWriter.writeToFile("****************************************************************************\r\n");
    }

    private void printPartitionResults(ArrayList<ClusterBillboard> result) {
        try {
            System.out.println("Partition Enum Greedy result:");
            fileWriter.writeToFile("Partition Enum Greedy result:\r\n");
            for (int i = 0; i <= budget; i += chargeDensity * 10) {
                System.out.print("B" + (i) + "(" + result.get(i).getInfluence() + ")");
                fileWriter.writeToFile("B" + (i) + "(" + result.get(i).getInfluence() + ")");
            }
            System.out.println();
            fileWriter.writeToFile("\r\n");
        } catch (Exception e) {

        }
    }

    private void printGreedyResults(ArrayList<BillboardSet> greedyResult) {
        try {
            System.out.println("Greedy result:");
            fileWriter.writeToFile("Greedy result:\r\n");
            for (int i = 0; i <= budget; i += chargeDensity * 10) {
                System.out.print("B" + (i) + "(" + greedyResult.get((i)).routeIDs.size() + ")");
                fileWriter.writeToFile("B" + (i) + "(" + greedyResult.get((i)).routeIDs.size() + ")");
            }
            System.out.println();
            fileWriter.writeToFile("\r\n");
        } catch (Exception e) {

        }
    }

    public static List<Billboard> getBillboard(List<Billboard> billboards) {
        List<Billboard> newBillboards = new ArrayList<>(billboards.size());
        for (Billboard billboard : billboards) {
            billboard.resetBillboard();
            //for (Route route : billboard.routes) {
            //    route.influenced = false;
            //}
            //billboard.influence = billboard.routes.size();
            newBillboards.add(billboard);
        }
        return newBillboards;
    }

    public static String print(String message, int length) {
        message = String.format("%1$-" + length + "s", message);
        return message;
    }

    public static String print(int number, int length) {
        String message = String.valueOf(number);
        message = String.format("%1$-" + length + "s", message);
        return message;
    }

    public static String println(String message, int length) {
        message = String.format("%1$-" + length + "s", message);
        return message;
    }

    public static String println(int number, int length) {
        String message = String.valueOf(number);
        message = String.format("%1$-" + length + "s", message);
        return message;
    }

    private void printCandidate(ArrayList<ArrayList<ClusterBillboard>> candidates) {
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
            fileWriter.writeToFile("ROW " + i + " : ");
            for (int n = chargeDensity; n < candidateRow.size(); n += chargeDensity) {
                ClusterBillboard cluster = candidateRow.get(n);
                cluster.updateInfluence();
                System.out.print(" B" + n + "{");
                fileWriter.writeToFile(" B" + n + "{");
                int billboardnumber = cluster.getBillboardList().size();
                for (Billboard billboard : cluster.getBillboardList()) {
                    String message = billboard.panelID + "|";
                    System.out.print(print(message, 11));
                    fileWriter.writeToFile(print(message, 11));
                }
                for (int m = 0; m < (length[n] - billboardnumber); m++) {
                    System.out.print(print("", 11));
                    fileWriter.writeToFile(print("", 11));
                }
                System.out.print("}");
                fileWriter.writeToFile("}");
            }
            System.out.println();
            fileWriter.writeToFile("\r\n");
        }

        for (int i = 0; i < candidates.size(); i++) {
            ArrayList<ClusterBillboard> candidateRow = candidates.get(i);
            System.out.print("ROW " + i + " : ");
            fileWriter.writeToFile("ROW " + i + " : ");
            for (int n = chargeDensity; n < candidateRow.size(); n += chargeDensity) {
                ClusterBillboard cluster = candidateRow.get(n);
                cluster.updateInfluence();
                System.out.print(" B" + n + "{");
                fileWriter.writeToFile(" B" + n + "{");
                int billboardNumber = cluster.getBillboardList().size();
                int tripNumber = 0;
                //for (Billboard billboard : cluster.getBillboard()) {
                //    tripNumber += billboard.routes.size();
                //}
                tripNumber = cluster.getInfluence();
                System.out.print(print(tripNumber + "}", 5));
                fileWriter.writeToFile(print(tripNumber + "}", 5));
            }
            System.out.println();
            fileWriter.writeToFile("\r\n");
        }
    }

    public void printFinalResult(ArrayList<ClusterBillboard> resultRow) {
        ArrayList<Integer> budgetList = new ArrayList<Integer>();
        budgetList.add(50);
        budgetList.add(100);
        budgetList.add(150);
        budgetList.add(200);
        budgetList.add(250);
        budgetList.add(300);
        budgetList.add(350);
        budgetList.add(400);
        ClusterBillboard finalResult;
        try {
            for (Integer budget : budgetList) {
                int tripNumber = 0;
                finalResult = resultRow.get(budget);
                for (Billboard billboard : finalResult.getBillboardList()) {
                    tripNumber += billboard.routes.size();
                }
                System.out.println("Budget    " + budget);
                System.out.println("Influence " + finalResult.getInfluence());
                System.out.println("Overlap   " + (tripNumber - finalResult.getInfluence()));
                System.out.println("Charge    " + finalResult.getCharge());
                System.out.print("Billboard ");
                for (Billboard billboard : finalResult.getBillboardList()) {
                    System.out.print(billboard.panelID + ",");
                }
                System.out.println();
            }
        } catch (Exception e) {

        }
    }
}

