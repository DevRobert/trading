package trading.application;

import trading.domain.DayCount;
import trading.domain.ISIN;
import trading.domain.challenges.HistoricalTestDataProvider;
import trading.domain.market.HistoricalMarketData;
import trading.domain.market.HistoricalStockData;
import trading.domain.market.MarketPriceSnapshot;
import trading.domain.simulation.MultiStockMarketDataStore;
import trading.domain.strategy.compound.MultiStockScoring;
import trading.domain.strategy.compound.Score;
import trading.domain.strategy.compound.Scores;
import trading.domain.strategy.compound.ScoringStrategy;

import java.util.List;

public class ScoringService {
    private MultiStockMarketDataStore multiStockMarketDataStore;

    public ScoringService(MultiStockMarketDataStore multiStockMarketDataStore) {
        this.multiStockMarketDataStore = multiStockMarketDataStore;
    }

    public Scores getCurrentScoring() {
        HistoricalTestDataProvider historicalTestDataProvider = new HistoricalTestDataProvider(this.multiStockMarketDataStore);

        List<MarketPriceSnapshot> historicalClosingPrices = historicalTestDataProvider.getHistoricalClosingPrices();
        HistoricalMarketData historicalMarketData = new HistoricalMarketData(historicalClosingPrices.get(0));

        for(int dayIndex = 1; dayIndex < historicalClosingPrices.size(); dayIndex++) {
            historicalMarketData.registerClosedDay(historicalClosingPrices.get(dayIndex));
        }

        return new MultiStockScoring().calculateScores(historicalMarketData, new ScoringStrategy() {
            @Override
            public Score calculateScore(HistoricalMarketData historicalMarketData, ISIN isin) {
                return ScoringService.calculateScore(historicalMarketData, isin);
            }
        });
    }

    private static Score calculateScore(HistoricalMarketData historicalMarketData, ISIN isin) {
        StringBuilder comment = new StringBuilder();

        DayCount buyTriggerLocalMaximumLookBehindPeriod = new DayCount(10);
        double buyTriggerMinDeclineSinceMaximumPercentage = 0.1;

        HistoricalStockData historicalStockData = historicalMarketData.getStockData(isin);

        double lastClosingPrice = historicalStockData.getLastClosingMarketPrice().getValue();
        comment.append("Last closing price: " + lastClosingPrice + "\n");

        double localMaximum = historicalStockData.getMaximumClosingMarketPrice(buyTriggerLocalMaximumLookBehindPeriod).getValue();
        comment.append("Local maximum: " + localMaximum + "\n");

        double maxBuyPrice = localMaximum * (1.0 - buyTriggerMinDeclineSinceMaximumPercentage);
        comment.append("Max buy price: " + maxBuyPrice + "\n");

        boolean buy = lastClosingPrice <= maxBuyPrice;
        double score;

        if(buy) {
            comment.append("Result: Buy!");
            score = 1.0;
        }
        else {
            comment.append("Result: Do not buy!");
            score = 0.0;
        }

        return new Score(score, comment.toString());
    }
}
