package Annealing;

import Knapsack.ClusterBillboard;
import PartitionEnumGreedy.GenerateCandidateSubThread;
import configure.Developer;
import entity.Billboard;
import fileIO.MyFileWriter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lancer on 2017/8/15.
 */
public class Annealing {

    private double budget;
    private List<Billboard> billboardList;
    private int threadNumber = 40;
    private MyFileWriter fileWriter;

    public Annealing(List<Billboard> billboardList,double budget){
        this.billboardList=billboardList;
        this.budget=budget;
        String root = System.getProperty("user.dir");
        if (Developer.SYSTEM.equals("Win"))
            fileWriter = new MyFileWriter(root + "\\AnnealResult.txt");
        else if (Developer.SYSTEM.equals("Linux"))
            fileWriter = new MyFileWriter(root + "/AnnealResult.txt");
    }

    public ArrayList<ClusterBillboard> getResult() {
        ArrayList<Thread> threadPool = new ArrayList<>();
        ArrayList<ClusterBillboard> result = new ArrayList<>((int) budget);
        for (int i = 0; i < budget; i++) {
            result.add(new ClusterBillboard());
        }
        for(int i=1; i<=budget;i++){
            threadPool.add(new AnnealingSub(billboardList, budget, result.get(i-1),fileWriter));
        }
        try{
            for (int i = 0; i < threadPool.size(); i += threadNumber) {
                for (int t = i; t < i + threadNumber && t < threadPool.size(); t++) {
                    threadPool.get(t).start();
                }
                for (int t = i; t < i + threadNumber && t < threadPool.size(); t++) {
                    threadPool.get(i).join();
                }
            }
        } catch (Exception e) {
            System.out.println("Tread Error!");
        }

        return result;
    }
}
