
import CheckIn.CheckinPoint;
import CheckIn.Unit;
import com.github.davidmoten.rtree.Entry;
import com.github.davidmoten.rtree.RTree;
import com.github.davidmoten.rtree.geometry.Geometries;
import com.github.davidmoten.rtree.geometry.Point;
import com.github.davidmoten.rtree.geometry.Rectangle;
//import com.sun.tools.internal.xjc.reader.dtd.bindinfo.BIAttribute;
import entity.Billboard;
import entity.Clusters;
import entity.Route;
import fileIO.MyFileReader;
import fileIO.MyFileWriter;
import fileIO.RTree.Deserialize;
import fileIO.finalResult.BillboardListDuplicator;
import fileIO.finalResult.FinalResultReader;
import fileIO.finalResult.MultipleResultReader;
import newPartition.OutputFileParser;
import partition.ClustersGetter;
import partition.PartitionAlgorithm;
import regionSelector.RandomRegionBillboardsGetter;
import regionSelector.RegionSelector;
import rx.Observable;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Created by marco on 30/04/2017.
 */
public class Test {


    public static void main(String[] args) {


        Test test = new Test();
        //test.printBillboardInfluence();
        //test.testSubList();
        test.testFixCost();
    }




    public void testFixCost() {

        List<Clusters> clustersList = new ArrayList<>();

        for (int i = 0; i < 10; i++) {

            Clusters clusters = readOneClusters("./" + ((i+1)*2) + "w-trip-" + 30 + "-Clusters-fix-cost.cluster");
            clustersList.add(clusters);
        }


        // just for print info

        for (int i = 0; i < clustersList.size(); i++) {

            Clusters clusters = clustersList.get(i);
            List<BillboardFixCost> fixCostBillboardList = new ArrayList<>();

            for (List<Billboard> cluster : clusters.clusters) {

                for (Billboard billboard : cluster) {
                    fixCostBillboardList.add(new BillboardFixCost(billboard.panelID, billboard.charge));
                }
            }

            Collections.sort(fixCostBillboardList);

            for (BillboardFixCost billboardFixCost : fixCostBillboardList)
                System.out.print("{" + billboardFixCost.panelID + " , " + billboardFixCost.charge + "}");
            System.out.println();
        }


        // check every billboard to see if cost is fixed

        for (int i = 0; i < 10; i++) {

            Clusters clusters1 = clustersList.get(i);

            for (int j = i+1; j < 10; j++) {

                Clusters clusters2 = clustersList.get(j);

                System.out.println("check if cost is fixed between  cluster" + i + " and cluster" + j);
                compareIfCostFixed(clusters1, clusters2);
                System.out.println( "                                                                   finished all right");
            }

        }
    }


