package trading.challenges;

import trading.simulation.RunParameters;
import trading.simulation.SimulationDriver;
import trading.simulation.SimulationDriverParameters;
import trading.simulation.SimulationReport;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChallengeExecutor {
    private ParameterTupleSource runParametersSource;
    private Iterator<Object[]> runParametersIterator;
    private Reporter reporter;
    private long nextRunIndex = 0;

    private void prepareReporting(String[] parameterNames) {
        String fileName = "/Users/robert/GitHub/data/data.csv";

        this.reporter = new Reporter(fileName);

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

        this.reporter.writeLine(0, String.join(";", fieldNames));
    }

    private void endReporting() {
       this.reporter.finish();
       this.reporter = null;
    }

    private RunParameters getNextRunParameters() {
        long runIndex;
        Object[] runParameters = null;

        synchronized (this.runParametersIterator) {
            runIndex = this.nextRunIndex;
            this.nextRunIndex++;

            if(this.runParametersIterator.hasNext()) {
                runParameters = this.runParametersIterator.next();
            }
        }

        return new RunParameters(runIndex, runParameters);
    }

    public void executeChallenge(Challenge challenge) {
        this.prepareReporting(challenge.getParameterNames());

        this.runParametersSource = challenge.getParametersSource();
        this.runParametersIterator = runParametersSource.getIterator();

        final int numThreads = Runtime.getRuntime().availableProcessors();

        int numSimulations = runParametersSource.size();
        System.out.println("Running " + String.format("%,d", numSimulations) + " simulations in " + numThreads + " threads...");

        final ExecutorService threads = Executors.newFixedThreadPool(numThreads);

        try {
            final CountDownLatch countDownLatch = new CountDownLatch(runParametersSource.size());

            for(int threadIndex = 0; threadIndex < numThreads; threadIndex++) {
                threads.execute(() -> {
                    RunParameters runParameters;

                    while((runParameters = this.getNextRunParameters()) != null) {
                        try {
                            SimulationDriverParameters simulationDriverParameters = challenge.buildSimulationDriverParametersForRun(runParameters.getParameters());
                            SimulationDriver simulationDriver = new SimulationDriver(simulationDriverParameters);
                            SimulationReport simulationReport = simulationDriver.runSimulation();

                            this.trackSimulationReport(simulationReport, runParameters);
                        }
                        catch(RuntimeException ex) {
                            this.reportException(runParameters, ex);
                        }
                        finally {
                            countDownLatch.countDown();
                        }

                        long numRemainingSimulations = countDownLatch.getCount();

                        if(numRemainingSimulations % 100000 == 0 && numRemainingSimulations > 0) {
                            double progress = 100.0 * (numSimulations - numRemainingSimulations) / numSimulations;
                            System.out.println("Progress: " + String.format("%.2f", progress) + " % - " + String.format("%,d", numRemainingSimulations) + " simulations remaining.");
                        }
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

    private void trackSimulationReport(SimulationReport simulationReport, RunParameters runParameters) {
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

        this.reporter.writeLine(runParameters.getRunIndex(), line);
    }

    private void reportException(RunParameters runParameters, Exception ex) {
        String message = "Exception occurred for run index " + runParameters.getRunIndex();

        for(int parameterIndex = 0; parameterIndex < runParameters.getParameters().length; parameterIndex++) {
            message += ";";
            message += runParameters.getParameters()[parameterIndex].toString();
        }

        message += ";" + ex.getMessage();

        System.out.println(message);

        this.reporter.skipLine(runParameters.getRunIndex());
    }
}
