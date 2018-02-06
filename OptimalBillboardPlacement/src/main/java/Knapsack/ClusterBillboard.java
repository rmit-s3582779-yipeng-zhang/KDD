package Knapsack;

import entity.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class ClusterBillboard implements Serializable {

    private double charge;
    private int influence;
    private Set<Route> routes = new TreeSet<>();

    public List<Billboard> getBillboardList() {
        return billboardList;
    }

    private double influencePerCharge;
    private List<Billboard> billboardList = new ArrayList<>();

    //private BillboardSet billboardSet=new BillboardSet();

    public void add(ClusterBillboard clusterBillboard) {

        this.charge += clusterBillboard.charge;
        this.influence += clusterBillboard.influence;
        //this.billboardSet.addSet((BillboardSet) clusterBillboard.billboardSet.clone());
        this.billboardList.addAll(clusterBillboard.billboardList);
        this.influencePerCharge = influence / charge;
    }

    public void updateInfluence() {
        this.charge = 0;
        this.influence = 0;
        for (Billboard billboard : billboardList) {
            billboard.resetBillboard();
        }
        for (Billboard billboard : billboardList) {
            this.charge += billboard.charge;
            billboard.updateInfluence();
            this.influence += billboard.influence;
            for (Route route : billboard.routes) {
                route.influenced = true;
            }
        }
        this.influencePerCharge = influence / charge;
    }

    public double getInfluencePerCharge() {
        return this.influencePerCharge;
    }

    public void set(ClusterBillboard clusterBillboard) {
        this.influence = clusterBillboard.influence;
        this.charge = clusterBillboard.charge;
        this.influencePerCharge = clusterBillboard.influencePerCharge;
        this.billboardList = clusterBillboard.billboardList;
        this.routes = clusterBillboard.routes;
    }

    public ClusterBillboard get() {
        return this;
    }

    public double getCharge() {
        return charge;
    }

    public int getInfluence() {
        return influence;
    }

    public void setCharge(double Charge) {
        this.charge = Charge;
    }

    public void setInfl(int influence) {
        this.influence = influence;
    }

    public Set<Route> getRoutes() {
        return routes;
    }

    public ClusterBillboard(BillboardSet billboardSet, double charge, int influence) {
        //this.billboardSet=billboardSet;
        this.charge = charge;
        this.influence = influence;
    }

    public ClusterBillboard(Billboard billboard) {
        //this.billboardSet.add(billboard);
        this.charge = billboard.charge;
        this.influence = billboard.influence;
        this.billboardList.add(billboard);
        this.influencePerCharge = influence / charge;
        this.routes.addAll(billboard.routes);
    }

    public ClusterBillboard() {
        this.charge = 0;
        this.influence = 0;
        this.influencePerCharge = 0;
    }

    public void add(Billboard billboard) {
        this.charge += billboard.charge;
        this.influence += billboard.influence;
        this.billboardList.add(billboard);
        this.influencePerCharge = influence / charge;
        this.routes.addAll(billboard.routes);
    }

    public void printInfl() {
        System.out.print("(");
        System.out.print(this.influence + ",");
        System.out.print(")");
    }

}
