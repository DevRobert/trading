package trading.domain;

import org.junit.Assert;
import org.junit.Test;

public class TransactionBuilderTest {
    @Test
    public void buildTransaction() {
        Transaction transaction = new TransactionBuilder()
                .setTransactionType(TransactionType.Buy)
                .setIsin(ISIN.MunichRe)
                .setQuantity(new Quantity(10))
                .setTotalPrice(new Amount(1000.0))
                .setCommission(new Amount(20.0))
                .build();

        Assert.assertNotNull(transaction);
        Assert.assertEquals(TransactionType.Buy, transaction.getTransactionType());
        Assert.assertEquals(ISIN.MunichRe, transaction.getIsin());
        Assert.assertEquals(new Quantity(10), transaction.getQuantity());
        Assert.assertEquals(new Amount(1000.0), transaction.getTotalPrice());
        Assert.assertEquals(new Amount(20.0), transaction.getCommission());
    }
}
