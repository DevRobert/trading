package trading.strategy.progressive;

import org.junit.Before;
import trading.ISIN;
import trading.strategy.ProgressiveTradingStrategyFactory;
import trading.strategy.TradingStrategyFactory;
import trading.strategy.TradingStrategyTestBase;

public abstract class ProgressiveTradingStrategyTestBase extends TradingStrategyTestBase {
    @Before
    public void before() {
        this.parametersBuilder.setParameter("isin", ISIN.MunichRe.getText());
        this.parametersBuilder.setParameter("buyTriggerRisingDaysInSequence", "1");
        this.parametersBuilder.setParameter("sellTriggerDecliningDaysInSequence", "1");
        this.parametersBuilder.setParameter("sellTriggerMaxDays", "3");
        this.parametersBuilder.setParameter("restartTriggerDecliningDaysInSequence", "0");
    }

    @Override
    protected TradingStrategyFactory getStrategyFactory() {
        return new ProgressiveTradingStrategyFactory();
    }
}
