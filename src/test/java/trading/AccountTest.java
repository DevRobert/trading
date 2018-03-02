package trading;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class AccountTest {
    private Account account;

    @Before()
    public void before() {
        Amount availableMoney = new Amount(10000.0);
        this.account = new Account(availableMoney);
    }

    @Test()
    public void accountIsInitializedWithSpecifiedAvailableMoney() {
        Assert.assertEquals(new Amount(10000.0), account.getAvailableMoney());
    }

    @Test
    public void accountIsInitializedWithZeroCommissions() {
        Assert.assertEquals(Amount.Zero, account.getCommissions());
    }

    @Test
    public void buyTransactionLeadsToNewPosition() throws StateException {
        ISIN isin = ISIN.MunichRe;
        Quantity quantity = new Quantity(1000);
        Amount fullPrice = new Amount(2000.0);
        Amount commission = new Amount(20.0);

        Transaction transaction = new Transaction(TransactionType.Buy, isin, quantity, fullPrice, commission);
        account.registerTransaction(transaction);

        Position position = account.getPosition(isin);

        Assert.assertEquals(isin, position.getISIN());
        Assert.assertEquals(quantity, position.getQuantity());
        Assert.assertEquals(fullPrice, position.getFullMarketPrice());
    }

    @Test
    public void commissionOfBuyTransactionIsRegistered() throws StateException {
        Amount fullPrice = new Amount(100.0);
        Amount commission = new Amount(20.0);
        Transaction transaction = new Transaction(TransactionType.Buy, ISIN.MunichRe, new Quantity(1), fullPrice, commission);
        account.registerTransaction(transaction);

        Assert.assertEquals(commission, account.getCommissions());
    }

    @Test
    public void sellTransactionCompensatesExistingPosition() throws StateException {
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

        Position position = account.getPosition(isin);
        Assert.assertEquals(Quantity.Zero, position.getQuantity());
        Assert.assertEquals(Amount.Zero, position.getFullMarketPrice());
    }

    @Test
    public void commissionOfSellTransactionIsRegistered() throws StateException {
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

    @Test
    public void sellTransactionWithoutPrecedingBuyTransactionFails() {
        ISIN isin = ISIN.MunichRe;
        Quantity quantity = new Quantity(1);
        Amount totalPrice = new Amount(1000.0);
        Amount commission = new Amount(10.0);

        Transaction transaction = new Transaction(TransactionType.Sell, isin, quantity, totalPrice, commission);

        try {
            account.registerTransaction(transaction);
        }
        catch(StateException ex) {
            Assert.assertEquals("The sell transaction could not be processed because there was no respective position found.", ex.getMessage());
            return;
        }

        Assert.fail("StateException expected.");
    }

    @Test
    public void partialSellTransactionFails() throws StateException {
        ISIN isin = ISIN.MunichRe;
        Amount fullBuyPrice = new Amount(1000.0);
        Amount fullSellPrice = new Amount(2000.0);
        Amount buyCommission = new Amount(10.0);
        Amount sellCommission = new Amount(10.0);

        Transaction buyTransaction = new Transaction(TransactionType.Buy, isin, new Quantity(2), fullBuyPrice, buyCommission);
        Transaction sellTransaction = new Transaction(TransactionType.Sell, isin, new Quantity(1), fullSellPrice, sellCommission);

        account.registerTransaction(buyTransaction);

        try {
            account.registerTransaction(sellTransaction);
        }
        catch(StateException ex) {
            Assert.assertEquals("Partial sell transactions are not supported.", ex.getMessage());
            return;
        }

        Assert.fail("StateException expected.");
    }

    @Test
    public void exceedingSellTransactionFails() throws StateException {
        ISIN isin = ISIN.MunichRe;
        Amount fullBuyPrice = new Amount(1000.0);
        Amount fullSellPrice = new Amount(2000.0);
        Amount buyCommission = new Amount(10.0);
        Amount sellCommission = new Amount(10.0);

        Transaction buyTransaction = new Transaction(TransactionType.Buy, isin, new Quantity(1), fullBuyPrice, buyCommission);
        Transaction sellTransaction = new Transaction(TransactionType.Sell, isin, new Quantity(2), fullSellPrice, sellCommission);

        account.registerTransaction(buyTransaction);

        try {
            account.registerTransaction(sellTransaction);
        }
        catch(StateException ex) {
            Assert.assertEquals("The sell transaction states a higher quantity than the position has.", ex.getMessage());
            return;
        }

        Assert.fail("StateException expected.");
    }

    @Test
    public void buyTransactionForUncompensatedPositionFails() throws StateException {
        ISIN isin = ISIN.MunichRe;
        Amount fullBuyPrice = new Amount(1000.0);
        Amount buyCommission = new Amount(10.0);

        Transaction buyTransaction = new Transaction(TransactionType.Buy, isin, new Quantity(2), fullBuyPrice, buyCommission);
        Transaction furtherBuyTransaction = new Transaction(TransactionType.Buy, isin, new Quantity(2), fullBuyPrice, buyCommission);

        account.registerTransaction(buyTransaction);

        try {
            account.registerTransaction(furtherBuyTransaction);
        }
        catch(StateException ex) {
            Assert.assertEquals("Subsequent buy transactions for uncompensated positions are not supported.", ex.getMessage());
            return;
        }

        Assert.fail("StateException expected.");
    }

    @Test
    public void buyTransactionForCompensatedPositionFails() throws StateException {
        ISIN isin = ISIN.MunichRe;
        Amount fullBuyPrice = new Amount(1000.0);
        Amount fullSellPrice = new Amount(2000.0);
        Amount furtherBuyPrice = new Amount(3000.0);
        Amount buyCommission = new Amount(10.0);
        Amount sellCommission = new Amount(10.0);

        Transaction buyTransaction = new Transaction(TransactionType.Buy, isin, new Quantity(2), fullBuyPrice, buyCommission);
        Transaction sellTransaction = new Transaction(TransactionType.Sell, isin, new Quantity(2), fullSellPrice, sellCommission);
        Transaction furtherBuyTransaction = new Transaction(TransactionType.Buy, isin, new Quantity(5), furtherBuyPrice, sellCommission);

        account.registerTransaction(buyTransaction);
        account.registerTransaction(sellTransaction);
        account.registerTransaction(furtherBuyTransaction);

        Position position = account.getPosition(isin);
        Assert.assertEquals(furtherBuyPrice, position.getFullMarketPrice());
        Assert.assertEquals(new Quantity(5), position.getQuantity());
    }

    @Test
    public void buyTransactionReducesAvailableMoney() throws StateException {
        // Initial available money: 10,000

        Amount totalPrice = new Amount(1000.0);
        Amount commission = new Amount(10.0);

        Transaction transaction = new Transaction(TransactionType.Buy, ISIN.MunichRe, new Quantity(1), totalPrice, commission);

        account.registerTransaction(transaction);

        // Expected available money: 10,000 - 1,000 - 10 = 8,990

        Assert.assertEquals(new Amount(8990.0), account.getAvailableMoney());
    }

    @Test
    public void sellTransactionIncreasesAvailableMoney() throws StateException {
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
    public void buyTransactionFailsIfNotEnoughAvailableMoney() {
        throw new NotImplementedException();
    }

    @Test
    public void buyTransactionSucceedsForExactlyEnoughAvailableMoney() {
        throw new NotImplementedException();
    }
}
