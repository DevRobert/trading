package trading.api;

import org.junit.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import trading.application.AccountService;
import trading.application.TradeList;
import trading.application.TradingService;
import trading.domain.Amount;
import trading.domain.ISIN;
import trading.domain.Quantity;
import trading.domain.account.Account;
import trading.domain.account.AccountId;
import trading.domain.broker.OrderRequest;
import trading.domain.broker.OrderType;
import trading.domain.market.InstrumentNameProvider;
import trading.domain.market.MarketPriceSnapshotBuilder;
import trading.domain.simulation.MultiStockMarketDataStore;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TradingControllerTest extends ControllerTestBase {
    @MockBean
    private AccountService accountService;

    @MockBean
    private TradingService tradingService;

    @MockBean
    private InstrumentNameProvider instrumentNameProvider;

    @MockBean
    private MultiStockMarketDataStore multiStockMarketDataStore;

    @Test
    public void calculateTrades() throws Exception {
        Account account = new Account(new Amount(10000.0));

        List<OrderRequest> trades = new ArrayList<>();
        trades.add(new OrderRequest(OrderType.BuyMarket, new ISIN("A"), new Quantity(10)));
        trades.add(new OrderRequest(OrderType.SellMarket, new ISIN("B"), new Quantity(20)));

        TradeList tradeList = new TradeList(LocalDate.of(2018, 5, 9), trades);

        given(accountService.getAccount(new AccountId(1))).willReturn(account);
        given(tradingService.calculateTrades(account)).willReturn(tradeList);
        given(instrumentNameProvider.getInstrumentName(new ISIN("A"))).willReturn("My first stock");

        given(multiStockMarketDataStore.getLastClosingPrices()).willReturn(
                new MarketPriceSnapshotBuilder()
                        .setMarketPrice(new ISIN("A"), new Amount(100.0))
                        .setMarketPrice(new ISIN("B"), new Amount(50.0))
                        .setDate(LocalDate.of(2018, 5, 9))
                        .build()
        );

        this.mvc.perform(MockMvcRequestBuilders
                .get("/api/trades/")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("trades", hasSize(2)))
                .andExpect(jsonPath("trades[0].type", is("Buy")))
                .andExpect(jsonPath("trades[0].isin", is("A")))
                .andExpect(jsonPath("trades[0].name", is("My first stock")))
                .andExpect(jsonPath("trades[0].quantity", is(10)))
                .andExpect(jsonPath("trades[0].marketPrice", is(100.0)))
                .andExpect(jsonPath("trades[0].totalPrice", is(1000.0)))
                .andExpect(jsonPath("trades[0].commission", is(2.08))) // Degiro Xetra
                .andExpect(jsonPath("trades[1].type", is("Sell")))
                .andExpect(jsonPath("trades[1].isin", is("B")))
                .andExpect(jsonPath("trades[1].name", is("Unknown")))
                .andExpect(jsonPath("trades[1].quantity", is(20)))
                .andExpect(jsonPath("trades[1].marketPrice", is(50.0)))
                .andExpect(jsonPath("trades[1].totalPrice", is(1000.0)))
                .andExpect(jsonPath("trades[1].commission", is(2.08))) // Degiro Xetra
                .andExpect(jsonPath("marketPricesDate", is("2018-05-09")));
    }
}
