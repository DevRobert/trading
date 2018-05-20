package trading.api;

import org.junit.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import trading.domain.Amount;
import trading.domain.ISIN;
import trading.domain.market.InstrumentNameProvider;
import trading.domain.market.MarketPriceSnapshot;
import trading.domain.market.MarketPriceSnapshotBuilder;
import trading.domain.simulation.MultiStockMarketDataStore;

import java.time.LocalDate;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class MarketControllerTest extends ControllerTestBase {
    @MockBean
    private MultiStockMarketDataStore multiStockMarketDataStore;

    @MockBean
    private InstrumentNameProvider instrumentNameProvider;

    @Test
    public void getInstruments() throws Exception {
        MarketPriceSnapshot marketPriceSnapshot = new MarketPriceSnapshotBuilder()
                .setMarketPrice(new ISIN("A"), new Amount(100.0))
                .setMarketPrice(new ISIN("B"), new Amount(200.0))
                .setDate(LocalDate.of(2000, 1, 2))
                .build();

        given(this.multiStockMarketDataStore.getLastClosingPrices()).willReturn(marketPriceSnapshot);

        given(this.instrumentNameProvider.getInstrumentName(new ISIN("A"))).willReturn("Company A");

        this.mvc.perform(MockMvcRequestBuilders
                .get("/api/market/instruments/")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("date", is("2000-01-02")))
                .andExpect(jsonPath("instruments", hasSize(2)))
                .andExpect(jsonPath("instruments[0].isin", is("A")))
                .andExpect(jsonPath("instruments[0].name", is("Company A")))
                .andExpect(jsonPath("instruments[0].marketPrice", is(100.0)))
                .andExpect(jsonPath("instruments[1].isin", is("B")))
                .andExpect(jsonPath("instruments[1].name", is("Unknown")))
                .andExpect(jsonPath("instruments[1].marketPrice", is(200.0)));
    }
}
