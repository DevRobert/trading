package trading.domain.strategy.compoundLocalMaximum;

import trading.domain.DayCount;
import trading.domain.ISIN;
import trading.domain.account.Account;
import trading.domain.market.HistoricalMarketData;
import trading.domain.market.HistoricalStockData;
import trading.domain.strategy.compound.Score;
import trading.domain.strategy.compound.ScoringStrategy;

public class LocalMaximumBuyScoringStrategy implements ScoringStrategy {
    private final DayCount buyTriggerLocalMaximumLookBehindPeriod;
    private final double buyTriggerMinDeclineSinceMaximumPercentage;
    private boolean commentsEnabled;

    public LocalMaximumBuyScoringStrategy(DayCount buyTriggerLocalMaximumLookBehindPeriod, double buyTriggerMinDeclineSinceMaximumPercentage) {
        this.buyTriggerLocalMaximumLookBehindPeriod = buyTriggerLocalMaximumLookBehindPeriod;
        this.buyTriggerMinDeclineSinceMaximumPercentage = buyTriggerMinDeclineSinceMaximumPercentage;
        this.commentsEnabled = false;
    }

    public void enableComments() {
        this.commentsEnabled = true;
    }

    @Override
    public Score calculateScore(HistoricalMarketData historicalMarketData, Account account, ISIN isin) {
        HistoricalStockData historicalStockData = historicalMarketData.getStockData(isin);

        if(historicalStockData.getDuration().getValue() < this.buyTriggerLocalMaximumLookBehindPeriod.getValue()) {
            return new Score(0.0, "Historical stock data history length not sufficient for scoring.");
        }

        double lastClosingPrice = historicalStockData.getLastClosingMarketPrice().getValue();
        double localMaximum = historicalStockData.getMaximumClosingMarketPrice(this.buyTriggerLocalMaximumLookBehindPeriod).getValue();
        double maxBuyPrice = localMaximum * (1.0 - this.buyTriggerMinDeclineSinceMaximumPercentage);
        boolean buy = lastClosingPrice <= maxBuyPrice;

        double score = 1.0;

        if(!buy) {
            score = maxBuyPrice / lastClosingPrice;
        }

        String comment = null;

        if(this.commentsEnabled) {
            StringBuilder commentBuilder = new StringBuilder();
            commentBuilder.append("Last closing price: " + lastClosingPrice + "\n");
            commentBuilder.append("Local maximum: " + localMaximum + "\n");
            commentBuilder.append("Max buy price: " + maxBuyPrice + "\n");
            commentBuilder.append(buy ? "Result: Buy!" : "Result: Do not buy!");
            comment = commentBuilder.toString();
        }

        return new Score(score, comment);
    }
}
