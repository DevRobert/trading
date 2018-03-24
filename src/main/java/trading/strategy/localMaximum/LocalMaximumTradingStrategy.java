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
 * Buy after declined from local maximum and sell after certain level reached (activation of trailing stop loss)
 * and then declined under certain level below maximum since buying (trailing stop loss) or declined under
 * certain level (stop loss).
 */
public class LocalMaximumTradingStrategy implements TradingStrategy {
    private final ProgressiveTradingStrategy progressiveTradingStrategy;
    private final HistoricalStockData historicalStockData;

    private final DayCount buyTriggerLocalMaximumLookBehindPeriod;
    private final double buyTriggerMinDeclineSinceMaximumPercentage;

    private final double activateTrailingStopLossMinRaiseSinceBuyingPercentage;
    private final double sellTriggerTrailingStopLossMinDeclineSinceMaximumAfterBuyingPercentage;
    private final double sellTriggerStopLossMinimumDeclineSinceBuyingPercentage;

    private double buyPrice = 0.0;
    private boolean sellTriggerTrailingStopLossActivated = false;
    private double maxPriceSinceBuying = 0.0;

    public LocalMaximumTradingStrategy(LocalMaximumTradingStrategyParameters parameters, TradingStrategyContext context) {
        this.historicalStockData = context.getHistoricalMarketData().getStockData(parameters.getIsin());

        ProgressiveTradingStrategyParametersBuilder parametersBuilder = new ProgressiveTradingStrategyParametersBuilder();

        parametersBuilder.setISIN(parameters.getIsin());

        parametersBuilder.setBuyTriggerFactory(historicalMarketData -> new DelegateTrigger(() -> this.shouldBuyStocks()));
        parametersBuilder.setSellTriggerFactory(historicalMarketData -> new DelegateTrigger(() -> this.shouldSellStocks()));
        parametersBuilder.setResetTriggerFactory(historicalMarketData -> new AlwaysFiresTrigger());

        this.progressiveTradingStrategy = new ProgressiveTradingStrategy(parametersBuilder.build(), context);

        this.buyTriggerLocalMaximumLookBehindPeriod = parameters.getBuyTriggerLocalMaximumLookBehindPeriod();
        this.buyTriggerMinDeclineSinceMaximumPercentage = parameters.getBuyTriggerMinDeclineFromMaximumPercentage();
        this.activateTrailingStopLossMinRaiseSinceBuyingPercentage = parameters.getActivateTrailingStopLossMinRaiseSinceBuyingPercentage();
        this.sellTriggerTrailingStopLossMinDeclineSinceMaximumAfterBuyingPercentage = parameters.getSellTriggerTrailingStopLossMinDeclineFromMaximumAfterBuyingPercentage();
        this.sellTriggerStopLossMinimumDeclineSinceBuyingPercentage = parameters.getSellTriggerStopLossMinimumDeclineSinceBuyingPercentage();
    }

    private boolean shouldBuyStocks() {
        double lastClosingPrice = this.historicalStockData.getLastClosingMarketPrice().getValue();

        double localMaximum = this.historicalStockData.getMaximumClosingMarketPrice(this.buyTriggerLocalMaximumLookBehindPeriod).getValue();
        double maxBuyPrice = localMaximum * (1.0 - this.buyTriggerMinDeclineSinceMaximumPercentage);

        if(lastClosingPrice <= maxBuyPrice) {
            this.sellTriggerTrailingStopLossActivated = false;
            this.maxPriceSinceBuying = 0.0;
            this.buyPrice = lastClosingPrice;

            return true;
        }

        return false;
    }

    private boolean shouldSellStocks() {
        double lastClosingPrice = this.historicalStockData.getLastClosingMarketPrice().getValue();

        this.updateMaxPriceSinceBuying(lastClosingPrice);
        this.updateTrailingStopLossActivation(lastClosingPrice);

        if(this.stopLoss(lastClosingPrice)) {
            return true;
        }

        return this.sellTriggerTrailingStopLossActivated && this.trailingStopLoss(lastClosingPrice, maxPriceSinceBuying);
    }

    private void updateMaxPriceSinceBuying(double lastClosingPrice) {
        if(lastClosingPrice > this.maxPriceSinceBuying) {
            this.maxPriceSinceBuying = lastClosingPrice;
        }
    }

    private boolean stopLoss(double lastClosingPrice) {
        double stopLossMaximumPrice = this.buyPrice * (1.0 - this.sellTriggerStopLossMinimumDeclineSinceBuyingPercentage);
        return lastClosingPrice <= stopLossMaximumPrice;
    }

    private void updateTrailingStopLossActivation(double lastClosingPrice) {
        if(this.sellTriggerTrailingStopLossActivated) {
            return;
        }

        double minimumActivationPrice = this.buyPrice * (1.0 + this.activateTrailingStopLossMinRaiseSinceBuyingPercentage);
        this.sellTriggerTrailingStopLossActivated = lastClosingPrice >= minimumActivationPrice;
    }

    private boolean trailingStopLoss(double lastClosingPrice, double maximumSinceBuying) {
        double trailingStopLossMaximumPrice = this.maxPriceSinceBuying * (1.0 - this.sellTriggerTrailingStopLossMinDeclineSinceMaximumAfterBuyingPercentage);
        return lastClosingPrice <= trailingStopLossMaximumPrice;
    }

    @Override
    public void prepareOrdersForNextTradingDay() {
        this.progressiveTradingStrategy.prepareOrdersForNextTradingDay();
    }
}
