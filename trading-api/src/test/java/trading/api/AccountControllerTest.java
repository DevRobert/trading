package trading.api;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import trading.application.AccountService;
import trading.domain.*;
import trading.domain.account.Account;
import trading.domain.account.AccountId;
import trading.domain.market.InstrumentNameProvider;
import trading.domain.market.MarketPriceSnapshot;
import trading.domain.market.MarketPriceSnapshotBuilder;
import trading.domain.simulation.MultiStockMarketDataStore;

import java.time.LocalDate;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class AccountControllerTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private AccountService accountService;

    @MockBean
    private InstrumentNameProvider instrumentNameProvider;

    @MockBean
    private MultiStockMarketDataStore multiStockMarketDataStore;

    @Before
    public void before() {
        Amount seedCapital = new Amount(10000.0);
        Account account = new Account(seedCapital);

        account.registerTransaction(new TransactionBuilder()
                .setTransactionType(TransactionType.Buy)
                .setIsin(new ISIN("A"))
                .setQuantity(new Quantity(10))
                .setTotalPrice(new Amount(1000.0))
                .setCommission(new Amount(20.0))
                .setDate(LocalDate.of(2018, 4, 12))
                .build());

        account.registerTransaction(new TransactionBuilder()
                .setTransactionType(TransactionType.Buy)
                .setIsin(new ISIN("B"))
                .setQuantity(new Quantity(10))
                .setTotalPrice(new Amount(1000.0))
                .setCommission(new Amount(20.0))
                .setDate(LocalDate.of(2018, 4, 13))
                .build());

        given(this.accountService.getAccount(new AccountId(2))).willReturn(account);

        given(this.instrumentNameProvider.getInstrumentName(new ISIN("A"))).willReturn("Company A");

        MarketPriceSnapshot lastClosingPrices = new MarketPriceSnapshotBuilder()
                .setMarketPrice(new ISIN("A"), new Amount(200.0))
                .setMarketPrice(new ISIN("B"), new Amount(300.0))
                .build();

        given(this.multiStockMarketDataStore.getLastClosingPrices()).willReturn(lastClosingPrices);
    }

    @Test
    public void getPositions() throws Exception {
        // Available money
        // 10,000
        // - 1,000 - 20 (A)
        // - 1,000 - 20 (B)
        // = 7960
        this.mvc.perform(MockMvcRequestBuilders
                .get("/api/account/positions/")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("positions", hasSize(2)))
                .andExpect(jsonPath("positions[0].isin", is("A")))
                .andExpect(jsonPath("positions[0].name", is("Company A")))
                .andExpect(jsonPath("positions[0].quantity", is(10)))
                .andExpect(jsonPath("positions[0].marketPrice", is(200.0)))
                .andExpect(jsonPath("positions[0].totalMarketPrice", is(2000.0)))
                .andExpect(jsonPath("positions[1].isin", is("B")))
                .andExpect(jsonPath("positions[1].name", is("Unknown")))
                .andExpect(jsonPath("positions[1].quantity", is(10)))
                .andExpect(jsonPath("positions[1].marketPrice", is(300.0)))
                .andExpect(jsonPath("positions[1].totalMarketPrice", is(3000.0)))
                .andExpect(jsonPath("summary.totalStocksQuantity", is(20)))
                .andExpect(jsonPath("summary.totalStocksMarketPrice", is(5000.0)))
                .andExpect(jsonPath("summary.availableMoney", is(7960.0)))
                .andExpect(jsonPath("summary.totalBalance", is(12960.0)));
    }

    @Test
    public void getPosition() throws Exception {
        this.mvc.perform(MockMvcRequestBuilders
                .get("/api/account/positions/DE0008430026")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("Test"));
    }

    @Test
    public void getTransactions() throws Exception {
        this.mvc.perform(MockMvcRequestBuilders
                .get("/api/account/transactions/")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("transactions", hasSize(2)))
                .andExpect(jsonPath("transactions[0].date", is("2018-04-12")))
                .andExpect(jsonPath("transactions[0].transactionType", is("Buy")))
                .andExpect(jsonPath("transactions[0].isin", is("A")))
                .andExpect(jsonPath("transactions[0].name", is("Company A")))
                .andExpect(jsonPath("transactions[0].quantity", is(10)))
                .andExpect(jsonPath("transactions[0].marketPrice", is(100.0)))
                .andExpect(jsonPath("transactions[0].totalPrice", is(1000.0)))
                .andExpect(jsonPath("transactions[0].commission", is(20.0)))
                .andExpect(jsonPath("transactions[1].date", is("2018-04-13")))
                .andExpect(jsonPath("transactions[1].transactionType", is("Buy")))
                .andExpect(jsonPath("transactions[1].isin", is("B")))
                .andExpect(jsonPath("transactions[1].name", is("Unknown")))
                .andExpect(jsonPath("transactions[1].quantity", is(10)))
                .andExpect(jsonPath("transactions[1].marketPrice", is(100.0)))
                .andExpect(jsonPath("transactions[1].totalPrice", is(1000.0)))
                .andExpect(jsonPath("transactions[1].commission", is(20.0)));
    }

    @Test
    public void getTransaction() throws Exception {
        this.mvc.perform(MockMvcRequestBuilders
                .get("/api/account/transactions/1")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("Test"));
    }
}
