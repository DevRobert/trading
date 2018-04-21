package trading.domain;

import org.junit.Assert;
import org.junit.Test;

public class QuantityTest {
    @Test
    public void equalsReturnsTrue_forEqualQuantities() {
        Quantity a = new Quantity(1);
        Quantity b = new Quantity(1);
        Assert.assertTrue(a.equals(b));
    }

    @Test
    public void equalsReturnsFalse_forUnequalQuantities() {
        Quantity a = new Quantity(1);
        Quantity b = new Quantity(2);
        Assert.assertFalse(a.equals(b));
    }

    @Test
    public void toStringReturnsValueAsString() {
        Quantity q = new Quantity(99);
        Assert.assertEquals("99", q.toString());
    }

    @Test
    public void subtract() {
        Quantity a = new Quantity(5);
        Quantity b = new Quantity(2);
        Quantity result = a.subtract(b);
        Assert.assertEquals(3, result.getValue());
    }

    @Test
    public void isZeroReturnsTrue_ifZeroValue() {
        Assert.assertTrue(new Quantity(0).isZero());
    }

    @Test
    public void isZeroReturnsFalse_ifNonZeroValue() {
        Assert.assertFalse(new Quantity(1).isZero());
    }
}
