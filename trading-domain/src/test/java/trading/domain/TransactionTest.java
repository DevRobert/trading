package trading.domain;

import org.junit.Assert;
import org.junit.Test;

public class TransactionTest {
    // Initialization

    @Test
    public void initializationFails_ifTransactionTypeNotSpecified() {
        try {
            new Transaction(null, ISIN.MunichRe, new Quantity(10), new Amount(1000.0), new Amount(20.0));
        }
        catch(RuntimeException ex) {
            Assert.assertEquals("The transaction type must be specified.", ex.getMessage());
            return;
        }

        Assert.fail("RuntimeException expected.");
    }

    @Test
    public void initializationFails_ifIsinNotSpecified() {
        try {
            new Transaction(TransactionType.Buy, null, new Quantity(10), new Amount(1000.0), new Amount(20.0));
        }
        catch(RuntimeException ex) {
            Assert.assertEquals("The transaction ISIN must be specified.", ex.getMessage());
            return;
        }

        Assert.fail("RuntimeException expected.");
    }

    @Test
    public void initializationFails_ifQuantityNegative() {
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
    public void initializationFails_ifQuantityZero() {
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

    @Test
    public void initializationFails_ifTotalPriceNotSpecified() {
        try {
            new Transaction(TransactionType.Buy, ISIN.MunichRe, new Quantity(1), null, new Amount(20.0));
        }
        catch(RuntimeException e) {
            Assert.assertEquals("The transaction total price must be specified.", e.getMessage());
            return;
        }

        Assert.fail("RuntimeException expected.");
    }

    @Test
    public void initialzationFails_ifCommissionNotSpecified() {
        try {
            new Transaction(TransactionType.Buy, ISIN.MunichRe, new Quantity(1), new Amount(1000.0), null);
        }
        catch(RuntimeException e) {
            Assert.assertEquals("The transaction commission must be specified.", e.getMessage());
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
