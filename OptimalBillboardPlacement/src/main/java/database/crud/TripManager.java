package database.crud;

import database.DatabaseManager;
import database.tables.Trip;
import database.tables.TripType;

import java.util.ArrayList;

/**
 * Created by marco on 02/04/2017.
 */
public class TripManager {

    public static int insert(ArrayList<Trip> trips) {

        DatabaseManager databaseManager = new DatabaseManager();
        databaseManager.connect();

        // insert into database
        String insertStmt = constructInsertStmt(trips);
        int rowsInfected = databaseManager.executeInsert(insertStmt);

        databaseManager.close();
        return rowsInfected;
    }

    private static String constructInsertStmt(ArrayList<Trip> trips) {

        String insertStmt = "";
        TripType tripType = trips.get(0).tripType;

        if (tripType == TripType.greenTrip)
            insertStmt = "INSERT INTO greenTrip (id, PULongitude, PULatitude, DOLongitude, DOLatitude, PUTime, DOTime, distance) VALUES ";
        else if (tripType == TripType.yellowTrip)
            insertStmt = "INSERT INTO yellowTrip (id, PULongitude, PULatitude, DOLongitude, DOLatitude, PUTime, DOTime, distance) VALUES ";

        int numberOfRecord = trips.size();

        for (int i = 0; i < numberOfRecord; i++)
            insertStmt += constructInsertSegment(trips, i);

        return insertStmt;
    }

    private static String constructInsertSegment(ArrayList<Trip> trips, int recordIndex) {

        Trip trip = trips.get(recordIndex);

        String insertSegment = "";

        if (recordIndex < trips.size() - 1) {

            insertSegment = "(" + trip.id + ", " + trip.PULongitude.doubleValue() + ", " + trip.PULatitude.doubleValue()
                    + ", " + trip.DOLongitude.doubleValue() + ", " + trip.DOLatitude.doubleValue() + ", '" +
                    trip.PUTime.toString().substring(0,19) + "', '" + trip.DOTime.toString().substring(0,19) + "', "
                    + trip.distance.doubleValue() + "), ";

        } else if (recordIndex == trips.size() - 1) {

            insertSegment = "(" + trip.id + ", " + trip.PULongitude.doubleValue() + ", " + trip.PULatitude.doubleValue()
                    + ", " + trip.DOLongitude.doubleValue() + ", " + trip.DOLatitude.doubleValue() + ", '" +
                    trip.PUTime.toString().substring(0,19) + "', '" + trip.DOTime.toString().substring(0,19) + "', "
                    + trip.distance.doubleValue() + ");";
        }
        return insertSegment;
    }

//    public int insert(Trip[] trip);
//
//    public ResultSet query(String queryStmt);
//
//    public boolean update(Trip oldTrip, Trip newTrip);
//
//    public boolean delete(Trip trip);

}
