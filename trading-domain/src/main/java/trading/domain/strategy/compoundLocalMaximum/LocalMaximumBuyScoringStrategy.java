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

    public LocalMaximumBuyScoringStrategy(DayCount buyTriggerLocalMaximumLookBehindPeriod, double buyTriggerMinDeclineSinceMaximumPercentage) {
        this.buyTriggerLocalMaximumLookBehindPeriod = buyTriggerLocalMaximumLookBehindPeriod;
        this.buyTriggerMinDeclineSinceMaximumPercentage = buyTriggerMinDeclineSinceMaximumPercentage;
    }

    @Override
    public Score calculateScore(HistoricalMarketData historicalMarketData, Account account, ISIN isin) {
        HistoricalStockData historicalStockData = historicalMarketData.getStockData(isin);

        StringBuilder comment = new StringBuilder();

        double lastClosingPrice = historicalStockData.getLastClosingMarketPrice().getValue();
        comment.append("Last closing price: " + lastClosingPrice + "\n");

        double localMaximum = historicalStockData.getMaximumClosingMarketPrice(this.buyTriggerLocalMaximumLookBehindPeriod).getValue();
        comment.append("Local maximum: " + localMaximum + "\n");

        double maxBuyPrice = localMaximum * (1.0 - this.buyTriggerMinDeclineSinceMaximumPercentage);
        comment.append("Max buy price: " + maxBuyPrice + "\n");

        boolean buy = lastClosingPrice <= maxBuyPrice;
        comment.append(buy ? "Result: Buy!" : "Result: Do not buy!");

        return new Score(buy ? 1.0 : 0.0, comment.toString());
    }
}
