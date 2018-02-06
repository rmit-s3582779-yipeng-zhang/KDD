package fileIO;

import Knapsack.ClusterBillboard;
import configure.Developer;
import entity.Billboard;
import Knapsack.ClusterBillboard;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class Serialize {

    //private static String root="G:/JAVA/Optimal-Billboard-Placement/cache/";
    //private static String root="G:\\JAVA\\Optimal-Billboard-Placement\\cache2\\";
    private static String root = "";

    public Serialize() {
        root = System.getProperty("user.dir");
    }

    public static void main(String[] args) {
        ArrayList<ClusterBillboard> clusters = new ArrayList<ClusterBillboard>();
        for (int i = 0; i < 5; i++) {
            ClusterBillboard cluster = new ClusterBillboard();
            cluster.setCharge(i);
            cluster.setInfl(i * 5);
            clusters.add(cluster);
        }
        //serialize(clusters,"");
        //ArrayList<ClusterBillboard> newclusters = deserialize("");
        //System.out.print("done");
    }

    public void serialize(List<List<Billboard>> billboards, String fileName) {

        try {
            ObjectOutputStream bjectOutputStream = new ObjectOutputStream(
                    new FileOutputStream(fileName));
            bjectOutputStream.writeObject(billboards);
            bjectOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void serialize(ArrayList<ClusterBillboard> clusters, String fileName) {

        try {
            ObjectOutputStream bjectOutputStream = new ObjectOutputStream(
                    new FileOutputStream(root + fileName));
            bjectOutputStream.writeObject(clusters);
            bjectOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public ArrayList<ClusterBillboard> deserialize(String fileName) {

        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(
                    root + fileName));
            ArrayList<ClusterBillboard> clusters = (ArrayList<ClusterBillboard>) objectInputStream.readObject();
            objectInputStream.close();
            return clusters;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void serializeUpperBoundMatrix(ArrayList<ArrayList<Double>> upperBoundMatrix, String fileName) {

        try {
            ObjectOutputStream bjectOutputStream = new ObjectOutputStream(
                    new FileOutputStream(root + fileName));
            bjectOutputStream.writeObject(upperBoundMatrix);
            bjectOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<ArrayList<Double>> deserializeUpperBoundMatrix(String fileName) {

        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(
                    root + fileName));
            ArrayList<ArrayList<Double>> upperBoundMatrix = (ArrayList<ArrayList<Double>>) objectInputStream.readObject();// 从流中读取User的数据
            objectInputStream.close();
            return upperBoundMatrix;
        } catch (FileNotFoundException e) {
            //System.out.println("File " + fileName + " doesn't exist.");
        } catch (IOException e) {
            //System.out.println("File " + fileName + " cannot read.");
        } catch (ClassNotFoundException e) {
            //System.out.println("File " + fileName + " cannot convert to object.");
        }
        return null;
    }

    public void serializeBillboard(List<List<Billboard>> billboardList, String fileName) {
        try {
            ObjectOutputStream ojectOutputStream = null;
            if (Developer.SYSTEM.equals("Mac"))
                ojectOutputStream = new ObjectOutputStream(new FileOutputStream("./" + fileName));

            if (Developer.SYSTEM.equals("Win"))
                ojectOutputStream = new ObjectOutputStream(new FileOutputStream(root + "\\" + fileName));

            if (Developer.SYSTEM.equals("Linux"))
                ojectOutputStream = new ObjectOutputStream(new FileOutputStream(root + "/" + fileName));

            ojectOutputStream.writeObject(billboardList);
            ojectOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<List<Billboard>> deserializeBillboard(String fileName) {
        try {
            ObjectInputStream objectInputStream = null;
            if (Developer.SYSTEM.equals("Mac"))
                objectInputStream = new ObjectInputStream(new FileInputStream("./" + fileName));

            if (Developer.SYSTEM.equals("Win"))
                objectInputStream = new ObjectInputStream(new FileInputStream(root + "\\" + fileName));

            if (Developer.SYSTEM.equals("Linux"))
                objectInputStream = new ObjectInputStream(new FileInputStream(root + "/" + fileName));

            List<List<Billboard>> billboardList = (List<List<Billboard>>) objectInputStream.readObject();
            objectInputStream.close();
            return billboardList;
        } catch (FileNotFoundException e) {
            //System.out.println("File " + fileName + " doesn't exist.");
        } catch (IOException e) {
            //System.out.println("File " + fileName + " cannot read.");
        } catch (ClassNotFoundException e) {
            //System.out.println("File " + fileName + " cannot convert to object.");
        }
        return  null;
    }

    public void serializeMatrix(ArrayList<ArrayList<ClusterBillboard>> clusterMatrix, String fileName) {

        try {
            ObjectOutputStream ojectOutputStream = new ObjectOutputStream(
                    new FileOutputStream(root + fileName));
            ojectOutputStream.writeObject(clusterMatrix);
            ojectOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<ArrayList<ClusterBillboard>> deserializeMatrix(String fileName) {

        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(
                    root + fileName));
            ArrayList<ArrayList<ClusterBillboard>> clusters = (ArrayList<ArrayList<ClusterBillboard>>) objectInputStream.readObject();// 从流中读取User的数据
            objectInputStream.close();
            return clusters;
        } catch (FileNotFoundException e) {
            //System.out.println("File " + fileName + " doesn't exist.");
        } catch (IOException e) {
            //System.out.println("File " + fileName + " cannot read.");
        } catch (ClassNotFoundException e) {
            //System.out.println("File " + fileName + " cannot convert to object.");
        }
        return null;
    }

}
