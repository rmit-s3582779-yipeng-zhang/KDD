package partition;

import entity.Billboard;
import fileIO.finalResult.FinalResultReader;
import fileIO.finalResult.MultipleResultReader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by marco on 01/07/2017.
 */
public class PartitionAlgorithm implements ClustersGetter {


    private FullGraph fullGraph;
    private List<SubGraph> subGraphList;
    private int combineResultFrom;
    private int combineResultTo;
    private int clusterNumber;


    public PartitionAlgorithm(int clusterNumber, int combineResultFrom, int combineResultTo) {

        this.combineResultFrom = combineResultFrom;
        this.combineResultTo = combineResultTo;
        this.clusterNumber = clusterNumber;
        setUpFullGraph();
        subGraphList = new ArrayList<>();
    }


    private void setUpFullGraph() {

//        FinalResultReader finalResultReader = new FinalResultReader();
//        List<Billboard> vertices = finalResultReader.getBillboards();

        MultipleResultReader multipleResultReader = new MultipleResultReader(combineResultFrom, combineResultTo);
        List<Billboard> vertices = multipleResultReader.getBillboards();

        fullGraph = new FullGraph(vertices);


//        List<Billboard> list = new ArrayList<>();
//
//        for (int i = 1; i < 35; i++) {
//
//            Billboard billboard = new Billboard();
//            billboard.panelID = "" + i;
//            list.add(billboard);
//        }
//
//        fullGraph = new FullGraph(list);
    }


    public static void main(String[] args) {

        PartitionAlgorithm partitionAlgorithm = new PartitionAlgorithm(1,1, 4);

        partitionAlgorithm.initSubGraphs();
        partitionAlgorithm.run2();
        partitionAlgorithm.printResult();
        partitionAlgorithm.printOverlapEntropy();
    }



    @Override
    public List<List<Billboard>> getClusters() {

        initSubGraphs();
        run2();

        List<List<Billboard>> clusters = new ArrayList<>();

        for (SubGraph subGraph : subGraphList) {

            List<Billboard> cluster = subGraph.getBillboards();
            clusters.add(cluster);
        }

        return clusters;
    }


    public void initSubGraphs() {


        for (int i = 0; i < fullGraph.getNumberOfVertices(); i++) {

            SubGraph subGraph = new SubGraph(fullGraph, i);
            subGraphList.add(subGraph);
        }
    }


    public void run() {

        int counter = 1;

        printResult();

        List<TwoGraph> twoGraphList = getNextRoundList();
        TwoGraph twoGraph = getTwoGraphToMerge(twoGraphList);

        while (twoGraph != null && (subGraphList.size() != 4) ) {

            updateSubGraphList(twoGraph);
            //printResult();

            System.out.println(counter++);
            //System.out.println();

            twoGraphList = getNextRoundList();
            twoGraph = getTwoGraphToMerge(twoGraphList);
        }
    }


    public void run2() {

        int round = 1;

        List<TwoGraph> remainMergeList = new ArrayList<>();
        TwoGraph temp = new TwoGraph(6,6,6,6,6,6);
        remainMergeList.add(temp);

        //while (remainMergeList.size() != 0) {
        while (subGraphList.size() > clusterNumber) {

            List<TwoGraph> sameSizeTwoGraphList = getSameSizeTwoGraphList(round);
            List<TwoGraph> sameSizeMergeList = getTwoGraphToMerge2(sameSizeTwoGraphList);

            updateSubGraphList2(sameSizeMergeList);
            //printResult();


            List<TwoGraph> remainTwoGraphList = getRemainList(round+1);
            remainMergeList = getTwoGraphToMerge2(remainTwoGraphList);

            updateSubGraphList2(remainMergeList);
            //printResult();

            round++;
            System.out.println(round);
        }
    }


    public void printResult() {

        for (int i = 0; i < subGraphList.size(); i++) {

            SubGraph subGraph = subGraphList.get(i);
            System.out.println(subGraph);
        }
    }


