package trading.account;

import org.junit.Assert;
import org.junit.Test;
import trading.Amount;
import trading.ISIN;
import trading.Quantity;

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
}
