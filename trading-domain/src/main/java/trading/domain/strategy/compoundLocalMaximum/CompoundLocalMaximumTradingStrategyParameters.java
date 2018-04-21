package trading.domain.strategy.compoundLocalMaximum;

import trading.domain.DayCount;

public class CompoundLocalMaximumTradingStrategyParameters {
    private final DayCount buyTriggerLocalMaximumLookBehindPeriod;
    private final double buyTriggerMinDeclineFromLocalMaximumPercentage;
    private final double sellTriggerTrailingStopLossMinDeclineFromMaximumAfterBuyingPercentage;
    private final double activateTrailingStopLossMinRaiseSinceBuyingPercentage;
    private final double sellTriggerStopLossMinimumDeclineSinceBuyingPercentage;
    private final double maximumPercentage;

    public CompoundLocalMaximumTradingStrategyParameters(DayCount buyTriggerLocalMaximumLookBehindPeriod, double buyTriggerMinDeclineFromLocalMaximumPercentage, double sellTriggerTrailingStopLossMinDeclineFromMaximumAfterBuyingPercentage, double activateTrailingStopLossMinRaiseSinceBuyingPercentage, double sellTriggerStopLossMinimumDeclineSinceBuyingPercentage, double maximumPercentage) {
        this.buyTriggerLocalMaximumLookBehindPeriod = buyTriggerLocalMaximumLookBehindPeriod;
        this.buyTriggerMinDeclineFromLocalMaximumPercentage = buyTriggerMinDeclineFromLocalMaximumPercentage;
        this.sellTriggerTrailingStopLossMinDeclineFromMaximumAfterBuyingPercentage = sellTriggerTrailingStopLossMinDeclineFromMaximumAfterBuyingPercentage;
        this.activateTrailingStopLossMinRaiseSinceBuyingPercentage = activateTrailingStopLossMinRaiseSinceBuyingPercentage;
        this.sellTriggerStopLossMinimumDeclineSinceBuyingPercentage = sellTriggerStopLossMinimumDeclineSinceBuyingPercentage;
        this.maximumPercentage = maximumPercentage;
    }

    public DayCount getBuyTriggerLocalMaximumLookBehindPeriod() {
        return this.buyTriggerLocalMaximumLookBehindPeriod;
    }

    public double getBuyTriggerMinDeclineFromLocalMaximumPercentage() {
        return this.buyTriggerMinDeclineFromLocalMaximumPercentage;
    }

    public double getSellTriggerTrailingStopLossMinDeclineFromMaximumAfterBuyingPercentage() {
        return this.sellTriggerTrailingStopLossMinDeclineFromMaximumAfterBuyingPercentage;
    }

    public double getActivateTrailingStopLossMinRaiseSinceBuyingPercentage() {
        return this.activateTrailingStopLossMinRaiseSinceBuyingPercentage;
    }

    public double getSellTriggerStopLossMinimumDeclineSinceBuyingPercentage() {
        return this.sellTriggerStopLossMinimumDeclineSinceBuyingPercentage;
    }

    public double getMaximumPercentage() {
        return this.maximumPercentage;
    }
}