    public void printOverlapEntropy() {

        for (int i = 0; i < subGraphList.size(); i++) {
            SubGraph subGraph1 = subGraphList.get(i);

            for (int j = i+1; j < subGraphList.size(); j++) {
                SubGraph subGraph2 = subGraphList.get(j);

                int overlap = SubGraph.calculateOverlapBetween(subGraph1, subGraph2);
                double entropy1 = subGraph1.calculateEntropyWith(subGraph2);
                double entropy2 = subGraph2.calculateEntropyWith(subGraph1);

                System.out.println(i + " " + j + " : " + "overlap: " + overlap + " entropy1: " + entropy1 + " entropy2: " + entropy2);
            }
        }
    }


    private void updateSubGraphList(TwoGraph twoGraphToMerge) {

        SubGraph subGraph1 = subGraphList.get(twoGraphToMerge.graphIndex1);
        SubGraph subGraph2 = subGraphList.get(twoGraphToMerge.graphIndex2);

        SubGraph newSubGraph = new SubGraph(subGraph1, subGraph2, twoGraphToMerge.density);

        subGraphList.remove(subGraph1);
        subGraphList.remove(subGraph2);
        subGraphList.add(newSubGraph);
    }


    private void updateSubGraphList2(List<TwoGraph> twoGraphToMergeList) {

        List<SubGraph> subGraphListToRemove = new ArrayList<>();
        List<SubGraph> subGraphListToAdd = new ArrayList<>();

        for (TwoGraph twoGraph : twoGraphToMergeList) {

            SubGraph subGraph1 = subGraphList.get(twoGraph.graphIndex1);
            SubGraph subGraph2 = subGraphList.get(twoGraph.graphIndex2);

            SubGraph newSubGraph = new SubGraph(subGraph1, subGraph2, twoGraph.density);

            subGraphListToRemove.add(subGraph1);
            subGraphListToRemove.add(subGraph2);

            subGraphListToAdd.add(newSubGraph);
        }

        subGraphList.removeAll(subGraphListToRemove);
        subGraphList.addAll(subGraphListToAdd);
    }




    private List<TwoGraph> getNextRoundList() {

        List<TwoGraph> twoGraphList = new ArrayList<>();

        int numberOfSubGraphes  = subGraphList.size()-1;
        setUpTwoGraphList(twoGraphList, subGraphList, numberOfSubGraphes);

        return twoGraphList;
    }




    // twoGraphList contains combinations of two subgraph of same size of 2^(round-1)

    private List<TwoGraph> getSameSizeTwoGraphList(int round) {

        List<TwoGraph> sameSizeTwoGraphList = new ArrayList<>();
        List<SubGraph> sameSizeSubGraphList = getSameSizeSubGraphList(round);

        int numberOfSubGraphes  = sameSizeSubGraphList.size();
        setUpTwoGraphList(sameSizeTwoGraphList, sameSizeSubGraphList, numberOfSubGraphes);

        return sameSizeTwoGraphList;
    }



    // twoGraphList contains combinations of two subgraph

    private  List<TwoGraph> getRemainList(int round) {

        List<TwoGraph> remainList = new ArrayList<>();

        int numberOfSubGraphes = subGraphList.size();
        setUpTwoGraphList(remainList, subGraphList, numberOfSubGraphes);

        return remainList;
    }




    private void setUpTwoGraphList(List<TwoGraph> twoGraphList, List<SubGraph> subGraphList, int numberOfSubGraphes) {

        for (int i = 0; i < numberOfSubGraphes; i++) {

            for (int j = i+1; j < numberOfSubGraphes; j++) {

                SubGraph subGraph1 = subGraphList.get(i);
                SubGraph subGraph2 = subGraphList.get(j);

                List<SubGraph> graphGroup = new ArrayList<>();
                graphGroup.add(subGraph1);
                graphGroup.add(subGraph2);

                int numberOfVertices = graphGroup.get(0).getNumOfVertices() + graphGroup.get(1).getNumOfVertices();
                double proximity = -1;
                double density = SubGraph.calculateDensity(graphGroup);
                double increment = density - (graphGroup.get(0).getDensity() + graphGroup.get(1).getDensity());
                if (increment > 0)
                    proximity = SubGraph.calculateProximity(graphGroup.get(0), graphGroup.get(1));

                TwoGraph twoGraph = new TwoGraph(i, j, density, increment, proximity, numberOfVertices);
                twoGraphList.add(twoGraph);
            }
        }
    }



