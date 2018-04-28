package trading.api.scoring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import trading.application.ScoringService;
import trading.domain.ISIN;
import trading.domain.strategy.compound.Score;
import trading.domain.strategy.compound.Scores;

import java.util.ArrayList;
import java.util.List;

@RestController
public class ScoringController {
    @Autowired
    private ScoringService scoringService;

    @RequestMapping("/api/scoring")
    public GetScoringResponse getScoring() {
        GetScoringResponse response = new GetScoringResponse();

        Scores scores = this.scoringService.getCurrentScoring();

        List<ScoreDto> scoreDtos = new ArrayList<>();

        for(ISIN isin: scores.getIsinsOrderByScoreDescending()) {
            Score score = scores.get(isin);

            ScoreDto scoreDto = new ScoreDto();
            scoreDto.setIsin(isin.getText());
            scoreDto.setName("Unknown (todo)");
            scoreDto.setScore(score.getValue());
            scoreDto.setComment(score.getComment());

            scoreDtos.add(scoreDto);
        }

        response.setScores(scoreDtos);

        return response;
    }
}
