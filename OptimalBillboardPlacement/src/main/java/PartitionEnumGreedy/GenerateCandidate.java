package PartitionEnumGreedy;

import Knapsack.ClusterBillboard;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import algorithms.GreedyAlgorithm;
import entity.*;
import fileIO.Serialize;
import partition.PartitionAlgorithm;
import Knapsack.*;
import fileIO.finalResult.MultipleResultReader;
import PartitionEnumGreedy.*;

/**
 * Created by Lancer on 2017/7/7.
 */
public class GenerateCandidate {

    private double budget;
    private List<Billboard> billboardsRaw=new ArrayList<Billboard>();
    private List<Billboard> bestBillboard=new ArrayList<Billboard>();
    private List<List<Billboard>> billboardLists;
    public static int chargeDensity = 5;

    public static void main(String[] args) {
        double BUDGET = 100;
        int clusterNumber = 11;
        int trajectoryNumber = 2;
        String clusterName = "Cluster" + clusterNumber + "_" + (int)BUDGET + "_" +chargeDensity+"_"+trajectoryNumber+".cluster";
        String upperBoundName = "Cluster" + clusterNumber + "_" + (int)BUDGET + "_" +chargeDensity+"_"+trajectoryNumber+".UB";
        ArrayList<ArrayList<ClusterBillboard>> candidates;
        ArrayList<ArrayList<Double>> upperBoundMatrix;
        Serialize serialize = new Serialize();
        GenerateCandidate generateCandidate = new GenerateCandidate();
        generateCandidate.budget=BUDGET;
        candidates = serialize.deserializeMatrix(clusterName);//read candidates from file
        if(candidates==null) {
            //PartitionAlgorithm partitionAlgorithm = new PartitionAlgorithm(clusterNumber,trajectoryNumber);
            PartitionAlgorithm partitionAlgorithm = new PartitionAlgorithm(clusterNumber,1,trajectoryNumber);
            generateCandidate.billboardLists=partitionAlgorithm.getClusters();
            candidates = generateCandidate.getCandidate();
            serialize.serializeMatrix(candidates,clusterName);
            upperBoundMatrix = generateCandidate.generateUpperBound();
            serialize.serializeUpperBoundMatrix(upperBoundMatrix,upperBoundName);
        }
        else{
            upperBoundMatrix = serialize.deserializeUpperBoundMatrix(upperBoundName);
        }

        printCandidate(candidates);
        //Knapsack5 knapsack = new Knapsack5(candidates,upperBoundMatrix, chargeDensity);
        Knapsack4 knapsack = new Knapsack4(candidates, chargeDensity);
        ArrayList<ClusterBillboard> result = knapsack.knapsack(BUDGET);
        MultipleResultReader multipleResultReader = new MultipleResultReader(1,trajectoryNumber);
        ArrayList<BillboardSet> greedyResult = generateCandidate.getGreedyList(multipleResultReader.getBillboards(),BUDGET);
        generateCandidate.printGreedyResults(greedyResult);
        generateCandidate.compareResult(greedyResult,result);
        //System.out.println(result.getInfluence());
        //System.out.println(result.getCharge());
        //for(Billboard billboard: result.getBillboard()){
        //    System.out.print(billboard.panelID+",");
        //}
    }

    private static void printCandidate(ArrayList<ArrayList<ClusterBillboard>> candidates){
        Integer[] length = new Integer[candidates.get(0).size()];
        for(int i=0;i<candidates.get(0).size();i++){
            length[i]=0;
        }
        for(int i=0;i<candidates.size();i++){
            ArrayList<ClusterBillboard> candidateRow=candidates.get(i);
            for(int n=0;n<candidateRow.size();n++){
                ClusterBillboard cluster = candidateRow.get(n);
                if(length[n]<cluster.getBillboardList().size()){
                    length[n]=cluster.getBillboardList().size();
                }
            }
        }

        for(int i=0;i<candidates.size();i++){
            ArrayList<ClusterBillboard> candidateRow=candidates.get(i);
            System.out.print("ROW "+i +" : ");
            for(int n=chargeDensity;n<candidateRow.size();n+=chargeDensity){
                ClusterBillboard cluster = candidateRow.get(n);
                cluster.updateInfluence();
                System.out.print(" B"+n+"{");
                int billboardnumber = cluster.getBillboardList().size();
                for(Billboard billboard:cluster.getBillboardList()){
                    String message = billboard.panelID+"|";
                    print(message,8);
                }
                for(int m=0;m<(length[n]-billboardnumber);m++){
                    print("",8);
                }
                System.out.print("}");
            }
            System.out.println();
        }

        for(int i=0;i<candidates.size();i++){
            ArrayList<ClusterBillboard> candidateRow=candidates.get(i);
            System.out.print("ROW "+i +" : ");
            for(int n=chargeDensity;n<candidateRow.size();n+=chargeDensity){
                ClusterBillboard cluster = candidateRow.get(n);
                cluster.updateInfluence();
                System.out.print(" B"+n+"{");
                int billboardNumber = cluster.getBillboardList().size();
                int tripNumber=0;
                for(Billboard billboard:cluster.getBillboardList()){
                    tripNumber+=billboard.routes.size();
                }
                print(tripNumber+"}",5);
            }
            System.out.println();
        }
    }

