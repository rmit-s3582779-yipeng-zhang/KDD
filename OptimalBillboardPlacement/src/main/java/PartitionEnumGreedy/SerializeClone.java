package PartitionEnumGreedy;

import entity.Billboard;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lancer on 2017/10/20.
 */
public class SerializeClone {
    private ByteArrayOutputStream out = new ByteArrayOutputStream();

    public void clone (List<Billboard> billboardList){
        try {
            ObjectOutputStream obs = new ObjectOutputStream(out);
            obs.writeObject(billboardList);
            obs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Billboard> getClone (){
        List<Billboard> billboardList = new ArrayList<Billboard>();
        try {
            ByteArrayInputStream ios = new ByteArrayInputStream(out.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(ios);
            billboardList = (List<Billboard>) ois.readObject();
            ois.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return billboardList;
    }
}
