package regionSelector;

import com.github.davidmoten.rtree.Entry;
import com.github.davidmoten.rtree.RTree;
import com.github.davidmoten.rtree.geometry.Geometries;
import com.github.davidmoten.rtree.geometry.Point;
import com.github.davidmoten.rtree.geometry.Rectangle;
//import com.sun.tools.internal.xjc.reader.dtd.bindinfo.BIAttribute;
import entity.Billboard;
import fileIO.FilePath;
import fileIO.MyFileWriter;
import fileIO.RTree.Deserialize;
import fileIO.finalResult.FinalResultReader;
import rx.*;

import java.util.*;

/**
 * Created by marco on 15/07/2017.
 */
public class RegionSelector implements RandomRegionBillboardsGetter {


    private Rectangle wholeRegion;
    List<Billboard> allBillboards;


    public RegionSelector(Rectangle wholeRegion) {

        this.wholeRegion = wholeRegion;

        FinalResultReader resultReader = new FinalResultReader();
        allBillboards = resultReader.getBillboards();
    }



    public static void main(String[] args) {


        RegionSelector selector = new RegionSelector(Geometries.rectangle(-74.260948,40.485284,-73.688285,40.920459));

        List<List<Billboard>> billboardList = selector.getRegionBillboards();

        for (List<Billboard> billboards : billboardList) {

            for (Billboard billboard : billboards)
                System.out.print(billboard.panelID + "\t");

            System.out.println();
        }

    }


    @Override
    public List<List<Billboard>> getRegionBillboards() {

        List<List<Billboard>> billboardList = new ArrayList<>();
        List<List<String>> billboardStringList = getRegionBillboradStrings();

        for (List<String> billboradStrings : billboardStringList) {

            List<Billboard> billboards = new ArrayList<>();
            billboardList.add(billboards);

            for (String panelID : billboradStrings) {

                Billboard billboard = getBillboardFrom(panelID);

                if (billboard != null)
                    billboards.add(billboard);
            }
        }
        return billboardList;
    }




    private List<List<String>> getRegionBillboradStrings() {

        List<List<String>> billboardStringList = new ArrayList<>();

        List<Rectangle> regions = generateRandomRegions();
        Iterator iterator = regions.iterator();

        while (iterator.hasNext()) {

            List<String> billboardStrings = findBillboardsIn((Rectangle)iterator.next());
            billboardStringList.add(billboardStrings);
        }

        return billboardStringList;
    }


    private List<Rectangle> generateRandomRegions() {

        List<Rectangle> regions = new ArrayList<>(10);

        for (int i = 0; i < 10; i++) {

            double x1 = Math.random() * (wholeRegion.x2()-wholeRegion.x1()) + wholeRegion.x1();
            double y1 = Math.random() * (wholeRegion.y2()-wholeRegion.y1()) + wholeRegion.y1();

            double x2 = x1 + 0.05;
            double y2 = y1 + 0.05;

            Rectangle region = Geometries.rectangle(x1, y1, x2, y2);
            if (isInWholeRegion(region))
                regions.add(region);
        }
        return regions;
    }



    private List<String> findBillboardsIn(Rectangle region) {

        List<String> billboardPanelIDs = new ArrayList<>();

        RTree<String, Point> billboardTree = RTree.create();
        billboardTree = Deserialize.deserialize(FilePath.billboardRTreePath, 10111112);

        try {

            rx.Observable<Entry<String, Point>> results = billboardTree.search(region);
            Iterable<Entry<String, Point>> resultsIterable = results.toBlocking().toIterable();
            Iterator<Entry<String, Point>> billboardIterator = resultsIterable.iterator();

            while (billboardIterator.hasNext()) {

                Entry<String, Point> billboard = billboardIterator.next();
                Point billboardLocation = billboard.geometry();

                //System.out.println("billboard: " + billboard.value() + " " + billboardLocation.toString());
                billboardPanelIDs.add(billboard.value().split("~")[0]);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return billboardPanelIDs;
    }


    private Billboard getBillboardFrom(String panelID) {

        for (Billboard billboard : allBillboards) {

            if (billboard.panelID.equals(panelID))
                return billboard;
        }
        return null;
    }





    private boolean isInWholeRegion(Rectangle region) {

        if (region.x1() < wholeRegion.x1() || region.x1() > wholeRegion.x2())
            return false;

        if (region.x2() < wholeRegion.x1() || region.x2() > wholeRegion.x2())
            return false;

        if (region.y1() < wholeRegion.y1() || region.y1() > wholeRegion.y2())
            return false;

        if (region.y2() < wholeRegion.y1() || region.y2() > wholeRegion.y2())
            return false;

        return true;
    }


}
