
package newPartition;

import entity.Billboard;
import entity.Route;
import fileIO.MyFileWriter;
import fileIO.finalResult.MultipleResultReader;
import partition.FullGraph;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

/**
 * Created by marco on 19/07/2017.
 */
public class InputFileGenerator {


    private List<Billboard> inputBillboards;
    private static final String outputFilePath = "./METIS-input.txt";
    private static final int inputFileGeneratorfrom = 1;
    private static final int inputFileGeneratorTo = 1;


    public InputFileGenerator(List<Billboard> inputBillboards) {

        this.inputBillboards = inputBillboards;
    }



    public static void main(String[] args) {


        MultipleResultReader multipleResultReader = new MultipleResultReader(inputFileGeneratorfrom, inputFileGeneratorTo);
        List<Billboard> billboards = multipleResultReader.getBillboards();

        System.out.println("finish getBillboards() size : " + billboards.size());

        InputFileGenerator inputFileGenerator = new InputFileGenerator(billboards);
        inputFileGenerator.generateInputFile();
    }



    public void generateInputFile() {

        MyFileWriter myFileWriter = new MyFileWriter(outputFilePath);


        FullGraph fullGraph = new FullGraph(inputBillboards);

        System.out.println("finish new FullGraph()");

        int numberOfVertices = fullGraph.getNumberOfVertices();
        int numberOfEdges = fullGraph.getNumberofEdge();
        List<List<Integer>> fileBody = fullGraph.generateInputFileBody();


        myFileWriter.writeToFile(numberOfVertices + " " + numberOfEdges + " 001 \n");

        for (List<Integer> row : fileBody) {

            for (int vertexOrWeight : row)
                myFileWriter.writeToFile(vertexOrWeight + " ");

            myFileWriter.writeToFile("\n");
        }

        myFileWriter.close();
    }


    public List<Billboard> getInputBillboards() {

        return inputBillboards;
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
