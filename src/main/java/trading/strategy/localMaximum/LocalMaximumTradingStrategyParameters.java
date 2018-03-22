package trading.strategy.localMaximum;

import trading.DayCount;
import trading.ISIN;

public class LocalMaximumTradingStrategyParameters {
    private ISIN isin;
    private DayCount buyTriggerLocalMaximumLookBehindPeriod;
    private double buyTriggerMinDistanceFromLocalMaximumPercentage;
    private double sellTriggerMinDistanceFromMaximumSinceBuyingPercentage;

    public ISIN getIsin() {
        return this.isin;
    }

    public DayCount getBuyTriggerLocalMaximumLookBehindPeriod() {
        return this.buyTriggerLocalMaximumLookBehindPeriod;
    }

    public double getBuyTriggerMinDistanceFromLocalMaximumPercentage() {
        return this.buyTriggerMinDistanceFromLocalMaximumPercentage;
    }

    public double getSellTriggerMinDistanceFromMaximumSinceBuyingPercentage() {
        return this.sellTriggerMinDistanceFromMaximumSinceBuyingPercentage;
    }

    public LocalMaximumTradingStrategyParameters(ISIN isin, DayCount buyTriggerLocalMaximumLookBehindPeriod, double buyTriggerMinDistanceFromLocalMaximumPercentage, double sellTriggerMinDistanceFromMaximumSinceBuyingPercentage) {
        this.isin = isin;
        this.buyTriggerLocalMaximumLookBehindPeriod = buyTriggerLocalMaximumLookBehindPeriod;
        this.buyTriggerMinDistanceFromLocalMaximumPercentage = buyTriggerMinDistanceFromLocalMaximumPercentage;
        this.sellTriggerMinDistanceFromMaximumSinceBuyingPercentage = sellTriggerMinDistanceFromMaximumSinceBuyingPercentage;
    }
}
