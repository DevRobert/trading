package trading.persistence.account;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import trading.domain.*;
import trading.domain.account.*;
import trading.persistence.MySqlRepositoryParameters;
import trading.persistence.MySqlRepositoryParametersBuilder;

import java.time.LocalDate;
import java.util.List;

public class MySqlAccountRepositoryTest {
    private AccountRepository accountRepository;

    @Before
    public void before() {
        MySqlRepositoryParameters parameters = new MySqlRepositoryParametersBuilder()
                .setServer("localhost")
                .setUsername("root")
                .setPassword("testtest")
                .setDatabase("trading_test")
                .build();

        this.accountRepository = new MySqlAccountRepository(parameters);
    }

    @Test
    public void createAndGetAccount() {
        // Create account

        ClientId clientId = new ClientId(1);
        Amount seedCapital = new Amount(10000.0);

        Account account = this.accountRepository.createAccount(clientId, seedCapital);

        Assert.assertNotNull(account.getId());
        Assert.assertTrue(account.getId().getValue() > 0);
        Assert.assertEquals(seedCapital, account.getAvailableMoney());

        // Get account

        Account accountFromDatabase = this.accountRepository.getAccount(account.getId());
        Assert.assertEquals(account.getId().getValue(), accountFromDatabase.getId().getValue());
        Assert.assertEquals(seedCapital, accountFromDatabase.getAvailableMoney());
    }

    @Test
    public void createAccountFails_ifAccountIdNotSet() {
        Account account = new Account(new Amount(10000.0));

        try {
            this.accountRepository.saveAccount(account);
        }
        catch(RuntimeException e) {
            Assert.assertEquals("The account id must be set so that the account can be updated.", e.getMessage());
            return;
        }

        Assert.fail("RuntimeException expected.");
    }

    @Test
    public void getAccount() {
        Account account = this.accountRepository.getAccount(new AccountId(1));

        Assert.assertNotNull(account);
        Assert.assertEquals(new Amount(10000.0), account.getAvailableMoney());
    }

    @Test
    public void getAccountTransactions() {
        Account account = this.accountRepository.getAccount(new AccountId(2));

        List<Transaction> transactions = account.getProcessedTransactions();
        Assert.assertEquals(3, transactions.size());

        MarketTransaction buyTransaction = (MarketTransaction) transactions.get(0);

        Assert.assertEquals(LocalDate.of(2018, 4, 16), buyTransaction.getDate());
        Assert.assertEquals(MarketTransactionType.Buy, buyTransaction.getTransactionType());
        Assert.assertEquals(new ISIN("DE0008430026"), buyTransaction.getIsin());
        Assert.assertEquals(new Quantity(10), buyTransaction.getQuantity());
        Assert.assertEquals(new Amount(5000.0), buyTransaction.getTotalPrice());
        Assert.assertEquals(new Amount(10.0), buyTransaction.getCommission());
        Assert.assertNotNull(buyTransaction.getId());

        MarketTransaction sellTransaction = (MarketTransaction) transactions.get(1);

        Assert.assertEquals(LocalDate.of(2018, 4, 17), sellTransaction.getDate());
        Assert.assertEquals(MarketTransactionType.Sell, sellTransaction.getTransactionType());
        Assert.assertEquals(new Quantity(10), sellTransaction.getQuantity());
        Assert.assertEquals(new ISIN("DE0008430026"), sellTransaction.getIsin());
        Assert.assertEquals(new Amount(5500.0), sellTransaction.getTotalPrice());
        Assert.assertEquals(new Amount(10.0), sellTransaction.getCommission());
        Assert.assertNotNull(sellTransaction.getId());

        DividendTransaction dividendTransaction = (DividendTransaction) transactions.get(2);

        Assert.assertEquals(LocalDate.of(2018, 4, 18), dividendTransaction.getDate());
        Assert.assertEquals(new Amount(50.0), dividendTransaction.getAmount());
        Assert.assertEquals(new ISIN("DE0008430026"), dividendTransaction.getIsin());
    }

    @Test
    public void getAccount_fails_forUnknownAccountId() {
        try {
            this.accountRepository.getAccount(new AccountId(Integer.MAX_VALUE));
        }
        catch(AccountNotFoundException ex) {
            return;
        }

        Assert.fail("AccountNotFoundException expected.");
    }

