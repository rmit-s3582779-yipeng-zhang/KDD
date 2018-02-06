package partition;

import entity.Billboard;
import entity.Route;
import fileIO.MyFileReader;
import fileIO.MyFileWriter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by marco on 20/06/2017.
 */
public class FullGraph {


    private int numberOfVertices;
    private List<Billboard> vertices;
    private List<List<Integer>> adjacentMatrix;  // used to store weight(overlap)


    public FullGraph(List<Billboard> vertices) {

        this.vertices = vertices;
        numberOfVertices = vertices.size();
        setUpMatrix();
        for (int i = 0; i < adjacentMatrix.size(); i++) {
            adjacentMatrix.get(i).set(i, 0);
        }
    }


    public int getOverlap(int billboardIndex1, int billboardIndex2) {

        int overlap = adjacentMatrix.get(billboardIndex1).get(billboardIndex2);
        return overlap;
    }


    public int getNumberOfVertices() {

        return numberOfVertices;
    }

    public int getNumberofEdge() {

        int edgeCounter = 0;

        for (int i = 0; i < adjacentMatrix.size(); i++) {

            for (int j = 0; j < adjacentMatrix.size(); j++) {

                if (adjacentMatrix.get(i).get(j) != 0)
                    edgeCounter++;
            }
        }

        return edgeCounter / 2;
    }


    public Billboard getBillboard(int index) {

        Billboard billboard = vertices.get(index);

        return billboard;
    }


    public List<Integer> getVertexIndexList() {

        List<Integer> indexList = new ArrayList<>();

        for (int i = 0; i < numberOfVertices; i++)
            indexList.add(i);

        return indexList;
    }

    public String getBillboardInfo(int billboardIndex) {

        Billboard billboard = vertices.get(billboardIndex);
        return billboard.panelID;
    }



    public List<List<Integer>> generateInputFileBody() {

        List<List<Integer>> inputFileBody = new ArrayList<>(adjacentMatrix.size());

        for (int i = 0; i < adjacentMatrix.size(); i++) {

            List<Integer> inputFileRow = new ArrayList<>(adjacentMatrix.size());

            for (int j = 0; j < adjacentMatrix.size(); j++) {

                int overlap = adjacentMatrix.get(i).get(j);

                if (overlap != 0) {

                    inputFileRow.add(j+1);         // edge
                    inputFileRow.add(overlap);   //weight
                }
            }

            inputFileBody.add(inputFileRow);
        }

        return inputFileBody;
    }


    private void setUpMatrix() {

        adjacentMatrix = new ArrayList<>();

        for (int i = 0; i < vertices.size(); i++) {

            List<Integer> row = setUpRow(i);
            adjacentMatrix.add(row);
        }



//        adjacentMatrix = new ArrayList<>();
//
//        MyFileReader myFileReader = new MyFileReader("./graph-matrix.txt");
//
//        String line = myFileReader.getNextLine();
//
//        while (line != null) {
//
//            String[] elements = line.split(" ");
//            List<Integer> row = new ArrayList<>();
//
//            for (int i = 0; i < 34; i++) {
//
//                row.add(Integer.parseInt(elements[i]));
//            }
//
//            adjacentMatrix.add(row);
//            line = myFileReader.getNextLine();
//        }
    }


    private List<Integer> setUpRow(int rowIndex) {

        List<Integer> row = new ArrayList<>();

        Billboard rowBillboard = vertices.get(rowIndex);

        for (Billboard billboard : vertices) {

            int overlap = setUpCell(rowBillboard, billboard);
            row.add(overlap);
        }
        return row;
    }


    private int setUpCell(Billboard billboard1, Billboard billboard2) {

        int overlap = 0;

        for (Route route1 : billboard1.routes) {

            for (Route route2 : billboard2.routes) {

                if (route1.routeID == route2.routeID) {
                    overlap++;
                    break;
                }
            }
        }
        return overlap;
    }

}
