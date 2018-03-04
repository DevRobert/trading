package trading.strategy;

import trading.account.Account;
import trading.broker.Broker;
import trading.market.HistoricalMarketData;

public class ProgressiveTradingStrategyFactory implements TradingStrategyFactory {
    @Override
    public TradingStrategy initializeTradingStrategy(TradingStrategyParameters parameters, Account account, Broker broker, HistoricalMarketData historicalMarketData) {
        ProgressiveTradingStrategyParameters parsedParameters = ProgressiveTradingStrategyParameters.parse(parameters);

        if(!historicalMarketData.getAvailableStocks().contains(parsedParameters.getISIN())) {
            throw new StrategyInitializationException(String.format("The ISIN parameter '%s' does not refer to an available stock.", parsedParameters.getISIN().getText()));
        }

        return new ProgressiveTradingStrategy(parsedParameters, account, broker);
    }
}
