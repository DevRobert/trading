package trading.cli;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import trading.domain.challenges.Challenge;
import trading.domain.challenges.ChallengeExecutor;
import trading.domain.challenges.CompoundLocalMaximumChallenge;
import trading.domain.challenges.HistoricalTestDataProvider;
import trading.domain.simulation.MultiStockMarketDataStore;

import java.util.Date;

@ShellComponent
public class SimulationCommands {
    @Autowired
    private MultiStockMarketDataStore multiStockMarketDataStore;

    @ShellMethod(value = "Runs the simulations.", key = "simulation")
    public void runSimulations() {
        HistoricalTestDataProvider historicalTestDataProvider = new HistoricalTestDataProvider(this.multiStockMarketDataStore);
        Challenge challenge = new CompoundLocalMaximumChallenge(historicalTestDataProvider);

        Date start = new Date();

        ChallengeExecutor challengeExecutor = new ChallengeExecutor(challenge);
        // challengeExecutor.setDailyReporting(true);
        challengeExecutor.executeChallenge();

        Date end = new Date();

        long durationMilliseconds = end.getTime() - start.getTime();
        double durationSeconds = ((double) durationMilliseconds) / 1000.0;

        System.out.println("Simulation duration: " + durationSeconds + " seconds");
    }
}
