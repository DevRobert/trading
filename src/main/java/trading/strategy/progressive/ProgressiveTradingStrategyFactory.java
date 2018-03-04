package trading.strategy.progressive;

import trading.account.Account;
import trading.broker.Broker;
import trading.market.HistoricalMarketData;
import trading.strategy.TradingStrategy;
import trading.strategy.TradingStrategyFactory;
import trading.strategy.TradingStrategyParameters;

public class ProgressiveTradingStrategyFactory implements TradingStrategyFactory {
    @Override
    public TradingStrategy initializeTradingStrategy(TradingStrategyParameters parameters, Account account, Broker broker, HistoricalMarketData historicalMarketData) {
        ProgressiveTradingStrategyParameters parsedParameters = ProgressiveTradingStrategyParameters.parse(parameters);
        return new ProgressiveTradingStrategy(parsedParameters, account, broker, historicalMarketData);
    }
}
