package trading.domain.account;

import org.junit.Assert;
import org.junit.Test;
import trading.domain.Amount;
import trading.domain.ISIN;
import trading.domain.Quantity;

public class PositionTest {
    @Test
    public void creationOfInitialPositionIsPending() {
        Position position = new Position(ISIN.MunichRe, Quantity.Zero, Amount.Zero);
        Assert.assertTrue(position.isCreationPending());
    }

    @Test
    public void confirmCreationOfPosition() {
        Position position = new Position(ISIN.MunichRe, Quantity.Zero, Amount.Zero);
        position.confirmCreation();
        Assert.assertFalse(position.isCreationPending());
    }

    @Test
    public void initializationWithNegativeQuantityFails() {
        try {
            new Position(ISIN.MunichRe, new Quantity(-1), Amount.Zero);
        }
        catch(RuntimeException ex) {
            Assert.assertEquals("The quantity must not be negative.", ex.getMessage());
            return;
        }

        Assert.fail("RuntimeException expected.");
    }

    @Test
    public void setNegativeQuantityFails() {
        Position position = new Position(ISIN.MunichRe, Quantity.Zero, Amount.Zero);

        try {
            position.setQuantity(new Quantity(-1));
        }
        catch(RuntimeException ex) {
            Assert.assertEquals("The quantity must not be negative.", ex.getMessage());
            return;
        }

        Assert.fail("RuntimeException expected.");
    }
}
