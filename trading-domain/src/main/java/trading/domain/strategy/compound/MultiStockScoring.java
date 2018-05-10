package trading.domain.strategy.compound;

import trading.domain.ISIN;
import trading.domain.account.Account;
import trading.domain.market.HistoricalMarketData;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class MultiStockScoring {
    public Scores calculateScores(HistoricalMarketData historicalMarketData, Account account, ScoringStrategy scoringStrategy, Set<ISIN> isins) {
        if(historicalMarketData == null) {
            throw new RuntimeException("The historical market data have to be specified.");
        }

        if(scoringStrategy == null) {
            throw new RuntimeException("The scoring strategy has to be specified.");
        }

        HashMap<ISIN, Score> values = new HashMap<>();

        for(ISIN isin: isins) {
            values.put(isin, scoringStrategy.calculateScore(historicalMarketData, account, isin));
        }

        return new Scores(values, historicalMarketData.getDate());
    }
}
