package fileIO;

import java.io.*;

/**
 * Created by marco on 29/03/2017.
 */
public class MyFileWriter {

    private Writer writer;
    private String outputFilePath;

    public MyFileWriter(String outputFilePath) {

        this.outputFilePath = outputFilePath;
        setUpWriter();
    }

    private void setUpWriter() {

        try {

            File file = new File(outputFilePath);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, "utf-8");
            writer = new BufferedWriter(outputStreamWriter);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void writeToFile(String content) {

        try {
            writer.write(content);
            writer.flush();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void close() {

        try {
            writer.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
