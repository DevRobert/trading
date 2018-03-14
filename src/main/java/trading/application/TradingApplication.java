package trading.application;

import trading.challenges.Challenge;
import trading.challenges.ChallengeExecutor;
import trading.challenges.LocalMaximumChallenge;

public class TradingApplication {
    public static void main(String[] args) {
        System.out.println("Trading application started.");

        Challenge challenge = new LocalMaximumChallenge();

        ChallengeExecutor challengeExecutor = new ChallengeExecutor();
        challengeExecutor.executeChallenge(challenge);
    }
}
