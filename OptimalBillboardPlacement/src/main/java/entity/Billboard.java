package entity;

//import sun.tools.tree.DoubleExpression;

import java.io.Serializable;
import java.util.*;

/**
 * Created by marco on 18/05/2017.
 */


// used to store info from billboardFinalResult.txt
// <-- billboard & its routes -->     (routes that got influenced by this billboard)

public class Billboard implements Comparable<Billboard>,Cloneable,Serializable {


    private static final long serialVersionUID = 1514767291607422315L;

    public String panelID;  // panelID = billboardID

    public int influence;   // number of routes get influenced

    public double charge;  //  cost  weeklyImpression / 3000

    public double influencePerCharge;   // initialized as routes.size() / charge

    public Set<Route> routes;  // googleRouteID (used to store routes influenced by this billboard)

    public boolean bestChoice;

    public double longitude;

    public double lantitude;


    @Override
    public Object clone() throws CloneNotSupportedException {

        Billboard cloneBillboard = (Billboard) super.clone();
        cloneBillboard.panelID=this.panelID;
        cloneBillboard.charge=this.charge;
        cloneBillboard.influence=routes.size();
        cloneBillboard.influencePerCharge=routes.size()/charge;
        Set<Route> routes2= new TreeSet<>();
        for(Route route:this.routes){
            route.influenced=false;
            routes2.add(route);
        }
        cloneBillboard.routes=routes2;
        return cloneBillboard;
    }


    public Billboard() {

        routes = new TreeSet<>();
        this.bestChoice = false;
    }


    public void resetBillboard(){

        for (Route route : routes) {
            route.influenced=false;
        }

        influence = routes.size();
        influencePerCharge = influence / charge;
    }

    public void updateInfluence() {

        int counter = 0;

        for (Route route : routes) {
            if (!route.influenced) // if not influenced by already picked billboards
                counter++;         // add one influence
        }

        influence = counter;
        influencePerCharge = influence / charge;
    }


    @Override
    public String toString() {

        String result = "";

        result += "panelID : " + panelID + "\n";
        result += "influence : " + influence + "\n";
        result += "charge : " + charge + "\n";
        result += "influence per charge : " + influencePerCharge + "\n";
        result += "routes : ";

        for (Route route : routes)
            result += (route.routeID + ", ");

        return result;
    }


    // order by descending order (9 8 7 6 5 4 3 2 1)

    @Override
    public int compareTo(Billboard o) {

//        double difference =  o.influencePerCharge - influencePerCharge;
//
//        if (difference > 0)
//            return 1;
//        else if (difference < 0)
//            return -1;
//        else
//            return 0;

        Double mine = new Double(influencePerCharge);
        Double others = new Double(o.influencePerCharge);

        return -mine.compareTo(others);
    }
}
