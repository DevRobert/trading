package trading.api.scoring;

import java.util.List;

public class GetScoringResponse {
    private List<ScoreDto> scores;

    public List<ScoreDto> getScores() {
        return scores;
    }

    public void setScores(List<ScoreDto> scores) {
        this.scores = scores;
    }
}
