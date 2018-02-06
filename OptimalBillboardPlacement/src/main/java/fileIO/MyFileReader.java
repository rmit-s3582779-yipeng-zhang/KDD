package fileIO;

import configure.Developer;

import java.io.*;

/**
 * Created by marco on 29/03/2017.
 */
public class MyFileReader {

    private String line = "";
    private String inputFilePath;
    private BufferedReader bufferedReader;
    private String root;

    public MyFileReader(String inputFilePath) {

        root = System.getProperty("user.dir");

        this.inputFilePath = inputFilePath;
        try {

            if (Developer.SYSTEM.equals("Mac"))
                bufferedReader = new BufferedReader(new FileReader(inputFilePath));

            if (Developer.SYSTEM.equals("Win"))
                bufferedReader = new BufferedReader(new FileReader(root + inputFilePath));

            if (Developer.SYSTEM.equals("Linux"))
                bufferedReader = new BufferedReader(new FileReader(root + inputFilePath));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getNextLine() {

        try {
            line = bufferedReader.readLine();
            if (line != null) {
                return line;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void close() {

        try {
            bufferedReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
