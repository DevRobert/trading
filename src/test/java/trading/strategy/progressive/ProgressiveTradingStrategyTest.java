package trading.strategy.progressive;

import org.junit.Assert;
import org.junit.Test;
import trading.Amount;
import trading.ISIN;
import trading.account.Position;

public class ProgressiveTradingStrategyTest extends ProgressiveTradingStrategyTestBase {
    /**
     * Default Parameters
     *
     *  - ISIN: MunichRe
     *  - buyTriggerRisingDaysInSequence: 1
     *  - sellTriggerDecliningDays: 1
     *  - sellTriggerMaxDays: 1
     *  - restartTriggerDecliningDays: 0
     */

     /**
     * Phase A
     *
     * Sets buy order for given ISIN after a series of {buyTriggerRisingDaysInSequence} days has passed.
     * The maximum possible amount of available money is used for this order.
     */

    @Test
    public void buyOrderIsSetInitiallyIfRisingDaysInSequenceIsZeroAndBuyTriggerNumRisingDaysInSequenceIsZero() {
        beginHistory(ISIN.MunichRe, new Amount(1000.0));
        addHistory(new Amount(900.00));

        // Now: RisingDaysInSequence = 0

        beginSimulation();

        Position position = account.getPosition(ISIN.MunichRe);
        Assert.assertFalse(position.getQuantity().getValue() > 0);
    }

    @Test
    public void buyOrderIsSetInitiallyIfPreInitDayWasNegativeAndNextDayIsPositiveAndBuyTriggerPositiveSeriesNumDaysIsOne() {

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
