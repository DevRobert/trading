package trading.cli;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import trading.application.ScoringService;
import trading.domain.ISIN;
import trading.domain.strategy.compound.Score;
import trading.domain.strategy.compound.Scores;

@ShellComponent
public class ScoringCommands {
    @Autowired
    private ScoringService scoringService;

    @ShellMethod(value = "Calculates the current scoring.", key = "scoring")
    public void getCurrentScoring() {
        Scores scores = this.scoringService.getCurrentScoring();

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
