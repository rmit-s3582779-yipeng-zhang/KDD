package CheckIn;

import database.DatabaseManager;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.sql.Timestamp;

/**
 * Created by Lancer on 2017/7/9.
 */
public class GenerateTrip {

    private double unitLo; // Longitude per meter
    private double unitLa; // latitude per meter
    private String SQL;
    private ArrayList<ArrayList<Square>> squareMatrix;
    private ArrayList<Double> SQLParameter = new ArrayList<Double>(2000);//insert into DB when SQL = 2000/5

    private Random random=new Random();
    private DatabaseManager DBM;
    private int number=0;

    private int round = 1;//how many time you want to generate trips

    public GenerateTrip(ArrayList<ArrayList<Square>> squareMatrix) {
        this.squareMatrix=squareMatrix;
        this.unitLo=Unit.unitLo;
        this.unitLa=Unit.unitLa;
        DBM=new DatabaseManager();
        DBM.connect("LA");
    }

    public void generateTrip(){
        Date date1 = new Date();
        System.out.println("squareMatrix.size : "+squareMatrix.size());
        int matrixnumber=0;
        for(int i = 0;i < round; i++){
            for(ArrayList<Square> matrixRow : squareMatrix){
                System.out.println("matrixnumber : " + matrixnumber++);
                System.out.println(number);
                for(Square square: matrixRow){
                    for(CheckinPoint point: square.getCheckinList()){
                        if(random.nextDouble()<0.003)
                            distribution(point.longitude,point.latitude);
                    }
                }
            }
        }

        Date date2 = new Date();
        long begin = date1.getTime();
        long end = date2.getTime();
        System.out.println("runtime: " + ((end - begin)/1000d));

        if(SQLParameter.size()>0)
            insertDB(SQLParameter);
    }

    private void randomTrip(double distance,double longitude1, double latitude1){
        double r = random.nextDouble() * 1000.0 + distance - 1000.0;// r = distance ~ distance - 1km
        double x = random.nextDouble() * r * (random.nextBoolean() ? 1 : -1);// x = -r ~ r
        double y = Math.sqrt( r * r - x * x ) * (random.nextBoolean() ? 1 : -1);
        double longitude2 = longitude1 + x * unitLo;
        double latitude2 = latitude1 + y * unitLa;
        generateParameter(longitude1,latitude1,longitude2,latitude2,r/1000.0);
    }

    private void generateParameter(double longitude1, double latitude1, double longitude2, double latitude2, double distance){
        SQLParameter.add(longitude1);
        SQLParameter.add(latitude1);
        SQLParameter.add(longitude2);
        SQLParameter.add(latitude2);
        SQLParameter.add(distance);
        if(SQLParameter.size()>=2000){
            insertDB(SQLParameter);
            SQLParameter = new ArrayList<Double>(2000);
        }
    }

    private void insertDB(ArrayList<Double> SQLParameter){
        SQL="insert into trip (PULongitude, PULatitude, DOLongitude, DOLatitude, PUTime, DOTime, distance) VALUES ";
        Date date = new Date();
        Timestamp time= new Timestamp(date.getTime());
        for(int i=0;i<SQLParameter.size();i+=5){
            SQL+="("+SQLParameter.get(i)+","+SQLParameter.get(i+1)+","+SQLParameter.get(i+2)+","+SQLParameter.get(i+3)+",'"+time+"','"+time+"',"+SQLParameter.get(i+4)+"),";
        }
        SQL=SQL.substring(0,SQL.length()-1);
        this.number += DBM.executeInsert(SQL);
    }

    private void distribution(double longitude, double latitude){

        ArrayList<DistributionParameter> list = new ArrayList<DistributionParameter>();

        list.add(new DistributionParameter(1000,1));
        list.add(new DistributionParameter(2000,5));
        list.add(new DistributionParameter(3000,5));
        list.add(new DistributionParameter(4000,4));
        list.add(new DistributionParameter(5000,3));
        for(DistributionParameter parameter: list){
            for(int i=0;i<parameter.repeat;i++){
                randomTrip(parameter.distance,longitude,latitude);
            }
        }

    }

    public class DistributionParameter{
        public int distance;
        public int repeat;
        DistributionParameter(int distance,int repeat){
            this.distance=distance;
            this.repeat=repeat;
        }
    }
}


