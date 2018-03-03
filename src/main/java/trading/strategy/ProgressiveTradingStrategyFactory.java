package trading.strategy;

import trading.AvailableStocks;
import trading.account.Account;
import trading.broker.Broker;

public class ProgressiveTradingStrategyFactory implements TradingStrategyFactory {
    @Override
    public TradingStrategy initializeTradingStrategy(TradingStrategyParameters parameters, Account account, Broker broker, AvailableStocks availableStocks) {
        ProgressiveTradingStrategyParameters parsedParameters = ProgressiveTradingStrategyParameters.parse(parameters);

        if(!availableStocks.getISINs().contains(parsedParameters.getISIN())) {
            throw new StrategyInitializationException(String.format("The ISIN parameter '%s' does not refer to an available stock.", parsedParameters.getISIN().getText()));
        }

        return new ProgressiveTradingStrategy(parsedParameters, account, broker);
    }
}
