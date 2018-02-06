package fileIO.RTree;

import com.github.davidmoten.rtree.RTree;
import com.github.davidmoten.rtree.Serializer;
import com.github.davidmoten.rtree.Serializers;
import com.github.davidmoten.rtree.geometry.Point;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * Created by marco on 30/04/2017.
 */
public class Serialize {


    public static void serialize(RTree rTree, String filePath) {

        try {

            FileOutputStream fileOutputStream = new FileOutputStream(filePath);
            OutputStream outputStream = new BufferedOutputStream(fileOutputStream);
            Serializer<String, Point> serializer = Serializers.flatBuffers().utf8();
            serializer.write(rTree, outputStream);
            outputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
