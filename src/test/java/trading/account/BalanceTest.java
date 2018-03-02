package trading.account;

import org.junit.Assert;
import org.junit.Test;
import trading.*;

public class BalanceTest extends AccountTestBase {
    @Test
    public void balanceIsInitializedWithInitialAvailableMoney() {
        Assert.assertEquals(new Amount(10000.0), account.getBalance());
    }

    @Test
    public void buyTransactionReducesBalanceByCommission() throws StateException {
        prepareAccountWithBuyTransaction();

        Assert.assertEquals(new Amount(9990.0), account.getBalance());
    }

    @Test
    public void sellTransactionReducesBalanceByCommissionForZeroMargin() throws StateException {
        this.prepareAccountWithBuyTransaction();

        // Intermediate balance = 9,990

        Amount fullPrice = new Amount(5000.0);
        Amount commission = new Amount(10.0);

        Transaction transaction = new Transaction(TransactionType.Sell, ISIN.MunichRe, new Quantity(10), fullPrice, commission);
        account.registerTransaction(transaction);

        // New balance = 9,990 - 10 = 9,980

        Assert.assertEquals(new Amount(9980.0), account.getBalance());
    }

    @Test
    public void sellTransactionIncreasesBalanceByPositiveMarginAndZeroCommissions() throws StateException {
        this.prepareAccountWithBuyTransaction();

        // Intermediate balance = 9,990

        Amount fullPrice = new Amount(6000.0);
        Amount commission = Amount.Zero;

        Transaction transaction = new Transaction(TransactionType.Sell, ISIN.MunichRe, new Quantity(10), fullPrice, commission);
        account.registerTransaction(transaction);

        // New balance = 9,990 + 1,000 = 10,990

        Assert.assertEquals(new Amount(10990.0), account.getBalance());
    }

    @Test
    public void sellTransactionDecreasesBalanceByNegativeMarginAndZeroCommissions() throws StateException {
        this.prepareAccountWithBuyTransaction();

        // Intermediate balance = 9,990

        Amount fullPrice = new Amount(4000.0);
        Amount commission = Amount.Zero;

        Transaction transaction = new Transaction(TransactionType.Sell, ISIN.MunichRe, new Quantity(10), fullPrice, commission);
        account.registerTransaction(transaction);

        // New balance = 9,990 - 1,000 = 8,990

        Assert.assertEquals(new Amount(8990.0), account.getBalance());
    }
}
