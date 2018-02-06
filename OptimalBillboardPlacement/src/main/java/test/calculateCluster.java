package test;

import fileIO.finalResult.FinalResultReader;
import entity.Billboard;
import Knapsack.ClusterBillboard;

import java.util.List;
import java.util.ArrayList;

import configure.Developer;
import entity.Clusters;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.Collections;

/**
 * Created by Lancer on 2017/7/15.
 */
public class calculateCluster {

    private static int chargeDensity = 5;
    private static int trajectoryNumber = 2;
    private static int clusterNumber = 10;
    private static double pow = 0.5;

    public static void main(String[] args) {
        //printClusterInfluence();
        //printBillboardInforSorted();
    }

    private List<List<Billboard>> getPartition() {

        try {
            String root = "G:\\JAVA\\Optimal-Billboard-Placement\\cluster\\";
            FileInputStream fis;
            if (Developer.SYSTEM.equals("Zhenhan")) {
                fis = new FileInputStream(trajectoryNumber + "w-trip-" + clusterNumber + "-Clusters.cluster");
            } else if (Developer.SYSTEM.equals("Yipeng")) {
                fis = new FileInputStream(root + trajectoryNumber + "w-trip-" + clusterNumber + "-Clusters.cluster");
            } else {
                fis = new FileInputStream(root + trajectoryNumber + "w-trip-" + clusterNumber + "-Clusters.cluster");
            }

            ObjectInputStream ois = new ObjectInputStream(fis);
            Object obj = ois.readObject();

            Clusters clusters = (Clusters) obj;

            List<List<Billboard>> clu = clusters.clusters;
            ois.close();

            List<List<Billboard>> clu2 = new ArrayList<>(clu.size());
            for (List<Billboard> billboards : clu) {
                List<Billboard> newRow = new ArrayList<>();
                int index = 0;
                for (Billboard billboard : billboards) {
                    if (billboard.routes.size() > 0)
                        billboard.charge = Math.pow(billboard.influence, pow);

                    billboard.charge = (int) (Math.round(billboard.charge / chargeDensity)) * chargeDensity;
                    if (billboard.charge == 0 && billboard.routes.size() > 0)
                        billboard.charge = chargeDensity;

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

    private static void printClusterInfluence() {
        ArrayList<String> target = new ArrayList<String>();
        ClusterBillboard culster = new ClusterBillboard();
        FinalResultReader finalResultReader = new FinalResultReader();
        List<Billboard> allBillboards = finalResultReader.getBillboards();
        double influence = 0;
        for (Billboard billboard : allBillboards) {
            culster.add(billboard);
            influence += billboard.routes.size();
        }
        culster.updateInfluence();
        System.out.println(influence);
        System.out.println(culster.getInfluence());
    }

    private static void printBillboardInforSorted() {
        FinalResultReader finalResultReader = new FinalResultReader();
        List<Billboard> allBillboards = finalResultReader.getBillboards();
        Collections.sort(allBillboards);
        for (Billboard billboard : allBillboards) {
            System.out.println(billboard.panelID + ": inf " + billboard.influence + ", cost " + billboard.charge);
        }
    }
}
