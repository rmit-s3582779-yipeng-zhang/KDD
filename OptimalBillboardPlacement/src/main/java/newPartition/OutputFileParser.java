
package newPartition;

import entity.Billboard;
import entity.Clusters;
import entity.Route;
import fileIO.MyFileReader;
import fileIO.MyFileWriter;
import fileIO.finalResult.MultipleResultReader;
import org.omg.PortableServer.LIFESPAN_POLICY_ID;
import partition.ClustersGetter;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by marco on 19/07/2017.
 */
public class OutputFileParser implements ClustersGetter {

    // input: METIS-output-1-1.txt + numberOfCluster + List<Billboard> billboards
    // (ATTENTION: this billboards must == InputFileGenerator.billboards)
    // output: "./2w-trip-10-Clusters.cluster"

    private static final String inputFilePath = "./METIS-input.txt";
    private static final int outputFileParserForm = 1;
    private static final int outputFileParserTo = 1;
    private static final int outputFileParserNumberOfClusters = 30; // the Number Of Clusters
    private static String outputFilePath;


    private int numberOfCluster;
    private List<Billboard> billboards;


    public OutputFileParser(List<Billboard> billboards, int numberOfCluster) {
        this.billboards = billboards;
        this.numberOfCluster = numberOfCluster;
    }

    public static void main(String[] args) {

        outputFilePath = "./20w-trip-" + outputFileParserNumberOfClusters + "-Clusters.cluster";

        MultipleResultReader multipleResultReader = new MultipleResultReader(outputFileParserForm, outputFileParserTo);
        List<Billboard> billboards = multipleResultReader.getBillboards();

        OutputFileParser outputFileParser = new OutputFileParser(billboards, outputFileParserNumberOfClusters);
        List<List<Billboard>> clusters = outputFileParser.getClusters();

        for (List<Billboard> cluster : clusters) {
            System.out.println("~~~~" + cluster.size());

            for (Billboard billboard : cluster) {
                //System.out.println(billboard.panelID + "  ");
            }
            System.out.println("________________________");
        }


        Clusters clusters1 = new Clusters(clusters);

        try {
            FileOutputStream fos = new FileOutputStream(outputFilePath);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(clusters1);

            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public List<List<Billboard>> getClusters() {

        List<List<Billboard>> clusters = new ArrayList<>();

        List<List<Integer>> vertexIndexClusters = getBillboradsIndex();

        for (int i = 0; i < vertexIndexClusters.size(); i++) {

            List<Integer> vertexIndexCluster = vertexIndexClusters.get(i);
            List<Billboard> cluster = new ArrayList<>();

            for (int vertexIndex : vertexIndexCluster) {

                Billboard billboard = billboards.get(vertexIndex - 1);
                cluster.add(billboard);
            }
            clusters.add(cluster);
        }
        return clusters;
    }


    private List<List<Integer>> getBillboradsIndex() {


        List<List<Integer>> clusters = new ArrayList<>();

        for (int i = 0; i < numberOfCluster; i++) {

            List<Integer> cluster = new ArrayList<>();
            clusters.add(cluster);
        }


        MyFileReader myFileReader = new MyFileReader(inputFilePath);
        String line = myFileReader.getNextLine();

        int vertexIndex = 1;

        while (line != null) {

            int clusterIndex = Integer.parseInt(line);

            List<Integer> cluster = clusters.get(clusterIndex);
            cluster.add(vertexIndex++);

            line = myFileReader.getNextLine();
        }
        myFileReader.close();

        return clusters;
    }


    private List<Billboard> generateTestBillboardList() {


        List<Billboard> billboards = new ArrayList<>();
        Billboard billboard1 = new Billboard();
        billboard1.panelID = "1";
        billboard1.routes.add(new Route(100));
        billboard1.routes.add(new Route(200));
        billboard1.routes.add(new Route(300));
        billboard1.routes.add(new Route(400));
        billboards.add(billboard1);

        Billboard billboard2 = new Billboard();
        billboard2.panelID = "2";
        billboard2.routes.add(new Route(400));
        billboard2.routes.add(new Route(500));
        billboard2.routes.add(new Route(600));
        billboard2.routes.add(new Route(1200));
        billboards.add(billboard2);

        Billboard billboard3 = new Billboard();
        billboard3.panelID = "3";
        billboard3.routes.add(new Route(200));
        billboard3.routes.add(new Route(300));
        billboard3.routes.add(new Route(500));
        billboard3.routes.add(new Route(600));
        billboard3.routes.add(new Route(700));
        billboard3.routes.add(new Route(800));
        billboard3.routes.add(new Route(900));
        billboard3.routes.add(new Route(1000));
        billboard3.routes.add(new Route(1100));
        billboards.add(billboard3);

        Billboard billboard4 = new Billboard();
        billboard4.panelID = "4";
        billboard4.routes.add(new Route(700));
        billboard4.routes.add(new Route(800));
        billboard4.routes.add(new Route(1200));
        billboard4.routes.add(new Route(1500));
        billboard4.routes.add(new Route(1600));
        billboard4.routes.add(new Route(1700));
        billboard4.routes.add(new Route(1800));
        billboard4.routes.add(new Route(1900));
        billboard4.routes.add(new Route(2000));
        billboard4.routes.add(new Route(2100));
        billboards.add(billboard4);

        Billboard billboard5 = new Billboard();
        billboard5.panelID = "5";
        billboard5.routes.add(new Route(100));
        billboard5.routes.add(new Route(900));
        billboard5.routes.add(new Route(1000));
        billboard5.routes.add(new Route(1100));
        billboard5.routes.add(new Route(1300));
        billboard5.routes.add(new Route(1400));
        billboards.add(billboard5);

        Billboard billboard6 = new Billboard();
        billboard6.panelID = "6";
        billboard6.routes.add(new Route(1300));
        billboard6.routes.add(new Route(1400));
        billboard6.routes.add(new Route(1500));
        billboard6.routes.add(new Route(1600));
        billboard6.routes.add(new Route(2200));
        billboard6.routes.add(new Route(2300));
        billboard6.routes.add(new Route(2400));
        billboard6.routes.add(new Route(2500));
        billboard6.routes.add(new Route(2600));
        billboard6.routes.add(new Route(2700));
        billboards.add(billboard6);

        Billboard billboard7 = new Billboard();
        billboard7.panelID = "7";
        billboard7.routes.add(new Route(1700));
        billboard7.routes.add(new Route(1800));
        billboard7.routes.add(new Route(1900));
        billboard7.routes.add(new Route(2000));
        billboard7.routes.add(new Route(2100));
        billboard7.routes.add(new Route(2200));
        billboard7.routes.add(new Route(2300));
        billboard7.routes.add(new Route(2400));
        billboard7.routes.add(new Route(2500));
        billboard7.routes.add(new Route(2600));
        billboard7.routes.add(new Route(2700));
        billboards.add(billboard7);

        return billboards;
    }
}

