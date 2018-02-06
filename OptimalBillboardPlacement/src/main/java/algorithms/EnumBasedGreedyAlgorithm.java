
package algorithms;

import PartitionEnumGreedy.BillboardListDuplicator;
import configure.Developer;
import entity.Billboard;
import entity.BillboardSet;
import entity.Route;
import fileIO.MyFileWriter;
import fileIO.Serialize;

import java.util.*;

/**
 * Created by marco on 18/05/2017.
 */
public class EnumBasedGreedyAlgorithm {

    public static final int K = 2;
    public static double BUDGET = 150;
    private static int trajectoryNumber = 40;
    private static int threadNumber = 4;
    private static int chargeDensity = 1;
    private static double costCoefficient = 10;
    private static int miniBillboardNumber = 75;
    private static double pow = 1;
    private static int multipleBillboardNumber = 2;

    private static boolean randomCost = false;// generate cost randomly
    private static boolean ifAddBillboardRandom = true;//add billboards randomly
    private static Region region[] = new Region[7];
    private static boolean readNYC = true;
    private static boolean readLA = false;

    private List<List<Billboard>> billboardLists = null;
    private MyFileWriter fileWriter;
    private List<Billboard> allBillboards;

    public static void main(String[] args) {
        //EnumBasedGreedyAlgorithm enumBasedGreedyAlgorithm = new EnumBasedGreedyAlgorithm(BUDGET);
        //enumBasedGreedyAlgorithm.getPhaseTwoMaxInfluence();
    }

    public EnumBasedGreedyAlgorithm(List<Billboard> billboardLists, double budget) {
        region[0] = new Region(-73.9859676361084, 40.780021490225984, -73.92365455627441, 40.809521739444506);
        region[1] = new Region(-73.94906044006348, 40.83342421616831, -73.78211975097656, 40.88860081193034);
        region[2] = new Region(-74.00287628173828, 40.57067539946112, -73.8888931274414, 40.612388698663665);
        region[3] = new Region(-74.0174674987793, 40.668399962792876, -73.97283554077148, 40.701984159668676);
        region[4] = new Region(-73.96116256713867, 40.65016889724004, -73.89593124389648, 40.67829474034605);
        region[5] = new Region(-73.88116836547852, 40.71213418976526, -73.8310432434082, 40.75323899431278);
        region[6] = new Region(-73.81692409515381, 40.69267860646093, -73.7653398513794, 40.71076792966806);

        this.BUDGET = budget;
        this.allBillboards = billboardLists;

        String root = System.getProperty("user.dir");
        if (Developer.SYSTEM.equals("Win"))
            fileWriter = new MyFileWriter(root + "\\EnumResult.txt");
        else if (Developer.SYSTEM.equals("Linux"))
            fileWriter = new MyFileWriter(root + "/EnumResult.txt");

        System.out.println("B" + BUDGET + ", T" + trajectoryNumber);
    }

    public void getPhaseTwoMaxInfluence() {

        System.out.println("Start Enum : Budget " + this.BUDGET);

        List<List<GreedyParameter>> lists = dispatchGreedyParameters();
        List<BillboardSet> sets = new ArrayList<>();

        for (int i = 0; i < threadNumber; i++) {

            BillboardSet maxSet = new BillboardSet();
            sets.add(maxSet);
        }

        List<Thread> threads = new ArrayList<>(threadNumber);
        for (int i = 0; i < threadNumber; i++) {
            String name = "Thread" + i;
            threads.add(new EnumPhaseTwoThread(BUDGET, name, lists.get(i), sets.get(i), allBillboards, fileWriter));
        }

        for (int i = 0; i < threadNumber; i++) {
            threads.get(i).start();
        }
    }

    private void setCluster(Region[] region) {
        List<List<Billboard>> newBillboardList = new ArrayList<>();
        for (int i = 0; i < region.length; i++) {
            newBillboardList.add(new ArrayList<Billboard>());
        }

        for (List<Billboard> billboards : billboardLists) {
            for (Billboard billboard : billboards) {
                for (int i = 0; i < region.length; i++) {
                    if (region[i].contain(billboard)) {
                        newBillboardList.get(i).add(billboard);
                        break;
                    }
                }
            }
        }
        billboardLists = newBillboardList;

        if (ifAddBillboardRandom) {
            for (int i = 0; i < billboardLists.size(); i++) {
                if (billboardLists.get(i).size() < miniBillboardNumber)
                    billboardLists.set(i, addRandomBillboard(billboardLists.get(i), region[i]));
            }
        }
    }

