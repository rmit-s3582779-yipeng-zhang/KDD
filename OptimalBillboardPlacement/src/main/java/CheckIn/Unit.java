package CheckIn;

/**
 * Created by Lancer on 2017/7/9.
 */
public class Unit {

    //the region of the rtree
    //top left
    public static final double longitude1 = -118.695520;
    public static final double latitude1 = 33.638259;
    //bottom right
    public static final double longitude2 = -117.040937;
    public static final double latitude2 = 34.324893 ;

    public static final double unitLo = 0.000008933562500; // Longitude per meter
    public static final double unitLa = 0.0000100421892857; // latitude per meter
    public static final double squareLength = 100.0;//the length of the square

    public static final String mapName = "./los angeles.rtree";
}
