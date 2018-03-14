package trading.challenges;

import trading.simulation.SimulationDriver;
import trading.simulation.SimulationDriverParameters;
import trading.simulation.SimulationReport;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class ChallengeExecutor {
    private List<String> reportLines;

    private void prepareReporting() {
        this.reportLines = new ArrayList<>();
    }

    private void endReporting() {
        String fileName = "/Users/robert/GitHub/data/data.csv";

        PrintWriter writer = null;

        try {
            writer = new PrintWriter(fileName, "UTF-8");
        }
        catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        for(String line: reportLines) {
            writer.println(line);
        }

        writer.close();

        this.reportLines = null;

        System.out.println("Report saved: " + fileName);
    }

    public void executeChallenge(Challenge challenge) {
        this.prepareReporting();

        List<Object[]> runParametersList = challenge.buildParametersForDifferentRuns();

        System.out.println("Running " + runParametersList.size() + " simulations...");

        for(Object[] runParameters: runParametersList) {
            SimulationDriverParameters simulationDriverParameters = challenge.buildSimulationDriverParametersForRun(runParameters);

            SimulationDriver simulationDriver = new SimulationDriver(simulationDriverParameters);
            SimulationReport simulationReport = simulationDriver.runSimulation();
            this.trackSimulationReport(simulationReport, runParameters);
        }

        System.out.println("All simulations completed.");

        this.endReporting();
    }

    private void trackSimulationReport(SimulationReport simulationReport, Object[] runParameters) {
        String line = simulationReport.getInitialAccountBalance().toString() +
                ";" + simulationReport.getFinalAccountBalance().toString();

        for(Object runParameter: runParameters) {
            line += ";" + runParameter.toString();
        }

        reportLines.add(line);
    }
}
