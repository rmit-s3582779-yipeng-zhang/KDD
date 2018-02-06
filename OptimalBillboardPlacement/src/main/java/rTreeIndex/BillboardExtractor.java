package rTreeIndex;

import fileIO.MyFileReader;
import fileIO.MyFileWriter;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Created by marco on 30/04/2017.
 */
public class BillboardExtractor {


//    static List<String> ids = new ArrayList<>();

    public static void main(String[] args) {

        removeHeader_Tail();
        generateBillboardFile();
        remove0();
        removeNullImpression();
        //generateBillboardFile2();
    }


    private static void removeHeader_Tail() {

        MyFileReader myFileReader = new MyFileReader("/Users/marco/Desktop/billboardOriginal.txt");
        MyFileWriter myFileWriter = new MyFileWriter("/Users/marco/Desktop/billboardIntermediate.txt");

        String line = myFileReader.getNextLine();

        while (line != null) {

            int indexOf2 = line.indexOf('{');
            int length = line.length();
            line = line.substring(indexOf2, length - 1);

            myFileWriter.writeToFile(line);
            myFileWriter.writeToFile("\n");
            line = myFileReader.getNextLine();
        }
        myFileWriter.close();
    }


    private static void generateBillboardFile() {

        MyFileReader myFileReader = new MyFileReader("/Users/marco/Desktop/billboardIntermediate.txt");
        MyFileWriter myFileWriter = new MyFileWriter("/Users/marco/Desktop/billboard.txt");

        String line = myFileReader.getNextLine();
        JSONParser parser = new JSONParser();

        int counter = 0;
        while (line != null) {

            long id = -999;
            double longitude = -1;
            double latitude = -1;
            long weeklyImpression = -1;

            try {

                JSONObject obj = (JSONObject) parser.parse(line);
                id = (long) obj.get("panelID");
                longitude = (double) obj.get("longitude");
                latitude = (double) obj.get("latitude");
                weeklyImpression = (long)obj.get("weeklyImpressions");

            } catch (ParseException e) {

                System.out.println("ERROR !!!");
                System.out.println("counter :" + counter);
                System.out.println("position: " + e.getPosition());
                System.out.println(e);

            } catch (NullPointerException e) {

                System.out.println("ERROR !!!");
                System.out.println("counter :" + counter);
                System.out.println(e);
            }

            myFileWriter.writeToFile(id + "~" + weeklyImpression + " " + longitude + " " + latitude + "\n");
            line = myFileReader.getNextLine();
            counter++;
        }
        myFileWriter.close();

        System.out.println("write " + counter + " lines.");
    }


    // remove those billboards whose coordinate is (0.000, 0.0000)
    private static void remove0() {

        MyFileReader myFileReader = new MyFileReader("/Users/marco/Desktop/billboard.txt");
        MyFileWriter myFileWriter = new MyFileWriter("./billboard.txt");

        String line = myFileReader.getNextLine();

        while (line != null) {

            String[] elements = line.split(" ");
            String id = elements[0];
            double longitude = Double.parseDouble(elements[1]);
            double latitude = Double.parseDouble(elements[2]);

            if (longitude == 0.0 || latitude == 0.0) {
                line = myFileReader.getNextLine();
                continue;
            }

            myFileWriter.writeToFile(id + " ");
            myFileWriter.writeToFile(longitude + " ");
            myFileWriter.writeToFile(latitude + "\n");

            line = myFileReader.getNextLine();
            //System.out.println(id);
        }

        myFileWriter.close();
    }

    // remove those billboards whose weekly impression is null
    private static void removeNullImpression() {

        MyFileReader myFileReader = new MyFileReader("./billboard.txt");
        MyFileWriter myFileWriter = new MyFileWriter("./billboard2.txt");

        String line = myFileReader.getNextLine();

        while (line != null) {

            String[] elements = line.split(" ");
            String id = elements[0];
            String longitude = elements[1];
            String latitude = elements[2];
            String[] elementsAgain = id.split("~");

            if (elementsAgain[1].equals("-1")) {
                line = myFileReader.getNextLine();
                continue;
            }

            myFileWriter.writeToFile(id + " ");
            myFileWriter.writeToFile(longitude + " ");
            myFileWriter.writeToFile(latitude + "\n");

            line = myFileReader.getNextLine();
            //System.out.println(id);
        }

        myFileWriter.close();

    }



    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~


    private static void generateBillboardFile2() {

        MyFileReader myFileReader = new MyFileReader("./NewYork.txt");
        MyFileWriter myFileWriter = new MyFileWriter("./billboard.txt");

        String line = myFileReader.getNextLine();

        while (line != null) {

            String[] stringArr = line.split(" ");
            myFileWriter.writeToFile(stringArr[0] + " " + stringArr[2] + " " + stringArr[1] + " " + "\n");

            line = myFileReader.getNextLine();
        }
        myFileWriter.close();
    }
}