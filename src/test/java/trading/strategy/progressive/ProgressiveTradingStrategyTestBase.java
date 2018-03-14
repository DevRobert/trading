package trading.strategy.progressive;

import org.junit.Before;
import trading.ISIN;
import trading.account.Account;
import trading.broker.Broker;
import trading.market.HistoricalMarketData;
import trading.strategy.NotImplementedTrigger;
import trading.strategy.TradingStrategy;
import trading.strategy.TradingStrategyContext;
import trading.strategy.TradingStrategyTestBase;

public abstract class ProgressiveTradingStrategyTestBase extends TradingStrategyTestBase {
    protected ProgressiveTradingStrategyParametersBuilder parametersBuilder = new ProgressiveTradingStrategyParametersBuilder();

    @Before
    public void progressiveTradingStrategyTestBaseBefore() {
        parametersBuilder.setISIN(ISIN.MunichRe);
        parametersBuilder.setBuyTriggerFactory((historicalMarketData) -> new NotImplementedTrigger());
        parametersBuilder.setSellTriggerFactory((historicalMarketData) -> new NotImplementedTrigger());
        parametersBuilder.setResetTriggerFactory((historicalMarketData) -> new NotImplementedTrigger());
    }

    @Override
    protected TradingStrategy initializeTradingStrategy(Account account, Broker broker, HistoricalMarketData historicalMarketData) {
        ProgressiveTradingStrategyParameters parameters = parametersBuilder.build();
        TradingStrategyContext tradingStrategyContext = new TradingStrategyContext(account, broker, historicalMarketData);
        return new ProgressiveTradingStrategy(parameters, tradingStrategyContext);
    }
}
