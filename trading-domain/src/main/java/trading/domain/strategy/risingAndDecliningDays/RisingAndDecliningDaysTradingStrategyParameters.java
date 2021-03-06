package trading.domain.strategy.risingAndDecliningDays;

import trading.domain.DayCount;
import trading.domain.ISIN;

public class RisingAndDecliningDaysTradingStrategyParameters {
    private ISIN isin;
    private DayCount buyAfterRisingDaysInSequence;
    private DayCount sellAfterDecliningDaysInSequence;

    public ISIN getISIN() {
        return this.isin;
    }

    public DayCount getBuyAfterRisingDaysInSequence() {
        return this.buyAfterRisingDaysInSequence;
    }

    public DayCount getSellAfterDecliningDaysInSequence() {
        return this.sellAfterDecliningDaysInSequence;
    }

    public RisingAndDecliningDaysTradingStrategyParameters(ISIN isin, DayCount buyAfterRisingDaysInSequence, DayCount sellAfterDecliningDaysInSequence) {
        this.isin = isin;
        this.buyAfterRisingDaysInSequence = buyAfterRisingDaysInSequence;
        this.sellAfterDecliningDaysInSequence = sellAfterDecliningDaysInSequence;
    }
}
