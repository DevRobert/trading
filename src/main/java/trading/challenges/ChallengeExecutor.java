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
    private ChallengeReporter reporter;
    private int nextRunIndex = 0;
    private CountDownLatch countDownLatch;
    private int numSimulations;
    private Challenge challenge;
    private boolean dailyReporting = false;

    public ChallengeExecutor(Challenge challenge) {
        this.challenge = challenge;
    }

    public void setDailyReporting(boolean dailyReporting) {
        this.dailyReporting = dailyReporting;
    }

    public void executeChallenge() {
        String fileName = "/Users/robert/GitHub/data/data.csv";
        this.reporter = new ChallengeReporter(fileName, this.challenge.getParameterNames());

        this.runParametersSource = challenge.getParametersSource();
        this.runParametersIterator = runParametersSource.getIterator();

        final int numThreads = Runtime.getRuntime().availableProcessors();

        this.numSimulations = runParametersSource.size();

        if(dailyReporting && this.numSimulations > 1000) {
            throw new RuntimeException("Daily reporting is not allowed for challenges containing more than 1,000 simulations.");
        }

        System.out.println("Running " + String.format("%,d", this.numSimulations) + " simulations in " + numThreads + " threads...");

        final ExecutorService threads = Executors.newFixedThreadPool(numThreads);

        this.countDownLatch = new CountDownLatch(this.numSimulations);

        try {
            for(int threadIndex = 0; threadIndex < numThreads; threadIndex++) {
                threads.execute(() -> this.runSimulationInWorkerThread());
            }

            countDownLatch.await();
        }
        catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        finally {
            threads.shutdown();

            this.reporter.close();
        }

        System.out.println("All simulations completed.");
    }

    private void runSimulationInWorkerThread() {
        RunParameters runParameters;

        while((runParameters = this.getNextRunParameters()) != null) {
            try {
                SimulationDriverParameters simulationDriverParameters = challenge.buildSimulationDriverParametersForRun(runParameters.getParameters());

                SimulationDriver simulationDriver = new SimulationDriver(simulationDriverParameters);
                simulationDriver.setDailyReporting(this.dailyReporting);
                SimulationReport simulationReport = simulationDriver.runSimulation();

                this.reporter.trackCompletedSimulation(runParameters, simulationReport);
            }
            catch(RuntimeException ex) {
                this.reporter.trackFailedSimulation(runParameters, ex);
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

        if(runParameters == null) {
            return null;
        }

        return new RunParameters(runIndex, runParameters);
    }
}
