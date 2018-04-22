package trading.cli;

import trading.application.ScoringService;
import trading.domain.ISIN;
import trading.domain.strategy.compound.Score;
import trading.domain.strategy.compound.Scores;

public class CurrentScoringApplication {
    public static void main(String[] args) {
        ScoringService scoringService = new ScoringService(Dependencies.getMultiStockMarketDataStore());
        Scores scores = scoringService.getCurrentScoring();

        System.out.println("Scores");
        System.out.println();

        for(ISIN isin: scores.getIsinsOrderByScoreDescending()) {
            Score score = scores.get(isin);
            System.out.println(isin.getText() + " - " + score.getValue());
        }

        System.out.println();
        System.out.println();

        System.out.println("Details");
        System.out.println();

        for(ISIN isin: scores.getIsinsOrderByScoreDescending()) {
            Score score = scores.get(isin);

            System.out.println(isin.getText() + " - " + score.getValue());
            System.out.println();

            System.out.println(score.getComment());
            System.out.println();
            System.out.println();
        }
    }
}
