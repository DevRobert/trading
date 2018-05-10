package trading.application;

import trading.domain.account.Account;
import trading.domain.strategy.compound.Scores;

public interface ScoringService {
    Scores calculateBuyScoring(Account account);
    Scores calculateSellScoring(Account account);
}
