package trading.strategy;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class NotImplementedTriggerTest {
    private NotImplementedTrigger notImplementedTrigger;

    @Before
    public void before() {
        this.notImplementedTrigger = new NotImplementedTrigger();
    }

    @Test
    public void throwsNotImplementedIfActivated() {
        try {
            this.notImplementedTrigger.activateTrigger();
        }
        catch(NotImplementedException ex) {
            return;
        }

        Assert.fail("NotImplementedException expected.");
    }

    @Test
    public void throwsNotImplementedIfFiresChecked() {
        try {
            this.notImplementedTrigger.checkFires();
        }
        catch(NotImplementedException ex) {
            return;
        }

        Assert.fail("NotImplementedException expected.");
    }
}
