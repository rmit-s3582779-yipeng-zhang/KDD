package rTreeIndex;

import com.github.davidmoten.rtree.RTree;
import com.github.davidmoten.rtree.geometry.Geometries;
import com.github.davidmoten.rtree.geometry.Point;
import database.DatabaseManager;
import fileIO.FilePath;
import fileIO.RTree.Serialize;

import java.math.BigDecimal;
import java.sql.SQLException;

/**
 * Created by marco on 30/04/2017.
 */
public class NYIndexConstructor {

    public RTree<String, Point> rTree = RTree.star().create();

    public static void main(String[] args) {

        NYIndexConstructor constructor = new NYIndexConstructor();

        try {
            constructor.constructRTree();

        } catch (Exception e) {
            e.printStackTrace();
        }

        Serialize.serialize(constructor.rTree, FilePath.nycTripRTreePath);
    }


    private void constructRTree() throws SQLException{

        int startFrom = 280000;
        int number = 20000;          // 2w routes
        int round = number / 1000;
        int remains = number % 1000;

        DatabaseManager databaseManager = new DatabaseManager();
        databaseManager.connect();

        for(int i = 0; i < round; i++) {

            int lowerBound = 1000 * i + 1 + startFrom;
            int upperBound = 1000 * (i+1) + startFrom;

            databaseManager.executeQuery("select * from greenTripGoogleRouteCoor where routeID between " + lowerBound + " and " + upperBound + ";");

            while (databaseManager.resultSet.next()) {

                String id = "" + databaseManager.resultSet.getInt(1) + "~" + databaseManager.resultSet.getInt(2);
                BigDecimal longitude = databaseManager.resultSet.getBigDecimal(3);
                BigDecimal latitude = databaseManager.resultSet.getBigDecimal(4);

                rTree = rTree.add(id, Geometries.point(longitude.doubleValue(), latitude.doubleValue()));
            }

            System.out.println(lowerBound + " -> " + upperBound);
        }

        int lowerBound = number - remains + 1 + startFrom;
        int upperBound = number + startFrom;

        System.out.println(lowerBound + " -> " + upperBound);

        databaseManager.executeQuery("select * from greenTripGoogleRouteCoor where routeID between " + lowerBound + " and " + upperBound + ";");

        while (databaseManager.resultSet.next()) {

            String id = "" + databaseManager.resultSet.getInt(1) + "~" + databaseManager.resultSet.getInt(2);
            BigDecimal longitude = databaseManager.resultSet.getBigDecimal(3);
            BigDecimal latitude = databaseManager.resultSet.getBigDecimal(4);

            rTree = rTree.add(id, Geometries.point(longitude.doubleValue(), latitude.doubleValue()));
        }


        databaseManager.close();
    }

}
