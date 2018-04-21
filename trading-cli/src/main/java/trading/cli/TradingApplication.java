package trading.cli;

import trading.domain.challenges.Challenge;
import trading.domain.challenges.ChallengeExecutor;
import trading.domain.challenges.CompoundLocalMaximumChallenge;
import trading.domain.challenges.HistoricalTestDataProvider;

import java.util.Date;

public class TradingApplication {
    public static void main(String[] args) {
        System.out.println("Trading application started.");

        HistoricalTestDataProvider historicalTestDataProvider = new HistoricalTestDataProvider(Dependencies.getMultiStockMarketDataStore());
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
