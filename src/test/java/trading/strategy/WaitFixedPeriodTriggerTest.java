package trading.strategy;

import org.junit.Assert;
import org.junit.Test;

public class WaitFixedPeriodTriggerTest {
    @Test
    public void triggerDoesNotFireImmediately_ifWaitDaysBeforeFireIsZero_andNotActivated() {
        WaitFixedPeriodTrigger waitFixedPeriodTrigger = new WaitFixedPeriodTrigger(0);

        Assert.assertFalse(waitFixedPeriodTrigger.checkFires());
    }

    @Test
    public void triggerFiresImmediately_ifWaitDaysBeforeFireIsZero() {
        WaitFixedPeriodTrigger waitFixedPeriodTrigger = new WaitFixedPeriodTrigger(0);

        waitFixedPeriodTrigger.activateTrigger();
        Assert.assertTrue(waitFixedPeriodTrigger.checkFires());
    }

    @Test
    public void triggerDoesNotFireImmediately_ifWaitDaysBeforeFireIsOne() {
        WaitFixedPeriodTrigger waitFixedPeriodTrigger = new WaitFixedPeriodTrigger(1);

        waitFixedPeriodTrigger.activateTrigger();
        Assert.assertFalse(waitFixedPeriodTrigger.checkFires());
    }

    @Test
    public void triggerFiresAfterOneDay_ifWaitDaysBeforeFireIsOne() {
        WaitFixedPeriodTrigger waitFixedPeriodTrigger = new WaitFixedPeriodTrigger(1);

        waitFixedPeriodTrigger.activateTrigger();
        waitFixedPeriodTrigger.notifyDayPassed();
        Assert.assertTrue(waitFixedPeriodTrigger.checkFires());
    }

    @Test
    public void triggerDoesNotFireImmediatelyAfterReset_ifWaitDaysBeforeFireIsOne() {
        WaitFixedPeriodTrigger waitFixedPeriodTrigger = new WaitFixedPeriodTrigger(1);

        waitFixedPeriodTrigger.activateTrigger();
        waitFixedPeriodTrigger.notifyDayPassed(); // should fire here

        waitFixedPeriodTrigger.activateTrigger();
        Assert.assertFalse(waitFixedPeriodTrigger.checkFires());
    }

    @Test
    public void triggerFiresOneDayAfterReset_ifWaitDaysBeforeFireIsOne() {
        WaitFixedPeriodTrigger waitFixedPeriodTrigger = new WaitFixedPeriodTrigger(1);

        waitFixedPeriodTrigger.activateTrigger();
        waitFixedPeriodTrigger.notifyDayPassed(); // should fire here

        waitFixedPeriodTrigger.activateTrigger();
        waitFixedPeriodTrigger.notifyDayPassed();
        Assert.assertTrue(waitFixedPeriodTrigger.checkFires());
    }
}
