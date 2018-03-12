package trading.strategy;

import org.junit.Assert;
import org.junit.Test;

public class NeverFiresTriggerTest {
    @Test
    public void fires() {
        Trigger trigger = new NeverFiresTrigger();
        Assert.assertFalse(trigger.checkFires());
    }
}
