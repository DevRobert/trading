package trading.application;

import trading.challenges.Challenge;
import trading.challenges.ChallengeExecutor;
import trading.challenges.DynamicLocalMaximumTimeframeChallenge;

import java.util.Date;

public class TradingApplication {
    public static void main(String[] args) {
        System.out.println("Trading application started.");

        Challenge challenge = new DynamicLocalMaximumTimeframeChallenge();

        Date start = new Date();

        ChallengeExecutor challengeExecutor = new ChallengeExecutor();
        challengeExecutor.executeChallenge(challenge);

        Date end = new Date();

        long durationMilliseconds = end.getTime() - start.getTime();
        double durationSeconds = ((double) durationMilliseconds) / 1000.0;

        System.out.println("Simulation duration: " + durationSeconds + " seconds");
    }
}
