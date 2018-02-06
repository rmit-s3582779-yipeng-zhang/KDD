package database.tables.green;

import database.tables.TripGoogleRoute;
import database.tables.TripType;

/**
 * Created by marco on 31/03/2017.
 */
public class GreenTripGoogleRoute extends TripGoogleRoute{

    public static long greenTripGoogleRouteID = 1;

    public GreenTripGoogleRoute() {

        tripType = TripType.greenTrip;
    }
}
