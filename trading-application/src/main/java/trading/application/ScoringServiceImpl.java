package trading.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import trading.domain.DayCount;
import trading.domain.ISIN;
import trading.domain.account.Account;
import trading.domain.market.HistoricalMarketData;
import trading.domain.simulation.MultiStockMarketDataStore;
import trading.domain.strategy.compound.MultiStockScoring;
import trading.domain.strategy.compound.Scores;
import trading.domain.strategy.compound.ScoringStrategy;
import trading.domain.strategy.compoundLocalMaximum.CompoundLocalMaximumTradingStrategyParameters;
import trading.domain.strategy.compoundLocalMaximum.LocalMaximumBuyScoringStrategy;
import trading.domain.strategy.compoundLocalMaximum.LocalMaximumSellScoringStrategy;

import java.util.Set;

@Component
public class ScoringServiceImpl implements ScoringService {
    @Autowired
    private MultiStockMarketDataStore multiStockMarketDataStore;

    @Autowired
    private TradingConfigurationService tradingConfigurationService;

    public ScoringServiceImpl(MultiStockMarketDataStore multiStockMarketDataStore) {
        this.multiStockMarketDataStore = multiStockMarketDataStore;
    }

    @Override
    public Scores calculateBuyScoring(Account account) {
        CompoundLocalMaximumTradingStrategyParameters parameters = this.tradingConfigurationService.getTradingStrategyParameters();

        DayCount buyTriggerLocalMaximumLookBehindPeriod = parameters.getBuyTriggerLocalMaximumLookBehindPeriod();
        double buyTriggerMinDeclineFromLocalMaximumPercentage = parameters.getBuyTriggerMinDeclineFromLocalMaximumPercentage();

        LocalMaximumBuyScoringStrategy scoringStrategy = new LocalMaximumBuyScoringStrategy(
                buyTriggerLocalMaximumLookBehindPeriod,
                buyTriggerMinDeclineFromLocalMaximumPercentage
        );

        scoringStrategy.enableComments();

        Set<ISIN> isins = this.multiStockMarketDataStore.getLastClosingPrices().getISINs();
        return calculateScoring(account, scoringStrategy, isins);
    }

    @Override
    public Scores calculateSellScoring(Account account) {
        CompoundLocalMaximumTradingStrategyParameters parameters = this.tradingConfigurationService.getTradingStrategyParameters();

        double activateTrailingStopLossMinRaiseSinceBuyingPercentage = parameters.getActivateTrailingStopLossMinRaiseSinceBuyingPercentage();
        double sellTriggerStopLossMinimumDeclineSinceBuyingPercentage = parameters.getSellTriggerStopLossMinimumDeclineSinceBuyingPercentage();
        double sellTriggerTrailingStopLossMinDeclineSinceMaximumAfterBuyingPercentage = parameters.getSellTriggerTrailingStopLossMinDeclineFromMaximumAfterBuyingPercentage();

        LocalMaximumSellScoringStrategy scoringStrategy = new LocalMaximumSellScoringStrategy(
                activateTrailingStopLossMinRaiseSinceBuyingPercentage,
                sellTriggerStopLossMinimumDeclineSinceBuyingPercentage,
                sellTriggerTrailingStopLossMinDeclineSinceMaximumAfterBuyingPercentage
        );

        scoringStrategy.enableComments();

        return calculateScoring(account, scoringStrategy, account.getCurrentStocks().keySet());
    }

    private Scores calculateScoring(Account account, ScoringStrategy scoringStrategy, Set<ISIN> isins) {
        HistoricalMarketData historicalMarketData = HistoricalMarketData.of(this.multiStockMarketDataStore.getAllClosingPrices());
        return new MultiStockScoring().calculateScores(historicalMarketData, account, scoringStrategy, isins);
    }
}
