package trading.api;

import org.junit.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import trading.application.AccountService;
import trading.application.ScoringService;
import trading.domain.Amount;
import trading.domain.ISIN;
import trading.domain.account.Account;
import trading.domain.account.AccountId;
import trading.domain.market.InstrumentNameProvider;
import trading.domain.strategy.compound.Score;
import trading.domain.strategy.compound.Scores;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ScoringControllerTest extends ControllerTestBase {
    @MockBean
    private AccountService accountService;

    @MockBean
    private ScoringService scoringService;

    @MockBean
    private InstrumentNameProvider instrumentNameProvider;

    @Test
    public void calculateBuyScoring() throws Exception {
        Account account = new Account(new Amount(10000.0));

        Map<ISIN, Score> values = new HashMap<>();
        values.put(new ISIN("A"), new Score(1.0, "My first comment"));
        values.put(new ISIN("B"), new Score(0.5, "My second comment"));
        Scores scores = new Scores(values, LocalDate.of(2018, 4, 29));

        given(this.accountService.getAccount(new AccountId(1))).willReturn(account);

        given(this.scoringService.calculateBuyScoring(account)).willReturn(scores);

        given(this.instrumentNameProvider.getInstrumentName(new ISIN("A"))).willReturn("My first stock");

        this.mvc.perform(MockMvcRequestBuilders
                .get("/api/scoring/buy")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("scores", hasSize(2)))
                .andExpect(jsonPath("scores[0].isin", is("A")))
                .andExpect(jsonPath("scores[0].name", is("My first stock")))
                .andExpect(jsonPath("scores[0].score", is(1.0)))
                .andExpect(jsonPath("scores[0].comment", is("My first comment")))
                .andExpect(jsonPath("scores[1].isin", is("B")))
                .andExpect(jsonPath("scores[1].name", is("Unknown")))
                .andExpect(jsonPath("scores[1].score", is(0.5)))
                .andExpect(jsonPath("scores[1].comment", is("My second comment")))
                .andExpect(jsonPath("marketPricesDate", is("2018-04-29")));
    }

    @Test
    public void calculateSellScoring() throws Exception {
        Account account = new Account(new Amount(10000.0));

        Map<ISIN, Score> values = new HashMap<>();
        values.put(new ISIN("A"), new Score(1.0, "My first comment"));
        values.put(new ISIN("B"), new Score(0.5, "My second comment"));
        Scores scores = new Scores(values, LocalDate.of(2018, 4, 29));

        given(this.accountService.getAccount(new AccountId(1))).willReturn(account);

        given(this.scoringService.calculateSellScoring(account)).willReturn(scores);

        given(this.instrumentNameProvider.getInstrumentName(new ISIN("A"))).willReturn("My first stock");

        this.mvc.perform(MockMvcRequestBuilders
                .get("/api/scoring/sell")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("scores", hasSize(2)))
                .andExpect(jsonPath("scores[0].isin", is("A")))
                .andExpect(jsonPath("scores[0].name", is("My first stock")))
                .andExpect(jsonPath("scores[0].score", is(1.0)))
                .andExpect(jsonPath("scores[0].comment", is("My first comment")))
                .andExpect(jsonPath("scores[1].isin", is("B")))
                .andExpect(jsonPath("scores[1].name", is("Unknown")))
                .andExpect(jsonPath("scores[1].score", is(0.5)))
                .andExpect(jsonPath("scores[1].comment", is("My second comment")))
                .andExpect(jsonPath("marketPricesDate", is("2018-04-29")));
    }
}
