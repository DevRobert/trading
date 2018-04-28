package trading.api;

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
import trading.application.ScoringService;
import trading.domain.ISIN;
import trading.domain.market.InstrumentNameProvider;
import trading.domain.strategy.compound.Score;
import trading.domain.strategy.compound.Scores;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.is;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ScoringControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ScoringService scoringService;

    @MockBean
    private InstrumentNameProvider instrumentNameProvider;

    @Test
    public void getScoring() throws Exception {
        Map<ISIN, Score> values = new HashMap<>();
        values.put(new ISIN("A"), new Score(1.0, "My first comment"));
        values.put(new ISIN("B"), new Score(0.5, "My second comment"));
        Scores scores = new Scores(values);

        given(this.scoringService.getCurrentScoring()).willReturn(scores);

        given(this.instrumentNameProvider.getInstrumentName(new ISIN("A"))).willReturn("My first stock");

        this.mockMvc.perform(MockMvcRequestBuilders
                .get("/api/scoring")
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
                .andExpect(jsonPath("scores[1].comment", is("My second comment")));
    }
}
