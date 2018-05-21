package trading.domain.account;

import org.junit.Assert;
import org.junit.Test;
import trading.domain.Amount;
import trading.domain.ISIN;
import trading.domain.Quantity;

import java.time.LocalDate;

public class BalanceTest extends AccountTestBase {
    @Test
    public void balanceIsInitializedWithInitialAvailableMoney() {
        Assert.assertEquals(new Amount(10000.0), account.getBalance());
    }

    @Test
    public void buyTransactionReducesBalanceByCommission() throws AccountStateException {
        prepareAccountWithBuyTransaction();

        Assert.assertEquals(new Amount(9990.0), account.getBalance());
    }

    @Test
    public void sellTransactionReducesBalanceByCommissionForZeroMargin() throws AccountStateException {
        this.prepareAccountWithBuyTransaction();

        // Intermediate balance = 9,990

        Amount fullPrice = new Amount(5000.0);
        Amount commission = new Amount(10.0);

        MarketTransaction transaction = new MarketTransactionBuilder()
                .setTransactionType(MarketTransactionType.Sell)
                .setIsin(ISIN.MunichRe)
                .setQuantity(new Quantity(10))
                .setTotalPrice(fullPrice)
                .setCommission(commission)
                .setDate(LocalDate.of(2000, 1, 1))
                .build();

        account.registerTransaction(transaction);

        // New balance = 9,990 - 10 = 9,980

        Assert.assertEquals(new Amount(9980.0), account.getBalance());
    }

    @Test
    public void sellTransactionIncreasesBalanceByPositiveMarginAndZeroCommissions() throws AccountStateException {
        this.prepareAccountWithBuyTransaction();

        // Intermediate balance = 9,990

        Amount fullPrice = new Amount(6000.0);
        Amount commission = Amount.Zero;

        MarketTransaction transaction = new MarketTransactionBuilder()
                .setTransactionType(MarketTransactionType.Sell)
                .setIsin(ISIN.MunichRe)
                .setQuantity(new Quantity(10))
                .setTotalPrice(fullPrice)
                .setCommission(commission)
                .setDate(LocalDate.of(2000, 1, 1))
                .build();

        account.registerTransaction(transaction);

        // New balance = 9,990 + 1,000 = 10,990

        Assert.assertEquals(new Amount(10990.0), account.getBalance());
    }

    @Test
    public void sellTransactionDecreasesBalanceByNegativeMarginAndZeroCommissions() throws AccountStateException {
        this.prepareAccountWithBuyTransaction();

        // Intermediate balance = 9,990

        Amount fullPrice = new Amount(4000.0);
        Amount commission = Amount.Zero;

        MarketTransaction transaction = new MarketTransactionBuilder()
                .setTransactionType(MarketTransactionType.Sell)
                .setIsin(ISIN.MunichRe)
                .setQuantity(new Quantity(10))
                .setTotalPrice(fullPrice)
                .setCommission(commission)
                .setDate(LocalDate.of(2000, 1, 1))
                .build();

        account.registerTransaction(transaction);

        // New balance = 9,990 - 1,000 = 8,990

        Assert.assertEquals(new Amount(8990.0), account.getBalance());
    }
}
