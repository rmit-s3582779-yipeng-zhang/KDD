package partition;

//import com.sun.tools.internal.xjc.reader.dtd.bindinfo.BIAttribute;
import entity.Billboard;
import entity.BillboardSet;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by marco on 30/06/2017.
 */
public class SubGraph {


    private FullGraph fullGraph;

    private List<Integer> vertices;  // index to FullGraph List<Billboard>

    private double density;



    public SubGraph(FullGraph fullGraph, int singleVertex) {

        this.fullGraph = fullGraph;

        vertices = new ArrayList<>();
        vertices.add(singleVertex);

        double outsideOverlap = 0.0;
        for (int i = 0; i < fullGraph.getNumberOfVertices(); i++)
            outsideOverlap += fullGraph.getOverlap(singleVertex, i);

        outsideOverlap -= fullGraph.getOverlap(singleVertex, singleVertex);

        this.density = (0 - outsideOverlap) / 1;
    }


    public SubGraph(SubGraph subGraph1, SubGraph subGraph2, double density) {

        this.fullGraph = subGraph1.fullGraph;

        vertices = new ArrayList<>();
        vertices.addAll(subGraph1.vertices);
        vertices.addAll(subGraph2.vertices);

        this.density = density;
    }


    public int getNumOfVertices() {

        return vertices.size();
    }


    public double getDensity() {

        return density;
    }


    public List<Billboard> getBillboards() {

        List<Billboard> billboards = new ArrayList<>();

        for (int vertexIndex : vertices) {

            Billboard billboard = fullGraph.getBillboard(vertexIndex);
            billboards.add(billboard);
        }
        return billboards;
    }



    public static double calculateDensity(List<SubGraph> graphGroup) {

        SubGraph subGraph1 = graphGroup.get(0);
        SubGraph subGraph2 = graphGroup.get(1);

        int insideOverlap = calculateInsideOverlap(graphGroup);
        int outsideOverlap = calculateOutsideOverlap(graphGroup);
        int numberOfVertices = subGraph1.vertices.size() + subGraph2.vertices.size();

        double density = (insideOverlap - outsideOverlap) / (double)numberOfVertices;

        return density;
    }


    public static double calculateProximity(SubGraph subGraph1, SubGraph subGraph2) {

        List<SubGraph> graphGroup1 = new ArrayList<>();
        graphGroup1.add(subGraph1);

        List<SubGraph> graphGroup2 = new ArrayList<>();
        graphGroup2.add(subGraph2);

        int betweenOverlap = calculateOverlapBetween(subGraph1, subGraph2);
        int outsideOverlap1 = calculateOutsideOverlap(graphGroup1);
        int outsideOverlap2 = calculateOutsideOverlap(graphGroup2);

        double proximity = 1;

        if ((outsideOverlap1 + outsideOverlap2) != 0)
            proximity = (2.0d * betweenOverlap) / (outsideOverlap1 + outsideOverlap2);

        return proximity;
    }



    private static int calculateInsideOverlap(List<SubGraph> graphGroup) {

        int insideOverlap = 0;

        SubGraph subGraph1 = graphGroup.get(0);
        SubGraph subGraph2 = graphGroup.get(1);

        List<Integer> allVertices = new ArrayList<>();
        allVertices.addAll(subGraph1.vertices);
        allVertices.addAll(subGraph2.vertices);

        for (int i = 0; i < allVertices.size(); i++) {

            for (int j = i+1; j < allVertices.size(); j++) {

                int vertexIndex1 = allVertices.get(i);
                int vertexIndex2 = allVertices.get(j);

                insideOverlap += subGraph1.fullGraph.getOverlap(vertexIndex1, vertexIndex2);
            }
        }
        return insideOverlap;
    }



    private static int calculateOutsideOverlap(List<SubGraph> graphGroup) {

        int outsideOverlap = 0;

        List<Integer> insideVertices = new ArrayList<>();
        List<Integer> outsideVertices = graphGroup.get(0).fullGraph.getVertexIndexList();

        for (int i = 0; i < graphGroup.size(); i++) {

            SubGraph subGraph = graphGroup.get(i);
            insideVertices.addAll(subGraph.vertices);
            outsideVertices.removeAll(subGraph.vertices);
        }

        outsideOverlap = calculateOverlap(graphGroup.get(0).fullGraph, insideVertices, outsideVertices);
        return outsideOverlap;
    }



    public static int calculateOverlapBetween(SubGraph subGraph1, SubGraph subGraph2) {

        int overlap = calculateOverlap(subGraph1.fullGraph, subGraph1.vertices, subGraph2.vertices);

        return overlap;
    }


    public double calculateEntropyWith(SubGraph anotherGraph) {

        double entropy = 0.0;

        List<Billboard> billboards = getBillboards();

        for (Billboard billboard : billboards) {

            double temporaryEntropy = 0.0;

            if (billboard.influence != 0)
                temporaryEntropy = ( billboard.influence + anotherGraph.getSubGraphInfluence()
                        - anotherGraph.getSubGraphInfluence(billboard) ) / (double)billboard.influence;

            if (temporaryEntropy > entropy)
                entropy = temporaryEntropy;
        }
        return entropy;
    }


    private int getSubGraphInfluence() {

        int influence = 0;

        List<Billboard> billboards = getBillboards();

        BillboardSet billboardSet = new BillboardSet();

        for (Billboard billboard : billboards)
            billboardSet.add(billboard);

        influence = billboardSet.routeIDs.size();
        return influence;
    }


    private int getSubGraphInfluence(Billboard addOneMoreBoard) {

        int influence = 0;

        List<Billboard> billboards = getBillboards();

        BillboardSet billboardSet = new BillboardSet();

        for (Billboard billboard : billboards)
            billboardSet.add(billboard);

        billboardSet.add(addOneMoreBoard);

        influence = billboardSet.routeIDs.size();
        return influence;
    }




    private static int calculateOverlap(FullGraph fullGraph, List<Integer> vertexIndexes1, List<Integer> vertexIndexes2) {

        int overlap = 0;

        for (int i = 0; i < vertexIndexes1.size(); i++) {

            for (int j = 0; j < vertexIndexes2.size(); j++) {

                int vertexIndex1 = vertexIndexes1.get(i);
                int vertexIndex2 = vertexIndexes2.get(j);

                //TODO this is wrong, can't just simply add them all!!!
                overlap += fullGraph.getOverlap(vertexIndex1, vertexIndex2);
            }
        }
        return overlap;
    }


    @Override
    public String toString() {

        String result = "";

        for (int i = 0; i < vertices.size(); i++) {

            int billboardIndex = vertices.get(i);
            result += fullGraph.getBillboardInfo(billboardIndex);
            result += " & ";
        }

        result += "density " + density;
        return result;
    }


}
