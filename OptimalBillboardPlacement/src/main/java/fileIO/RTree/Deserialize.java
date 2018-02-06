package fileIO.RTree;

import com.github.davidmoten.rtree.InternalStructure;
import com.github.davidmoten.rtree.RTree;
import com.github.davidmoten.rtree.Serializer;
import com.github.davidmoten.rtree.Serializers;
import com.github.davidmoten.rtree.geometry.Point;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * Created by marco on 30/04/2017.
 */
public class Deserialize {


    public static RTree deserialize(String filePath, long numberOfBytes) {

        RTree<String, Point> rTree = null;

        try {

            FileInputStream fileInputStream = new FileInputStream(filePath);
            InputStream inputStream = new BufferedInputStream(fileInputStream);
            Serializer<String, Point> serializer = Serializers.flatBuffers().utf8();
            rTree = serializer.read(inputStream, numberOfBytes, InternalStructure.DEFAULT);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return rTree;
    }

}
