package trading.account;

import org.junit.Assert;
import org.junit.Test;
import trading.*;

public class AvailableMoneyTest extends AccountTestBase {
    @Test()
    public void accountIsInitializedWithSpecifiedAvailableMoney() {
        Assert.assertEquals(new Amount(10000.0), account.getAvailableMoney());
    }

    @Test
    public void buyTransactionReducesAvailableMoney() throws AccountStateException {
        // Initial available money: 10,000

        Amount totalPrice = new Amount(1000.0);
        Amount commission = new Amount(10.0);

        Transaction transaction = new Transaction(TransactionType.Buy, ISIN.MunichRe, new Quantity(1), totalPrice, commission);

        account.registerTransaction(transaction);

        // Expected available money: 10,000 - 1,000 - 10 = 8,990

        Assert.assertEquals(new Amount(8990.0), account.getAvailableMoney());
    }

    @Test
    public void sellTransactionIncreasesAvailableMoney() throws AccountStateException {
        // Initial available money: 10,000

        Amount totalBuyPrice = new Amount(1000.0);
        Amount buyCommission = new Amount(10.0);

        Transaction buyTransaction = new Transaction(TransactionType.Buy, ISIN.MunichRe, new Quantity(1), totalBuyPrice, buyCommission);

        account.registerTransaction(buyTransaction);

        // Interim available money: 10,000 - 1,000 - 10 = 8,990

        Amount totalSellPrice = new Amount(2000.0);
        Amount sellCommission = new Amount(20.0);

        Transaction sellTransaction = new Transaction(TransactionType.Sell, ISIN.MunichRe, new Quantity(1), totalSellPrice, sellCommission);

        account.registerTransaction(sellTransaction);

        // Expected available money: 8,990 + 2,000 - 20 = 10,970

        Assert.assertEquals(new Amount(10970.0), account.getAvailableMoney());
    }

    @Test
    public void buyTransactionFailsIfNotEnoughAvailableMoneyForTotalPrice() {
        Amount buyTotalPrice = new Amount(11000.0);
        Amount buyCommission = new Amount(1000.0);

        Transaction transaction = new Transaction(TransactionType.Buy, ISIN.MunichRe, new Quantity(1), buyTotalPrice, buyCommission);

        try {
            account.registerTransaction(transaction);
        } catch (AccountStateException ex) {
            Assert.assertEquals("The transaction amount (total price and commission) exceeds the available money.", ex.getMessage());
            return;
        }

        Assert.fail("AccountStateException expected.");
    }

    @Test
    public void buyTransactionFailsIfEnoughAvailableMoneyForTotalPriceButNotForCommission() {
        Amount buyTotalPrice = new Amount(10000.0);
        Amount buyCommission = new Amount(1000.0);

        Transaction transaction = new Transaction(TransactionType.Buy, ISIN.MunichRe, new Quantity(1), buyTotalPrice, buyCommission);

        try {
            account.registerTransaction(transaction);
        } catch (AccountStateException ex) {
            Assert.assertEquals("The transaction amount (total price and commission) exceeds the available money.", ex.getMessage());
            return;
        }

        Assert.fail("AccountStateException expected.");
    }

    @Test
    public void buyTransactionSucceedsIfExactlyEnoughMoneyAvailable() throws AccountStateException {
        Amount buyTotalPrice = new Amount(9000.0);
        Amount buyCommission = new Amount(1000.0);

        Transaction transaction = new Transaction(TransactionType.Buy, ISIN.MunichRe, new Quantity(1), buyTotalPrice, buyCommission);
        account.registerTransaction(transaction);

        Assert.assertEquals(new Amount(0.0), account.getAvailableMoney());
    }

    @Test
    public void buyTransactionSucceedsIfMoreThanEnoughMoneyAvailable() throws AccountStateException {
        Amount buyTotalPrice = new Amount(8000.0);
        Amount buyCommission = new Amount(1000.0);

        Transaction transaction = new Transaction(TransactionType.Buy, ISIN.MunichRe, new Quantity(1), buyTotalPrice, buyCommission);
        account.registerTransaction(transaction);

        Assert.assertEquals(new Amount(1000.0), account.getAvailableMoney());
    }

    @Test
    public void sellTransactionSucceedsEvenIfCommissionExceedsTotalPriceAndNoMoneyIsAvailable() throws AccountStateException {
        Amount buyTotalPrice = new Amount(9000.0);
        Amount buyCommission = new Amount(1000.0);

        Transaction buyTransaction = new Transaction(TransactionType.Buy, ISIN.MunichRe, new Quantity(1), buyTotalPrice, buyCommission);
        account.registerTransaction(buyTransaction);

        // Interim available money: 10,000 - 9,000 - 1,000 = 0

        Amount sellTotalPrice = new Amount(5.0);
        Amount sellCommission = new Amount(10.0);
        Transaction sellTransaction = new Transaction(TransactionType.Sell, ISIN.MunichRe, new Quantity(1), sellTotalPrice, sellCommission);
        account.registerTransaction(sellTransaction);

        // Available money: 0 + 5 - 10 = -5

        Assert.assertEquals(new Amount(-5.0), account.getAvailableMoney());
    }
}
