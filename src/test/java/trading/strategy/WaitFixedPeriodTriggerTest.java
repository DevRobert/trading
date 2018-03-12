package trading.strategy;

import org.junit.Assert;
import org.junit.Test;
import trading.DayCount;

public class WaitFixedPeriodTriggerTest {
    @Test
    public void triggerFiresImmediately_ifWaitDaysBeforeFireIsZero() {
        WaitFixedPeriodTrigger waitFixedPeriodTrigger = new WaitFixedPeriodTrigger(new DayCount(0));

        Assert.assertTrue(waitFixedPeriodTrigger.checkFires());
    }

    @Test
    public void triggerDoesNotFireImmediately_ifWaitDaysBeforeFireIsOne() {
        WaitFixedPeriodTrigger waitFixedPeriodTrigger = new WaitFixedPeriodTrigger(new DayCount(1));

        Assert.assertFalse(waitFixedPeriodTrigger.checkFires());
    }

    @Test
    public void triggerFiresAfterOneDay_ifWaitDaysBeforeFireIsOne() {
        WaitFixedPeriodTrigger waitFixedPeriodTrigger = new WaitFixedPeriodTrigger(new DayCount(1));

        waitFixedPeriodTrigger.notifyDayPassed();
        Assert.assertTrue(waitFixedPeriodTrigger.checkFires());
    }
}
