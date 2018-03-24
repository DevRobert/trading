package trading.strategy.localMaximum;

import trading.DayCount;
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
 * Buy after negative distance from local maximum reached and sell after
 * local maximum passed and declined under certain level below maximum since buying.
 */
public class LocalMaximumTradingStrategy implements TradingStrategy {
    private final ProgressiveTradingStrategy progressiveTradingStrategy;
    private final HistoricalStockData historicalStockData;

    private final DayCount buyTriggerLocalMaximumLookBehindPeriod;
    private final double buyTriggerMinDistanceFromLocalMaximumPercentage;
    private final double sellTriggerMinDistanceFromMaximumSinceBuyingPercentage;
    private final double sellTriggerStopLossMinDistanceFromBuyingPercentage;

    private double buyPrice = 0.0;
    private double buyLocalMaximum = 0.0;
    private boolean buyLocalMaximumPassed = false;
    private double maximumSinceBuying = 0.0;

    public LocalMaximumTradingStrategy(LocalMaximumTradingStrategyParameters parameters, TradingStrategyContext context) {
        this.historicalStockData = context.getHistoricalMarketData().getStockData(parameters.getIsin());

        ProgressiveTradingStrategyParametersBuilder parametersBuilder = new ProgressiveTradingStrategyParametersBuilder();

        parametersBuilder.setISIN(parameters.getIsin());

        parametersBuilder.setBuyTriggerFactory(historicalMarketData -> new DelegateTrigger(() -> this.shouldBuyStocks()));
        parametersBuilder.setSellTriggerFactory(historicalMarketData -> new DelegateTrigger(() -> this.shouldSellStocks()));
        parametersBuilder.setResetTriggerFactory(historicalMarketData -> new AlwaysFiresTrigger());

        this.progressiveTradingStrategy = new ProgressiveTradingStrategy(parametersBuilder.build(), context);

        this.buyTriggerLocalMaximumLookBehindPeriod = parameters.getBuyTriggerLocalMaximumLookBehindPeriod();
        this.buyTriggerMinDistanceFromLocalMaximumPercentage = parameters.getBuyTriggerMinDistanceFromLocalMaximumPercentage();
        this.sellTriggerMinDistanceFromMaximumSinceBuyingPercentage = parameters.getSellTriggerMinDistanceFromMaximumSinceBuyingPercentage();
        this.sellTriggerStopLossMinDistanceFromBuyingPercentage = 0.0;
    }

    private boolean shouldBuyStocks() {
        double localMaximum = this.historicalStockData.getMaximumClosingMarketPrice(this.buyTriggerLocalMaximumLookBehindPeriod).getValue();
        double minDelta = localMaximum * this.buyTriggerMinDistanceFromLocalMaximumPercentage;
        double maxBuyPrice = localMaximum - minDelta;
        double lastClosingMarketPrice = this.historicalStockData.getLastClosingMarketPrice().getValue();

        if(lastClosingMarketPrice <= maxBuyPrice) {
            this.buyLocalMaximum = localMaximum;
            this.buyLocalMaximumPassed = false;
            this.maximumSinceBuying = 0.0;
            this.buyPrice = lastClosingMarketPrice;

            return true;
        }

        return false;
    }

    private boolean shouldSellStocks() {
        double lastClosingPrice = this.historicalStockData.getLastClosingMarketPrice().getValue();

        this.updateLocalMaximumPassed(lastClosingPrice);
        this.updateMaximumSinceBuying(lastClosingPrice);

        if(this.stopLoss(lastClosingPrice)) {
            return true;
        }

        if(!this.buyLocalMaximumPassed) {
            return false;
        }

        if(this.trailingStopLoss(lastClosingPrice, maximumSinceBuying)) {
            return true;
        }

        return false;
    }

    private void updateLocalMaximumPassed(double lastClosingPrice) {
        if(this.buyLocalMaximumPassed) {
            return;
        }

        this.buyLocalMaximumPassed = lastClosingPrice >= this.buyLocalMaximum;
    }

    private void updateMaximumSinceBuying(double lastClosingPrice) {
        if(lastClosingPrice > this.maximumSinceBuying) {
            this.maximumSinceBuying = lastClosingPrice;
        }
    }

    private boolean stopLoss(double lastClosingPrice) {
        double stopLossPriceMaximumPrice = this.buyPrice * (1.0 - this.sellTriggerStopLossMinDistanceFromBuyingPercentage);
        return lastClosingPrice <= stopLossPriceMaximumPrice;
    }

    private boolean trailingStopLoss(double lastClosingPrice, double maximumSinceBuying) {
        double trailingStopLossMaximumPrice = this.maximumSinceBuying * (1.0 - this.sellTriggerMinDistanceFromMaximumSinceBuyingPercentage);
        return lastClosingPrice <= trailingStopLossMaximumPrice;
    }

    @Override
    public void prepareOrdersForNextTradingDay() {
        this.progressiveTradingStrategy.prepareOrdersForNextTradingDay();
    }
}
