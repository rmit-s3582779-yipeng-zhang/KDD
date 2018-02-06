package database.tables.green;

import database.tables.Trip;
import database.tables.TripType;

/**
 * Created by marco on 31/03/2017.
 */
public class GreenTrip extends Trip {

    public static long greenTripID = 1;

    public GreenTrip() {

        tripType = TripType.greenTrip;
    }
}
