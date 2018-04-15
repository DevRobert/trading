package trading.strategy.compound;

import trading.ISIN;
import trading.market.HistoricalMarketData;

public interface ScoringStrategy {
    Score calculateScore(HistoricalMarketData historicalMarketData, ISIN isin);
}
