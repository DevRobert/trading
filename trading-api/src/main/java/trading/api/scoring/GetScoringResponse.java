package trading.api.scoring;

import java.time.LocalDate;
import java.util.List;

public class GetScoringResponse {
    private List<ScoreDto> scores;
    private LocalDate marketPricesDate;

    public List<ScoreDto> getScores() {
        return scores;
    }

    public void setScores(List<ScoreDto> scores) {
        this.scores = scores;
    }

    public LocalDate getMarketPricesDate() {
        return this.marketPricesDate;
    }

    public void setMarketPricesDate(LocalDate marketPricesDate) {
        this.marketPricesDate = marketPricesDate;
    }
}
