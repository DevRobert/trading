package trading.strategy.localMaximum;

import trading.DayCount;
import trading.ISIN;

public class LocalMaximumTradingStrategyParameters {
    private ISIN isin;
    private DayCount localMaximumLookBehindPeriod;
    private double minDistanceFromLocalMaxmiumPercentage;

    public ISIN getIsin() {
        return isin;
    }

    public DayCount getLocalMaximumLookBehindPeriod() {
        return localMaximumLookBehindPeriod;
    }

    public double getMinDistanceFromLocalMaxmiumPercentage() {
        return minDistanceFromLocalMaxmiumPercentage;
    }

    public LocalMaximumTradingStrategyParameters(ISIN isin, DayCount localMaximumLookBehindPeriod, double minDistanceFromLocalMaxmiumPercentage) {
        this.isin = isin;
        this.localMaximumLookBehindPeriod = localMaximumLookBehindPeriod;
        this.minDistanceFromLocalMaxmiumPercentage = minDistanceFromLocalMaxmiumPercentage;
    }
}
