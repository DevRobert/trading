package trading.domain.strategy.progressive;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import trading.domain.Amount;
import trading.domain.DayCount;
import trading.domain.ISIN;
import trading.domain.market.HistoricalMarketData;
import trading.domain.strategy.StrategyInitializationException;
import trading.domain.strategy.WaitFixedPeriodTrigger;

import java.time.LocalDate;

public class ProgressiveTradingStrategyParametersTest {
    private ProgressiveTradingStrategyParametersBuilder builder;

    @Before
    public void before() {
        HistoricalMarketData historicalMarketData = new HistoricalMarketData(ISIN.MunichRe, new Amount(1000.0), LocalDate.now());

        this.builder = new ProgressiveTradingStrategyParametersBuilder();
        this.builder.setISIN(ISIN.MunichRe);
        this.builder.setBuyTriggerFactory(isin -> new WaitFixedPeriodTrigger(historicalMarketData, new DayCount(1)));
        this.builder.setSellTriggerFactory(isin -> new WaitFixedPeriodTrigger(historicalMarketData, new DayCount(1)));
        this.builder.setResetTriggerFactory(isin -> new WaitFixedPeriodTrigger(historicalMarketData, new DayCount(1)));
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
