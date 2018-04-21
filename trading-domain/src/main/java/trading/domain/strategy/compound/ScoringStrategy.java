package trading.domain.strategy.compound;

import trading.domain.ISIN;
import trading.domain.market.HistoricalMarketData;

public interface ScoringStrategy {
    Score calculateScore(HistoricalMarketData historicalMarketData, ISIN isin);
}
