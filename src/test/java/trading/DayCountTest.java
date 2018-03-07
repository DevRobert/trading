package trading;

import org.junit.Assert;
import org.junit.Test;

public class DayCountTest {
    @Test
    public void returnsValue() {
        DayCount dayCount = new DayCount(1);
        Assert.assertEquals(1, dayCount.getValue());
    }

    @Test
    public void equalsReturnsTrue_ifDayCountsEqual() {
        DayCount a = new DayCount(1);
        DayCount b = new DayCount(1);
        Assert.assertTrue(a.equals(b));
    }

    @Test
    public void equalsReturnsFalse_ifDayCountsUnequal() {
        DayCount a = new DayCount(1);
        DayCount b = new DayCount(2);
        Assert.assertFalse(a.equals(b));
    }

    @Test
    public void isZeroReturnsTrue_ifDayCountZero() {
        DayCount dayCount = new DayCount(0);
        Assert.assertTrue(dayCount.isZero());
    }

    @Test
    public void isZeroReturnsFalse_ifDayCountOne() {
        DayCount dayCount = new DayCount(1);
        Assert.assertFalse(dayCount.isZero());
    }

    @Test
    public void isZeroReturnsFalse_ifDayCountMinusOne() {
        DayCount dayCount = new DayCount(-1);
        Assert.assertFalse(dayCount.isZero());
    }

    @Test
    public void toStringReturnsZero_ForZeroDays() {
        DayCount dayCount = new DayCount(0);
        Assert.assertEquals("0", dayCount.toString());
    }

    @Test
    public void toStringReturnsOne_forOneDay() {
        DayCount dayCount = new DayCount(1);
        Assert.assertEquals("1", dayCount.toString());
    }
}
