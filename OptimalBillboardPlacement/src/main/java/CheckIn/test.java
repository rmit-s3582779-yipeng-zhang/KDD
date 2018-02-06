package CheckIn;

import java.util.ArrayList;

/**
 * Created by Lancer on 2017/7/9.
 */
public class test {

    public static void main(String[] args) {

        //GenerateCheckinRTree constructor = new GenerateCheckinRTree();
        //GenerateHeatMaps generateHeatMaps=new GenerateHeatMaps( constructor.getRTree());//rTree from constructor
        GenerateHeatMaps generateHeatMaps=new GenerateHeatMaps();//rTree from file
        ArrayList<ArrayList<Square>> squareMatrix = generateHeatMaps.getMatrix();
        GenerateTrip generateTrip = new GenerateTrip(squareMatrix);
        generateTrip.generateTrip();
    }
}