    private  ArrayList<ArrayList<ClusterBillboard>> getCandidate() {
        ArrayList<ArrayList<ClusterBillboard>> finalCandidate=new  ArrayList<ArrayList<ClusterBillboard>>();
        int i=0;
        for(List<Billboard> billboardList : billboardLists){
            if(i==7)
                System.out.print("");
            Date date1 = new Date();
            billboardsRaw=billboardList;
            ArrayList<ClusterBillboard> candidate = combineCandidate();
            finalCandidate.add(candidate);
            System.out.println("Finish "+i++);
            Date date2 = new Date();
            long begin = date1.getTime();
            long end = date2.getTime();
            System.out.println("runtime: " + ((end - begin)/1000d));
        }
        return finalCandidate;
    }

    private  ArrayList<ClusterBillboard> combineCandidate() {
        Date date1 = new Date();
        ArrayList<ClusterBillboard> candidates = generateCandidateK1();
        candidates = generateCandidateK2(candidates);
        candidates = generateCandidateK3(candidates);
        Date date2 = new Date();
        long begin = date1.getTime();
        long end = date2.getTime();
        System.out.println("runtime: " + ((end - begin)/1000d));
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
            for (int j = i+1; j < billboardsRaw.size(); j++) {
                for (int k = j+1; k < billboardsRaw.size(); k++) {
                    GreedyParameter parameter = new GreedyParameter();
                    parameter.firstBillboardIndex = i;
                    parameter.secondBillboardIndex = j;
                    parameter.thirdBillboardIndex = k;
                    parameter.budgetRemains = budget - billboardsRaw.get(i).charge - billboardsRaw.get(j).charge
                            - billboardsRaw.get(k).charge;
                    parameterList.add(parameter);
                }
            }
        }
        return parameterList;
    }

    //generate candidate list while k = 1
    private ArrayList<ClusterBillboard> generateCandidateK1 (){
        int indexMax;
        double influence;
        ArrayList<ClusterBillboard> candidates = new ArrayList<ClusterBillboard>((int)budget+1);
        List<Billboard> billboards;
        billboards=getBillboards();
        candidates.add(new ClusterBillboard());
        Billboard billboard=new Billboard();
        indexMax=billboards.size();
        for(int i=1;i<=budget;i+=1){
            influence=0;
            for(int n=0;n<indexMax;n++){
                if(billboards.get(n).charge<=i){
                    if(billboards.get(n).influence>influence){
                        billboard=billboards.get(n);
                        influence=billboard.influence;
                    }
                }
            }
            if(billboard.charge==0)
                candidates.add(new ClusterBillboard());//cannot find any one meeting the condition
            else
                candidates.add(new ClusterBillboard(billboard));
        }
        return candidates;
    }

    //generate candidate list while k = 2
    private ArrayList<ClusterBillboard> generateCandidateK2 (ArrayList<ClusterBillboard> results){
        int indexMax;
        double influence;
        List<Billboard> billboards;
        billboards=getBillboards();
        int billboardSize=billboards.size();
        ArrayList<ClusterBillboard> candidates=new ArrayList<ClusterBillboard>(billboardSize*billboardSize);
        ClusterBillboard clusterBillboard;

        for(int i=0;i<billboardSize; i++){
            for (int n=i+1; n<billboardSize;n++){
                clusterBillboard=new ClusterBillboard(billboards.get(i));
                clusterBillboard.add(billboards.get(n));
                candidates.add(clusterBillboard);
            }
        }
        indexMax=candidates.size();
        clusterBillboard=new ClusterBillboard();
        for(int i=chargeDensity;i<=budget;i+=chargeDensity){
            influence=0;
            for(int n=0;n<indexMax;n++){
                if(candidates.get(n).getCharge()<=i){
                    candidates.get(n).updateInfluence();
                    if(candidates.get(n).getInfluence()> influence){
                        clusterBillboard=candidates.get(n);
                        influence=clusterBillboard.getInfluence();
                    }
                }
            }
            if(clusterBillboard.getCharge()>0 && results.get(i).getInfluence()<clusterBillboard.getInfluence())
                results.get(i).set(clusterBillboard);
        }
        return results;
    }

    //generate candidate list while k > 3
    private ArrayList<ClusterBillboard> generateCandidateK3 (ArrayList<ClusterBillboard> results){
        int indexMax;
        List<Billboard> billboards;
        ClusterBillboard greedyResult = new ClusterBillboard();
        GreedyAlgorithm greedyAlgorithm = new GreedyAlgorithm();
        List<GreedyParameter> greedyParameters=generateGreedyParameters();//get the enum parameters
        indexMax=greedyParameters.size();

        System.out.println("indexMax : "+indexMax);
        for(int n=0; n<indexMax;n++){
            if(n%1000==0)
                System.out.print(n/1000+" , ");

            billboards=getBillboards();
            GreedyParameter greedyParameter = greedyParameters.get(n);
            ClusterBillboard candidate=new ClusterBillboard();
            candidate.add(billboards.get(greedyParameter.firstBillboardIndex));//get the enum form all billboards based on the enum parameters
            candidate.add(billboards.get(greedyParameter.secondBillboardIndex));
            candidate.add(billboards.get(greedyParameter.thirdBillboardIndex));

            int cost=(int)Math.ceil(candidate.getCharge());
            if(budget-cost>=0){//check, if it's already over budget
                candidate.updateInfluence();//update the candidates influence
                //cost = candidate.cost (k=3)
                if(candidate.getInfluence()>results.get(cost).getInfluence()){//update if these three enum are  better
                    results.get(cost).set(candidate);
                }
                //cost = candidate.cost+1 ~  budget (k>3)
                for(int i = cost + chargeDensity;i<=budget-cost;i+=chargeDensity){
                    double remainBudget = i;
                    billboards=getBillboards();//fetch billboards from this cluster
                    candidate=new ClusterBillboard();
                    candidate.add(billboards.get(greedyParameter.firstBillboardIndex));
                    candidate.add(billboards.get(greedyParameter.secondBillboardIndex));
                    candidate.add(billboards.get(greedyParameter.thirdBillboardIndex));
                    candidate.updateInfluence();//update candidate routes information
                    //delete 3 enum from candidate billboards
                    billboards.remove(greedyParameter.thirdBillboardIndex);
                    billboards.remove(greedyParameter.secondBillboardIndex);
                    billboards.remove(greedyParameter.firstBillboardIndex);

                    //remove all billboards which is over budget
                    for(int m=0;m<billboards.size();m++){
                        if(billboards.get(m).charge>i) {
                            billboards.remove(m);
                            m--;
                        }
                    }
                    if(billboards.size()==0)
                        continue;

                    /*
                    if(greedyResult.getBillboard().size()>0) {
                        ClusterBillboard lastGreedyResult = greedyResult;
                        greedyResult=new ClusterBillboard();
                        for (Billboard billboard : lastGreedyResult.getBillboard()) {
                            if (billboard.bestChoice == true) {
                                for(int m=0;m<billboards.size();m++){
                                    if(billboards.get(m).panelID.equals(billboard.panelID)){
                                        greedyResult.add(billboard);
                                        billboards.remove(m);
                                        break;
                                    }
                                }
                                remainBudget -= billboard.charge;
                            }
                        }
                        greedyAlgorithm.updateBillboards(greedyResult.getBillboard());
                    }
                    */
                    // do greedy is there is any billboards doesn't over budget
                    if(billboards.size()>0){
                        greedyResult = new ClusterBillboard();
                        //BillboardSet billboardSet = greedyAlgorithm.greedy(allBillboards, i,greddyResult);//get results form greedy
                        //greedyAlgorithm.updateBillboardsLazyForward(billboards);
                        greedyAlgorithm.greedy(billboards, remainBudget,greedyResult);//get results form greedy
                    }

                    if(greedyResult.getBillboardList().size()>0){// check if there is any billboard which can be chosen based on this budget
                        ClusterBillboard temporaryCluster = new ClusterBillboard();
                        temporaryCluster.add(candidate);
                        temporaryCluster.add(greedyResult);
                        temporaryCluster.updateInfluence();
                        if(temporaryCluster.getInfluence()>results.get(i+cost).getInfluence()){//update if it's better
                            results.get(i+cost).set(temporaryCluster);
                        }
                    }
                }
            }
        }
        return results;
    }

    public List<Billboard> getBillboards(){
        try{
            List<Billboard> billboards=new ArrayList<Billboard>();
            for(Billboard billboard:billboardsRaw){
                billboard.resetBillboard();
                billboards.add((Billboard) billboard.clone());
            }
            return billboards;
        }
        catch(CloneNotSupportedException e){
            e.printStackTrace();
            return null;
        }
    }

    private ArrayList<ArrayList<Double>> generateUpperBound(){
        ArrayList<ArrayList<Double>> upperBoundMatrix;
        GreedyAlgorithmForced greedyAlgorithmForced = new GreedyAlgorithmForced();
        upperBoundMatrix=new ArrayList<ArrayList<Double>>(billboardLists.size()+1);
        ArrayList<Double> upperBoundRow ;
        for(int i=0;i<=billboardLists.size();i++){
            upperBoundRow = new ArrayList<Double>((int)budget+1);
            for(int n=0;n<=budget;n++){
                if(i==0 || n==0){
                    upperBoundRow.add(0.0);
                }
                else{
                    List<Billboard> newBillboards = getBillboard(billboardLists.get(i-1));
                    upperBoundRow.add(greedyAlgorithmForced.greedy(newBillboards,n));
                }
            }
            upperBoundMatrix.add(upperBoundRow);
        }
        return upperBoundMatrix;
    }

    private ArrayList<BillboardSet> getGreedyList(List<Billboard> remainingBillboards, double budget){
        ArrayList<BillboardSet> greedyResult = new ArrayList<BillboardSet>((int)budget+1);
        GreedyAlgorithm greedyAlgorithm = new GreedyAlgorithm();
        List<Billboard> temporalBillboards;
        for(int i = 0; i<= budget; i++){
            temporalBillboards = getBillboard(remainingBillboards);
            greedyResult.add(greedyAlgorithm.greedy(temporalBillboards,i));
        }
        return greedyResult;
    }

    private void compareResult(ArrayList<BillboardSet> greedyResult, ArrayList<ClusterBillboard> result){
        System.out.println("Compare result:");
        double deviation;
        for(int i = 0; i<= budget; i=i+chargeDensity){
            deviation=greedyResult.get(i).routeIDs.size() - result.get(i).getInfluence();
            //if(deviation<0)
                System.out.print("B" + i + "(" + deviation + ")");
        }

    }

    private void printGreedyResults(ArrayList<BillboardSet> greedyResult){
        try{
            System.out.println("Greedy result:");
            for(int i = 0; i<= budget; i+=(20*chargeDensity)) {
                for (int j = i ; j < i + (20*chargeDensity); j=j+chargeDensity){
                    System.out.print("B" + (j) + "("+greedyResult.get((j)).routeIDs.size()+")");
                }
                System.out.println();
            }
            System.out.println();
        } catch (Exception e){

        }
    }

    private List<Billboard> getBillboard(List<Billboard> billboards){
        List<Billboard> newBillboards = new ArrayList<Billboard>(billboards.size());
        for(Billboard billboard: billboards){
            for(Route route: billboard.routes){
                route.influenced=false;
            }
            billboard.influence=billboard.routes.size();
            newBillboards.add(billboard);
        }
        return newBillboards;
    }

    public static void print(String message, int length)
    {
        message=String.format("%1$-"+length+"s",message);
        System.out.print(message);
    }

    public static void println(String message, int length)
    {
        message=String.format("%1$-"+length+"s",message);
        System.out.println(message);
    }
}
