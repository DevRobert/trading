package trading.strategy;

import trading.AvailableStocks;
import trading.account.Account;
import trading.broker.Broker;

public interface TradingStrategyFactory {
    TradingStrategy initializeTradingStrategy(TradingStrategyParameters parameters, Account account, Broker broker, AvailableStocks availableStocks);
}
