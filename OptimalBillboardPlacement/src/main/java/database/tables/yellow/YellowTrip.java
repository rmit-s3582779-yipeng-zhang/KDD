package database.tables.yellow;

import database.tables.Trip;
import database.tables.TripType;

/**
 * Created by marco on 31/03/2017.
 */
public class YellowTrip extends Trip{

    public static long yellowTripID = 1;

    public YellowTrip() {

        tripType = TripType.yellowTrip;
    }
}
