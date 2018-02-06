package fileIO.finalResult;

import configure.Developer;
import fileIO.MyFileReader;
import entity.Billboard;

import java.util.*;

/**
 * Created by Lancer on 2017/8/3.
 */
public class ReadLocation {

    private String filePath;
    private double lon1, lan1, lon2, lan2;

    private List<Billboard> billboards = new ArrayList<>();

    public ReadLocation() {
        initialize();
    }

    public ReadLocation(double lon1, double lan1, double lon2, double lan2) {
        if(Developer.SYSTEM.equals("Win"))
            filePath = "\\OptimalBillboardPlacement\\billboard.txt";
        else if(Developer.SYSTEM.equals("Linux"))
            filePath = "/billboard.txt";
        this.lon1 = lon1;
        this.lan1 = lan1;
        this.lon2 = lon2;
        this.lan2 = lan2;
        initialize();
    }

    public void setLocation(List<Billboard> rawBillboards) {
        for (Billboard billboard1 : rawBillboards) {
            for (Billboard billboard2 : billboards) {
                if (billboard1.panelID.equals(billboard2.panelID)) {
                    //System.out.println( billboard2.lantitude+","+ billboard2.longitude);
                    billboard1.lantitude = billboard2.lantitude;
                    billboard1.longitude = billboard2.longitude;
                    break;
                }
            }
        }
    }

    public void initialize() {
        MyFileReader finalResultReader = new MyFileReader(filePath);

        String line = finalResultReader.getNextLine();
        while (line != null) {

            String[] elements = line.split("~");

            if (elements.length == 1) {
                line = finalResultReader.getNextLine();
                continue;
            }

            String id = elements[0];

            elements = elements[1].split(" ");

            if (lan1 != 0) {
                if (lon1 > Double.valueOf(elements[1]) || lon2 < Double.valueOf(elements[1])){
                    line = finalResultReader.getNextLine();
                    continue;
                }
                if (lan1 > Double.valueOf(elements[2]) || lan2 < Double.valueOf(elements[1])){
                    line = finalResultReader.getNextLine();
                    continue;
                }
            }

            Billboard billboard = new Billboard();
            billboard.panelID = id;
            billboard.longitude = Double.valueOf(elements[1]);
            billboard.lantitude = Double.valueOf(elements[2]);

            billboards.add(billboard);

            line = finalResultReader.getNextLine();
        }
        System.out.print("");
    }
}
