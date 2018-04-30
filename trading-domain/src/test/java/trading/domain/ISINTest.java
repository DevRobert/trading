package trading.domain;

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

    @Test
    public void initializationFails_ifTextNotSpecified() {
        try {
            new ISIN(null);
        }
        catch(DomainException ex) {
            Assert.assertEquals("The ISIN text must be specified.", ex.getMessage());
            return;
        }

        Assert.fail("DomainException expected.");
    }

    @Test
    public void initializationFails_ifTextEmpty() {
        try {
            new ISIN("");
        }
        catch(DomainException ex) {
            Assert.assertEquals("The ISIN text must not be empty.", ex.getMessage());
            return;
        }

        Assert.fail("DomainException expected.");
    }
}
