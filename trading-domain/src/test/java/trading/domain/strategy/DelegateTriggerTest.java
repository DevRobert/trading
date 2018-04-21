package trading.domain.strategy;

import org.junit.Assert;
import org.junit.Test;

public class DelegateTriggerTest {
    @Test
    public void constructionFails_ifNoConditionSpecified() {
        DelegateTriggerCondition condition = null;

        try {
            new DelegateTrigger(condition);
        }
        catch(RuntimeException ex) {
            Assert.assertEquals("The condition must be specified.", ex.getMessage());
            return;
        }

        Assert.fail("RuntimeException expected.");
    }

    @Test
    public void fires_ifConditionReturnsTrue() {
        DelegateTriggerCondition delegateTriggerCondition = () -> true;
        DelegateTrigger delegateTrigger = new DelegateTrigger(delegateTriggerCondition);
        Assert.assertTrue(delegateTrigger.checkFires());
    }

    @Test
    public void doesNotFire_ifConditionReturnsFalse() {
        DelegateTriggerCondition delegateTriggerCondition = () -> false;
        DelegateTrigger delegateTrigger = new DelegateTrigger(delegateTriggerCondition);
        Assert.assertFalse(delegateTrigger.checkFires());
    }
}
