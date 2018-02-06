package CheckIn;

/**
 * Created by Lancer on 2017/7/9.
 */
public class CheckinPoint {

    public double longitude;
    public double latitude;
    private int checkinNumber;

    public CheckinPoint(double longitude,double latitude,int checkinNumber){
        this.longitude=longitude;
        this.latitude=latitude;
        this.checkinNumber=checkinNumber;
    }

    public CheckinPoint(double longitude,double latitude){
        this.longitude=longitude;
        this.latitude=latitude;
    }

    public void addCheckinNumber(){
        this.checkinNumber++;
    }
}
