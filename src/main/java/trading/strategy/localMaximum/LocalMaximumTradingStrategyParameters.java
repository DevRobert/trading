package trading.strategy.localMaximum;

import trading.DayCount;
import trading.ISIN;

public class LocalMaximumTradingStrategyParameters {
    private ISIN isin;
    private DayCount localMaximumLookBehindPeriod;
    private double minDistanceFromLocalMaximumPercentage;

    public ISIN getIsin() {
        return isin;
    }

    public DayCount getLocalMaximumLookBehindPeriod() {
        return localMaximumLookBehindPeriod;
    }

    public double getMinDistanceFromLocalMaximumPercentage() {
        return minDistanceFromLocalMaximumPercentage;
    }

    public LocalMaximumTradingStrategyParameters(ISIN isin, DayCount localMaximumLookBehindPeriod, double minDistanceFromLocalMaximumPercentage) {
        this.isin = isin;
        this.localMaximumLookBehindPeriod = localMaximumLookBehindPeriod;
        this.minDistanceFromLocalMaximumPercentage = minDistanceFromLocalMaximumPercentage;
    }
}