    // subGraphList contains subgraph of size 2^(round-1)

    private List<SubGraph> getSameSizeSubGraphList(int round) {

        List<SubGraph> subGraphList = new ArrayList<>();

        for (SubGraph subGraph : subGraphList) {

            if (subGraph.getNumOfVertices() == Math.pow(2, round-1))
                subGraphList.add(subGraph);
        }
        return subGraphList;
    }




    private TwoGraph getTwoGraphToMerge(List<TwoGraph> twoGraphList) {

        Collections.sort(twoGraphList);
        TwoGraph twoGraph = twoGraphList.get(0);
        return twoGraph;

//        int maxIndex = 0;
//        double maxProximity = -1;
//
//        for (int i = 0; i < twoGraphList.size(); i++) {
//
//            TwoGraph twoGraph = twoGraphList.get(i);
//
//            if (twoGraph.densityIncrement > 0) {
//
//                if (twoGraph.proximity > maxProximity) {
//                    maxProximity = twoGraph.proximity;
//                    maxIndex = i;
//                }
//            }
//        }
//
//        TwoGraph twoGraph = twoGraphList.get(maxIndex);
//
//        if (maxProximity == -1)
//            return null;
//
//        return twoGraph;

    }



    private List<TwoGraph> getTwoGraphToMerge2(List<TwoGraph> twoGraphList) {

        List<TwoGraph> twoGraphToMergeList = new ArrayList<>();

        Collections.sort(twoGraphList);

        for (TwoGraph twoGraph : twoGraphList) {

            if (twoGraph.densityIncrement > 0  && !isTwoGraphConflict(twoGraphToMergeList, twoGraph))
                twoGraphToMergeList.add(twoGraph);

        }

        return twoGraphToMergeList;
    }


    private boolean isTwoGraphConflict(List<TwoGraph> twoGraphToMergeList, TwoGraph twoGraph) {

        for (TwoGraph twoGraphInList : twoGraphToMergeList) {

            if (twoGraphInList.graphIndex1 == twoGraph.graphIndex1)
                return true;
            if (twoGraphInList.graphIndex1 == twoGraph.graphIndex2)
                return true;
            if (twoGraphInList.graphIndex2 == twoGraph.graphIndex1)
                return true;
            if (twoGraphInList.graphIndex2 == twoGraph.graphIndex2)
                return true;
        }
        return false;
    }



    private class TwoGraph implements Comparable<TwoGraph> {

        public int graphIndex1;
        public int graphIndex2;
        public double density;
        public double densityIncrement;
        public double proximity;
        public int numOfvertices;


        public TwoGraph(int graphIndex1, int graphIndex2, double density, double densityIncrement, double proximity, int numOfvertices) {

            this.graphIndex1 = graphIndex1;
            this.graphIndex2 = graphIndex2;
            this.density = density;
            this.densityIncrement = densityIncrement;
            this.proximity = proximity;
            this.numOfvertices = numOfvertices;
        }


        // positive afterward
        @Override
        public int compareTo(TwoGraph o) {

            if (densityIncrement < 0 && o.densityIncrement >= 0) {

                return 1;

            } else if (densityIncrement >= 0 && o.densityIncrement >= 0) {

                if (proximity > o.proximity)
                    return -1;
                else if (proximity == o.proximity)
                    return 0;
                else
                    return 1;

            } else {

                if (densityIncrement > o.densityIncrement)
                    return -1;
                else if (densityIncrement == o.densityIncrement)
                    return 0;
                else
                    return 1;
            }
        }
    }

}
