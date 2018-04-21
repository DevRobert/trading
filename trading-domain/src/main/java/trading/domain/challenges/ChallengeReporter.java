package trading.domain.challenges;

import trading.domain.simulation.RunParameters;
import trading.domain.simulation.SimulationDayReport;
import trading.domain.simulation.SimulationReport;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChallengeReporter {
    private BufferedWriter bufferedWriter;
    private String fileName;
    private Object writeLock = new Object();
    private long nextExpectedRunIndex = 0;
    private Map<Long, String> deferredLines = new HashMap<>();

    public ChallengeReporter(String fileName, String[] parameterNames) {
        this.fileName = fileName;

        try {
            this.bufferedWriter = new BufferedWriter(new FileWriter(fileName, false));
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Writing report: " + fileName);

        this.writeHeaderLine(parameterNames);
    }

    public void trackCompletedSimulation(RunParameters runParameters, SimulationReport simulationReport) {
        String line = runParameters.getRunIndex() +
                ";" + simulationReport.getInitialAccountBalance().toString() +
                ";" + simulationReport.getFinalAccountBalance().toString() +
                ";" + simulationReport.getAverageMarketRateOfReturn() +
                ";" + simulationReport.getRealizedRateOfReturn() +
                ";" + simulationReport.getAddedRateOfReturn() +
                ";" + simulationReport.getTransactions().size();

        for(Object runParameter: runParameters.getParameters()) {
            line += ";" + runParameter.toString();
        }

        this.writeLine(runParameters.getRunIndex(), line);

        this.generateDailyReport(runParameters, simulationReport);
    }

    private void generateDailyReport(RunParameters runParameters, SimulationReport simulationReport) {
        if(simulationReport.getDayReports() == null) {
            return;
        }

        Path mainReportPath = Paths.get(this.fileName);
        Path reportDirectory = mainReportPath.getParent();
        String dailyPath = reportDirectory.toString() + File.separator + "data_detailed_" + runParameters.getRunIndex() + ".csv";

        BufferedWriter bufferedWriter = null;

        try {
            bufferedWriter = new BufferedWriter(new FileWriter(dailyPath));

            bufferedWriter.write("day;account_balance;available_money;average_market_rate_of_return;realized_rate_of_return");
            bufferedWriter.newLine();

            for(int dayIndex = 0; dayIndex < simulationReport.getDayReports().size(); dayIndex++) {
                SimulationDayReport simulationDayReport = simulationReport.getDayReports().get(dayIndex);

                bufferedWriter.write(((Integer) dayIndex).toString());
                bufferedWriter.write(";");

                bufferedWriter.write(simulationDayReport.getAccountBalance().toString());
                bufferedWriter.write(";");

                bufferedWriter.write(simulationDayReport.getAvailableMoney(). toString());
                bufferedWriter.write(";");

                bufferedWriter.write(((Double) simulationDayReport.getAverageMarketRateOfReturn()).toString());
                bufferedWriter.write(";");

                bufferedWriter.write(((Double) simulationDayReport.getRealizedRateOfReturn()).toString());
                bufferedWriter.newLine();
            }

            bufferedWriter.close();
        }
        catch (IOException exception) {
            if(bufferedWriter != null) {
                try {
                    bufferedWriter.close();
                }
                catch (IOException closeException) {
                    closeException.printStackTrace();
                }
            }

            throw new RuntimeException(exception);
        }
    }

    public void trackFailedSimulation(RunParameters runParameters, Exception ex) {
        String message = "Exception occurred for run index " + runParameters.getRunIndex();

        for(int parameterIndex = 0; parameterIndex < runParameters.getParameters().length; parameterIndex++) {
            message += ";";
            message += runParameters.getParameters()[parameterIndex].toString();
        }

        message += ";" + ex.getMessage();

        System.out.println(message);

        this.skipLine(runParameters.getRunIndex());
    }

    private void writeHeaderLine(String[] parameterNames) {
        List<String> fieldNames = new ArrayList();

        fieldNames.add("simulation");
        fieldNames.add("initial_account_balance");
        fieldNames.add("final_account_balance");
        fieldNames.add("average_market_rate_of_return");
        fieldNames.add("realized_rate_of_return");
        fieldNames.add("added_rate_of_return");
        fieldNames.add("transactions");

        for(String parameterName: parameterNames) {
            fieldNames.add(parameterName);
        }

        this.writeLine(-1, String.join(";", fieldNames));
    }

    private void writeLine(long runIndex, String line) {
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

    private void skipLine(long runIndex) {
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

    public void close() {
        try {
            this.bufferedWriter.close();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Report finished: " + fileName);
    }
}
