package trading.domain.strategy.localMaximum;

import trading.domain.DayCount;
import trading.domain.ISIN;

public class DynamicLocalMaximumTradingStrategyParameters {
    private ISIN isin;

    private DayCount risingIndicatorLookBehindPeriod;
    private double risingIndicatorMinRisingPercentage;

    private DayCount risingBuyTriggerLocalMaximumLookBehindPeriod;
    private double risingBuyTriggerMinDistanceFromLocalMaximumPercentage;

    private DayCount decliningBuyTriggerLocalMaximumLookBehindPeriod;
    private double decliningBuyTriggerMinDistanceFromLocalMaximumPercentage;

    private double sellTriggerMinDistanceFromMaximumSinceBuyingPercentage;

    public ISIN getIsin() {
        return isin;
    }

    public DayCount getRisingIndicatorLookBehindPeriod() {
        return risingIndicatorLookBehindPeriod;
    }

    public double getRisingIndicatorMinRisingPercentage() {
        return risingIndicatorMinRisingPercentage;
    }

    public DayCount getRisingBuyTriggerLocalMaximumLookBehindPeriod() {
        return risingBuyTriggerLocalMaximumLookBehindPeriod;
    }

    public double getRisingBuyTriggerMinDistanceFromLocalMaximumPercentage() {
        return risingBuyTriggerMinDistanceFromLocalMaximumPercentage;
    }

    public DayCount getDecliningBuyTriggerLocalMaximumLookBehindPeriod() {
        return decliningBuyTriggerLocalMaximumLookBehindPeriod;
    }

    public double getDecliningBuyTriggerMinDistanceFromLocalMaximumPercentage() {
        return decliningBuyTriggerMinDistanceFromLocalMaximumPercentage;
    }

    public double getSellTriggerMinDistanceFromMaximumSinceBuyingPercentage() {
        return sellTriggerMinDistanceFromMaximumSinceBuyingPercentage;
    }

    public DynamicLocalMaximumTradingStrategyParameters(ISIN isin, DayCount risingIndicatorLookBehindPeriod, double risingIndicatorMinRisingPercentage, DayCount risingBuyTriggerLocalMaximumLookBehindPeriod, double risingBuyTriggerMinDistanceFromLocalMaximumPercentage, DayCount decliningBuyTriggerLocalMaximumLookBehindPeriod, double decliningBuyTriggerMinDistanceFromLocalMaximumPercentage, double sellTriggerMinDistanceFromMaximumSinceBuyingPercentage) {
        this.isin = isin;
        this.risingIndicatorLookBehindPeriod = risingIndicatorLookBehindPeriod;
        this.risingIndicatorMinRisingPercentage = risingIndicatorMinRisingPercentage;
        this.risingBuyTriggerLocalMaximumLookBehindPeriod = risingBuyTriggerLocalMaximumLookBehindPeriod;
        this.risingBuyTriggerMinDistanceFromLocalMaximumPercentage = risingBuyTriggerMinDistanceFromLocalMaximumPercentage;
        this.decliningBuyTriggerLocalMaximumLookBehindPeriod = decliningBuyTriggerLocalMaximumLookBehindPeriod;
        this.decliningBuyTriggerMinDistanceFromLocalMaximumPercentage = decliningBuyTriggerMinDistanceFromLocalMaximumPercentage;
        this.sellTriggerMinDistanceFromMaximumSinceBuyingPercentage = sellTriggerMinDistanceFromMaximumSinceBuyingPercentage;
    }
}
