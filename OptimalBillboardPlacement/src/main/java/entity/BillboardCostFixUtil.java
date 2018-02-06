package entity;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by marco on 25/07/2017.
 */
public class BillboardCostFixUtil {

    public static final int numberOfClusterSerializedToRead = 10;
    public static final int numberOfClusterParition = 30;

    private List<Clusters> clustersList;
    private List<Billboard> showUpBillboards;
    private int indexOfNextClustersToFix;


    public static void main(String[] args) {


        BillboardCostFixUtil costFixUtil = new BillboardCostFixUtil();

        costFixUtil.fixCost();

        costFixUtil.serialzeClustersList();
    }


    public BillboardCostFixUtil() {

        indexOfNextClustersToFix = 0;

        showUpBillboards = new ArrayList<>();
        clustersList = new ArrayList<>();

        for (int i = 0; i < numberOfClusterSerializedToRead; i++) {

            Clusters clusters = readOneClusters("./" + ((i+1)*2) + "w-trip-" + numberOfClusterParition + "-Clusters.cluster");
            clustersList.add(clusters);
        }
        initShowUpBillboard();
    }




    public void fixCost() {

        while (indexOfNextClustersToFix < numberOfClusterSerializedToRead) {

            updateShowUpBillboard();
        }

        for (Clusters clusters : clustersList) {

            for (List<Billboard> cluster : clusters.clusters) {

                for (Billboard billboard : cluster)
                    fixBillboardCost(billboard);
            }
        }
    }




    public void serialzeClustersList() {


        for (int i = 0; i < clustersList.size(); i++) {

            Clusters clusters = clustersList.get(i);
            serialzeOneClusters(clusters, "./" + ((i+1)*2) + "w-trip-" + numberOfClusterParition + "-Clusters-fix-cost.cluster");
        }
    }



    private Clusters readOneClusters(String filePath) {

        Clusters clusters = null;

        try {

            FileInputStream fis = new FileInputStream(filePath);
            ObjectInputStream ois = new ObjectInputStream(fis);
            Object obj = ois.readObject();

            clusters = (Clusters) obj;

            ois.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return clusters;
    }



    private void initShowUpBillboard() {

        Clusters clusters = clustersList.get(0);

        for (int i = 0; i < clusters.clusters.size(); i++) {

            List<Billboard> billboards = clusters.clusters.get(i);
            showUpBillboards.addAll(billboards);
        }
        indexOfNextClustersToFix++;
    }




    private void updateShowUpBillboard() {

        Clusters clusters = clustersList.get(indexOfNextClustersToFix);

        for (int i = 0; i < clusters.clusters.size(); i++) {

            List<Billboard> cluster = clusters.clusters.get(i);

            for (Billboard billboard : cluster) {

                if(!showUp(billboard))
                    showUpBillboards.add(billboard);
            }
        }
        indexOfNextClustersToFix++;
    }



    private boolean showUp(Billboard billboard) {

        for (Billboard showUpBillboard : showUpBillboards) {

            if (showUpBillboard.panelID.equals(billboard.panelID))
                return true;
        }
        return false;
    }



    private Billboard fixBillboardCost(Billboard billboard) {


        // TODO Reminder charge is fixed to influence

        for (Billboard showUpBillboard : showUpBillboards)
            showUpBillboard.charge = showUpBillboard.influence;

        for (Billboard showUpBillboard : showUpBillboards) {

            if (showUpBillboard.panelID.equals(billboard.panelID)) {

                billboard.charge = showUpBillboard.charge;
                return billboard;
            }
        }
        System.out.println("can not fix billboard cost : billboard panelID " + billboard.panelID);
        return null;
    }


    private void serialzeOneClusters(Clusters clusters, String outputFilePath) {

        try {

            FileOutputStream fos = new FileOutputStream(outputFilePath);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(clusters);

            fos.close();

        } catch (Exception e) {

            e.printStackTrace();
        }

    }
}
