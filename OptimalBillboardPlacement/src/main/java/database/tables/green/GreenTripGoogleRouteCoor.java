package database.tables.green;

import database.tables.TripGoogleRouteCoor;
import database.tables.TripType;

/**
 * Created by marco on 31/03/2017.
 */
public class GreenTripGoogleRouteCoor extends TripGoogleRouteCoor {

    public static long greenTripGoogleRouteCoorID = 1;

    public GreenTripGoogleRouteCoor() {

        tripType = TripType.greenTrip;
    }
}
