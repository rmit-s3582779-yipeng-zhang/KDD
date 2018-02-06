package Analysis;

import entity.Billboard;

import java.util.List;

/**
 * Created by Lancer on 2017/8/21.
 */
public class BillboardAnalysis {

    public void influenceDistribution(List<List<Billboard>> billboardList){
        int[] influence = new int[100];

        for(List<Billboard> billboards:billboardList){
            for(Billboard billboard: billboards){
                influence[(int)Math.floor(billboard.influence/50.0)]++;
            }
        }

        for(int i=0;i<influence.length;i++){
            System.out.println("Influence " + i*50 + "~" +(i+1)*50 +" : " + influence[i]);
        }
    }
}
