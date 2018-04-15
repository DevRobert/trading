package trading.strategy.progressive;

import org.junit.Before;
import trading.ISIN;
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
    protected TradingStrategy initializeTradingStrategy(TradingStrategyContext tradingStrategyContext) {
        ProgressiveTradingStrategyParameters parameters = parametersBuilder.build();
        return new ProgressiveTradingStrategy(parameters, tradingStrategyContext);
    }
}
