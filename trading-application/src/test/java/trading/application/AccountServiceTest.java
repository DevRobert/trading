package trading.application;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import trading.domain.*;
import trading.domain.account.*;
import trading.domain.broker.DynamicCommissionStrategyParameters;
import trading.domain.strategy.compoundLocalMaximum.CompoundLocalMaximumTradingStrategyParameters;

import java.time.LocalDate;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

public class AccountServiceTest {
    private TradingConfigurationService tradingConfigurationService;

    @Before
    public void before() {
        this.tradingConfigurationService = new TradingConfigurationService() {
            @Override
            public DynamicCommissionStrategyParameters getCommissionStrategyParameters() {
                return null;
            }

            @Override
            public CompoundLocalMaximumTradingStrategyParameters getTradingStrategyParameters() {
                return null;
            }

            @Override
            public TaxStrategy getTaxStrategy() {
                return null;
            }
        };
    }

    @Test
    public void getAccount_returnsAccountFromRepository() {
        AccountId accountId = new AccountId(100);

        Account account = new AccountBuilder()
                .setAvailableMoney(new Amount(10000.0))
                .setTaxStrategy(TaxStrategies.getNoTaxesStrategy())
                .build();

        AccountRepository accountRepository = mock(AccountRepository.class);
        given(accountRepository.getAccount(eq(accountId), any())).willReturn(account);

        AccountService accountService = new AccountServiceImpl(accountRepository, this.tradingConfigurationService);
        Account accountFromService = accountService.getAccount(accountId);

        Assert.assertSame(account, accountFromService);
    }

    @Test
    public void registerTransaction_addsTransactionToAccountFromRepository_andSavesAccount() {
        AccountId accountId = new AccountId(100);
        TransactionId transactionId = new TransactionId(50);

        Account account = new AccountBuilder()
                .setAvailableMoney(new Amount(10000.0))
                .setTaxStrategy(TaxStrategies.getNoTaxesStrategy())
                .build();

        MarketTransaction transaction = new MarketTransactionBuilder()
                .setTransactionType(MarketTransactionType.Buy)
                .setIsin(ISIN.MunichRe)
                .setQuantity(new Quantity(1))
                .setTotalPrice(new Amount(100.0))
                .setCommission(new Amount(5.0))
                .setDate(LocalDate.now())
                .build();

        AccountRepository accountRepository = mock(AccountRepository.class);

        given(accountRepository.getAccount(eq(accountId), any())).willReturn(account);

        doAnswer(invocation -> {
            Account saveAccount = invocation.getArgument(0);
            Assert.assertSame(account, saveAccount);
            Assert.assertTrue("The transaction has not been registered.", account.getProcessedTransactions().contains(transaction));

            transaction.setId(transactionId);
            return null;
        }).when(accountRepository).saveAccount(account);

        AccountService accountService = new AccountServiceImpl(accountRepository, this.tradingConfigurationService);
        accountService.registerTransaction(accountId, transaction);

        verify(accountRepository).saveAccount(account);
    }
}
