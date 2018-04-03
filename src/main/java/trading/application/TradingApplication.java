package trading.application;

import trading.challenges.Challenge;
import trading.challenges.ChallengeExecutor;
import trading.challenges.LocalMaximumChallenge;

import java.util.Date;

public class TradingApplication {
    public static void main(String[] args) {
        System.out.println("Trading application started.");

        Challenge challenge = new LocalMaximumChallenge();

        Date start = new Date();

        ChallengeExecutor challengeExecutor = new ChallengeExecutor(challenge);
        challengeExecutor.setDailyReporting(true);
        challengeExecutor.executeChallenge();

        Date end = new Date();

        long durationMilliseconds = end.getTime() - start.getTime();
        double durationSeconds = ((double) durationMilliseconds) / 1000.0;

        System.out.println("Simulation duration: " + durationSeconds + " seconds");
    }
}
