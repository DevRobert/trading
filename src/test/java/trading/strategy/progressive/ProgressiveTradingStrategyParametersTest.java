package trading.strategy.progressive;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import trading.ISIN;
import trading.strategy.StrategyInitializationException;
import trading.strategy.WaitFixedPeriodTrigger;

public class ProgressiveTradingStrategyParametersTest {
    private ProgressiveTradingStrategyParametersBuilder builder;

    @Before
    public void before() {
        this.builder = new ProgressiveTradingStrategyParametersBuilder();
        this.builder.setISIN(ISIN.MunichRe);
        this.builder.setBuyTrigger(new WaitFixedPeriodTrigger(1));
        this.builder.setSellTrigger(new WaitFixedPeriodTrigger(1));
        this.builder.setResetTrigger(new WaitFixedPeriodTrigger(1));
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
    public void failsIfNoBuyTriggerSpecified() {
        this.builder.setBuyTrigger(null);

        try {
            this.builder.build();
        }
        catch(StrategyInitializationException ex) {
            Assert.assertEquals("The buy trigger must be specified.", ex.getMessage());
            return;
        }

        Assert.fail("StrategyInitializationException expected.");
    }

    @Test
    public void failsIfNoSellTriggerSpecified() {
        this.builder.setSellTrigger(null);

        try {
            this.builder.build();
        }
        catch(StrategyInitializationException ex) {
            Assert.assertEquals("The sell trigger must be specified.", ex.getMessage());
            return;
        }

        Assert.fail("StrategyInitializationException expected.");
    }

    @Test
    public void failsIfNoResetTriggerSpecified() {
        this.builder.setResetTrigger(null);

        try {
            this.builder.build();
        }
        catch(StrategyInitializationException ex) {
            Assert.assertEquals("The reset trigger must be specified.", ex.getMessage());
            return;
        }

        Assert.fail("StrategyInitializationException expected.");
    }
}
