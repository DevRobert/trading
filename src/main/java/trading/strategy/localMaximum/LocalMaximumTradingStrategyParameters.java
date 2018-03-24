package trading.strategy.localMaximum;

import trading.DayCount;
import trading.ISIN;

public class LocalMaximumTradingStrategyParameters {
    private final ISIN isin;
    private final DayCount buyTriggerLocalMaximumLookBehindPeriod;
    private final double buyTriggerMinDeclineFromLocalMaximumPercentage;
    private final double sellTriggerTrailingStopLossMinDeclineFromMaximumAfterBuyingPercentage;
    private final double activateTrailingStopLossMinRaiseSinceBuyingPercentage;
    private final double sellTriggerStopLossMinimumDeclineSinceBuyingPercentage;

    public ISIN getIsin() {
        return this.isin;
    }

    public DayCount getBuyTriggerLocalMaximumLookBehindPeriod() {
        return this.buyTriggerLocalMaximumLookBehindPeriod;
    }

    public double getBuyTriggerMinDeclineFromMaximumPercentage() {
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

    public LocalMaximumTradingStrategyParameters(ISIN isin, DayCount buyTriggerLocalMaximumLookBehindPeriod, double buyTriggerMinDeclineFromLocalMaximumPercentage, double sellTriggerTrailingStopLossMinDeclineFromMaximumAfterBuyingPercentage, double activateTrailingStopLossMinRaiseSinceBuyingPercentage, double sellTriggerStopLossMinimumDeclineSinceBuyingPercentage) {
        this.isin = isin;
        this.buyTriggerLocalMaximumLookBehindPeriod = buyTriggerLocalMaximumLookBehindPeriod;
        this.buyTriggerMinDeclineFromLocalMaximumPercentage = buyTriggerMinDeclineFromLocalMaximumPercentage;
        this.sellTriggerTrailingStopLossMinDeclineFromMaximumAfterBuyingPercentage = sellTriggerTrailingStopLossMinDeclineFromMaximumAfterBuyingPercentage;
        this.activateTrailingStopLossMinRaiseSinceBuyingPercentage = activateTrailingStopLossMinRaiseSinceBuyingPercentage;
        this.sellTriggerStopLossMinimumDeclineSinceBuyingPercentage = sellTriggerStopLossMinimumDeclineSinceBuyingPercentage;
    }
}
