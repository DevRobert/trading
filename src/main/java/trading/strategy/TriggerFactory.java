package trading.strategy;

import trading.market.HistoricalMarketData;

public interface TriggerFactory {
    Trigger createTrigger(HistoricalMarketData historicalMarketData);
}
