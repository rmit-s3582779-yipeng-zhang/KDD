package CheckIn;

import java.util.ArrayList;

/**
 * Created by Lancer on 2017/7/9.
 */
public class Square {
    private int locationX;
    private int locationY;
    private double longitude;
    private double latitude;
    private int checkinNumber;
    private ArrayList<CheckinPoint> checkinList = new ArrayList<CheckinPoint>();

    public Square(int locationX,int locationY,double longitude,double latitude,int checkinNumber){
        this.locationX=locationX;
        this.locationY=locationY;
        this.longitude=longitude;
        this.latitude=latitude;
        this.checkinNumber=checkinNumber;
    }

    public void addCheckinPoint(CheckinPoint point){
        checkinList.add(point);
    }

    public void setCheckinPoint(ArrayList<CheckinPoint> checkinList){
        this.checkinList=checkinList;
    }

    public ArrayList<CheckinPoint> getCheckinList(){ return checkinList; }

    public int getHeat(){ return checkinNumber;}

}
