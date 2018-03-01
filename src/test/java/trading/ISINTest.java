package trading;

import org.junit.Assert;
import org.junit.Test;

public class ISINTest {
    @Test
    public void testEqualISINs() {
        ISIN a = new ISIN("A");
        ISIN b = new ISIN("A");
        Assert.assertTrue(a.equals(b));
    }

    @Test
    public void testUnequalISINs() {
        ISIN a = new ISIN("A");
        ISIN b = new ISIN("B");
        Assert.assertFalse(a.equals(b));
    }
}
