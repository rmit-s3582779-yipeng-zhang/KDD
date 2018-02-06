package entity;

import java.io.Serializable;
import java.util.*;

/**
 * Created by marco on 18/05/2017.
 */
public class BillboardSet implements Cloneable,Serializable {


    private static final long serialVersionUID = 1800117219585319555L;


    public int numberOfBillboards = 0;

    public List<String> billboards;    // panelID~charge

    public Set<Integer> routeIDs;       // googleRouteID

    public double cost = 0;


    public BillboardSet() {

        billboards = new ArrayList<>();
        routeIDs = new TreeSet<>();
    }


    public void add(Billboard billboard) {

        this.cost += billboard.charge;

        billboards.add(billboard.panelID + "~" + billboard.charge);

        for (Route route : billboard.routes)
            routeIDs.add(route.routeID);
        numberOfBillboards++;
    }


    public void addSet(BillboardSet set) {

        numberOfBillboards += set.numberOfBillboards;
        billboards.addAll(set.billboards);
        routeIDs.addAll(set.routeIDs);
    }




    @Override
    public Object clone() throws CloneNotSupportedException {

        BillboardSet cloneBillboardSet = (BillboardSet) super.clone();

        List<String> cloneBillboards = new ArrayList<>();
        Set<Integer> cloneRoutes = new TreeSet<>();

        try {
            for (String item : billboards)
                cloneBillboards.add(item);
            for (Integer item : routeIDs)
                cloneRoutes.add(item);
        } catch (Exception e) {
            e.printStackTrace();
        }

        cloneBillboardSet.billboards = cloneBillboards;
        cloneBillboardSet.routeIDs = cloneRoutes;

        return cloneBillboardSet;
    }


    @Override
    public String toString() {

        String result = "";
        result += "billboard id : ";

        for (String billboard : billboards)
            result += billboard.split("~")[0] + ", ";

        result += "\nbillboard cost : ";

        for (String billboard : billboards)
            result += billboard.split("~")[1] + ", ";

        result += "\nnumber of billboards: " + billboards.size();

        result += "\nnumber of routes get influenced: " + routeIDs.size();

        result += "\n-----------------------------------------------------";

        return result;
    }
}
