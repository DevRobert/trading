package trading.strategy;

import org.junit.Assert;
import org.junit.Test;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class NotImplementedTriggerTest {
    @Test
    public void throwsNotImplementedIfActivated() {
        try {
            new NotImplementedTrigger();
        }
        catch(NotImplementedException ex) {
            return;
        }

        Assert.fail("NotImplementedException expected.");
    }
}
