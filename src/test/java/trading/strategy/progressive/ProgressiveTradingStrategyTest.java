package trading.strategy.progressive;

import org.junit.Test;

public class ProgressiveTradingStrategyTest extends ProgressiveTradingStrategyTestBase {
     /**
     * Phase A
     *
     * Sets buy order for given ISIN after a series of {buyTriggerRisingDaysInSequence} days has passed.
     * The maximum possible amount of available money is used for this order.
     */

    @Test
    public void buyOrderIsSetIfNumRisingDaysInSequenceIsZeroAndBuyTriggerNumRisingDaysInSequenceIsZero() {

    }

    @Test
    public void buyOrderIsSetIfPreInitDayWasNegativeAndNextDayIsPositiveAndBuyTriggerPositiveSeriesNumDaysIsOne() {

    }

    @Test
    public void buyOrderIsSetAfterOneDayIfPreInitDayWasNegativeAndNextTwoDaysArePositive() {

    }

    @Test
    public void buyOrderIsSetAfterOneDayIfBuyTriggerPositiveSeriesNumDaysIsOne() {

    }

    @Test
    public void buyOrderIsSetAfterFiveDaysIfBuyTriggerPositiveSeriesNumDaysIsFive() {

    }


    /**
     * Phase B
     *
     * Sets sell order for bought position when one of the following condition occurs:
     *  - {sellTriggerNumNegativeDays} days with negative performance have passed after buying.
     *  - {sellTriggerNumMaxDays} days have passed after buying.
     */

    /**
     * Phase C
     *
     * {restartTriggerNumNegativeDays} days with negative performance have to be passed, so that Phase A is entered again.
     */
}
