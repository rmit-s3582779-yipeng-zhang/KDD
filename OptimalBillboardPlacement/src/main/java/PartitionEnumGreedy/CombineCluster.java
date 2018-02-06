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

public class CombineCluster {
	
	private double theta;
	private Clusters clusters;
	
	public CombineCluster(Clusters clusters, double theta){
		this.clusters = clusters;
		this.theta = theta;
		newPartitionOverlap();
	}
	
	public Clusters getClusters(){
		return clusters;
	}

    private void newPartitionOverlap() {

        try {
            List<List<Billboard>> clusterList = clusters.clusters;

            for (int i = 0; i < clusterList.size(); i++) {

                for (int j = i + 1; j < clusterList.size(); j++) {

                    double overlap = clusters.calculateOverlapBetween(i, j);
                    double influence = clusters.getInfluence(i) + clusters.getInfluence(j);
                    if((overlap / influence)> theta){
                    	clusters.merge(i, j);
                    	i = 0;
                    	break;
                    }

                }
            }
            System.out.println("Overlap :");
            for (int i = 0; i < clusterList.size(); i++) {
            	for (int j = i + 1; j < clusterList.size(); j++) {
            		double overlap = clusters.calculateOverlapBetween(i, j);
            		System.out.println("("+i+","+j+")"+overlap);
            	}
            }

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
