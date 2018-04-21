package trading.domain.strategy;

import org.junit.Assert;
import org.junit.Test;

public class AlwaysFiresTriggerTest {
   @Test
   public void fires() {
       Trigger trigger = new AlwaysFiresTrigger();
       Assert.assertTrue(trigger.checkFires());
   }
}
