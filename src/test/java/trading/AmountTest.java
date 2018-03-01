package trading;

import org.junit.Assert;
import org.junit.Test;

public class AmountTest {
    @Test
    public void testEqualAmounts() {
        Amount a = new Amount(1.0);
        Amount b = new Amount(1.0);
        Assert.assertTrue(a.equals(b));
    }

    @Test
    public void testUnequalAmounts() {
        Amount a = new Amount(1.0);
        Amount b = new Amount(2.0);
        Assert.assertFalse(a.equals(b));
    }

    @Test
    public void testAddAmount() {
        Amount a = new Amount(1.0);
        Amount b = new Amount(2.0);
        Amount result = a.add(b);
        Assert.assertEquals(3.0, result.getValue(), 0.0);
    }

    @Test
    public void testSubtractAmount() {
        Amount a = new Amount(3.0);
        Amount b = new Amount(2.0);
        Amount result = a.subtract(b);
        Assert.assertEquals(1.0, result.getValue(), 0.0);
    }

    @Test
    public void testToString() {
        Amount a = new Amount(1234.567);
        Assert.assertEquals("1234.567", a.toString());
    }
}
