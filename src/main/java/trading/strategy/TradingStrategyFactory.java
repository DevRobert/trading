package trading.strategy;

import trading.account.Account;
import trading.broker.Broker;
import trading.market.HistoricalMarketData;

public interface TradingStrategyFactory {
    TradingStrategy initializeTradingStrategy(TradingStrategyParameters parameters, Account account, Broker broker, HistoricalMarketData historicalMarketData);
}
