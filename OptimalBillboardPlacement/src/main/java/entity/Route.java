package entity;

import java.io.Serializable;

/**
 * Created by marco on 04/06/2017.
 */


// used to store info from billboardFinalResult.txt
// <-- routes & its billboards -->    (billboards that can influence this route)

public class Route implements Comparable<Route>,Serializable {


    private static final long serialVersionUID = 1916328504671498190L;


    public int routeID;

    public boolean influenced;


    public Route() {

    }

    public Route(int routeID) {
        this.routeID = routeID;
    }

    @Override
    public int compareTo(Route o) {

        return routeID - o.routeID;
    }



    //    public Set<Integer> billboardArraylistIndexes;


//    public Route() {
//
//        billboardArraylistIndexes = new TreeSet<>();
//    }
}
