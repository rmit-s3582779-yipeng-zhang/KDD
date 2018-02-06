package database.tables.yellow;

import database.DatabaseManager;
import database.tables.TripGoogleRoute;
import database.tables.TripType;

/**
 * Created by marco on 31/03/2017.
 */
public class YellowTripGoogleRoute extends TripGoogleRoute{

    public static long yellowTripGoogleRouteID = 1;

    public YellowTripGoogleRoute() {

        tripType = TripType.yellowTrip;
    }

    public static void updatePrimaryKeyID() {

        DatabaseManager databaseManager = new DatabaseManager();
        databaseManager.connect();

        // 4 TODO update id and do the same for other tables ids
        databaseManager.executeQuery("");
    }
}
