package trading.domain.strategy;

import org.junit.Assert;
import org.junit.Test;

public class NotImplementedTriggerTest {
    @Test
    public void throwsNotImplementedIfActivated() {
        try {
            new NotImplementedTrigger();
        }
        catch(RuntimeException ex) {
            return;
        }

        Assert.fail("RuntimeException expected.");
    }
}
