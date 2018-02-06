
package entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by marco on 19/07/2017.
 */
public class Clusters implements Serializable {


    private static final long serialVersionUID = -6867925453002165840L;

    public List<List<Billboard>> clusters;


    public Clusters(List<List<Billboard>> clusters) {

        this.clusters = clusters;
    }

    public int calculateOverlapBetween(int clusterIndex1, int clusterIndex2) {

        if (clusterIndex1 < 0 || clusterIndex1 >= clusters.size())
            return -1;

        if (clusterIndex2 < 0 || clusterIndex2 >= clusters.size())
            return -1;


        BillboardSet billboardSet1 = new BillboardSet();
        BillboardSet billboardSet2 = new BillboardSet();

        List<Billboard> cluster1 = clusters.get(clusterIndex1);
        List<Billboard> cluster2 = clusters.get(clusterIndex2);

        for (int i = 0; i < cluster1.size(); i++) {

            Billboard billboard = cluster1.get(i);
            billboardSet1.add(billboard);
        }

        for (int i = 0; i < cluster2.size(); i++) {

            Billboard billboard = cluster2.get(i);
            billboardSet2.add(billboard);
        }

        int overlap = 0;

        for (int routeID1 : billboardSet1.routeIDs) {

            for (int routeID2 : billboardSet2.routeIDs) {

                if (routeID1 == routeID2)
                    overlap++;
            }
        }

        return overlap;
    }


    public int getInfluence(int clusterIndex) {

        if (clusterIndex < 0 || clusterIndex >= clusters.size())
            return -1;

        List<Billboard> cluster = clusters.get(clusterIndex);
        BillboardSet billboardSet = new BillboardSet();

        for (int i = 0; i < cluster.size(); i++) {

            Billboard billboard = cluster.get(i);
            billboardSet.add(billboard);
        }

        int influence = billboardSet.routeIDs.size();
        return influence;
    }





    public void merge(int clusterIndex1, int clusterIndex2) {


        if (clusterIndex1 < 0 || clusterIndex1 >= clusters.size())
            System.err.println("index out of bound!!!!!");

        if (clusterIndex2 < 0 || clusterIndex2 >= clusters.size())
            System.err.println("index out of bound!!!!!");


        List<Billboard> cluster1 = clusters.get(clusterIndex1);
        List<Billboard> cluster2 = clusters.get(clusterIndex2);

        List<Billboard> newCluster = new ArrayList<>();

        newCluster.addAll(cluster1);
        newCluster.addAll(cluster2);

        clusters.remove(cluster1);
        clusters.remove(cluster2);

        clusters.add(newCluster);
    }

}
