package trading;

import org.junit.Assert;
import org.junit.Test;

public class TransactionTest {
    // Initialization

    @Test
    public void initializationWithNegativeQuantityFails() {
        Amount totalPrice = new Amount(10000.0);
        Amount commission = new Amount(500.0);

        try {
            new Transaction(TransactionType.Buy, ISIN.MunichRe, new Quantity(-1), totalPrice, commission);
        }
        catch(RuntimeException ex) {
            Assert.assertEquals("The transaction quantity must not be negative.", ex.getMessage());
            return;
        }

        Assert.fail("RuntimeException expected.");
    }

    @Test
    public void initializationWithZeroQuantityFails() {
        Amount totalPrice = new Amount(10000.0);
        Amount commission = new Amount(500.0);

        try {
            new Transaction(TransactionType.Buy, ISIN.MunichRe, new Quantity(0), totalPrice, commission);
        }
        catch(RuntimeException ex) {
            Assert.assertEquals("The transaction quantity must not be zero.", ex.getMessage());
            return;
        }

        Assert.fail("RuntimeException expected.");
    }

    // toString

    @Test
    public void toString_ifBuy() {
        ISIN isin = new ISIN("ISIN");
        Amount totalPrice = new Amount(10000.0);
        Amount commission = new Amount(500.0);

        Transaction transaction = new Transaction(TransactionType.Buy, isin, new Quantity(10), totalPrice, commission);
        Assert.assertEquals("Buy 10 ISIN for total 10000.0 plus 500.0 commission", transaction.toString());
    }

    @Test
    public void toString_ifSell() {
        ISIN isin = new ISIN("ISIN");
        Amount totalPrice = new Amount(10000.0);
        Amount commission = new Amount(500.0);

        Transaction transaction = new Transaction(TransactionType.Sell, isin, new Quantity(10), totalPrice, commission);
        Assert.assertEquals("Sell 10 ISIN for total 10000.0 plus 500.0 commission", transaction.toString());
    }
}
