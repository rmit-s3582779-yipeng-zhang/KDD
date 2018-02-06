package PartitionEnumGreedy;

import entity.Billboard;
import entity.Route;
import fileIO.FilePath;

import java.util.*;

/**
 * Created by marco on 23/07/2017.
 */
public class BillboardListDuplicator {

    private List<Billboard> billboardList;

    private List<Route> routes;  // use routeIndex1 to retrieve route object


    private Set<Integer> routeIDSet;
    private List<List<Integer>> routeIDsOfBillboards;   // { {routeID1, routeID2}, {routeID1, routeID2} }
    private List<List<Integer>> routeIndexesOfBillboards;   // { {routeIndex1, routeIndex2}, {routeIndex1, routeIndex2} }


    public BillboardListDuplicator(List<Billboard> billboardList) {

        this.billboardList = billboardList;
        routes = new ArrayList<>();

        routeIDSet = new TreeSet<>();
        routeIDsOfBillboards = new ArrayList<>();
        routeIndexesOfBillboards = new ArrayList<>();

        init(); // only init routeIDsOfBillboards
        newRoutes();
    }

    public List<Billboard> getBillboards(int name) {
        newRoutes();
        List<Billboard> duplicateBillboardList = new ArrayList<>();

        for (int i = 0; i < billboardList.size(); i++) {

            double jumpChance = Math.random();
            if (jumpChance < 0.05)
                continue;

            Billboard billboard = billboardList.get(i);
            Billboard duplicateBillboard = new Billboard();

            duplicateBillboard.panelID = billboard.panelID + name;
            duplicateBillboard.influence = billboard.influence;
            duplicateBillboard.charge = billboard.charge;
            duplicateBillboard.bestChoice = billboard.bestChoice;
            duplicateBillboard.influencePerCharge = billboard.influencePerCharge;

            List<Integer> routeIndexes = routeIndexesOfBillboards.get(i);

            for (int j = 0; j < routeIndexes.size(); j++) {

                int routeIndex = routeIndexes.get(j);
                Route route = routes.get(routeIndex);
                route.routeID -= name * 10000;
                duplicateBillboard.routes.add(route);
            }

            duplicateBillboardList.add(duplicateBillboard);
        }
        return duplicateBillboardList;
    }

    public List<Billboard> getBillboards(double budget) {
        List<Billboard> duplicateBillboardList = new ArrayList<>();

        for (int i = 0; i < billboardList.size(); i++) {

            if (billboardList.get(i).charge > budget)
                continue;

            Billboard billboard = billboardList.get(i);
            Billboard duplicateBillboard = new Billboard();

            duplicateBillboard.panelID = billboard.panelID;
            duplicateBillboard.influence = billboard.influence;
            duplicateBillboard.charge = billboard.charge;
            duplicateBillboard.bestChoice = billboard.bestChoice;
            duplicateBillboard.influencePerCharge = billboard.influencePerCharge;

            List<Integer> routeIndexes = routeIndexesOfBillboards.get(i);

            for (int j = 0; j < routeIndexes.size(); j++) {

                int routeIndex = routeIndexes.get(j);
                Route route = routes.get(routeIndex);
                //route.influenced=false;
                duplicateBillboard.routes.add(route);
            }

            duplicateBillboardList.add(duplicateBillboard);
        }
        return duplicateBillboardList;
    }


    public List<Billboard> getBillboards() {
        List<Billboard> duplicateBillboardList = new ArrayList<>();

        for (int i = 0; i < billboardList.size(); i++) {

            Billboard billboard = billboardList.get(i);
            Billboard duplicateBillboard = new Billboard();

            duplicateBillboard.panelID = billboard.panelID;
            duplicateBillboard.influence = billboard.influence;
            //duplicateBillboard.charge = billboard.charge;
            duplicateBillboard.charge = 0;
            duplicateBillboard.bestChoice = billboard.bestChoice;
            duplicateBillboard.influencePerCharge = billboard.influencePerCharge;

            List<Integer> routeIndexes = routeIndexesOfBillboards.get(i);

            for (int j = 0; j < routeIndexes.size(); j++) {

                int routeIndex = routeIndexes.get(j);
                Route route = routes.get(routeIndex);
                route.influenced = false;
                duplicateBillboard.routes.add(route);
            }

            duplicateBillboardList.add(duplicateBillboard);
        }
        return duplicateBillboardList;
    }


    // initialize panelIDs, weeklyImpressions, routeIDSet, routeIDsOfBillboards, routeIndexesOfBillboards

    private void init() {


        for (Billboard billboard : billboardList) {

            List<Integer> routeIDs = new ArrayList<>();

            for (Route route : billboard.routes) {

                routeIDs.add(route.routeID);
            }
            routeIDsOfBillboards.add(routeIDs);
        }

        setUpRouteIDsAndIndexes();
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
        routes = new ArrayList<Route>();
        for (int routeID : routeIDs) {

            try {
                Route route = new Route();

                route.routeID = routeID;
                route.influenced = false;

                routes.add(route);
            } catch (Exception e) {
            }

        }
    }

}
