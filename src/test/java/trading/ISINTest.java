package trading;

import org.junit.Assert;
import org.junit.Test;

public class ISINTest {
    @Test
    public void returnsText() {
        String text = "A";
        ISIN isin = new ISIN(text);
        Assert.assertEquals(text, isin.getText());
    }

    @Test
    public void equalsReturnsTrueForEqualISINs() {
        ISIN a = new ISIN("A");
        ISIN b = new ISIN("A");
        Assert.assertTrue(a.equals(b));
    }

    @Test
    public void equalsReturnsFalseForUnequalISINs() {
        ISIN a = new ISIN("A");
        ISIN b = new ISIN("B");
        Assert.assertFalse(a.equals(b));
    }

    @Test
    public void toStringReturnsISIN() {
        ISIN a = new ISIN("A");
        Assert.assertEquals("A", a.toString());
    }
}
