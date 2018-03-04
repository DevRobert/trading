package trading.strategy.progressive;

import org.junit.Assert;
import org.junit.Test;
import trading.Amount;
import trading.Quantity;
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
     * <p>
     * Sets buy order for given ISIN after a series of {buyTriggerRisingDaysInSequence} days has passed.
     * The maximum possible amount of available money is used for this order.
     */

    @Test
    public void buyOrderIsSetInitially_IfParameterBuyTriggerNumRisingDaysInSequenceIsZero_AndNumRisingDaysInSequenceIsZero() {
        this.buyTriggerRisingDaysInSequence = 0;

        beginHistory(isin, new Amount(1000.0));

        // RisingDaysInSequence = 0

        beginSimulation();
        passDay(new Amount(1000.0));

        assertPositionHasPositiveQuantity(isin);
    }

    @Test
    public void buyOrderIsNotSetInitially_IfParameterBuyTriggerNumRisingDaysInSequenceIsOne_AndNumRisingDaysInSequenceIsZero() {
        this.buyTriggerRisingDaysInSequence = 1;

        beginHistory(isin, new Amount(1000.0));

        // RisingDaysInSequence = 0

        beginSimulation();
        passDay(new Amount(1000.0));

        assertNoneOrEmptyPosition(isin);
    }

    @Test
    public void buyOrderIsSetInitially_IfParameterBuyTriggerNumRisingDaysInSequenceIsOne_AndNumRisingDaysInSequenceIsOne() {
        this.buyTriggerRisingDaysInSequence = 1;

        beginHistory(isin, new Amount(1000.0));
        addHistory(new Amount(1100.0));

        // RisingDaysInSequence = 1

        beginSimulation();
        passDay(new Amount(1000.0));

        assertPositionHasPositiveQuantity(isin);
    }

    @Test
    public void buyOrderIsSetInitially_IfParameterBuyTriggerNumRisingDaysInSequenceIsOne_AndNumRisingDaysInSequenceIsTwo() {
        this.buyTriggerRisingDaysInSequence = 1;

        beginHistory(isin, new Amount(1000.0));
        addHistory(new Amount(1100.0));
        addHistory(new Amount(1200.0));

        // RisingDaysInSequence = 2

        beginSimulation();
        passDay(new Amount(1000.0));

        assertPositionHasPositiveQuantity(isin);
    }

    @Test
    public void buyOrderIsSetAfterOneRisingDay_IfParameterBuyTriggerNumRisingDaysInSequenceIsOne_AndHistoricalNumRisingDaysInSequenceIsZero() {
        this.buyTriggerRisingDaysInSequence = 1;

        beginHistory(isin, new Amount(1000.0));
        // RisingDaysInSequence = 0

        beginSimulation();
        passDay(new Amount(1100.0));
        // RisingDaysInSequence = 1

        openDay();

        assertPositionHasPositiveQuantity(isin);
    }

    @Test
    public void buyOrderMaximumQuantityIsOrdered_IfNoCommissions() {
        // Seed capital = 50,000

        this.buyTriggerRisingDaysInSequence = 0;

        beginHistory(isin, new Amount(1000.0));

        // RisingDaysInSequence = 0

        beginSimulation();
        passDay(new Amount(1000.0));

        // Expected price = 1,000
        // Expected quantity = 50
        // Expected full price = 50,000

        Position position = account.getPosition(isin);
        Assert.assertEquals(new Quantity(50), position.getQuantity());
    }

    @Test
    public void buyOrderIsSetAboutMaximumAmount_IncludingCommissions() {

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
