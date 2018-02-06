package database.crud;

import database.DatabaseManager;
import database.tables.TripGoogleRoute;
import database.tables.TripType;

import java.util.ArrayList;

/**
 * Created by marco on 02/04/2017.
 */
public class TripGoogleRouteManager {

    public static int insert(ArrayList<TripGoogleRoute> googleRoutes) {

        DatabaseManager databaseManager = new DatabaseManager();
        databaseManager.connect();

        // insert into database
        String insertStmt = constructInsertStmt(googleRoutes);
        int rowsInfected = databaseManager.executeInsert(insertStmt);

        databaseManager.close();
        return rowsInfected;
    }

    private static String constructInsertStmt(ArrayList<TripGoogleRoute> googleRoutes) {

        String insertStmt = "";
        TripType tripType = googleRoutes.get(0).tripType;

        if (tripType == TripType.greenTrip)
            insertStmt = "INSERT INTO greenTripGoogleRoute (id, tripID, distance, duration, numOfCoordinate, granularity) VALUES ";
        else if (tripType == TripType.yellowTrip)
            insertStmt = "INSERT INTO yellowTripGoogleRoute (id, tripID, distance, duration, numOfCoordinate, granularity) VALUES ";

        int numberOfRecord = googleRoutes.size();

        for (int i = 0; i < numberOfRecord; i++)
            insertStmt += constructInsertSegment(googleRoutes, i);

        return insertStmt;
    }

    private static String constructInsertSegment(ArrayList<TripGoogleRoute> googleRoutes, int recordIndex) {

        TripGoogleRoute route = googleRoutes.get(recordIndex);

        String insertSegment = "";

        if (recordIndex < googleRoutes.size() - 1) {

            insertSegment = "(" + route.id + ", " + route.tripID + ", " + route.distance + ", " + route.duration + ", "
                    + route.numberOfCoordinate + ", " + route.granularity.doubleValue() + "), ";

        } else if (recordIndex == googleRoutes.size() - 1) {

            insertSegment = "(" + route.id + ", " + route.tripID + ", " + route.distance + ", " + route.duration + ", "
                    + route.numberOfCoordinate + ", " + route.granularity.doubleValue() + ");";
        }

        return insertSegment;
    }

//    public static ResultSet query(String queryStmt) {
//
//        return null;
//    }
//
//
//    public static boolean update(TripGoogleRoute oldRoute, TripGoogleRoute newRoute) {
//
//        return false;
//    }
//
//
//    public static boolean delete(TripGoogleRoute googleRoute) {
//
//        return false;
//    }

}
