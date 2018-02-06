package database.tables;

import java.math.BigDecimal;

/**
 * Created by marco on 02/04/2017.
 */
public class TripGoogleRoute {

    public long id;

    public long tripID;

    public long distance;

    public long duration;

    public int numberOfCoordinate;

    public BigDecimal granularity;

    public TripType tripType;
}
