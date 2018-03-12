package trading;

import org.junit.Assert;
import org.junit.Test;

public class TransactionTest {
    @Test
    public void toString_isBuy() {
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