    private List<Billboard> addRandomBillboard(List<Billboard> billboards, Region region) {

        Set<Route> routes = new HashSet<Route>();
        List<Route> routeList = new ArrayList<Route>(routes.size());

        int totalInfluence = 0;

        for (int i = 0; i < billboards.size(); i++) {
            routes.addAll(billboards.get(i).routes);
            totalInfluence += billboards.get(i).routes.size();
        }

        Iterator iter = routes.iterator();
        while (iter.hasNext()) {
            routeList.add((Route) iter.next());
        }

        int meanInfluence = totalInfluence / billboards.size();

        List<Billboard> newBillboards = new ArrayList<Billboard>(miniBillboardNumber);
        newBillboards.addAll(billboards);

        for (int i = 0; i < (miniBillboardNumber - billboards.size()); i++) {
            routes = new HashSet<Route>();
            Billboard billboard = new Billboard();
            region.setRandomRegion(billboard);
            int routeSize = (int) ((double) meanInfluence * Math.random() * Math.random());
            for (int n = 0; n < routeSize; n++) {
                int index = (int) (Math.random() * ((double) routes.size()));
                routes.add(routeList.get(index));
            }
            billboard.panelID = String.valueOf((int) (Math.random() * 10000000));
            billboard.routes.addAll(routes);
            billboard.influence = billboard.routes.size();
            //add cost
            if (randomCost)
                billboard.charge = Math.random() * 100;
            else {
                if (billboard.routes.size() > 0)
                    billboard.charge = Math.pow(billboard.influence, pow);
                //billboard.charge=billboard.routes.size()/5;
            }
            billboard.charge = billboard.charge / costCoefficient;
            billboard.charge = (int) (Math.round(billboard.charge / chargeDensity)) * chargeDensity;

            if (billboard.charge == 0 && billboard.routes.size() > 0)
                billboard.charge = chargeDensity;
            billboard.influencePerCharge = billboard.influence / billboard.charge;


            newBillboards.add(billboard);
        }
        return newBillboards;
    }

    private List<GreedyParameter> generateGreedyParameters() {

        List<GreedyParameter> parameterList = new ArrayList<>();
        //FinalResultReader finalResultReader = new FinalResultReader();
        //List<Billboard> allBillboards = finalResultReader.getBillboards();

        String city = null;
        Serialize serialize = new Serialize();
        if (readNYC)
            city = "NYC";
        else if (readLA)
            city = "LA";
        String billboardFileName = "T" + trajectoryNumber + "B" + multipleBillboardNumber * 500 + "C" + (int) costCoefficient + city;
        try {
            if (allBillboards == null)
                billboardLists = serialize.deserializeBillboard(billboardFileName);
        } catch (Exception e) {

        }
        for (List<Billboard> billboards : billboardLists) {
            allBillboards.addAll(billboards);
        }

        System.out.println("Multiple : " + multipleBillboardNumber);
        System.out.println("Billboard size : " + allBillboards.size());

        BillboardListDuplicator duplicator = new BillboardListDuplicator(allBillboards);
        allBillboards = duplicator.getBillboards();

        for (int i = 0; i < allBillboards.size(); i++) {

            for (int j = i + 1; j < allBillboards.size(); j++) {

                for (int k = j + 1; k < allBillboards.size(); k++) {

                    GreedyParameter parameter = new GreedyParameter();

                    parameter.firstBillboardIndex = i;
                    parameter.secondBillboardIndex = j;
                    parameter.thirdBillboardIndex = k;

                    parameter.budgetRemains = BUDGET - allBillboards.get(i).charge - allBillboards.get(j).charge
                            - allBillboards.get(k).charge;
                    if (parameter.budgetRemains >= 0) {
                        parameterList.add(parameter);
                        //System.out.println(allBillboards.get(i).charge+"  "+allBillboards.get(j).charge+"  "+allBillboards.get(k).charge);
                    }
                }
            }
        }
        return parameterList;
    }

    private List<List<GreedyParameter>> dispatchGreedyParameters() {

        List<List<GreedyParameter>> lists = new ArrayList<>();
        List<GreedyParameter> parameterList = generateGreedyParameters();

        int rounds = parameterList.size(); // how many times to call callGreedy()

        for (int i = 0; i < threadNumber; i++) {

            List<GreedyParameter> list = new ArrayList<>();
            lists.add(list);
        }

        int index = 0;
        for (int n = index; n < rounds; n += threadNumber) {
            for (int i = 0; i < threadNumber; i++) {
                if ((i + n) == rounds)
                    return lists;
                lists.get(i).add(parameterList.get(n + i));
            }
        }
        return lists;
    }


    public class GreedyParameter {

        public double budgetRemains;

        public int firstBillboardIndex;

        public int secondBillboardIndex;

        public int thirdBillboardIndex;


        @Override
        public String toString() {

            String result = "";
            result += "budgetRemains : " + budgetRemains + "\n";
            result += "firstBillboardIndex : " + firstBillboardIndex + "\n";
            result += "secondBillboardIndex : " + secondBillboardIndex + "\n";
            result += "thirdBillboardIndex : " + thirdBillboardIndex + "\n";

            return result;
        }

    }

    private static class Region {
        public double lon1;
        public double lan1;
        public double lon2;
        public double lan2;

        public Region(double lon1, double lan1, double lon2, double lan2) {
            this.lon1 = lon1;
            this.lan1 = lan1;
            this.lon2 = lon2;
            this.lan2 = lan2;
        }

        public void setRandomRegion(Billboard billboard) {
            double lon = Math.random() * (lon2 - lon1) + lon1;
            billboard.longitude = lon;
            double lan = Math.random() * (lan2 - lan1) + lan1;
            billboard.lantitude = lan;
        }

        public boolean contain(Billboard billboard) {
            if (billboard.lantitude < lan1)
                return false;
            if (billboard.lantitude > lan2)
                return false;
            if (billboard.longitude < lon1)
                return false;
            if (billboard.longitude > lon2)
                return false;
            return true;
        }
    }

}

