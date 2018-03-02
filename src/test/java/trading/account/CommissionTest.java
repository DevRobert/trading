package trading.account;

import org.junit.Assert;
import org.junit.Test;
import trading.*;

public class CommissionTest extends AccountTestBase {
    @Test
    public void accountIsInitializedWithZeroCommissions() {
        Assert.assertEquals(Amount.Zero, account.getCommissions());
    }

    @Test
    public void commissionOfBuyTransactionIsRegistered() throws AccountStateException {
        Amount fullPrice = new Amount(100.0);
        Amount commission = new Amount(20.0);
        Transaction transaction = new Transaction(TransactionType.Buy, ISIN.MunichRe, new Quantity(1), fullPrice, commission);
        account.registerTransaction(transaction);

        Assert.assertEquals(commission, account.getCommissions());
    }

    @Test
    public void commissionOfSellTransactionIsRegistered() throws AccountStateException {
        ISIN isin = ISIN.MunichRe;
        Quantity quantity = new Quantity(1);
        Amount fullBuyPrice = new Amount(1000.0);
        Amount fullSellPrice = new Amount(2000.0);
        Amount buyCommission = new Amount(10.0);
        Amount sellCommission = new Amount(10.0);

        Transaction buyTransaction = new Transaction(TransactionType.Buy, isin, quantity, fullBuyPrice, buyCommission);
        Transaction sellTransaction = new Transaction(TransactionType.Sell, isin, quantity, fullSellPrice, sellCommission);

        account.registerTransaction(buyTransaction);
        account.registerTransaction(sellTransaction);

        Assert.assertEquals(new Amount(20.0), account.getCommissions());
    }
}
