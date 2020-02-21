package trading.api.account;

import org.junit.Before;
import org.springframework.boot.test.mock.mockito.MockBean;
import trading.api.ControllerTestBase;
import trading.application.AccountService;
import trading.domain.*;
import trading.domain.account.*;
import trading.domain.market.InstrumentNameProvider;
import trading.domain.market.MarketPriceSnapshot;
import trading.domain.market.MarketPriceSnapshotBuilder;
import trading.domain.simulation.MultiStockMarketDataStore;
import trading.domain.taxes.ProfitCategories;

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
        Account account = new AccountBuilder()
                .setAvailableMoney(new Amount(10000.0))
                .setTaxStrategy(new TaxStrategyImpl(0.1))
                .build();

        account.registerTransaction(new MarketTransactionBuilder()
                .setDate(LocalDate.of(2018, 4, 12))
                .setTransactionType(MarketTransactionType.Buy)
                .setIsin(new ISIN("A"))
                .setQuantity(new Quantity(10))
                .setTotalPrice(new Amount(1000.0))
                .setCommission(new Amount(20.0))
                .build());

        account.registerTransaction(new MarketTransactionBuilder()
                .setDate(LocalDate.of(2018, 4, 13))
                .setTransactionType(MarketTransactionType.Buy)
                .setIsin(new ISIN("B"))
                .setQuantity(new Quantity(10))
                .setTotalPrice(new Amount(1000.0))
                .setCommission(new Amount(20.0))
                .build());

        account.registerTransaction(new DividendTransactionBuilder()
                .setDate(LocalDate.of(2018, 4, 14))
                .setIsin(new ISIN("B"))
                .setAmount(new Amount(100.0))
                .build());

        account.registerTransaction(new TaxPaymentTransactionBuilder()
                .setDate(LocalDate.of(2018, 4, 15))
                .setProfitCategory(ProfitCategories.Dividends)
                .setTaxPeriodYear(2018)
                .setTaxedProfit(new Amount(10.0))
                .setPaidTaxes(new Amount(5.0))
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
