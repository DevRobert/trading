package trading.domain.account;

import org.junit.Assert;
import org.junit.Test;
import trading.domain.Amount;
import trading.domain.ISIN;
import trading.domain.Quantity;

import java.time.LocalDate;

public class CommissionTest extends AccountTestBase {
    @Test
    public void accountIsInitializedWithZeroCommissions() {
        Assert.assertEquals(Amount.Zero, account.getCommissions());
    }

    @Test
    public void commissionOfBuyTransactionIsRegistered() throws AccountStateException {
        Amount fullPrice = new Amount(100.0);
        Amount commission = new Amount(20.0);

        Transaction transaction = new TransactionBuilder()
                .setTransactionType(TransactionType.Buy)
                .setIsin(ISIN.MunichRe)
                .setQuantity(new Quantity(1))
                .setTotalPrice(fullPrice)
                .setCommission(commission)
                .setDate(LocalDate.of(2000, 1, 1))
                .build();

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

        Transaction buyTransaction = new TransactionBuilder()
                .setTransactionType(TransactionType.Buy)
                .setIsin(isin)
                .setQuantity(quantity)
                .setTotalPrice(fullBuyPrice)
                .setCommission(buyCommission)
                .setDate(LocalDate.of(2000, 1, 1))
                .build();

        Transaction sellTransaction = new TransactionBuilder()
                .setTransactionType(TransactionType.Sell)
                .setIsin(isin)
                .setQuantity(quantity)
                .setTotalPrice(fullSellPrice)
                .setCommission(sellCommission)
                .setDate(LocalDate.of(2000, 1, 2))
                .build();

        account.registerTransaction(buyTransaction);
        account.registerTransaction(sellTransaction);

        Assert.assertEquals(new Amount(20.0), account.getCommissions());
    }
}
