package CheckIn;

import com.github.davidmoten.rtree.RTree;
import com.github.davidmoten.rtree.geometry.Geometries;
import com.github.davidmoten.rtree.geometry.Point;
import database.DatabaseManager;

import java.math.BigDecimal;
import java.sql.SQLException;

/**
 * Created by Lancer on 2017/7/9.
 */


public class GenerateCheckinRTree {

    public RTree<String, Point> rTree = RTree.star().create();

    private double longitude1;
    private double latitude1;
    private double longitude2;
    private double latitude2;
    private String mapName;

    public RTree<String, Point> getRTree(){
        return rTree;
    }

    public GenerateCheckinRTree(){
        try{
            this.longitude1=Unit.longitude1;
            this.latitude1=Unit.latitude1;
            this.longitude2=Unit.longitude2;
            this.latitude2=Unit.latitude2;
            this.mapName=Unit.mapName;
            constructRTree();
            //Serialize.serialize(rTree, mapName);
        }catch (SQLException e){
            e.printStackTrace();
        }catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void constructRTree() throws SQLException{

        int maxNumber=0;
        int number = 180000;
        int round = number / 1000;
        int remains = number % 1000;

        DatabaseManager databaseManager = new DatabaseManager();
        databaseManager.connect();

        databaseManager.executeQuery("select count(*) from checkin where "
                +"(Longitude between "+longitude1+" and "+longitude2+") and (latitude between "+latitude1+" and "+latitude2+");");
        while (databaseManager.resultSet.next()) {
            maxNumber=databaseManager.resultSet.getInt(1);
        }

        if(number > maxNumber){
            number = maxNumber;
            round = number / 1000;
            remains = number % 1000;
        }
        for(int i = 0; i < round; i++) {

            int lowerBound = 1000 * i;
            int upperBound = 1000 * (i+1) - 1;

            databaseManager.executeQuery("select ID,Longitude,Latitude from checkin where "
                    +"(Longitude between "+longitude1+" and "+longitude2+") and (latitude between "+latitude1+" and "+latitude2+")"
                    +" limit "+lowerBound+","+upperBound+"");

            while (databaseManager.resultSet.next()) {

                String id = "" + databaseManager.resultSet.getInt(1);
                BigDecimal longitude = databaseManager.resultSet.getBigDecimal(2);
                BigDecimal latitude = databaseManager.resultSet.getBigDecimal(3);

                rTree = rTree.add(id, Geometries.point(longitude.doubleValue(), latitude.doubleValue()));
            }

            System.out.println(lowerBound + " -> " + upperBound);
        }

        int lowerBound = number - remains;
        int upperBound = number - 1;

        System.out.println(lowerBound + " -> " + upperBound);

        databaseManager.executeQuery("select ID,Longitude,Latitude from checkin where "
                +"(Longitude between "+longitude1+" and "+longitude2+") and (latitude between "+latitude1+" and "+latitude2+")"
                +" limit "+lowerBound+","+upperBound+"");

        while (databaseManager.resultSet.next()) {

            String id = "" + databaseManager.resultSet.getInt(1);
            BigDecimal longitude = databaseManager.resultSet.getBigDecimal(2);
            BigDecimal latitude = databaseManager.resultSet.getBigDecimal(3);

            rTree = rTree.add(id, Geometries.point(longitude.doubleValue(), latitude.doubleValue()));
        }

        databaseManager.close();
    }
}
