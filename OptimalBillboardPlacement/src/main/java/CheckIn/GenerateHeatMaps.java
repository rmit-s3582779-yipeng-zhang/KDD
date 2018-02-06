package CheckIn;

import com.github.davidmoten.rtree.Entry;
import com.github.davidmoten.rtree.RTree;
import com.github.davidmoten.rtree.geometry.Geometries;
import com.github.davidmoten.rtree.geometry.Point;
import fileIO.RTree.Deserialize;
import rx.Observable;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Lancer on 2017/7/9.
 */
public class GenerateHeatMaps {

    private double longitude1;
    private double latitude1;
    private double longitude2;
    private double latitude2 ;
    private double unitLo;
    private double unitLa;
    private int longitudeNumber;
    private int latitudeNumber;
    private double squareLength;
    private double numberOfPoint = 0;
    private ArrayList<ArrayList<Square>> squareMatrix;

    private RTree<String, Point> rTree;

    public GenerateHeatMaps(){

        this.rTree = Deserialize.deserialize(Unit.mapName, 471657548);
        this.longitude1=Unit.longitude1;
        this.latitude1=Unit.latitude1;
        this.longitude2=Unit.longitude2;
        this.latitude2=Unit.latitude2;
        this.squareLength=Unit.squareLength;
        this.unitLo=Unit.unitLo;
        this.unitLa=Unit.unitLa;

    }

    public GenerateHeatMaps(RTree<String, Point> rTree){
        this.rTree=rTree;
        this.longitude1=Unit.longitude1;
        this.latitude1=Unit.latitude1;
        this.longitude2=Unit.longitude2;
        this.latitude2=Unit.latitude2;
        this.squareLength=Unit.squareLength;
        this.unitLo=Unit.unitLo;
        this.unitLa=Unit.unitLa;
    }

    public ArrayList<ArrayList<Square>> getMatrix(){
        generateMatrix();
        System.out.println("numberOfPoint : "+numberOfPoint);
        return squareMatrix;
    }

    private void generateMatrix(){

        longitudeNumber=Math.abs((int)((longitude1-longitude2)/unitLo/squareLength));
        latitudeNumber=Math.abs((int)((latitude1-latitude2)/unitLa/squareLength));
        squareMatrix=new ArrayList<ArrayList<Square>>(longitudeNumber);

        for(int y=0;y<latitudeNumber;y++){
            ArrayList<Square> squareMatrixRow = new ArrayList<Square>(longitudeNumber);
            for(int x=0;x<longitudeNumber;x++){
                Square squre = getHeat(x,y);
                if(squre!=null)
                    squareMatrixRow.add(getHeat(x,y));
            }
            if(squareMatrixRow.size()>0)
                squareMatrix.add(squareMatrixRow);
        }
    }

    private Square getHeat(int x, int y){
        int heat = 0;
        double longitude1 = this.longitude1+((double)x)*unitLo*squareLength;
        double latitude1 = this.latitude1+((double)y)*unitLa*squareLength;
        double longitude2 = longitude1 + unitLo*squareLength;
        double latitude2 = latitude1 + unitLa*squareLength;
        ArrayList<CheckinPoint> checkinList = new ArrayList<CheckinPoint>();

        Observable<Entry<String, Point>> results = rTree.search(Geometries.rectangle(longitude1,latitude1,longitude2,latitude2));
        //Observable<Entry<String, Point>> results = rTree.search(Geometries.rectangle(this.longitude1,this.latitude1,this.longitude2,this.latitude2));
        Iterable<Entry<String, Point>> resultsIterable = results.toBlocking().toIterable();
        Iterator<Entry<String, Point>> checkinIterator = resultsIterable.iterator();

        while (checkinIterator.hasNext()) {

            Entry<String, Point> checkin = checkinIterator.next();
            Point checkinLocation = checkin.geometry();
            checkinList.add(new CheckinPoint(checkinLocation.x(),checkinLocation.y()));

            heat++;
            numberOfPoint++;
        }

        if(heat>0){
            Square square = new Square(x,y,(longitude1+longitude2)/2, (latitude1+latitude2)/2,heat);
            square.setCheckinPoint(checkinList);
            return square;
        }
        else
            return null;
    }

}
