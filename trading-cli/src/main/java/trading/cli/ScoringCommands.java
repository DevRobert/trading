package trading.cli;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import trading.application.AccountService;
import trading.application.ScoringService;
import trading.domain.ISIN;
import trading.domain.account.Account;
import trading.domain.account.AccountId;
import trading.domain.strategy.compound.Score;
import trading.domain.strategy.compound.Scores;

@ShellComponent
public class ScoringCommands {
    @Autowired
    private AccountService accountService;

    @Autowired
    private ScoringService scoringService;

    @ShellMethod(value = "Calculates the current scoring.", key = "scoring")
    public void getCurrentScoring(@ShellOption("type") String type) {
        Account account = this.accountService.getAccount(new AccountId(1));

        Scores scores;

        System.out.println("Score: " + type);

        if(type.equalsIgnoreCase("buy")) {
            scores = this.scoringService.calculateBuyScoring(account);
        }
        else if(type.equalsIgnoreCase("sell")) {
            scores = this.scoringService.calculateSellScoring(account);
        }
        else {
            System.out.println("Type must be 'buy' or 'sell'.");
            return;
        }

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
