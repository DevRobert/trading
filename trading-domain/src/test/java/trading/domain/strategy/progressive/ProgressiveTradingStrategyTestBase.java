package trading.domain.strategy.progressive;

import org.junit.Before;
import trading.domain.ISIN;
import trading.domain.strategy.NotImplementedTrigger;
import trading.domain.strategy.TradingStrategy;
import trading.domain.strategy.TradingStrategyContext;
import trading.domain.strategy.TradingStrategyTestBase;

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
