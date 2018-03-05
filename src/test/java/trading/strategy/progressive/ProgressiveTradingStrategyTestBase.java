package trading.strategy.progressive;

import org.junit.Before;
import trading.ISIN;
import trading.account.Account;
import trading.broker.Broker;
import trading.market.HistoricalMarketData;
import trading.strategy.NotImplementedTrigger;
import trading.strategy.TradingStrategy;
import trading.strategy.TradingStrategyTestBase;

public abstract class ProgressiveTradingStrategyTestBase extends TradingStrategyTestBase {
    protected ProgressiveTradingStrategyParametersBuilder parametersBuilder = new ProgressiveTradingStrategyParametersBuilder();

    @Before
    public void progressiveTradingStrategyTestBaseBefore() {
        parametersBuilder.setISIN(ISIN.MunichRe);
        parametersBuilder.setBuyTrigger(new NotImplementedTrigger());
        parametersBuilder.setSellTrigger(new NotImplementedTrigger());
        parametersBuilder.setResetTrigger(new NotImplementedTrigger());
    }

    @Override
    protected TradingStrategy initializeTradingStrategy(Account account, Broker broker, HistoricalMarketData historicalMarketData) {
        ProgressiveTradingStrategyParameters parameters = parametersBuilder.build();
        return new ProgressiveTradingStrategy(parameters, account, broker, historicalMarketData);
    }
}
