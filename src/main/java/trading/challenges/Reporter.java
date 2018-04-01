package trading.challenges;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Reporter {
    private BufferedWriter bufferedWriter;
    private String fileName;
    private Object writeLock = new Object();
    private long nextExpectedRunIndex = 0;
    private Map<Long, String> deferredLines = new HashMap<>();

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

    public void writeLine(long runIndex, String line) {
        synchronized (writeLock) {
            if(runIndex <= this.nextExpectedRunIndex) {
                this.nextExpectedRunIndex = runIndex + 1;

                this.writeLineToBufferedWriter(line);
                this.writeDeferredLines();
            }
            else {
                this.deferredLines.put(runIndex, line);
            }
        }
    }

    public void skipLine(long runIndex) {
        this.writeLine(runIndex, "");
    }

    private void writeDeferredLines() {
        while(this.deferredLines.containsKey(this.nextExpectedRunIndex)) {
            String line = this.deferredLines.get(this.nextExpectedRunIndex);
            this.deferredLines.remove(this.nextExpectedRunIndex);

            writeLineToBufferedWriter(line);
            this.nextExpectedRunIndex++;
        }
    }

    private void writeLineToBufferedWriter(String line) {
        if(line.length() == 0) {
            return;
        }

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
