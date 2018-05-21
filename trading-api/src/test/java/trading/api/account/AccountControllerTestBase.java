package trading.api.account;

import org.junit.Before;
import org.springframework.boot.test.mock.mockito.MockBean;
import trading.api.ControllerTestBase;
import trading.application.AccountService;
import trading.domain.*;
import trading.domain.account.Account;
import trading.domain.account.AccountId;
import trading.domain.account.MarketTransactionBuilder;
import trading.domain.account.TransactionType;
import trading.domain.market.InstrumentNameProvider;
import trading.domain.market.MarketPriceSnapshot;
import trading.domain.market.MarketPriceSnapshotBuilder;
import trading.domain.simulation.MultiStockMarketDataStore;

import java.time.LocalDate;

import static org.mockito.BDDMockito.given;

public abstract class AccountControllerTestBase extends ControllerTestBase {
    @MockBean
    protected AccountService accountService;

    @MockBean
    private InstrumentNameProvider instrumentNameProvider;

    @MockBean
    private MultiStockMarketDataStore multiStockMarketDataStore;

    @Before
    public void before() {
        Amount seedCapital = new Amount(10000.0);
        Account account = new Account(seedCapital);

        account.registerTransaction(new MarketTransactionBuilder()
                .setTransactionType(TransactionType.Buy)
                .setIsin(new ISIN("A"))
                .setQuantity(new Quantity(10))
                .setTotalPrice(new Amount(1000.0))
                .setCommission(new Amount(20.0))
                .setDate(LocalDate.of(2018, 4, 12))
                .build());

        account.registerTransaction(new MarketTransactionBuilder()
                .setTransactionType(TransactionType.Buy)
                .setIsin(new ISIN("B"))
                .setQuantity(new Quantity(10))
                .setTotalPrice(new Amount(1000.0))
                .setCommission(new Amount(20.0))
                .setDate(LocalDate.of(2018, 4, 13))
                .build());

        given(this.accountService.getAccount(new AccountId(1))).willReturn(account);

        given(this.instrumentNameProvider.getInstrumentName(new ISIN("A"))).willReturn("Company A");

        MarketPriceSnapshot lastClosingPrices = new MarketPriceSnapshotBuilder()
                .setMarketPrice(new ISIN("A"), new Amount(200.0))
                .setMarketPrice(new ISIN("B"), new Amount(300.0))
                .setDate(LocalDate.of(2018, 5, 9))
                .build();

        given(this.multiStockMarketDataStore.getLastClosingPrices()).willReturn(lastClosingPrices);
    }
}
