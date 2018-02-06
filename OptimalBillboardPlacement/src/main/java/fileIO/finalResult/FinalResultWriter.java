package fileIO.finalResult;

import com.github.davidmoten.rtree.Entry;
import com.github.davidmoten.rtree.RTree;
import com.github.davidmoten.rtree.geometry.Geometries;
import com.github.davidmoten.rtree.geometry.Point;
import com.github.davidmoten.rtree.geometry.Rectangle;
import configure.Developer;
import fileIO.FilePath;
import fileIO.MyFileReader;
import fileIO.MyFileWriter;
import fileIO.RTree.Deserialize;
import rx.Observable;

import java.util.*;

/**
 * Created by marco on 04/06/2017.
 */

// input : "./billboard.rtree"
//          + "./nycTrip.rtree"
//          + "./laTrip.rtree"
//
// output : "./billboardProcessedResult.txt" +
//          "./billboardFinalResult.txt"
//          + "./billboardCombineResult.txt"

public class FinalResultWriter {


    public static final boolean useNYC = false;
    public static final double distance = 0.0005;

    private RTree<String, Point> billboardTree = RTree.create();
    private RTree<String, Point> tripTree = RTree.star().create();


    public static void main(String[] args) {

        FinalResultWriter finalResultWriter = new FinalResultWriter();

        finalResultWriter.writeProcessedResultToFile();
        finalResultWriter.writeFinalResultToFile();
        finalResultWriter.writeCombineResultToFile();
    }


    public void writeProcessedResultToFile() {

        if (useNYC)
            findBillboardsIn(Geometries.rectangle(-74.260948, 40.485284, -73.688285, 40.920459));  // new york
        else
            findBillboardsIn(Geometries.rectangle(-118.642000, 33.717900, -118.145000, 34.330962));  // los angeles

        //finalResultWriter.findBillboardsIn(Geometries.rectangle(-74.2,40.48,-73.5,40.9));  // new york
        //finalResultWriter.findBillboardsIn(Geometries.rectangle(-74.060948,40.740284,-73.688285,40.920459));  // 203 billboards
        //finalResultWriter.findBillboardsIn(Geometries.rectangle(-74.000948,40.805284,-73.888285,40.920459));  // 78 billboards
    }


