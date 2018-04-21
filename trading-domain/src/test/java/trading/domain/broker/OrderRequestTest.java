package trading.domain.broker;

import org.junit.Assert;
import org.junit.Test;
import trading.domain.ISIN;
import trading.domain.Quantity;

public class OrderRequestTest {
    // Initialization

    @Test
    public void initializationWithNegativeQuantityFails() {
        try {
            new OrderRequest(OrderType.BuyMarket, ISIN.MunichRe, new Quantity(-1));
        }
        catch(RuntimeException ex) {
            Assert.assertEquals("The quantity must not be negative.", ex.getMessage());
            return;
        }

        Assert.fail("RuntimeException expected.");
    }

    @Test
    public void initializationWithZeroQuantityFails() {
        try {
            new OrderRequest(OrderType.BuyMarket, ISIN.MunichRe, new Quantity(0));
        }
        catch(RuntimeException ex) {
            Assert.assertEquals("The quantity must not be zero.", ex.getMessage());
            return;
        }

        Assert.fail("RuntimeException expected.");
    }
}
