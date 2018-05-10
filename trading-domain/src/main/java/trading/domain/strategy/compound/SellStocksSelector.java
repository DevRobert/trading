package trading.domain.strategy.compound;

import trading.domain.ISIN;
import trading.domain.Quantity;

import java.util.HashMap;
import java.util.Map;

public class SellStocksSelector {
    private final Score minSellScore;

    public SellStocksSelector(Score minSellScore) {
        this.minSellScore = minSellScore;
    }

    public Map<ISIN, Quantity> selectStocks(Scores scores, Map<ISIN, Quantity> currentStocks) {
        Map<ISIN, Quantity> sellStocks = new HashMap<>();

        for(ISIN isin: scores.getIsinsOrderByScoreDescending()) {
            Score score = scores.get(isin);

            if(score.getValue() < this.minSellScore.getValue()) {
                break;
            }

            Quantity quantity = currentStocks.get(isin);
            sellStocks.put(isin, quantity);
        }

        return sellStocks;
    }
}
