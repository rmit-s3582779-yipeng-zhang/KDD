package Annealing;

import Knapsack.ClusterBillboard;
import PartitionEnumGreedy.BillboardListDuplicator;
import algorithms.GreedyAlgorithm;
import entity.Billboard;
import fileIO.MyFileWriter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Lancer on 2017/8/30.
 */
public class AnnealingSub extends Thread {

    private ClusterBillboard cluster;
    private double budget;
    private List<Billboard> billboards;
    private double temperature = 100000000.0D;
    private double coolingRate = 0.99999;
    private double absoluteTemperature = 0.00001;
    private double budgetRemain = 0;
    private MyFileWriter fileWriter;

    public AnnealingSub(List<Billboard> newBillboards, double budget, ClusterBillboard cluster, MyFileWriter fileWriter) {
        this.billboards = newBillboards;
        this.budget = budget;
        this.cluster = cluster;
        this.fileWriter = fileWriter;
    }

    @Override
    public void run() {
        System.out.println("Start");
        copyBillboard();
        getInitialResult();
        anneal();
        System.out.println("End");
    }

    private void copyBillboard() {
        BillboardListDuplicator duplicator = new BillboardListDuplicator(billboards);
        this.billboards = duplicator.getBillboards();
        for (int i = 0; i < billboards.size(); i++) {
            if (billboards.get(i).influence == 0 || billboards.get(i).charge > budget) {
                billboards.remove(i);
                i--;
            }
        }
        System.out.println(this.getName() + " Billboard number : " + this.billboards.size());
    }

    private void getInitialResult() {
        GreedyAlgorithm greedyAlgorithm = new GreedyAlgorithm();
        greedyAlgorithm.greedy(billboards, budget, cluster);
    }

    private void anneal() {

        Date date1 = new Date();
        int i = 0;
        while (temperature > absoluteTemperature) {
            i++;
            if (i == 100000) {
                System.out.println(this.getName() + " : " + cluster.getInfluence());
                i = 0;
            }
            ClusterBillboard clusterNew = generateNewResult();
            int deltaInfluence = clusterNew.getInfluence() - cluster.getInfluence();
            if ((deltaInfluence > 0) || Math.exp(deltaInfluence / temperature) > Math.random()) {
                cluster = clusterNew;
            }

            temperature *= coolingRate;
        }

        Date date2 = new Date();
        long begin = date1.getTime();
        long end = date2.getTime();
        System.out.println("Total runtime: " + ((end - begin) / 1000d));
        fileWriter.writeToFile("Budget : " + this.budget + "   ");
        fileWriter.writeToFile("Total runtime: " + ((end - begin) / 1000d) + "   ");
        System.out.println("Influence : " + cluster.getInfluence());
        fileWriter.writeToFile("Influence : " + cluster.getInfluence() + "\r\n");
    }

    private ClusterBillboard generateNewResult() {
        ClusterBillboard clusterNew = new ClusterBillboard();
        for (Billboard billboard : cluster.getBillboardList()) {
            clusterNew.add(billboard);
        }
        clusterNew.updateInfluence();

        int changeBillboardNumber = (int) (cluster.getBillboardList().size() * Math.random());

        double totalChangedBudget = budgetRemain;
        budgetRemain = 0;
        for (int i = 0; i < changeBillboardNumber; i++) {
            int index = (int) (clusterNew.getBillboardList().size() * Math.random());
            Billboard billboard = clusterNew.getBillboardList().get(index);
            totalChangedBudget += billboard.charge;
            billboard.resetBillboard();
            clusterNew.getBillboardList().remove(index);
        }

        List<Billboard> billboards = getBillboards();
        while (billboards.size() > 0) {
            for (int i = 0; i < billboards.size(); i++) {
                if (billboards.get(i).charge > totalChangedBudget) {
                    billboards.remove(i);
                    i--;
                }
            }
            if (billboards.size() <= 0)
                break;
            int index = (int) (billboards.size() * Math.random());
            for (Billboard billboard : clusterNew.getBillboardList()) {
                if (billboard.panelID.equals(billboards.get(index).panelID))
                    continue;
            }
            clusterNew.add(billboards.get(index));
            totalChangedBudget -= billboards.get(index).charge;
        }
        clusterNew.updateInfluence();
        return clusterNew;
    }

    public List<Billboard> getBillboards() {
        try {
            List<Billboard> billboards = new ArrayList<Billboard>();
            for (Billboard billboard : this.billboards) {
                billboard.resetBillboard();
                billboards.add((Billboard) billboard.clone());
            }
            return billboards;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }
}
