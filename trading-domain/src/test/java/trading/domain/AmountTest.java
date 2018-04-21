package trading.domain;

import org.junit.Assert;
import org.junit.Test;

public class AmountTest {
    @Test
    public void returnsValue() {
        double value = 5.0;
        Amount amount = new Amount(value);
        Assert.assertEquals(value, amount.getValue(), 0.0);
    }

    @Test
    public void equalAmounts() {
        Amount a = new Amount(1.0);
        Amount b = new Amount(1.0);
        Assert.assertTrue(a.equals(b));
    }

    @Test
    public void unequalAmounts() {
        Amount a = new Amount(1.0);
        Amount b = new Amount(2.0);
        Assert.assertFalse(a.equals(b));
    }

    @Test
    public void addAmount() {
        Amount a = new Amount(1.0);
        Amount b = new Amount(2.0);
        Amount result = a.add(b);
        Assert.assertEquals(3.0, result.getValue(), 0.0);
    }

    @Test
    public void subtractAmount() {
        Amount a = new Amount(3.0);
        Amount b = new Amount(2.0);
        Amount result = a.subtract(b);
        Assert.assertEquals(1.0, result.getValue(), 0.0);
    }

    @Test
    public void multiply() {
        Amount a = new Amount(2.0);
        Quantity b = new Quantity(3);
        Amount result = a.multiply(b);
        Assert.assertEquals(new Amount(6.0), result);
    }

    @Test
    public void testToString() {
        Amount a = new Amount(1234.567);
        Assert.assertEquals("1234.567", a.toString());
    }
}