    private void compareIfCostFixed(Clusters clusters1, Clusters clusters2) {

        List<Billboard> billboards1 = new ArrayList<>();
        List<Billboard> billboards2 = new ArrayList<>();

        for (List<Billboard> cluster : clusters1.clusters)
            billboards1.addAll(cluster);

        for (List<Billboard> cluster : clusters2.clusters)
            billboards2.addAll(cluster);

        List<Integer> matchList = new ArrayList<>();

        for (Billboard billboard1 : billboards1) {

            int match = 0;

            for (Billboard billboard2 : billboards2) {

                if (billboard1.panelID.equals(billboard2.panelID) && billboard1.charge == billboard2.charge)
                    match++;

            }
            matchList.add(match);
        }

        for (int match : matchList) {
            if (match != 1)
                System.out.println("cost not fixed");
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


    public class BillboardFixCost implements Comparable<BillboardFixCost> {

        public String panelID;
        public double charge;

        public BillboardFixCost(String panelID, double charge) {
            this.panelID = panelID;
            this.charge = charge;
        }

        @Override
        public int compareTo(BillboardFixCost o) {
            return panelID.compareTo(o.panelID);
        }
    }


    public void testSubList() {


        List<Integer> integers = new ArrayList<>();
        for (int i = 0; i < 100; i++)
            integers.add(i);

        List<Integer> sublist1 = integers.subList(0, 50);
        List<Integer> sublist2 = integers.subList(50, 100);

        for (int i : integers)
            System.out.print(i + " ");
        System.out.println();
        System.out.println();

        for (int i : sublist1)
            System.out.print(i + " ");
        System.out.println();
        for (int i : sublist2)
            System.out.print(i + " ");
        System.out.println();
        System.out.println();

        System.out.println("after change : ");

        integers.set(0, 100);

        for (int i : integers)
            System.out.print(i + " ");
        System.out.println();
        System.out.println();

        for (int i : sublist1)
            System.out.print(i + " ");
        System.out.println();
        for (int i : sublist2)
            System.out.print(i + " ");
        System.out.println();
        System.out.println();


    }


    public void printBillboardInfluence() {

        List<Billboard> less20 = new ArrayList<>();
        List<Billboard> less50 = new ArrayList<>();
        List<Billboard> less100 = new ArrayList<>();
        List<Billboard> less200 = new ArrayList<>(); // 100 - 200
        List<Billboard> less300 = new ArrayList<>();
        List<Billboard> less400 = new ArrayList<>();
        List<Billboard> less500 = new ArrayList<>();
        List<Billboard> less600 = new ArrayList<>();
        List<Billboard> less700 = new ArrayList<>();
        List<Billboard> less800 = new ArrayList<>();
        List<Billboard> less900 = new ArrayList<>();
        List<Billboard> less1000 = new ArrayList<>();
        List<Billboard> greater1000 = new ArrayList<>();


        try {

            FileInputStream fis = new FileInputStream("./2w-trip-10-Clusters.cluster");
            ObjectInputStream ois = new ObjectInputStream(fis);
            Object obj = ois.readObject();

            Clusters clusters = (Clusters) obj;
            List<List<Billboard>> clusterList = clusters.clusters;


            for (int i = 0; i < clusterList.size(); i++) {

                List<Billboard> cluster = clusterList.get(i);

                for (int j = 0; j < cluster.size(); j++) {

                    Billboard billboard = cluster.get(j);
                    int influence = billboard.influence;

                    if (influence < 20)
                        less20.add(billboard);
                    else if (influence < 50)
                        less50.add(billboard);
                    else if (influence < 100)
                        less100.add(billboard);
                    else if (influence < 200)
                        less200.add(billboard);
                    else if (influence < 300)
                        less300.add(billboard);
                    else if (influence < 400)
                        less400.add(billboard);
                    else if (influence < 500)
                        less500.add(billboard);
                    else if (influence < 600)
                        less600.add(billboard);
                    else if (influence < 700)
                        less700.add(billboard);
                    else if (influence < 800)
                        less800.add(billboard);
                    else if (influence < 900)
                        less900.add(billboard);
                    else if (influence < 1000)
                        less1000.add(billboard);
                    else
                        greater1000.add(billboard);
                }
            }

            System.out.println("influence less than 20 : " + less20.size());
            System.out.println("influence less than 50 : " + less50.size());
            System.out.println("influence less than 100 : " + less100.size());
            System.out.println("influence less than 200 : " + less200.size());
            System.out.println("influence less than 300 : " + less300.size());
            System.out.println("influence less than 400 : " + less400.size());
            System.out.println("influence less than 500 : " + less500.size());
            System.out.println("influence less than 600 : " + less600.size());
            System.out.println("influence less than 700 : " + less700.size());
            System.out.println("influence less than 800 : " + less800.size());
            System.out.println("influence less than 900 : " + less900.size());
            System.out.println("influence less than 1000 : " + less1000.size());
            System.out.println("influence greater than 1000 : " + greater1000.size());
            ois.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public void testNewPartitionOverlap() {

        try {

            FileInputStream fis = new FileInputStream("./2w-trip-10-Clusters.cluster");
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

                for (int j = i+1; j < clusterList.size(); j++) {


                    int overlap = clusters.calculateOverlapBetween(i, j);
                    ClusterOverlap clusterOverlap = new ClusterOverlap(i,j,overlap);
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





    public void testBillboardListDuplicator() {

        List<Billboard> billboards = new ArrayList<>();
        Billboard billboard1 = new Billboard();
        billboard1.routes.add(new Route(1));
        billboard1.routes.add(new Route(2));
        billboards.add(billboard1);
        Billboard billboard2 = new Billboard();
        billboard2.routes.add(new Route(2));
        billboards.add(billboard2);

        BillboardListDuplicator billboardListDuplicator = new BillboardListDuplicator(billboards);

        List<Billboard> dupList = billboardListDuplicator.getBillboards();

        Iterator<Route> iterator = billboards.get(0).routes.iterator();
        while (iterator.hasNext()){
            Route route = iterator.next();
            route.influenced = true;
            break;
        }

        System.out.println("origin billboards:");

        Iterator<Route> iterator2 = billboards.get(0).routes.iterator();
        while (iterator2.hasNext()){
            Route route = iterator2.next();
            System.out.println(route.influenced);
        }


        Iterator<Route> iterator3 = billboards.get(1).routes.iterator();
        while (iterator3.hasNext()){
            Route route = iterator3.next();
            System.out.println(route.influenced);
        }
        System.out.println();


        System.out.println("duplicate billboards:");

        Iterator<Route> iterator4 = dupList.get(0).routes.iterator();
        while (iterator4.hasNext()){
            Route route = iterator4.next();
            System.out.println(route.influenced);
        }




        Iterator<Route> iterator5 = dupList.get(1).routes.iterator();
        while (iterator5.hasNext()){
            Route route = iterator5.next();
            System.out.println(route.influenced);
        }
    }





    public void testRegionSelector() {

        RegionSelector selector = new RegionSelector(Geometries.rectangle(-74.260948,40.485284,-73.688285,40.920459));

        List<List<Billboard>> billboradList = selector.getRegionBillboards();

        for (List<Billboard> billboards : billboradList) {

            for (Billboard billboard : billboards)
                System.out.print(billboard.influence + "\t");

            System.out.println();
        }
    }





    public void testPartitionSampleGraph() {

        MyFileReader myFileReader = new MyFileReader("./karate-graph.txt");
        String line = myFileReader.getNextLine();

        List<Edge> edges = new ArrayList<>();

        String from = "-1";
        String to = "-1";

        while (line != null) {

            if (line.contains("source")) {
                from = line.split(" ")[5];
            } else if (line.contains("target")) {
                to = line.split(" ")[5];
                Edge edge = new Edge(from, to);
                edges.add(edge);
            }
            line = myFileReader.getNextLine();
        }
        myFileReader.close();


        String[][] matrix = new String[34][34];

        for (int i = 0; i < 34; i++ ) {

            for (int j = 0; j < 34; j++ ) {

                matrix[i][j] = "0";
            }
        }

        for (int i = 0; i < edges.size(); i++) {

            Edge edge = edges.get(i);
            matrix[Integer.parseInt(edge.from)-1][Integer.parseInt(edge.to)-1] = "1";
            matrix[Integer.parseInt(edge.to)-1][Integer.parseInt(edge.from)-1] = "1";
        }


        MyFileWriter myFileWriter = new MyFileWriter("./graph-matrix.txt");

        for (int i = 0; i < 34; i++) {

            String line2 = "";

            for (int j = 0; j < 34; j++) {

                line2 += matrix[i][j];
                line2 += " ";
            }

            myFileWriter.writeToFile(line2 + "\n");
        }
        myFileWriter.close();
    }




    public void testComparable() {

        TestComp testComp = new TestComp();
        testComp.i = 1;

        TestComp testComp1 = new TestComp();
        testComp1.i = 2;

        List<TestComp> list = new ArrayList<>();

        list.add(testComp);
        list.add(testComp1);

        Collections.sort(list);

        for (int i = 0; i < list.size(); i++ ) {

            System.out.println(list.get(i).i);
        }
    }



    class TestComp implements Comparable<TestComp> {

        int i;


        @Override
        public int compareTo(TestComp o) {

//            if (i > o.i)
//                return -1;
//            else if (i == o.i)
//                return 0;
//            else
//                return 1;
            return i - o.i;
        }
    }


    class Edge {

        String from;
        String to;

        public Edge(String from, String to) {
            this.from = from;
            this.to = to;
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








    // without fix cost support
    // billboard.cost = pow( billboard.influence, 0.66)
    //
    //
    //
    // with fix cost support
    // billboard.cost = pow ( billboard.cost, 0.66)
    //
    // because billboard.cost = billboard.influence(2w)
    //         billboard.influence = billboard.influence(4w)































}
