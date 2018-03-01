package trading;

import org.junit.Assert;
import org.junit.Test;

public class QuantityTest {
    @Test
    public void testEqualQuantities() {
        Quantity a = new Quantity(1);
        Quantity b = new Quantity(1);
        Assert.assertTrue(a.equals(b));
    }

    @Test
    public void testUnequalQuantities() {
        Quantity a = new Quantity(1);
        Quantity b = new Quantity(2);
        Assert.assertFalse(a.equals(b));
    }

    @Test
    public void testToString() {
        Quantity q = new Quantity(99);
        Assert.assertEquals("99", q.toString());
    }

    @Test
    public void testSubtract() {
        Quantity a = new Quantity(5);
        Quantity b = new Quantity(2);
        Quantity result = a.subtract(b);
        Assert.assertEquals(3, result.getValue());
    }

    @Test
    public void testIsZeroTrue() {
        Assert.assertTrue(new Quantity(0).isZero());
    }

    @Test
    public void testIsZeroFalse() {
        Assert.assertFalse(new Quantity(1).isZero());
    }
}