    @Test
    public void registerTransactions() {
        // Create account and register transactions

        Account account = this.accountRepository.createAccount(new ClientId(1), new Amount(10000.0));

        ISIN isin = new ISIN("DE0008430026");
        Quantity quantity = new Quantity(10);

        MarketTransaction buyTransaction = new MarketTransactionBuilder()
                .setDate(LocalDate.of(2018, 4, 27))
                .setTransactionType(MarketTransactionType.Buy)
                .setIsin(isin)
                .setQuantity(quantity)
                .setTotalPrice(new Amount(10000.0))
                .setCommission(new Amount(10.0))
                .build();

        MarketTransaction sellTransaction = new MarketTransactionBuilder()
                .setDate(LocalDate.of(2018, 4, 30))
                .setTransactionType(MarketTransactionType.Sell)
                .setIsin(isin)
                .setQuantity(quantity)
                .setTotalPrice(new Amount(11000.0))
                .setCommission(new Amount(10.0))
                .build();

        DividendTransaction dividendTransaction = new DividendTransactionBuilder()
                .setDate(LocalDate.of(2018, 5, 1))
                .setIsin(isin)
                .setAmount(new Amount(100.0))
                .build();

        account.registerTransaction(buyTransaction);
        account.registerTransaction(sellTransaction);
        account.registerTransaction(dividendTransaction);

        this.accountRepository.saveAccount(account);

        Assert.assertNotNull(buyTransaction.getId());
        Assert.assertNotNull(sellTransaction.getId());
        Assert.assertNotNull(dividendTransaction.getId());

        // Read account and transactions from database

        Account accountFromDatabase = this.accountRepository.getAccount(account.getId());

        List<Transaction> transactionsFromDatabase = accountFromDatabase.getProcessedTransactions();

        Assert.assertEquals(3, transactionsFromDatabase.size());

        MarketTransaction buyTransactionFromDatabase = (MarketTransaction) transactionsFromDatabase.get(0);
        Assert.assertEquals(buyTransaction.getDate(), buyTransactionFromDatabase.getDate());
        Assert.assertEquals(buyTransaction.getTransactionType(), buyTransactionFromDatabase.getTransactionType());
        Assert.assertEquals(buyTransaction.getIsin(), buyTransactionFromDatabase.getIsin());
        Assert.assertEquals(buyTransaction.getQuantity(), buyTransactionFromDatabase.getQuantity());
        Assert.assertEquals(buyTransaction.getTotalPrice(), buyTransactionFromDatabase.getTotalPrice());
        Assert.assertEquals(buyTransaction.getCommission(), buyTransactionFromDatabase.getCommission());

        MarketTransaction sellTransactionFromDatabase = (MarketTransaction) transactionsFromDatabase.get(1);
        Assert.assertEquals(sellTransaction.getDate(), sellTransaction.getDate());
        Assert.assertEquals(sellTransaction.getTransactionType(), sellTransactionFromDatabase.getTransactionType());
        Assert.assertEquals(sellTransaction.getIsin(), sellTransactionFromDatabase.getIsin());
        Assert.assertEquals(sellTransaction.getQuantity(), sellTransactionFromDatabase.getQuantity());
        Assert.assertEquals(sellTransaction.getTotalPrice(), sellTransactionFromDatabase.getTotalPrice());
        Assert.assertEquals(sellTransaction.getCommission(), sellTransactionFromDatabase.getCommission());

        DividendTransaction dividendTransactionFromDatabase = (DividendTransaction) transactionsFromDatabase.get(2);
        Assert.assertEquals(dividendTransaction.getDate(), dividendTransactionFromDatabase.getDate());
        Assert.assertEquals(dividendTransaction.getIsin(), dividendTransactionFromDatabase.getIsin());
        Assert.assertEquals(dividendTransaction.getAmount(), dividendTransactionFromDatabase.getAmount());
    }

    @Test
    public void appendTransactions() {
        // Create account with first transaction

        Account account = this.accountRepository.createAccount(new ClientId(1), new Amount(10000.0));

        account.registerTransaction(new MarketTransactionBuilder()
                .setTransactionType(MarketTransactionType.Buy)
                .setIsin(ISIN.MunichRe)
                .setQuantity(new Quantity(1))
                .setTotalPrice(new Amount(100.0))
                .setCommission(new Amount(10.0))
                .setDate(LocalDate.of(2018, 4, 27))
                .build());

        this.accountRepository.saveAccount(account);

        // Register second transaction

        account.registerTransaction(new MarketTransactionBuilder()
                .setTransactionType(MarketTransactionType.Sell)
                .setIsin(ISIN.MunichRe)
                .setQuantity(new Quantity(1))
                .setTotalPrice(new Amount(110.0))
                .setCommission(new Amount(10.0))
                .setDate(LocalDate.of(2018, 4, 30))
                .build());

        this.accountRepository.saveAccount(account);

        // Get account from database

        Account accountFromDatabase = this.accountRepository.getAccount(account.getId());

        List<Transaction> transactionsFromDatabase = accountFromDatabase.getProcessedTransactions();
        Assert.assertEquals(2, transactionsFromDatabase.size());
    }
}
