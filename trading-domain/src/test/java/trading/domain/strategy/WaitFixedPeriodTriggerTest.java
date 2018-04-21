package trading.domain.strategy;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import trading.domain.Amount;
import trading.domain.DayCount;
import trading.domain.ISIN;
import trading.domain.market.HistoricalMarketData;

public class WaitFixedPeriodTriggerTest {
    private HistoricalMarketData historicalMarketData;

    @Before
    public void before() {
        this.historicalMarketData = new HistoricalMarketData(ISIN.MunichRe, new Amount(1000.0));
    }

    @Test
    public void triggerFiresImmediately_ifWaitDaysBeforeFireIsZero() {
        WaitFixedPeriodTrigger waitFixedPeriodTrigger = new WaitFixedPeriodTrigger(this.historicalMarketData, new DayCount(0));

        Assert.assertTrue(waitFixedPeriodTrigger.checkFires());
    }

    @Test
    public void triggerDoesNotFireImmediately_ifWaitDaysBeforeFireIsOne() {
        WaitFixedPeriodTrigger waitFixedPeriodTrigger = new WaitFixedPeriodTrigger(this.historicalMarketData, new DayCount(1));

        Assert.assertFalse(waitFixedPeriodTrigger.checkFires());
    }

    @Test
    public void triggerFiresAfterOneDay_ifWaitDaysBeforeFireIsOne() {
        WaitFixedPeriodTrigger waitFixedPeriodTrigger = new WaitFixedPeriodTrigger(this.historicalMarketData, new DayCount(1));

        historicalMarketData.registerClosedDay(new Amount(1100.0));

        Assert.assertTrue(waitFixedPeriodTrigger.checkFires());
    }
}
