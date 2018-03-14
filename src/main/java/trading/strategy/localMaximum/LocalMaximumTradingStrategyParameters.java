package trading.strategy.localMaximum;

import trading.DayCount;
import trading.ISIN;

public class LocalMaximumTradingStrategyParameters {
    private ISIN isin;
    private DayCount localMaxiumLookBehindPeriod;
    private double minDistanceFromLocalMaxmiumPercentage;

    public ISIN getIsin() {
        return isin;
    }

    public DayCount getLocalMaxiumLookBehindPeriod() {
        return localMaxiumLookBehindPeriod;
    }

    public double getMinDistanceFromLocalMaxmiumPercentage() {
        return minDistanceFromLocalMaxmiumPercentage;
    }

    public LocalMaximumTradingStrategyParameters(ISIN isin, DayCount localMaxiumLookBehindPeriod, double minDistanceFromLocalMaxmiumPercentage) {
        this.isin = isin;
        this.localMaxiumLookBehindPeriod = localMaxiumLookBehindPeriod;
        this.minDistanceFromLocalMaxmiumPercentage = minDistanceFromLocalMaxmiumPercentage;
    }
}
