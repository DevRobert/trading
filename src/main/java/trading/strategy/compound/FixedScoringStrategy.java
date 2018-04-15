package trading.strategy.compound;

import trading.ISIN;
import trading.market.HistoricalMarketData;

import java.util.HashMap;
import java.util.Map;

public class FixedScoringStrategy implements ScoringStrategy {
    private final Map<ISIN, Score> fixedScores;

    public FixedScoringStrategy() {
        this.fixedScores = new HashMap<>();
    }

    public FixedScoringStrategy setScore(ISIN isin, Score score) {
        this.fixedScores.put(isin, score);
        return this;
    }

    @Override
    public Score calculateScore(HistoricalMarketData historicalMarketData, ISIN isin) {
        return this.fixedScores.get(isin);
    }
}
