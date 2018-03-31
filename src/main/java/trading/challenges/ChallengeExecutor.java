package trading.challenges;

import trading.simulation.SimulationDriver;
import trading.simulation.SimulationDriverParameters;
import trading.simulation.SimulationReport;

import java.util.Iterator;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChallengeExecutor {
    private ParameterTupleSource runParametersSource;
    private Iterator<Object[]> runParametersIterator;
    private Reporter reporter;

    private void prepareReporting() {
        String fileName = "/Users/robert/GitHub/data/data.csv";
        this.reporter = new Reporter(fileName);
    }

    private void endReporting() {
       this.reporter.finish();
       this.reporter = null;
    }

    private Object[] getNextRunParameters() {
        Object[] result = null;

        synchronized (this.runParametersIterator) {
            if(this.runParametersIterator.hasNext()) {
                result = this.runParametersIterator.next();
            }
        }

        return result;
    }

    public void executeChallenge(Challenge challenge) {
        this.prepareReporting();

        this.runParametersSource = challenge.buildParametersForDifferentRuns();
        this.runParametersIterator = runParametersSource.getIterator();

        final int numThreads = Runtime.getRuntime().availableProcessors();

        int numSimulations = runParametersSource.size();
        System.out.println("Running " + String.format("%,d", numSimulations) + " simulations in " + numThreads + " threads...");

        final ExecutorService threads = Executors.newFixedThreadPool(numThreads);

        try {
            final CountDownLatch countDownLatch = new CountDownLatch(runParametersSource.size());

            for(int threadIndex = 0; threadIndex < numThreads; threadIndex++) {
                threads.execute(() -> {
                    Object[] runParameters;

                    while((runParameters = this.getNextRunParameters()) != null) {
                        try {
                            SimulationDriverParameters simulationDriverParameters = challenge.buildSimulationDriverParametersForRun(runParameters);
                            SimulationDriver simulationDriver = new SimulationDriver(simulationDriverParameters);
                            SimulationReport simulationReport = simulationDriver.runSimulation();
                            this.trackSimulationReport(simulationReport, runParameters);
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

    private void trackSimulationReport(SimulationReport simulationReport, Object[] runParameters) {
        String line = simulationReport.getInitialAccountBalance().toString() +
                ";" + simulationReport.getFinalAccountBalance().toString() +
                ";" + simulationReport.getAverageMarketRateOfReturn() +
                ";" + simulationReport.getRealizedRateOfReturn() +
                ";" + simulationReport.getAddedRateOfReturn() +
                ";" + simulationReport.getTransactions().size();

        for(Object runParameter: runParameters) {
            line += ";" + runParameter.toString();
        }

        this.reporter.writeLine(line);
    }
}
