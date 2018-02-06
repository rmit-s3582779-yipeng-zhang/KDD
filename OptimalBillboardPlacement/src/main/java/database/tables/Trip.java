package database.tables;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Created by marco on 02/04/2017.
 */
public class Trip {

    public long id;

    public BigDecimal PULongitude;

    public BigDecimal PULatitude;

    public BigDecimal DOLongitude;

    public BigDecimal DOLatitude;

    public Timestamp PUTime;

    public Timestamp DOTime;

    public BigDecimal distance;

    public TripType tripType;

}
