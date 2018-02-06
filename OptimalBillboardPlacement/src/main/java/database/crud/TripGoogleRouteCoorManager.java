package database.crud;

import database.DatabaseManager;
import database.tables.TripGoogleRouteCoor;
import database.tables.TripType;

import java.util.ArrayList;

/**
 * Created by marco on 03/04/2017.
 */
public class TripGoogleRouteCoorManager {


    public static int insert(ArrayList<TripGoogleRouteCoor> googleRouteCoors) {

        DatabaseManager databaseManager = new DatabaseManager();
        databaseManager.connect();

        // insert into database
        String insertStmt = constructInsertStmt(googleRouteCoors);
        int rowsInfected = databaseManager.executeInsert(insertStmt);

        databaseManager.close();
        return rowsInfected;
    }

    private static String constructInsertStmt(ArrayList<TripGoogleRouteCoor> googleRoutesCoors) {

        String insertStmt = "";
        TripType tripType = googleRoutesCoors.get(0).tripType;

        if (tripType == TripType.greenTrip)
            insertStmt = "INSERT INTO greenTripGoogleRouteCoor (id, routeID, longitude, latitude) VALUES ";
        else if (tripType == TripType.yellowTrip)
            insertStmt = "INSERT INTO yellowTripGoogleRouteCoor (id, routeID, longitude, latitude) VALUES ";

        int numberOfRecord = googleRoutesCoors.size();

        for (int i = 0; i < numberOfRecord; i++)
            insertStmt += constructInsertSegment(googleRoutesCoors, i);

        return insertStmt;
    }

    private static String constructInsertSegment(ArrayList<TripGoogleRouteCoor> googleRoutesCoors, int recordIndex) {

        TripGoogleRouteCoor coor = googleRoutesCoors.get(recordIndex);

        String insertSegment = "";

        if (recordIndex < googleRoutesCoors.size() - 1) {

            insertSegment = "(" + coor.id + ", " + coor.routeID + ", " + coor.longitude.doubleValue() + ", " + coor.latitude.doubleValue() + "), ";

        } else if (recordIndex == googleRoutesCoors.size() - 1) {

            insertSegment = "(" + coor.id + ", " + coor.routeID + ", " + coor.longitude.doubleValue() + ", " + coor.latitude.doubleValue() + ");";
        }

        return insertSegment;
    }


//    public static ResultSet query(String queryStmt) {
//
//        return null;
//    }
//
//
//    public static boolean update(TripGoogleRouteCoor oldCoor, TripGoogleRouteCoor newCoor) {
//
//        return false;
//    }
//
//
//    public static boolean delete(TripGoogleRouteCoor googleRouteCoor) {
//
//        return false;
//    }

}
