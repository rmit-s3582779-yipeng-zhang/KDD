package PartitionEnumGreedy;


import entity.Billboard;
import entity.Clusters;
import configure.Developer;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by marco on 30/04/2017.
 */
public class ClusterCalcuate {

    private static int chargeDensity = 5;
    private static int trajectoryNumber = 2;
    private static int clusterNumber = 10;
    private static double pow = 0.5;


    public static void main(String[] args) {


        ClusterCalcuate test = new ClusterCalcuate();
        test.testNewPartitionOverlap();
        //List<List<Billboard>> billboards = test.getPartition();
        //test.getDistribution(billboards);

    }

    private void getDistribution(List<List<Billboard>> billboardList){
        Integer[] influence = new Integer[100];
        Arrays.fill(influence, 0);

        for(List<Billboard> billboards: billboardList){
            for(Billboard billboard: billboards){
                influence[(int)(billboard.influence/100)]++;
            }
        }

        for(int i=0;i<100;i++){
            if(influence[i]>0)
            System.out.println("Influence <" + (i+1)*100 + " : " + influence[i]);
        }
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


    public void testNewPartitionOverlap() {

        try {
        	FileInputStream fis;
            String root = "G:\\JAVA\\Optimal-Billboard-Placement\\cluster\\";
            if (Developer.SYSTEM.equals("Zhenhan"))
                fis = new FileInputStream("./cluster/2w-trip-10-Clusters.cluster");
            else
            	fis = new FileInputStream(root + "2w-trip-10-Clusters.cluster");
            ObjectInputStream ois = new ObjectInputStream(fis);
            Object obj = ois.readObject();

            Clusters clusters = (Clusters) obj;


            //clusters.merge(2, 4);


            List<List<Billboard>> clusterList = clusters.clusters;


            for (int i = 0; i < clusterList.size(); i++) {

                int influence = clusters.getInfluence(i);
                System.out.println("cluster" + i + " influence: " + influence);
            }

            System.out.println();

            List<ClusterOverlap> clusterOverlaps = new ArrayList<>();

            for (int i = 0; i < clusterList.size(); i++) {

                for (int j = i + 1; j < clusterList.size(); j++) {


                    int overlap = clusters.calculateOverlapBetween(i, j);
                    ClusterOverlap clusterOverlap = new ClusterOverlap(i, j, overlap);
                    clusterOverlaps.add(clusterOverlap);

                }
            }

            Collections.sort(clusterOverlaps);

            for (ClusterOverlap clusterOverlap : clusterOverlaps)
                System.out.println(clusterOverlap);


            ois.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    class ClusterOverlap implements Comparable<ClusterOverlap> {

        int index1;
        int index2;
        int overlap;

        public ClusterOverlap(int index1, int index2, int overlap) {
            this.index1 = index1;
            this.index2 = index2;
            this.overlap = overlap;
        }


        @Override
        public int compareTo(ClusterOverlap o) {
            return o.overlap - overlap;
        }

        @Override
        public String toString() {

            StringBuffer stringBuffer = new StringBuffer();

            stringBuffer.append("overlap between cluster" + index1 + " and cluster" + index2 + " is " + overlap);
            return stringBuffer.toString();
        }
    }

}
