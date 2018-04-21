package trading.domain.strategy.compound;

import trading.domain.ISIN;
import trading.domain.market.HistoricalMarketData;

import java.util.HashMap;

public class MultiStockScoring {
    public Scores calculateScores(HistoricalMarketData historicalMarketData, ScoringStrategy scoringStrategy) {
        if(historicalMarketData == null) {
            throw new RuntimeException("The historical market data have to be specified.");
        }

        if(scoringStrategy == null) {
            throw new RuntimeException("The scoring strategy has to be specified.");
        }

        HashMap<ISIN, Score> values = new HashMap<>();

        for(ISIN isin: historicalMarketData.getAvailableStocks()) {
            values.put(isin, scoringStrategy.calculateScore(historicalMarketData, isin));
        }

        return new Scores(values);
    }
}
