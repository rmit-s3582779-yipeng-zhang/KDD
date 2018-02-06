package fileIO.finalResult;

import configure.Developer;
import entity.Billboard;
import entity.Route;
import fileIO.FilePath;
import fileIO.MyFileReader;

import java.util.*;

/**
 * Created by marco on 18/07/2017.
 */
public class MultipleResultReader {


    public static final boolean readFromCombineBillboardFile = false;  //change 25.1

    private List<String> panelIDs = new ArrayList<>();
    private List<Integer> weeklyImpressions = new ArrayList<>();
    private Set<Integer> routeIDSet = new TreeSet<>();
    private List<List<Integer>> routeIDsOfBillboards = new ArrayList<>();   // { {routeID1, routeID2}, {routeID1, routeID2} }
    private List<List<Integer>> routeIndexesOfBillboards = new ArrayList<>();   // { {routeIndex1, routeIndex2}, {routeIndex1, routeIndex2} }


    private List<Route> routes = null;  // use routeIndex1 to retrieve route object
    private List<Billboard> billboards = null;

    private int combineResultFrom;
    private int combineResultTo;

    private static int chargeDensity = 1;
    private static double costCoefficient = 10;


    public MultipleResultReader(int combineResultFrom, int combineResultTo) {

        this.combineResultFrom = combineResultFrom;
        this.combineResultTo = combineResultTo;
        routes = new ArrayList<>();
        billboards = new ArrayList<>();

        init();
    }

    private double getCharge(double influence) {
        double charge;
        if (influence < 50)
            charge = 10;
        else if (influence < 100)
            charge = 20;
        else if (influence < 200)
            charge = 100;
        else if (influence < 300)
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
            //billboard.charge = (int) (Math.round(billboard.charge / 5)) * 5;

            List<Integer> routeIndexes = routeIndexesOfBillboards.get(i);

            if (routeIndexes.size() == 0)
                continue;

            for (int j = 0; j < routeIndexes.size(); j++) {

                int routeIndex = routeIndexes.get(j);
                Route route = routes.get(routeIndex);
                billboard.routes.add(route);
            }
            billboard.influence = billboard.routes.size();
            //billboard.charge = getCharge(billboard.influence);
            //billboard.charge = Math.pow(billboard.influence,(2.0/3.0));
            billboard.charge = (int) (Math.round(billboard.charge / chargeDensity)) * chargeDensity;
            billboard.charge = billboard.charge / costCoefficient;

            if (billboard.charge == 0 && billboard.routes.size() > 0)
                billboard.charge = chargeDensity;
            billboard.influencePerCharge = billboard.influence / billboard.charge;

            billboards.add(billboard);
        }

//        //remove those billboard whose influence is 0
//        Iterator<Billboard> iterator = billboards.iterator();
//        while (iterator.hasNext()) {
//
//            Billboard billboard = iterator.next();
//            if (billboard.influence == 0)
//                iterator.remove();
//        }

        return billboards;
    }


    // initialize panelIDs, weeklyImpressions, routeIDSet, routeIDsOfBillboards, routeIndexesOfBillboards

    private void init() {

        String baseFilePath = "";

        if (readFromCombineBillboardFile)
            baseFilePath = FilePath.billboardCombineResultPath;
        else
            baseFilePath = FilePath.billboardFinalResultPath;

        if (Developer.SYSTEM.equals("Win")) {
            baseFilePath = "\\" + baseFilePath.split("/")[1];
        }
        for (int i = combineResultFrom; i <= combineResultTo; i++) {

            String filePath;
            if (combineResultFrom == combineResultTo)
                filePath = baseFilePath.split(".txt")[0] + ".txt";
            else
                filePath = baseFilePath.split(".txt")[0] + i + ".txt";

            if (i == combineResultFrom)
                processOneCombineResultFile(filePath, true);
            else
                processOneCombineResultFile(filePath, false);
        }

        setUpRouteIDsAndIndexes();
    }


    private void processOneCombineResultFile(String filePath, boolean isFirstCalled) {

        MyFileReader finalResultReader = new MyFileReader(filePath);

        String line = "";

        int lineIndex = 0;

        while (true) {

            line = finalResultReader.getNextLine();
            if (line == null)
                break;

            String[] elements = line.split(" ");
//            if (elements.length == 1)
//                continue;    // skip those billboard which can not influence any route

            String panelID = elements[0].split("~")[0]; // panelID~weeklyImpression
            int weeklyImpression = Integer.parseInt(elements[0].split("~")[1]);

            if (weeklyImpression == 0)
                continue;    // skip those billboards which has no weeklyImpression

            List<Integer> routeIDs = new ArrayList<>();

            for (int i = 1; i < elements.length; i++) {

                int routeID = Integer.parseInt(elements[i]);
                routeIDs.add(routeID);
            }

            if (isFirstCalled) {
                panelIDs.add(panelID);
                weeklyImpressions.add(weeklyImpression);
                routeIDsOfBillboards.add(routeIDs);
            } else {
                routeIDsOfBillboards.get(lineIndex).addAll(routeIDs);
            }

            lineIndex++;
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
