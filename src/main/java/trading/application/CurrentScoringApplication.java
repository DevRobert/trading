package trading.application;

import trading.DayCount;
import trading.ISIN;
import trading.challenges.HistoricalTestDataProvider;
import trading.market.HistoricalMarketData;
import trading.market.HistoricalStockData;
import trading.market.MarketPriceSnapshot;
import trading.strategy.compound.MultiStockScoring;
import trading.strategy.compound.Score;
import trading.strategy.compound.Scores;
import trading.strategy.compound.ScoringStrategy;

import java.util.List;

public class CurrentScoringApplication {
    public static void main(String[] args) {
        List<MarketPriceSnapshot> historicalClosingPrices = HistoricalTestDataProvider.getHistoricalClosingPrices();
        HistoricalMarketData historicalMarketData = new HistoricalMarketData(historicalClosingPrices.get(0));

        for(int dayIndex = 1; dayIndex < historicalClosingPrices.size(); dayIndex++) {
            historicalMarketData.registerClosedDay(historicalClosingPrices.get(dayIndex));
        }

        Scores scores = new MultiStockScoring().calculateScores(historicalMarketData, new ScoringStrategy() {
            @Override
            public Score calculateScore(HistoricalMarketData historicalMarketData, ISIN isin) {
                if(shouldBuyStocks(historicalMarketData, isin)) {
                    return new Score(1.0);
                }
                else {
                    return new Score(0);
                }
            }
        });

        System.out.println("");
        System.out.println("Scores");

        for(ISIN isin: scores.getIsinsOrderByScoreDescending()) {
            System.out.println(isin.getText() + " - " + scores.get(isin).getValue());
        }
    }

    private static boolean shouldBuyStocks(HistoricalMarketData historicalMarketData, ISIN isin) {
        System.out.println("");
        System.out.println("Calculate score for isin: " + isin.getText());

        DayCount buyTriggerLocalMaximumLookBehindPeriod = new DayCount(10);
        double buyTriggerMinDeclineSinceMaximumPercentage = 0.1;

        HistoricalStockData historicalStockData = historicalMarketData.getStockData(isin);

        double lastClosingPrice = historicalStockData.getLastClosingMarketPrice().getValue();

        System.out.println("Last closing price: " + lastClosingPrice);

        double localMaximum = historicalStockData.getMaximumClosingMarketPrice(buyTriggerLocalMaximumLookBehindPeriod).getValue();

        System.out.println("Local maximum: " + localMaximum);

        double maxBuyPrice = localMaximum * (1.0 - buyTriggerMinDeclineSinceMaximumPercentage);

        System.out.println("Max buy price: " + maxBuyPrice);

        if(lastClosingPrice <= maxBuyPrice) {
            System.out.println("Result: Buy!");
            return true;
        }

        System.out.println("Result: Do not buy!");
        return false;
    }
}
