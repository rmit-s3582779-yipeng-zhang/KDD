package fileIO.finalResult;

import com.sun.javafx.geom.Vec2f;
import entity.Billboard;
import entity.Route;
import fileIO.FilePath;
import fileIO.MyFileReader;

import java.util.*;

/**
 * Created by marco on 18/05/2017.
 */
public class FinalResultReader {


    public static final boolean readFromCombineBillboardFile = true;

    private static boolean isInitialized = false;

    private static List<String> panelIDs = new ArrayList<>();
    private static List<Integer> weeklyImpressions = new ArrayList<>();
    private static Set<Integer> routeIDSet = new TreeSet<>();
    private static List<List<Integer>> routeIDsOfBillboards = new ArrayList<>();   // { {routeID1, routeID2}, {routeID1, routeID2} }
    private static List<List<Integer>> routeIndexesOfBillboards = new ArrayList<>();   // { {routeIndex1, routeIndex2}, {routeIndex1, routeIndex2} }


    private List<Route> routes = null;  // use routeIndex1 to retrieve route object
    private List<Billboard> billboards = null;


    public FinalResultReader() {

        if (!isInitialized) {

            init();
            isInitialized = true;
        }
        routes = new ArrayList<>();
        billboards = new ArrayList<>();
    }

    private double getCharge(double influence){
        double charge;
        if(influence < 50)
            charge = 10;
        else if(influence < 100)
            charge = 20;
        else if(influence < 200)
            charge = 100;
        else if(influence < 300)
            charge = 200;
        else
            charge = 300;
        return charge;
    }


    public List<Billboard> getBillboards() {

        newRoutes();

        for (int i = 0; i < panelIDs.size(); i++) {

            Billboard billboard = new Billboard();

            billboard.panelID = panelIDs.get(i);
            //billboard.charge = weeklyImpressions.get(i) / 3000;
            //billboard.charge = (int)(Math.round(billboard.charge / 5)) * 5;

            List<Integer> routeIndexes = routeIndexesOfBillboards.get(i);

            for (int j = 0; j < routeIndexes.size(); j++) {

                int routeIndex = routeIndexes.get(j);
                Route route = routes.get(routeIndex);
                billboard.routes.add(route);
            }
            billboard.influence = billboard.routes.size();
            //billboard.charge = getCharge(billboard.influence);
            billboard.charge = Math.pow(billboard.influence,(2.0/3.0));
            billboard.influencePerCharge = billboard.influence / billboard.charge;

            billboards.add(billboard);
        }
        return billboards;
    }




    // initialize panelIDs, weeklyImpressions, routeIDSet, routeIDsOfBillboards, routeIndexesOfBillboards

    private void init() {

        String baseFilePath = "";

        if (readFromCombineBillboardFile)
            baseFilePath = FilePath.billboardCombineResultPath;
        else
            baseFilePath = FilePath.billboardFinalResultPath;


        String filePath = baseFilePath.split(".txt")[0] + "1.txt";
        processOneCombineResultFile(filePath);

        setUpRouteIDsAndIndexes();
    }


    private void processOneCombineResultFile(String filePath) {

        MyFileReader finalResultReader = new MyFileReader(filePath);

        String line = "";

        while (true) {

            line = finalResultReader.getNextLine();
            if (line == null)
                break;

            String[] elements = line.split(" ");
            if (elements.length == 1)
                continue;    // skip those billboard which can not influence any route

            String panelID = elements[0].split("~")[0]; // panelID~weeklyImpression
            int weeklyImpression = Integer.parseInt(elements[0].split("~")[1]);

            if (weeklyImpression == 0)
                continue;    // skip those billboards which has no weeklyImpression

            List<Integer> routeIDs = new ArrayList<>();

            for (int i = 1; i < elements.length; i++) {

                int routeID = Integer.parseInt(elements[i]);
                routeIDs.add(routeID);
            }

            panelIDs.add(panelID);
            weeklyImpressions.add(weeklyImpression);

            routeIDsOfBillboards.add(routeIDs);
        }
        finalResultReader.close();
    }


    // set up routeIDSet and routeIndexesOfBillboards

    private void setUpRouteIDsAndIndexes() {

        for (int i = 0; i < routeIDsOfBillboards.size(); i++) {

            List<Integer> routeIDs = routeIDsOfBillboards.get(i);

            for (int j = 0; j < routeIDs.size(); j++) {

                int routeID = routeIDs.get(j);
                routeIDSet.add(routeID);
            }
        }

        for (int i = 0; i < routeIDsOfBillboards.size(); i++) {

            List<Integer> routeIndexes = new ArrayList<>();
            List<Integer> routeIDs = routeIDsOfBillboards.get(i);

            for (int j = 0; j < routeIDs.size(); j++) {

                int routeID = routeIDs.get(j);
                int routeIndex = getRouteIndex(routeID);
                routeIndexes.add(routeIndex);
            }
            routeIndexesOfBillboards.add(routeIndexes);
        }
    }



    private int getRouteIndex(int routeID) {

        List<Integer> routeIDs = new ArrayList<>();
        routeIDs.addAll(routeIDSet);

        for (int i = 0; i < routeIDs.size(); i++) {

            int id = routeIDs.get(i);

            if (id == routeID)
                return i;
        }

        System.out.println("can not get index for routeID : " + routeID);
        return -1;
    }



    private void newRoutes() {

        List<Integer> routeIDs = new ArrayList<>();
        routeIDs.addAll(routeIDSet);

        for (int routeID : routeIDs) {

            Route route = new Route();

            route.routeID = routeID;
            route.influenced = false;

            routes.add(route);
        }
    }

}

