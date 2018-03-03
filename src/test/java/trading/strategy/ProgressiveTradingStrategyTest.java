package trading.strategy;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import trading.ISIN;

/**
 * Test of progressive trading strategy with parameters:
 *
 *  1. (string) isin
 *  2. (int) buyTriggerPositiveSeriesNumDays; >= 0
 *  3. (int) sellTriggerNumNegativeDays; >= 1
 *  4. (int) sellTriggerNumMaxDays; >= 1
 *  5. (int) restartTriggerNumNegativeDays; >= 0
 */
public class ProgressiveTradingStrategyTest extends TradingStrategyTestBase {
    // Test Initialization

    @Before
    public void before() {
        this.parametersBuilder.setParameter("isin", ISIN.MunichRe.getText());
        this.parametersBuilder.setParameter("buyTriggerPositiveSeriesNumDays", "1");
        this.parametersBuilder.setParameter("sellTriggerNumNegativeDays", "1");
        this.parametersBuilder.setParameter("sellTriggerNumMaxDays", "3");
        this.parametersBuilder.setParameter("restartTriggerNumNegativeDays", "0");
    }

    @Override
    protected TradingStrategyFactory getStrategyFactory() {
        return new ProgressiveTradingStrategyFactory();
    }

    // Validate Parameter 1: isin

    @Test
    public void initializationFailsIfISINParameterNotSpecified() {
        testInitializationFailsForMissingParameter("isin");
    }

    @Test
    public void initializationFailsIfISINParameterDoesNotReferToAnAvailableStock() {
        this.parametersBuilder.setParameter("isin", ISIN.Allianz.getText());

        try {
            beginTestCase();
        }
        catch(StrategyInitializationException ex) {
            Assert.assertEquals("The ISIN parameter 'DE0008404005' does not refer to an available stock.", ex.getMessage());
            return;
        }

        Assert.fail("StrategyInitializationException expected.");
    }

    // Validate Parameter 2: buyTriggerPositiveSeriesNumDays; >=0

    @Test
    public void initializationFailsIfBuyTriggerPositiveSeriesNumDaysParameterNotSpecified() {
        testInitializationFailsForMissingParameter("buyTriggerPositiveSeriesNumDays");
    }

    @Test
    public void initializationFailsIfBuyTriggerPositiveSeriesNumDaysParameterNotAValidInteger() {
        testInitializationFailsForInvalidIntegerParameter("buyTriggerPositiveSeriesNumDays");
    }

    @Test
    public void initializationFailsIfBuyTriggerPositiveSeriesNumDaysParameterNegative() {
        testInitializationFailsForNegativeParameterValue("buyTriggerPositiveSeriesNumDays");
    }

    // Validate Parameter 3: sellTriggerNumNegativeDays; >= 1

    @Test
    public void initializationFailsIfSellTriggerNumNegativeDaysParameterNotSpecified() {
        testInitializationFailsForMissingParameter("sellTriggerNumNegativeDays");
    }

    @Test
    public void initializationFailsIfSellTriggerNumNegativeDaysParameterNotAValidInteger() {
        testInitializationFailsForInvalidIntegerParameter("sellTriggerNumNegativeDays");
    }

    @Test
    public void initializationFailsIfSellTriggerNumNegativeDaysParameterNegative() {
        testInitializationFailsForNegativeParameterValue("sellTriggerNumNegativeDays");
    }

    @Test
    public void initializationFailsIfSellTriggerNumNegativeDaysParameterZero() {
        testInitializationFailsForZeroParameterValue("sellTriggerNumNegativeDays");
    }

    // Validate Parameter 4: sellTriggerNumMaxDays; >= 1

    @Test
    public void initializationFailsIfSellTriggerNumMaxDaysParameterNotSpecified() {
        testInitializationFailsForMissingParameter("sellTriggerNumMaxDays");
    }

    @Test
    public void initializationFailsIfSellTriggerNumMaxDaysParameterNotAValidInteger() {
        testInitializationFailsForInvalidIntegerParameter("sellTriggerNumMaxDays");
    }

    @Test
    public void initializationFailsIfSellTriggerNumMaxDaysParameterNegative() {
        testInitializationFailsForNegativeParameterValue("sellTriggerNumMaxDays");
    }

    @Test
    public void initializationFailsIfSellTriggerNumMaxDaysParameterZero() {
        testInitializationFailsForZeroParameterValue("sellTriggerNumMaxDays");
    }

    // Validate Parameter 5: restartTriggerNumNegativeDays; >= 0

    @Test
    public void initializationFailsIfRestartTriggerNumNegativeDaysParameterNotSpecified() {
        testInitializationFailsForMissingParameter("restartTriggerNumNegativeDays");
    }

    @Test
    public void initializationFailsIfRestartTriggerNumNegativeDaysParameterNotAValidInteger() {
        testInitializationFailsForInvalidIntegerParameter("restartTriggerNumNegativeDays");
    }

    @Test
    public void initializationFailsIfRestartTriggerNumNegativeDaysParameterNegative() {
        testInitializationFailsForNegativeParameterValue("restartTriggerNumNegativeDays");
    }

    // Happy Path

    @Test
    public void happyPath() {
        // todo split
        //    expectNoTrades();
        //    dayPassesWithQuote(new Amount(1000.0));
        //    expectNoTrades();
        //    dayPassesWithQuote(new Amount(1000.0));
        //    expectNoTrades();
        //    dayPassesWithQuote(new Amount(1000.0)
    }

    // Variations of Parameter 2: buyTriggerPositiveSeriesNumDays

    @Test
    public void buyOrderIsSetAfterOneDayIfBuyTriggerPositiveSeriesNumDaysIsOne() {
        throw new NotImplementedException();
    }

    @Test
    public void buyOrderIsSetAfterFiveDaysIfBuyTriggerPositiveSeriesNumDaysIsFive() {
        throw new NotImplementedException();
    }

    // Variations of Parameter 3: sellTriggerNumNegativeDays

    // TODO

    // Variations of Parameter 4: sellTriggerNumMaxDays

    // TODO

    // Variations of Parameter 5: restartTriggerNumNegativeDays

    // TODO
}