    private void findBillboardsIn(Rectangle region) {

        String root = System.getProperty("user.dir");

        if (Developer.SYSTEM.equals("Mac")) {

            billboardTree = Deserialize.deserialize(FilePath.billboardRTreePath, 10111112);
            if (useNYC)
                tripTree = Deserialize.deserialize(FilePath.nycTripRTreePath, 188542964);
            else
                tripTree = Deserialize.deserialize(FilePath.laTripRTreePath, 202234292);

        }

        if (Developer.SYSTEM.equals("Win")) {


            billboardTree = Deserialize.deserialize(root + FilePath.billboardRTreePath, 10111112);
            if (useNYC)
                tripTree = Deserialize.deserialize(root + FilePath.nycTripRTreePath, 262591164);
            else
                //laTripTree = Deserialize.deserialize(root + FilePath.laTripRTreePath, 26745704);
                tripTree = Deserialize.deserialize(root + FilePath.laTripRTreePath, 471657548);

        }


        MyFileWriter processedResultWriter = new MyFileWriter(FilePath.billboardProcessedResultPath);

        try {

            Observable<Entry<String, Point>> results = billboardTree.search(region);
            Iterable<Entry<String, Point>> resultsIterable = results.toBlocking().toIterable();
            Iterator<Entry<String, Point>> billboardIterator = resultsIterable.iterator();

            while (billboardIterator.hasNext()) {

                Entry<String, Point> billboard = billboardIterator.next();
                Point billboardLocation = billboard.geometry();

                System.out.println("billboard: " + billboard.value() + " " + billboardLocation.toString());
                processedResultWriter.writeToFile(billboard.value() + " " + billboardLocation.x() + " " + billboardLocation.y() + "\n");

                findTrajectoryPointAround(billboardLocation, processedResultWriter);
            }

            processedResultWriter.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void findTrajectoryPointAround(Point billboardLocation, MyFileWriter processedResultWriter) {

        Observable<Entry<String, Point>> results = null;

        if (useNYC) {
            results = tripTree.search(Geometries.rectangle(
                    billboardLocation.x() - distance, billboardLocation.y() - distance,
                    billboardLocation.x() + distance, billboardLocation.y() + distance));
        } else {
            results = tripTree.search(Geometries.rectangle(
                    billboardLocation.x() - distance, billboardLocation.y() - distance,
                    billboardLocation.x() + distance, billboardLocation.y() + distance));
        }
        Iterable<Entry<String, Point>> resultsIterable = results.toBlocking().toIterable();
        Iterator<Entry<String, Point>> routeIterator = resultsIterable.iterator();


        int counter = 0;
        while (routeIterator.hasNext()) {

            counter++;
            Entry<String, Point> route = routeIterator.next();
            //System.out.println(entry.value() + " " + entry.geometry().x() + " " + entry.geometry().y());
            processedResultWriter.writeToFile(route.value() + " " + route.geometry().x() + " " + route.geometry().y() + "\n");
        }
        System.out.println("\nnumber of trajectory point: " + counter + "\n\n\n");
        processedResultWriter.writeToFile("\n");
    }


    private void writeFinalResultToFile() {

        MyFileReader processedResultReader = new MyFileReader(FilePath.billboardProcessedResultPath);
        MyFileWriter finalResultWriter = new MyFileWriter(FilePath.billboardFinalResultPath);

        String line = processedResultReader.getNextLine();

        while (line != null) {

            String[] elemts = line.split(" ");
            finalResultWriter.writeToFile(elemts[0] + " ");
            Set<Integer> routes = new TreeSet<>();

            while (true) {

                line = processedResultReader.getNextLine();

                if (line.equals(""))
                    break;

                try {
                    String[] elements = line.split(" ");
                    //String routeID = elements[0].split("~")[1];   // get routeID
                    String routeID = elements[0];   // get routeID //change 2018

                    routes.add(Integer.parseInt(routeID));
                }
                catch(Exception e){}


            }

            String lineToWrite = routes.toString();
            lineToWrite = lineToWrite.replace(",", "");
            lineToWrite = lineToWrite.replace("[", "");
            lineToWrite = lineToWrite.replace("]", "");
            finalResultWriter.writeToFile(lineToWrite + "\n");

            line = processedResultReader.getNextLine();
        }
        finalResultWriter.close();
    }


    private void writeCombineResultToFile() {

        MyFileReader processedResultReader = new MyFileReader(FilePath.billboardProcessedResultPath);
        MyFileWriter combineResultWriter = new MyFileWriter(FilePath.billboardCombineResultPath);

        Set<BillboardLongLat> billboardLongLats = new TreeSet<>();
        String line = processedResultReader.getNextLine();

        while (line != null) {

            String[] elements = line.split(" "); // line = panelID~weeklyImpression longitude latitude

            BillboardLongLat billboardLongLat = new BillboardLongLat();
            billboardLongLat.panelIDWeeklyImpression = elements[0];
            billboardLongLat.longitude = Double.parseDouble(elements[1]);
            billboardLongLat.latitude = Double.parseDouble(elements[2]);

            boolean isContained = false;

            if (billboardLongLats.contains(billboardLongLat)) {
                isContained = true;
            } else {
                billboardLongLats.add(billboardLongLat);
            }

            while (true) {

                line = processedResultReader.getNextLine();
                if (line.equals(""))
                    break;

                if (!isContained) { // if not contained, get and store routeIDs, else do nothing

                    try {
                        String[] elemts = line.split(" ");
                        //String routeID = elemts[0].split("~")[1];   // get routeID
                        String routeID = elemts[0];   // get routeID //change 2018
                        billboardLongLat.routeIDs.add(Integer.parseInt(routeID));
                    }
                    catch(Exception e){}

                }
            }

            line = processedResultReader.getNextLine();
        }

        for (BillboardLongLat billboardLongLat : billboardLongLats)
            combineResultWriter.writeToFile(billboardLongLat.toString() + "\n");
        combineResultWriter.close();
    }


    private class BillboardLongLat implements Comparable<BillboardLongLat> {

        public String panelIDWeeklyImpression;

        public double longitude;
        public double latitude;

        public Set<Integer> routeIDs;

        public BillboardLongLat() {
            routeIDs = new TreeSet<>();
        }


        @Override
        public int compareTo(BillboardLongLat o) {

            if (longitude == o.longitude && latitude == o.latitude)
                return 0;
            else if (longitude > o.longitude)
                return 1;
            else
                return -1;
        }

        @Override
        public String toString() {

            String result = panelIDWeeklyImpression;

            for (int routeID : routeIDs) {
                result += " ";
                result += "" + routeID;
            }
            return result;
        }
    }
}
