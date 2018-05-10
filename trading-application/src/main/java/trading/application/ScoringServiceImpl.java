package trading.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import trading.domain.DayCount;
import trading.domain.ISIN;
import trading.domain.account.Account;
import trading.domain.challenges.HistoricalTestDataProvider;
import trading.domain.market.HistoricalMarketData;
import trading.domain.market.MarketPriceSnapshot;
import trading.domain.simulation.MultiStockMarketDataStore;
import trading.domain.strategy.compound.MultiStockScoring;
import trading.domain.strategy.compound.Scores;
import trading.domain.strategy.compound.ScoringStrategy;
import trading.domain.strategy.compoundLocalMaximum.LocalMaximumBuyScoringStrategy;
import trading.domain.strategy.compoundLocalMaximum.LocalMaximumSellScoringStrategy;

import java.util.List;
import java.util.Set;

@Component
public class ScoringServiceImpl implements ScoringService {
    @Autowired
    private MultiStockMarketDataStore multiStockMarketDataStore;

    public ScoringServiceImpl(MultiStockMarketDataStore multiStockMarketDataStore) {
        this.multiStockMarketDataStore = multiStockMarketDataStore;
    }

    @Override
    public Scores calculateBuyScoring(Account account) {
        DayCount buyTriggerLocalMaximumLookBehindPeriod = new DayCount(10);
        double buyTriggerMinDeclineSinceMaximumPercentage = 0.1;

        ScoringStrategy scoringStrategy = new LocalMaximumBuyScoringStrategy(
                buyTriggerLocalMaximumLookBehindPeriod,
                buyTriggerMinDeclineSinceMaximumPercentage
        );

        Set<ISIN> isins = this.multiStockMarketDataStore.getAllClosingPrices().get(0).getISINs();
        return calculateScoring(account, scoringStrategy, isins);
    }

    @Override
    public Scores calculateSellScoring(Account account) {
        double activateTrailingStopLossMinRaiseSinceBuyingPercentage = 0.03;
        double sellTriggerStopLossMinimumDeclineSinceBuyingPercentage = 0.1;
        double sellTriggerTrailingStopLossMinDeclineSinceMaximumAfterBuyingPercentage = 0.07;

        ScoringStrategy scoringStrategy = new LocalMaximumSellScoringStrategy(
                activateTrailingStopLossMinRaiseSinceBuyingPercentage,
                sellTriggerStopLossMinimumDeclineSinceBuyingPercentage,
                sellTriggerTrailingStopLossMinDeclineSinceMaximumAfterBuyingPercentage
        );

        return calculateScoring(account, scoringStrategy, account.getCurrentStocks().keySet());
    }

    private Scores calculateScoring(Account account, ScoringStrategy scoringStrategy, Set<ISIN> isins) {
        HistoricalTestDataProvider historicalTestDataProvider = new HistoricalTestDataProvider(this.multiStockMarketDataStore);

        List<MarketPriceSnapshot> historicalClosingPrices = historicalTestDataProvider.getHistoricalClosingPrices();
        HistoricalMarketData historicalMarketData = new HistoricalMarketData(historicalClosingPrices.get(0));

        for(int dayIndex = 1; dayIndex < historicalClosingPrices.size(); dayIndex++) {
            historicalMarketData.registerClosedDay(historicalClosingPrices.get(dayIndex));
        }

        return new MultiStockScoring().calculateScores(historicalMarketData, account, scoringStrategy, isins);
    }
}
