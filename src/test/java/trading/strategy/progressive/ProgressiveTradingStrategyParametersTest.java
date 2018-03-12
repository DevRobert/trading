package trading.strategy.progressive;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import trading.DayCount;
import trading.ISIN;
import trading.strategy.StrategyInitializationException;
import trading.strategy.WaitFixedPeriodTrigger;

public class ProgressiveTradingStrategyParametersTest {
    private ProgressiveTradingStrategyParametersBuilder builder;

    @Before
    public void before() {
        this.builder = new ProgressiveTradingStrategyParametersBuilder();
        this.builder.setISIN(ISIN.MunichRe);
        this.builder.setBuyTriggerFactory((historicalMarketData) -> new WaitFixedPeriodTrigger(new DayCount(1)));
        this.builder.setSellTriggerFactory((historicalMarketData) -> new WaitFixedPeriodTrigger(new DayCount(1)));
        this.builder.setResetTriggerFactory((historicalMarketData) -> new WaitFixedPeriodTrigger(new DayCount(1)));
    }

    @Test
    public void failsIfNoISINSpecified() {
        this.builder.setISIN(null);

        try {
            this.builder.build();
        }
        catch(StrategyInitializationException ex) {
            Assert.assertEquals("The ISIN must be specified.", ex.getMessage());
            return;
        }

        Assert.fail("StrategyInitializationException expected.");
    }

    @Test
    public void failsIfNoBuyTriggerFactorySpecified() {
        this.builder.setBuyTriggerFactory(null);

        try {
            this.builder.build();
        }
        catch(StrategyInitializationException ex) {
            Assert.assertEquals("The buy trigger factory must be specified.", ex.getMessage());
            return;
        }

        Assert.fail("StrategyInitializationException expected.");
    }

    @Test
    public void failsIfNoSellTriggerFactorySpecified() {
        this.builder.setSellTriggerFactory(null);

        try {
            this.builder.build();
        }
        catch(StrategyInitializationException ex) {
            Assert.assertEquals("The sell trigger factory must be specified.", ex.getMessage());
            return;
        }

        Assert.fail("StrategyInitializationException expected.");
    }

    @Test
    public void failsIfNoResetTriggerFactorySpecified() {
        this.builder.setResetTriggerFactory(null);

        try {
            this.builder.build();
        }
        catch(StrategyInitializationException ex) {
            Assert.assertEquals("The reset trigger factory must be specified.", ex.getMessage());
            return;
        }

        Assert.fail("StrategyInitializationException expected.");
    }
}
