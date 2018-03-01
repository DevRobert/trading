package trading;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class AccountTest {
    private Account account;

    @Before()
    public void before() {
        Amount startingBalance = new Amount(10000.0);
        this.account = new Account(startingBalance);
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
    public void accountStartsWithZeroCommissions() {
        Assert.assertEquals(Amount.Zero, account.getCommissions());
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
    public void sellTransactionWithoutPrecedingBuyTransactionIsDenied() {
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
    public void partialSellTransactionIsDenied() throws StateException {
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
    public void exceedingSellTransactionIsDenied() throws StateException {
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
    public void buyTransactionForUncompensatedPositionIsDenied() throws StateException {
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
    public void buyTransactionForCompensatedPositionIsAccepted() throws StateException {
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
}
