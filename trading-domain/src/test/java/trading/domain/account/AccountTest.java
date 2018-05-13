package trading.domain.account;

import org.junit.Assert;
import org.junit.Test;
import trading.domain.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class AccountTest extends AccountTestBase {
    @Test
    public void retrievalOfUnknownPositionFails() {
        try {
            account.getPosition(ISIN.MunichRe);
        }
        catch(PositionNotFoundException ex) {
            return;
        }

        Assert.fail("PositionNotFoundException expected.");
    }

    @Test
    public void buyTransactionLeadsToNewPosition() throws AccountStateException {
        ISIN isin = ISIN.MunichRe;
        Quantity quantity = new Quantity(1000);
        Amount fullPrice = new Amount(2000.0);
        Amount commission = new Amount(20.0);

        Transaction transaction = new TransactionBuilder()
                .setTransactionType(TransactionType.Buy)
                .setIsin(isin)
                .setQuantity(quantity)
                .setTotalPrice(fullPrice)
                .setCommission(commission)
                .setDate(LocalDate.of(2000, 1, 1))
                .build();

        account.registerTransaction(transaction);

        Position position = account.getPosition(isin);

        Assert.assertEquals(isin, position.getISIN());
        Assert.assertEquals(quantity, position.getQuantity());
        Assert.assertEquals(fullPrice, position.getFullMarketPrice());
    }

    @Test
    public void buyTransactionLeadsToConfirmedPosition() throws AccountStateException {
        Amount fullPrice = new Amount(1000.0);
        Amount commission = new Amount(10.0);

        Transaction transaction = new TransactionBuilder()
                .setTransactionType(TransactionType.Buy)
                .setIsin(ISIN.MunichRe)
                .setQuantity(new Quantity(1))
                .setTotalPrice(fullPrice)
                .setCommission(commission)
                .setDate(LocalDate.of(2000, 1, 1))
                .build();

        account.registerTransaction(transaction);

        Position position = account.getPosition(ISIN.MunichRe);
        Assert.assertFalse(position.isCreationPending());
    }

    @Test
    public void sellTransactionCompensatesExistingPosition() throws AccountStateException {
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

        Position position = account.getPosition(isin);
        Assert.assertEquals(Quantity.Zero, position.getQuantity());
        Assert.assertEquals(Amount.Zero, position.getFullMarketPrice());
    }

    @Test
    public void sellTransactionWithoutPrecedingBuyTransactionFails() {
        ISIN isin = ISIN.MunichRe;
        Quantity quantity = new Quantity(1);
        Amount totalPrice = new Amount(1000.0);
        Amount commission = new Amount(10.0);

        Transaction transaction = new TransactionBuilder()
                .setTransactionType(TransactionType.Sell)
                .setIsin(isin)
                .setQuantity(quantity)
                .setTotalPrice(totalPrice)
                .setCommission(commission)
                .setDate(LocalDate.of(2000, 1, 1))
                .build();

        try {
            account.registerTransaction(transaction);
        }
        catch(AccountStateException ex) {
            Assert.assertEquals("The sell transaction could not be processed because there was no respective position found.", ex.getMessage());
            return;
        }

        Assert.fail("AccountStateException expected.");
    }

    @Test
    public void partialSellTransactionFails() throws AccountStateException {
        ISIN isin = ISIN.MunichRe;
        Amount fullBuyPrice = new Amount(1000.0);
        Amount fullSellPrice = new Amount(2000.0);
        Amount buyCommission = new Amount(10.0);
        Amount sellCommission = new Amount(10.0);

        Transaction buyTransaction = new TransactionBuilder()
                .setTransactionType(TransactionType.Buy)
                .setIsin(isin)
                .setQuantity(new Quantity(2))
                .setTotalPrice(fullBuyPrice)
                .setCommission(buyCommission)
                .setDate(LocalDate.of(2000, 1, 1))
                .build();

        Transaction sellTransaction = new TransactionBuilder()
                .setTransactionType(TransactionType.Sell)
                .setIsin(isin)
                .setQuantity(new Quantity(1))
                .setTotalPrice(fullSellPrice)
                .setCommission(sellCommission)
                .setDate(LocalDate.of(2000, 1, 2))
                .build();

        account.registerTransaction(buyTransaction);

        try {
            account.registerTransaction(sellTransaction);
        }
        catch(AccountStateException ex) {
            Assert.assertEquals("Partial sell transactions are not supported.", ex.getMessage());
            return;
        }

        Assert.fail("AccountStateException expected.");
    }

    @Test
    public void exceedingSellTransactionFails() throws AccountStateException {
        ISIN isin = ISIN.MunichRe;
        Amount fullBuyPrice = new Amount(1000.0);
        Amount fullSellPrice = new Amount(2000.0);
        Amount buyCommission = new Amount(10.0);
        Amount sellCommission = new Amount(10.0);

        Transaction buyTransaction = new TransactionBuilder()
                .setTransactionType(TransactionType.Buy)
                .setIsin(isin)
                .setQuantity(new Quantity(1))
                .setTotalPrice(fullBuyPrice)
                .setCommission(buyCommission)
                .setDate(LocalDate.of(2000, 1, 1))
                .build();

        Transaction sellTransaction = new TransactionBuilder()
                .setTransactionType(TransactionType.Sell)
                .setIsin(isin)
                .setQuantity(new Quantity(2))
                .setTotalPrice(fullSellPrice)
                .setCommission(sellCommission)
                .setDate(LocalDate.of(2000, 1, 2))
                .build();

        account.registerTransaction(buyTransaction);

        try {
            account.registerTransaction(sellTransaction);
        }
        catch(AccountStateException ex) {
            Assert.assertEquals("The sell transaction states a higher quantity than the position has.", ex.getMessage());
            return;
        }

        Assert.fail("AccountStateException expected.");
    }

    @Test
    public void buyTransactionForNonEmptyPositionFails() throws AccountStateException {
        ISIN isin = ISIN.MunichRe;
        Amount fullBuyPrice = new Amount(1000.0);
        Amount buyCommission = new Amount(10.0);

        Transaction buyTransaction = new TransactionBuilder()
                .setTransactionType(TransactionType.Buy)
                .setIsin(isin)
                .setQuantity(new Quantity(2))
                .setTotalPrice(fullBuyPrice)
                .setCommission(buyCommission)
                .setDate(LocalDate.of(2000, 1, 1))
                .build();

        Transaction furtherBuyTransaction = new TransactionBuilder()
                .setTransactionType(TransactionType.Buy)
                .setIsin(isin)
                .setQuantity(new Quantity(2))
                .setTotalPrice(fullBuyPrice)
                .setCommission(buyCommission)
                .setDate(LocalDate.of(2000, 1, 2))
                .build();

        account.registerTransaction(buyTransaction);

        try {
            account.registerTransaction(furtherBuyTransaction);
        }
        catch(AccountStateException ex) {
            Assert.assertEquals("Subsequent buy transactions for non-empty positions are not supported.", ex.getMessage());
            return;
        }

        Assert.fail("AccountStateException expected.");
    }

    @Test
    public void buyTransactionForCompensatedPositionPasses() throws AccountStateException {
        ISIN isin = ISIN.MunichRe;
        Amount fullBuyPrice = new Amount(1000.0);
        Amount fullSellPrice = new Amount(2000.0);
        Amount furtherBuyPrice = new Amount(3000.0);
        Amount buyCommission = new Amount(10.0);
        Amount sellCommission = new Amount(10.0);

        Transaction buyTransaction = new TransactionBuilder()
                .setTransactionType(TransactionType.Buy)
                .setIsin(isin)
                .setQuantity(new Quantity(2))
                .setTotalPrice(fullBuyPrice)
                .setCommission(buyCommission)
                .setDate(LocalDate.of(2000, 1, 1))
                .build();

        Transaction sellTransaction = new TransactionBuilder()
                .setTransactionType(TransactionType.Sell)
                .setIsin(isin)
                .setQuantity(new Quantity(2))
                .setTotalPrice(fullSellPrice)
                .setCommission(sellCommission)
                .setDate(LocalDate.of(2000, 1, 2))
                .build();

        Transaction furtherBuyTransaction = new TransactionBuilder()
                .setTransactionType(TransactionType.Buy)
                .setIsin(isin)
                .setQuantity(new Quantity(5))
                .setTotalPrice(furtherBuyPrice)
                .setCommission(buyCommission)
                .setDate(LocalDate.of(2000, 1, 3))
                .build();

        account.registerTransaction(buyTransaction);
        account.registerTransaction(sellTransaction);
        account.registerTransaction(furtherBuyTransaction);

        Position position = account.getPosition(isin);
        Assert.assertEquals(furtherBuyPrice, position.getFullMarketPrice());
        Assert.assertEquals(new Quantity(5), position.getQuantity());
    }

    @Test
    public void noEmptyPositionIsCreatedForFailedTransaction() {
        Amount fullPrice = new Amount(20000.0);
        Amount commission = new Amount(10.0);

        Transaction transaction = new TransactionBuilder()
                .setTransactionType(TransactionType.Sell)
                .setIsin(ISIN.MunichRe)
                .setQuantity(new Quantity(1))
                .setTotalPrice(fullPrice)
                .setCommission(commission)
                .setDate(LocalDate.of(2000, 1, 1))
                .build();

        boolean registrationFailed = false;

        try {
            account.registerTransaction(transaction);
        }
        catch(AccountStateException ex) {
            registrationFailed = true;
        }

        if(!registrationFailed) {
            Assert.fail("AccountStateException expected.");
        }

        try {
            account.getPosition(ISIN.MunichRe);
        }
        catch(PositionNotFoundException ex) {
            return;
        }

        Assert.fail("PositionNotFoundException expected.");
    }

    @Test
    public void returnsInitiallyEmptyTransactionLists() {
        Account account = new Account(new Amount(50000.0));
        List<Transaction> transaction = account.getProcessedTransactions();
        Assert.assertEquals(0, transaction.size());
    }

    @Test
    public void returnsRegisteredTransactions() {
        Account account = new Account(new Amount(50000.0));

        Transaction firstTransaction = new TransactionBuilder()
                .setTransactionType(TransactionType.Buy)
                .setIsin(ISIN.MunichRe)
                .setQuantity(new Quantity(1))
                .setTotalPrice(new Amount(100.0))
                .setCommission(Amount.Zero)
                .setDate(LocalDate.of(2000, 1, 1))
                .build();

        Transaction secondTransaction = new TransactionBuilder()
                .setTransactionType(TransactionType.Sell)
                .setIsin(ISIN.MunichRe)
                .setQuantity(new Quantity(1))
                .setTotalPrice(new Amount(110.0))
                .setCommission(Amount.Zero)
                .setDate(LocalDate.of(2000, 1, 2))
                .build();

        account.registerTransaction(firstTransaction);
        account.registerTransaction(secondTransaction);

        Assert.assertEquals(2, account.getProcessedTransactions().size());
        Assert.assertTrue(account.getProcessedTransactions().contains(firstTransaction));
        Assert.assertTrue(account.getProcessedTransactions().contains(secondTransaction));
    }

    @Test
    public void returnsCurrentStocks() {
        Account account = new Account(new Amount(50000.0));

        Transaction firstTransaction = new TransactionBuilder()
                .setTransactionType(TransactionType.Buy)
                .setIsin(ISIN.MunichRe)
                .setQuantity(new Quantity(1))
                .setTotalPrice(new Amount(1000.0))
                .setCommission(Amount.Zero)
                .setDate(LocalDate.of(2000, 1, 1))
                .build();

        Transaction secondTransaction = new TransactionBuilder()
                .setTransactionType(TransactionType.Buy)
                .setIsin(ISIN.Allianz)
                .setQuantity(new Quantity(2))
                .setTotalPrice(new Amount(500.0))
                .setCommission(Amount.Zero)
                .setDate(LocalDate.of(2000, 1, 2))
                .build();

        account.registerTransaction(firstTransaction);
        account.registerTransaction(secondTransaction);

        Map<ISIN, Quantity> stocks = account.getCurrentStocks();

        Assert.assertEquals(2, stocks.size());
        Assert.assertEquals(new Quantity(1), stocks.get(ISIN.MunichRe));
        Assert.assertEquals(new Quantity(2), stocks.get(ISIN.Allianz));
    }

    @Test
    public void returnsCurrentStocksWithoutEmptyPositions() {
        Account account = new Account(new Amount(10000.0));

        account.registerTransaction(new TransactionBuilder()
                .setTransactionType(TransactionType.Buy)
                .setIsin(ISIN.MunichRe)
                .setQuantity(new Quantity(10))
                .setTotalPrice(new Amount(1000.0))
                .setCommission(new Amount(0.0))
                .setDate(LocalDate.of(2000, 1, 1))
                .build());

        account.registerTransaction(new TransactionBuilder()
                .setTransactionType(TransactionType.Buy)
                .setIsin(ISIN.Allianz)
                .setQuantity(new Quantity(10))
                .setTotalPrice(new Amount(1000.0))
                .setCommission(new Amount(0.0))
                .setDate(LocalDate.of(2000, 1, 2))
                .build());

        account.registerTransaction(new TransactionBuilder()
                .setTransactionType(TransactionType.Sell)
                .setIsin(ISIN.MunichRe)
                .setQuantity(new Quantity(10))
                .setTotalPrice(new Amount(1000.0))
                .setCommission(new Amount(0.0))
                .setDate(LocalDate.of(2000, 1, 3))
                .build());

        Map<ISIN, Quantity> currentStocks = account.getCurrentStocks();

        Assert.assertEquals(1, currentStocks.size());
        Assert.assertTrue(currentStocks.containsKey(ISIN.Allianz));
    }

    @Test
    public void returnsTotalMarketPrice() {
        Account account = new Account(new Amount(50000.0));

        Transaction firstTransaction = new TransactionBuilder()
                .setTransactionType(TransactionType.Buy)
                .setIsin(ISIN.MunichRe)
                .setQuantity(new Quantity(1))
                .setTotalPrice(new Amount(1000.0))
                .setCommission(Amount.Zero)
                .setDate(LocalDate.of(2000, 1, 1))
                .build();

        Transaction secondTransaction = new TransactionBuilder()
                .setTransactionType(TransactionType.Buy)
                .setIsin(ISIN.Allianz)
                .setQuantity(new Quantity(2))
                .setTotalPrice(new Amount(500.0))
                .setCommission(Amount.Zero)
                .setDate(LocalDate.of(2000, 1, 2))
                .build();

        account.registerTransaction(firstTransaction);
        account.registerTransaction(secondTransaction);

        Assert.assertEquals(new Amount(1500.0), account.getTotalStocksMarketPrice());
    }

    @Test
    public void returnsTotalStockQuantity() {
        Account account = new Account(new Amount(50000.0));

        Transaction firstTransaction = new TransactionBuilder()
                .setTransactionType(TransactionType.Buy)
                .setIsin(ISIN.MunichRe)
                .setQuantity(new Quantity(1))
                .setTotalPrice(new Amount(1000.0))
                .setCommission(Amount.Zero)
                .setDate(LocalDate.of(2000, 1, 1))
                .build();

        Transaction secondTransaction = new TransactionBuilder()
                .setTransactionType(TransactionType.Buy)
                .setIsin(ISIN.Allianz)
                .setQuantity(new Quantity(2))
                .setTotalPrice(new Amount(500.0))
                .setCommission(Amount.Zero)
                .setDate(LocalDate.of(2000, 1, 2))
                .build();

        Transaction thirdTransaction = new TransactionBuilder()
                .setTransactionType(TransactionType.Buy)
                .setIsin(ISIN.DeutscheBank)
                .setQuantity(new Quantity(3))
                .setTotalPrice(new Amount(500.0))
                .setCommission(Amount.Zero)
                .setDate(LocalDate.of(2000, 1, 3))
                .build();

        account.registerTransaction(firstTransaction);
        account.registerTransaction(secondTransaction);
        account.registerTransaction(thirdTransaction);

        Assert.assertEquals(new Quantity(6), account.getTotalStocksQuantity());
    }

    @Test
    public void returnsLastTransaction() {
        Transaction firstBuyTransaction = new TransactionBuilder()
                .setTransactionType(TransactionType.Buy)
                .setIsin(ISIN.MunichRe)
                .setQuantity(new Quantity(1))
                .setTotalPrice(new Amount(100.0))
                .setCommission(Amount.Zero)
                .setDate(LocalDate.of(2000, 1, 1))
                .build();

        Transaction sellTransaction = new TransactionBuilder()
                .setTransactionType(TransactionType.Sell)
                .setIsin(ISIN.MunichRe)
                .setQuantity(new Quantity(1))
                .setTotalPrice(new Amount(150.0))
                .setCommission(Amount.Zero)
                .setDate(LocalDate.of(2000, 1, 2))
                .build();

        Transaction secondBuyTransaction = new TransactionBuilder()
                .setTransactionType(TransactionType.Buy)
                .setIsin(ISIN.MunichRe)
                .setQuantity(new Quantity(2))
                .setTotalPrice(new Amount(120.0))
                .setCommission(new Amount(0))
                .setDate(LocalDate.of(2000, 1, 3))
                .build();

        Transaction otherBuyTransaction = new TransactionBuilder()
                .setTransactionType(TransactionType.Buy)
                .setIsin(ISIN.Allianz)
                .setQuantity(new Quantity(1))
                .setTotalPrice(new Amount(100.0))
                .setCommission(new Amount(0))
                .setDate(LocalDate.of(2000, 1, 4))
                .build();

        this.account.registerTransaction(firstBuyTransaction);
        this.account.registerTransaction(sellTransaction);
        this.account.registerTransaction(secondBuyTransaction);
        this.account.registerTransaction(otherBuyTransaction);

        Transaction lastTransaction = this.account.getLastTransaction(ISIN.MunichRe);

        Assert.assertSame(lastTransaction, secondBuyTransaction);
    }

    @Test
    public void getLastTransactionFails_ifNoBuyTransactionAvailable() {
        Transaction otherBuyTransaction = new TransactionBuilder()
                .setTransactionType(TransactionType.Buy)
                .setIsin(ISIN.Allianz)
                .setQuantity(new Quantity(1))
                .setTotalPrice(new Amount(100.0))
                .setCommission(new Amount(0))
                .setDate(LocalDate.of(2000, 1, 1))
                .build();

        this.account.registerTransaction(otherBuyTransaction);

        try {
            this.account.getLastTransaction(ISIN.MunichRe);
        }
        catch(RuntimeException e) {
            Assert.assertEquals("There was no transaction registered yet for the specified ISIN.", e.getMessage());
            return;
        }

        Assert.fail("RuntimeException expected.");
    }

    // AccountId

    @Test
    public void setAccountId() {
        Account account = new Account(new Amount(10000.0));
        AccountId accountId = new AccountId(1);

        account.setId(accountId);

        Assert.assertSame(accountId, account.getId());
    }

    @Test
    public void setAccountIdFails_ifAccountIdAlreadySet() {
        Account account = new Account(new Amount(10000.0));
        account.setId(new AccountId(1));

        try {
            account.setId(new AccountId(2));
        }
        catch(DomainException e) {
            Assert.assertEquals("The account id must not be changed if set once.", e.getMessage());
            return;
        }

        Assert.fail("DomainException expected.");
    }
}
