package trading.challenges;

import trading.simulation.SimulationDriver;
import trading.simulation.SimulationDriverParameters;
import trading.simulation.SimulationReport;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChallengeExecutor {
    private Collection<String> reportLines;

    private void prepareReporting() {
        this.reportLines = Collections.synchronizedCollection(new ArrayList<String>());
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

        final int numThreads = Runtime.getRuntime().availableProcessors();

        System.out.println("Running " + runParametersList.size() + " simulations in " + numThreads + " threads...");

        final ExecutorService threads = Executors.newFixedThreadPool(numThreads);

        try {
            final CountDownLatch countDownLatch = new CountDownLatch(runParametersList.size());

            for(Object[] runParameters: runParametersList) {
                threads.execute(() -> {
                    try {
                        SimulationDriverParameters simulationDriverParameters = challenge.buildSimulationDriverParametersForRun(runParameters);
                        SimulationDriver simulationDriver = new SimulationDriver(simulationDriverParameters);
                        SimulationReport simulationReport = simulationDriver.runSimulation();
                        this.trackSimulationReport(simulationReport, runParameters);
                    } finally {
                        countDownLatch.countDown();
                    }
                });
            }

            countDownLatch.await();
        }
        catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        finally {
            threads.shutdown();
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
