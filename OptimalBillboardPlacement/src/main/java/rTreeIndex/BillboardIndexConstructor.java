package rTreeIndex;

import com.github.davidmoten.rtree.RTree;
import com.github.davidmoten.rtree.geometry.Geometries;
import com.github.davidmoten.rtree.geometry.Point;
import fileIO.FilePath;
import fileIO.MyFileReader;
import fileIO.RTree.Serialize;

/**
 * Created by marco on 30/04/2017.
 */
public class BillboardIndexConstructor {

    public RTree<String, Point> rTree = RTree.create();
    private static String root;


    public static void main(String[] args) {

        root = System.getProperty("user.dir");

        BillboardIndexConstructor constructor = new BillboardIndexConstructor();
        constructor.constructRTree();

        Serialize.serialize(constructor.rTree, FilePath.billboardRTreePath);
    }


    private void constructRTree() {

        MyFileReader myFileReader = new MyFileReader(FilePath.billboardFilePath);
        String line = myFileReader.getNextLine();

        while (line != null) {

            String[] elements = line.split(" ");
            String id = elements[0];
            double longitude = Double.parseDouble(elements[1]);
            double latitude = Double.parseDouble(elements[2]);

            rTree = rTree.add(id, Geometries.point(longitude, latitude));

            line = myFileReader.getNextLine();
        }
    }


}
