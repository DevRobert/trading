package trading.strategy.localMaximum;

import trading.market.HistoricalStockData;
import trading.strategy.AlwaysFiresTrigger;
import trading.strategy.DelegateTrigger;
import trading.strategy.TradingStrategy;
import trading.strategy.TradingStrategyContext;
import trading.strategy.progressive.ProgressiveTradingStrategy;
import trading.strategy.progressive.ProgressiveTradingStrategyParametersBuilder;

/**
 * Trading strategy:
 *
 * Buy after distance from local maximum reached and sell after
 * maximum passed and declined under certain level below maximum since buying.
 */
public class LocalMaximumTradingStrategy implements TradingStrategy {
    private final ProgressiveTradingStrategy progressiveTradingStrategy;
    private final HistoricalStockData historicalStockData;
    private final LocalMaximumTradingStrategyParameters parameters;

    private double buyLocalMaximum = 0.0;
    private boolean buyLocalMaximumPassed = false;
    private double maximumSinceBuying = 0.0;

    public LocalMaximumTradingStrategy(LocalMaximumTradingStrategyParameters parameters, TradingStrategyContext context) {
        this.parameters = parameters;
        this.historicalStockData = context.getHistoricalMarketData().getStockData(parameters.getIsin());

        ProgressiveTradingStrategyParametersBuilder parametersBuilder = new ProgressiveTradingStrategyParametersBuilder();

        parametersBuilder.setISIN(parameters.getIsin());

        parametersBuilder.setBuyTriggerFactory(historicalMarketData -> new DelegateTrigger(() -> this.shouldBuyStocks()));
        parametersBuilder.setSellTriggerFactory(historicalMarketData -> new DelegateTrigger(() -> this.shouldSellStocks()));
        parametersBuilder.setResetTriggerFactory(historicalMarketData -> new AlwaysFiresTrigger());

        this.progressiveTradingStrategy = new ProgressiveTradingStrategy(parametersBuilder.build(), context);
    }

    private boolean shouldBuyStocks() {
        double localMaximum = this.historicalStockData.getMaximumClosingMarketPrice(this.parameters.getBuyTriggerLocalMaximumLookBehindPeriod()).getValue();
        double minDelta = localMaximum * this.parameters.getBuyTriggerMinDistanceFromLocalMaximumPercentage();
        double maxBuyPrice = localMaximum - minDelta;
        double lastClosingMarketPrice = this.historicalStockData.getLastClosingMarketPrice().getValue();

        if(lastClosingMarketPrice <= maxBuyPrice) {
            this.buyLocalMaximum = localMaximum;
            this.buyLocalMaximumPassed = false;
            this.maximumSinceBuying = 0.0;

            return true;
        }

        return false;
    }

    private boolean shouldSellStocks() {
        double lastClosingPrice = this.historicalStockData.getLastClosingMarketPrice().getValue();

        if(!this.buyLocalMaximumPassed) {
            this.buyLocalMaximumPassed = lastClosingPrice >= this.buyLocalMaximum;
        }

        if(!this.buyLocalMaximumPassed) {
            return false;
        }

        if(lastClosingPrice > this.maximumSinceBuying) {
            this.maximumSinceBuying = lastClosingPrice;
        }

        double sellTriggerMinDeltaFromMaximumSinceBuying = this.parameters.getSellTriggerMinDistanceFromMaximumSinceBuyingPercentage() * this.maximumSinceBuying;
        double sellTriggerMaxPrice = this.maximumSinceBuying - sellTriggerMinDeltaFromMaximumSinceBuying;

        return lastClosingPrice <= sellTriggerMaxPrice;
    }

    @Override
    public void prepareOrdersForNextTradingDay() {
        this.progressiveTradingStrategy.prepareOrdersForNextTradingDay();
    }
}
