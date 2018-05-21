package trading.domain.account;

import org.junit.Assert;
import org.junit.Test;
import trading.domain.Amount;
import trading.domain.DomainException;
import trading.domain.ISIN;
import trading.domain.Quantity;

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

    // Process buy transaction

    @Test
    public void buyTransactionLeadsToNewPosition() throws AccountStateException {
        ISIN isin = ISIN.MunichRe;
        Quantity quantity = new Quantity(1000);
        Amount fullPrice = new Amount(2000.0);
        Amount commission = new Amount(20.0);

        MarketTransaction transaction = new MarketTransactionBuilder()
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

        MarketTransaction transaction = new MarketTransactionBuilder()
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
    public void buyTransactionForNonEmptyPositionFails() throws AccountStateException {
        ISIN isin = ISIN.MunichRe;
        Amount fullBuyPrice = new Amount(1000.0);
        Amount buyCommission = new Amount(10.0);

        MarketTransaction buyTransaction = new MarketTransactionBuilder()
                .setTransactionType(TransactionType.Buy)
                .setIsin(isin)
                .setQuantity(new Quantity(2))
                .setTotalPrice(fullBuyPrice)
                .setCommission(buyCommission)
                .setDate(LocalDate.of(2000, 1, 1))
                .build();

        MarketTransaction furtherBuyTransaction = new MarketTransactionBuilder()
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

        MarketTransaction buyTransaction = new MarketTransactionBuilder()
                .setTransactionType(TransactionType.Buy)
                .setIsin(isin)
                .setQuantity(new Quantity(2))
                .setTotalPrice(fullBuyPrice)
                .setCommission(buyCommission)
                .setDate(LocalDate.of(2000, 1, 1))
                .build();

        MarketTransaction sellTransaction = new MarketTransactionBuilder()
                .setTransactionType(TransactionType.Sell)
                .setIsin(isin)
                .setQuantity(new Quantity(2))
                .setTotalPrice(fullSellPrice)
                .setCommission(sellCommission)
                .setDate(LocalDate.of(2000, 1, 2))
                .build();

        MarketTransaction furtherBuyTransaction = new MarketTransactionBuilder()
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

        MarketTransaction transaction = new MarketTransactionBuilder()
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

    // Process sell transaction

    @Test
    public void sellTransactionCompensatesExistingPosition() throws AccountStateException {
        ISIN isin = ISIN.MunichRe;
        Quantity quantity = new Quantity(1);
        Amount fullBuyPrice = new Amount(1000.0);
        Amount fullSellPrice = new Amount(2000.0);
        Amount buyCommission = new Amount(10.0);
        Amount sellCommission = new Amount(10.0);

        MarketTransaction buyTransaction = new MarketTransactionBuilder()
                .setTransactionType(TransactionType.Buy)
                .setIsin(isin)
                .setQuantity(quantity)
                .setTotalPrice(fullBuyPrice)
                .setCommission(buyCommission)
                .setDate(LocalDate.of(2000, 1, 1))
                .build();

        MarketTransaction sellTransaction = new MarketTransactionBuilder()
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

        MarketTransaction transaction = new MarketTransactionBuilder()
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

        MarketTransaction buyTransaction = new MarketTransactionBuilder()
                .setTransactionType(TransactionType.Buy)
                .setIsin(isin)
                .setQuantity(new Quantity(2))
                .setTotalPrice(fullBuyPrice)
                .setCommission(buyCommission)
                .setDate(LocalDate.of(2000, 1, 1))
                .build();

        MarketTransaction sellTransaction = new MarketTransactionBuilder()
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

        MarketTransaction buyTransaction = new MarketTransactionBuilder()
                .setTransactionType(TransactionType.Buy)
                .setIsin(isin)
                .setQuantity(new Quantity(1))
                .setTotalPrice(fullBuyPrice)
                .setCommission(buyCommission)
                .setDate(LocalDate.of(2000, 1, 1))
                .build();

        MarketTransaction sellTransaction = new MarketTransactionBuilder()
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

    // Process dividend transaction

    @Test
    public void dividendTransactionIncreasesAvailableMoneyAndBalance() {
        // Seed capital: 10,000
        // Buy Transaction: 2,000
        // Dividend: 1,000
        // Available money: 10,000 - 2,000 + 1,000 = 9,000
        // Balance: 10,000 + 1,000

        MarketTransaction buyTransaction = new MarketTransactionBuilder()
                .setDate(LocalDate.of(2000, 1, 1))
                .setTransactionType(TransactionType.Buy)
                .setIsin(ISIN.MunichRe)
                .setQuantity(new Quantity(2))
                .setTotalPrice(new Amount(2000.0))
                .setCommission(Amount.Zero)
                .build();

        DividendTransaction dividendTransaction = new DividendTransactionBuilder()
                .setDate(LocalDate.of(2000, 1, 2))
                .setAmount(new Amount(1000.0))
                .setIsin(ISIN.MunichRe)
                .build();

        this.account.registerTransaction(buyTransaction);
        this.account.registerTransaction(dividendTransaction);

        Assert.assertEquals(new Amount(9000.0), account.getAvailableMoney());
        Assert.assertEquals(new Amount(11000.0), account.getBalance());
    }

    @Test
    public void dividendTransactionFailsIfStocksHaveNeverBeenBought() {
        DividendTransaction dividendTransaction = new DividendTransactionBuilder()
                .setDate(LocalDate.now())
                .setAmount(new Amount(1000.0))
                .setIsin(ISIN.Allianz)
                .build();

        try {
            this.account.registerTransaction(dividendTransaction);
        }
        catch(DomainException e) {
            Assert.assertEquals("The dividend transaction cannot be registered as there was no respective position found for the given ISIN.", e.getMessage());
            return;
        }

        Assert.fail("DomainException expected.");
    }

    @Test
    public void dividendTransactionFailsIfStocksSoldExactly32DaysBeforeDividendDate() {
        MarketTransaction buyTransaction = new MarketTransactionBuilder()
                .setDate(LocalDate.of(2018, 1, 1))
                .setTransactionType(TransactionType.Buy)
                .setIsin(ISIN.MunichRe)
                .setQuantity(new Quantity(2))
                .setTotalPrice(new Amount(2000.0))
                .setCommission(Amount.Zero)
                .build();

        MarketTransaction sellTransaction = new MarketTransactionBuilder()
                .setDate(LocalDate.of(2018, 3, 1))
                .setTransactionType(TransactionType.Sell)
                .setIsin(ISIN.MunichRe)
                .setQuantity(new Quantity(2))
                .setTotalPrice(new Amount(2000.0))
                .setCommission(Amount.Zero)
                .build();

        DividendTransaction dividendTransaction = new DividendTransactionBuilder()
                .setDate(LocalDate.of(2018, 4, 3))
                .setAmount(new Amount(1000.0))
                .setIsin(ISIN.MunichRe)
                .build();

        this.account.registerTransaction(buyTransaction);
        this.account.registerTransaction(sellTransaction);

        try {
            this.account.registerTransaction(dividendTransaction);
        }
        catch(DomainException e) {
            Assert.assertEquals("The dividend transaction cannot be registered as the dividend date (2018-04-03) lies more than 30 days after the close date of the respective position (2018-03-01).", e.getMessage());
            return;
        }

        Assert.fail("DomainException expected.");
    }

    @Test
    public void dividendTransactionFailsIfStocksSoldExactly31DaysBeforeDividendDate() {
        MarketTransaction buyTransaction = new MarketTransactionBuilder()
                .setDate(LocalDate.of(2018, 1, 1))
                .setTransactionType(TransactionType.Buy)
                .setIsin(ISIN.MunichRe)
                .setQuantity(new Quantity(2))
                .setTotalPrice(new Amount(2000.0))
                .setCommission(Amount.Zero)
                .build();

        MarketTransaction sellTransaction = new MarketTransactionBuilder()
                .setDate(LocalDate.of(2018, 3, 1))
                .setTransactionType(TransactionType.Sell)
                .setIsin(ISIN.MunichRe)
                .setQuantity(new Quantity(2))
                .setTotalPrice(new Amount(2000.0))
                .setCommission(Amount.Zero)
                .build();

        DividendTransaction dividendTransaction = new DividendTransactionBuilder()
                .setDate(LocalDate.of(2018, 4, 2))
                .setAmount(new Amount(1000.0))
                .setIsin(ISIN.MunichRe)
                .build();

        this.account.registerTransaction(buyTransaction);
        this.account.registerTransaction(sellTransaction);

        try {
            this.account.registerTransaction(dividendTransaction);
        }
        catch(DomainException e) {
            Assert.assertEquals("The dividend transaction cannot be registered as the dividend date (2018-04-02) lies more than 30 days after the close date of the respective position (2018-03-01).", e.getMessage());
            return;
        }

        Assert.fail("DomainException expected.");
    }

    @Test
    public void dividendTransactionPassesIfStocksSoldExactly30DaysBeforeDividendDate() {
        MarketTransaction buyTransaction = new MarketTransactionBuilder()
                .setDate(LocalDate.of(2018, 1, 1))
                .setTransactionType(TransactionType.Buy)
                .setIsin(ISIN.MunichRe)
                .setQuantity(new Quantity(2))
                .setTotalPrice(new Amount(2000.0))
                .setCommission(Amount.Zero)
                .build();

        MarketTransaction sellTransaction = new MarketTransactionBuilder()
                .setDate(LocalDate.of(2018, 3, 1))
                .setTransactionType(TransactionType.Sell)
                .setIsin(ISIN.MunichRe)
                .setQuantity(new Quantity(2))
                .setTotalPrice(new Amount(2000.0))
                .setCommission(Amount.Zero)
                .build();

        DividendTransaction dividendTransaction = new DividendTransactionBuilder()
                .setDate(LocalDate.of(2018, 3, 31))
                .setAmount(new Amount(1000.0))
                .setIsin(ISIN.MunichRe)
                .build();

        this.account.registerTransaction(buyTransaction);
        this.account.registerTransaction(sellTransaction);
        this.account.registerTransaction(dividendTransaction);

        Assert.assertTrue(this.account.getProcessedTransactions().contains(dividendTransaction));
    }

    @Test
    public void dividendTransactionPassesIfStocksSoldExactly29DaysBeforeDividendDate() {
        MarketTransaction buyTransaction = new MarketTransactionBuilder()
                .setDate(LocalDate.of(2018, 1, 1))
                .setTransactionType(TransactionType.Buy)
                .setIsin(ISIN.MunichRe)
                .setQuantity(new Quantity(2))
                .setTotalPrice(new Amount(2000.0))
                .setCommission(Amount.Zero)
                .build();

        MarketTransaction sellTransaction = new MarketTransactionBuilder()
                .setDate(LocalDate.of(2018, 3, 1))
                .setTransactionType(TransactionType.Sell)
                .setIsin(ISIN.MunichRe)
                .setQuantity(new Quantity(2))
                .setTotalPrice(new Amount(2000.0))
                .setCommission(Amount.Zero)
                .build();

        DividendTransaction dividendTransaction = new DividendTransactionBuilder()
                .setDate(LocalDate.of(2018, 3, 30))
                .setAmount(new Amount(1000.0))
                .setIsin(ISIN.MunichRe)
                .build();

        this.account.registerTransaction(buyTransaction);
        this.account.registerTransaction(sellTransaction);
        this.account.registerTransaction(dividendTransaction);

        Assert.assertTrue(this.account.getProcessedTransactions().contains(dividendTransaction));
    }

    // Transaction order

    @Test
    public void registerTransactionPasses_ifDateEqualsLastRegisteredTransaction() {
        MarketTransaction firstTransaction = new MarketTransactionBuilder()
                .setDate(LocalDate.of(2000, 2, 10))
                .setTransactionType(TransactionType.Buy)
                .setIsin(ISIN.MunichRe)
                .setQuantity(new Quantity(1))
                .setTotalPrice(new Amount(1000.0))
                .setCommission(Amount.Zero)
                .build();

        MarketTransaction secondTransaction = new MarketTransactionBuilder()
                .setDate(LocalDate.of(2000, 2, 10))
                .setTransactionType(TransactionType.Sell)
                .setIsin(ISIN.MunichRe)
                .setQuantity(new Quantity(1))
                .setTotalPrice(new Amount(1000.0))
                .setCommission(Amount.Zero)
                .build();

        this.account.registerTransaction(firstTransaction);
        this.account.registerTransaction(secondTransaction);

        Assert.assertTrue(this.account.getProcessedTransactions().contains(secondTransaction));
    }

    @Test
    public void registerTransactionPasses_ifDateAfterLastRegisteredTransaction() {
        MarketTransaction firstTransaction = new MarketTransactionBuilder()
                .setDate(LocalDate.of(2000, 2, 10))
                .setTransactionType(TransactionType.Buy)
                .setIsin(ISIN.MunichRe)
                .setQuantity(new Quantity(1))
                .setTotalPrice(new Amount(1000.0))
                .setCommission(Amount.Zero)
                .build();

        MarketTransaction secondTransaction = new MarketTransactionBuilder()
                .setDate(LocalDate.of(2000, 2, 11))
                .setTransactionType(TransactionType.Sell)
                .setIsin(ISIN.MunichRe)
                .setQuantity(new Quantity(1))
                .setTotalPrice(new Amount(1000.0))
                .setCommission(Amount.Zero)
                .build();

        this.account.registerTransaction(firstTransaction);
        this.account.registerTransaction(secondTransaction);

        Assert.assertTrue(this.account.getProcessedTransactions().contains(secondTransaction));
    }

    @Test
    public void registerTransactionFails_ifDateBeforeLastRegisteredTransaction() {
        MarketTransaction firstTransaction = new MarketTransactionBuilder()
                .setDate(LocalDate.of(2000, 2, 10))
                .setTransactionType(TransactionType.Buy)
                .setIsin(ISIN.MunichRe)
                .setQuantity(new Quantity(1))
                .setTotalPrice(new Amount(1000.0))
                .setCommission(Amount.Zero)
                .build();

        MarketTransaction secondTransaction = new MarketTransactionBuilder()
                .setDate(LocalDate.of(2000, 2, 9))
                .setTransactionType(TransactionType.Sell)
                .setIsin(ISIN.MunichRe)
                .setQuantity(new Quantity(1))
                .setTotalPrice(new Amount(1000.0))
                .setCommission(Amount.Zero)
                .build();

        this.account.registerTransaction(firstTransaction);

        try {
            this.account.registerTransaction(secondTransaction);
        }
        catch(DomainException e) {
            Assert.assertEquals("The transaction cannot be registered as its date (2000-02-09) " +
                    "lies before the date of the last registered transaction (2000-02-10).",
                    e.getMessage());

            return;
        }

        Assert.fail("DomainException expected.");
    }

    // Transaction list

    @Test
    public void returnsInitiallyEmptyTransactionLists() {
        Account account = new Account(new Amount(50000.0));
        List<Transaction> transaction = account.getProcessedTransactions();
        Assert.assertEquals(0, transaction.size());
    }

    @Test
    public void returnsRegisteredTransactions() {
        Account account = new Account(new Amount(50000.0));

        MarketTransaction firstTransaction = new MarketTransactionBuilder()
                .setTransactionType(TransactionType.Buy)
                .setIsin(ISIN.MunichRe)
                .setQuantity(new Quantity(1))
                .setTotalPrice(new Amount(100.0))
                .setCommission(Amount.Zero)
                .setDate(LocalDate.of(2000, 1, 1))
                .build();

        MarketTransaction secondTransaction = new MarketTransactionBuilder()
                .setTransactionType(TransactionType.Sell)
                .setIsin(ISIN.MunichRe)
                .setQuantity(new Quantity(1))
                .setTotalPrice(new Amount(110.0))
                .setCommission(Amount.Zero)
                .setDate(LocalDate.of(2000, 1, 2))
                .build();

        DividendTransaction thirdTransaction = new DividendTransactionBuilder()
                .setDate(LocalDate.of(2000, 1, 3))
                .setIsin(ISIN.MunichRe)
                .setAmount(new Amount(10.0))
                .build();

        account.registerTransaction(firstTransaction);
        account.registerTransaction(secondTransaction);
        account.registerTransaction(thirdTransaction);

        Assert.assertEquals(3, account.getProcessedTransactions().size());
        Assert.assertSame(firstTransaction, account.getProcessedTransactions().get(0));
        Assert.assertSame(secondTransaction, account.getProcessedTransactions().get(1));
        Assert.assertSame(thirdTransaction, account.getProcessedTransactions().get(2));
    }

    @Test
    public void returnsLastTransaction() {
        MarketTransaction firstBuyTransaction = new MarketTransactionBuilder()
                .setTransactionType(TransactionType.Buy)
                .setIsin(ISIN.MunichRe)
                .setQuantity(new Quantity(1))
                .setTotalPrice(new Amount(100.0))
                .setCommission(Amount.Zero)
                .setDate(LocalDate.of(2000, 1, 1))
                .build();

        MarketTransaction sellTransaction = new MarketTransactionBuilder()
                .setTransactionType(TransactionType.Sell)
                .setIsin(ISIN.MunichRe)
                .setQuantity(new Quantity(1))
                .setTotalPrice(new Amount(150.0))
                .setCommission(Amount.Zero)
                .setDate(LocalDate.of(2000, 1, 2))
                .build();

        MarketTransaction secondBuyTransaction = new MarketTransactionBuilder()
                .setTransactionType(TransactionType.Buy)
                .setIsin(ISIN.MunichRe)
                .setQuantity(new Quantity(2))
                .setTotalPrice(new Amount(120.0))
                .setCommission(new Amount(0))
                .setDate(LocalDate.of(2000, 1, 3))
                .build();

        MarketTransaction otherBuyTransaction = new MarketTransactionBuilder()
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

        MarketTransaction lastTransaction = this.account.getLastMarketTransaction(ISIN.MunichRe);

        Assert.assertSame(lastTransaction, secondBuyTransaction);
    }

    @Test
    public void getLastTransactionFails_ifNoBuyTransactionAvailable() {
        MarketTransaction otherBuyTransaction = new MarketTransactionBuilder()
                .setTransactionType(TransactionType.Buy)
                .setIsin(ISIN.Allianz)
                .setQuantity(new Quantity(1))
                .setTotalPrice(new Amount(100.0))
                .setCommission(new Amount(0))
                .setDate(LocalDate.of(2000, 1, 1))
                .build();

        this.account.registerTransaction(otherBuyTransaction);

        try {
            this.account.getLastMarketTransaction(ISIN.MunichRe);
        }
        catch(RuntimeException e) {
            Assert.assertEquals("There was no transaction registered yet for the specified ISIN.", e.getMessage());
            return;
        }

        Assert.fail("RuntimeException expected.");
    }

    // Current stocks

    @Test
    public void returnsCurrentStocks() {
        Account account = new Account(new Amount(50000.0));

        MarketTransaction firstTransaction = new MarketTransactionBuilder()
                .setTransactionType(TransactionType.Buy)
                .setIsin(ISIN.MunichRe)
                .setQuantity(new Quantity(1))
                .setTotalPrice(new Amount(1000.0))
                .setCommission(Amount.Zero)
                .setDate(LocalDate.of(2000, 1, 1))
                .build();

        MarketTransaction secondTransaction = new MarketTransactionBuilder()
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

        account.registerTransaction(new MarketTransactionBuilder()
                .setTransactionType(TransactionType.Buy)
                .setIsin(ISIN.MunichRe)
                .setQuantity(new Quantity(10))
                .setTotalPrice(new Amount(1000.0))
                .setCommission(new Amount(0.0))
                .setDate(LocalDate.of(2000, 1, 1))
                .build());

        account.registerTransaction(new MarketTransactionBuilder()
                .setTransactionType(TransactionType.Buy)
                .setIsin(ISIN.Allianz)
                .setQuantity(new Quantity(10))
                .setTotalPrice(new Amount(1000.0))
                .setCommission(new Amount(0.0))
                .setDate(LocalDate.of(2000, 1, 2))
                .build());

        account.registerTransaction(new MarketTransactionBuilder()
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

    // Statistics

    @Test
    public void returnsTotalMarketPrice() {
        Account account = new Account(new Amount(50000.0));

        MarketTransaction firstTransaction = new MarketTransactionBuilder()
                .setTransactionType(TransactionType.Buy)
                .setIsin(ISIN.MunichRe)
                .setQuantity(new Quantity(1))
                .setTotalPrice(new Amount(1000.0))
                .setCommission(Amount.Zero)
                .setDate(LocalDate.of(2000, 1, 1))
                .build();

        MarketTransaction secondTransaction = new MarketTransactionBuilder()
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

        MarketTransaction firstTransaction = new MarketTransactionBuilder()
                .setTransactionType(TransactionType.Buy)
                .setIsin(ISIN.MunichRe)
                .setQuantity(new Quantity(1))
                .setTotalPrice(new Amount(1000.0))
                .setCommission(Amount.Zero)
                .setDate(LocalDate.of(2000, 1, 1))
                .build();

        MarketTransaction secondTransaction = new MarketTransactionBuilder()
                .setTransactionType(TransactionType.Buy)
                .setIsin(ISIN.Allianz)
                .setQuantity(new Quantity(2))
                .setTotalPrice(new Amount(500.0))
                .setCommission(Amount.Zero)
                .setDate(LocalDate.of(2000, 1, 2))
                .build();

        MarketTransaction thirdTransaction = new MarketTransactionBuilder()
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
