package trading.challenges;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Reporter {
    private BufferedWriter bufferedWriter;
    private String fileName;

    public Reporter(String fileName) {
        this.fileName = fileName;

        try {
            this.bufferedWriter = new BufferedWriter(new FileWriter(fileName, false));
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Writing report: " + fileName);
    }

    public void writeLine(String line) {
        try {
            this.bufferedWriter.write(line + System.lineSeparator());
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void finish() {
        try {
            this.bufferedWriter.close();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Report finished: " + fileName);
    }
}
