package database.tables.yellow;

import database.tables.TripGoogleRouteCoor;
import database.tables.TripType;

/**
 * Created by marco on 31/03/2017.
 */
public class YellowTripGoogleRouteCoor extends TripGoogleRouteCoor{

    public static long yellowTripGoogleRouteCoorID = 1;

    public YellowTripGoogleRouteCoor() {

        tripType = TripType.yellowTrip;
    }
}
