package trading.domain.strategy.compound;

import trading.domain.ISIN;
import trading.domain.account.Account;
import trading.domain.market.HistoricalMarketData;

public interface ScoringStrategy {
    Score calculateScore(HistoricalMarketData historicalMarketData, Account account, ISIN isin);
}
