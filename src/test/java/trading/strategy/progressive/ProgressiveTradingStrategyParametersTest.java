package trading.strategy.progressive;

import org.junit.Assert;
import org.junit.Test;
import trading.Amount;
import trading.ISIN;
import trading.strategy.StrategyInitializationException;

public class ProgressiveTradingStrategyParametersTest extends ProgressiveTradingStrategyTestBase {
    // Validate Parameter 1: (ISIN) isin

    @Test
    public void initializationFailsIfISINParameterNotSpecified() {
        testInitializationFailsForMissingParameter("isin");
    }

    @Test
    public void initializationFailsIfISINParameterDoesNotReferToAnAvailableStock() {
        this.parametersBuilder.setParameter("isin", ISIN.Allianz.getText());

        beginHistory(ISIN.MunichRe, new Amount(1000.0));

        try {
            beginSimulation();
        }
        catch(StrategyInitializationException ex) {
            Assert.assertEquals("The ISIN parameter 'DE0008404005' does not refer to an available stock.", ex.getMessage());
            return;
        }

        Assert.fail("StrategyInitializationException expected.");
    }

    // Validate Parameter 2: (int) buyTriggerRisingDaysInSequence; >=0

    @Test
    public void initializationFailsIfBuyTriggerRisingDaysInSequenceParameterNotSpecified() {
        testInitializationFailsForMissingParameter("buyTriggerRisingDaysInSequence");
    }

    @Test
    public void initializationFailsIfBuyTriggerRisingDaysInSequenceParameterNotAValidInteger() {
        testInitializationFailsForInvalidIntegerParameter("buyTriggerRisingDaysInSequence");
    }

    @Test
    public void initializationFailsIfBuyTriggerRisingDaysInSequenceParameterNegative() {
        testInitializationFailsForNegativeParameterValue("buyTriggerRisingDaysInSequence");
    }

    // Validate Parameter 3: (int) sellTriggerDecliningDaysInSequence; >= 1

    @Test
    public void initializationFailsIfSellTriggerDecliningDaysParameterNotSpecified() {
        testInitializationFailsForMissingParameter("sellTriggerDecliningDays");
    }

    @Test
    public void initializationFailsIfSellTriggerDecliningDaysParameterNotAValidInteger() {
        testInitializationFailsForInvalidIntegerParameter("sellTriggerDecliningDays");
    }

    @Test
    public void initializationFailsIfSellTriggerDecliningDaysParameterNegative() {
        testInitializationFailsForNegativeParameterValue("sellTriggerDecliningDays");
    }

    @Test
    public void initializationFailsIfSellTriggerDecliningDaysParameterZero() {
        testInitializationFailsForZeroParameterValue("sellTriggerDecliningDays");
    }

    // Validate Parameter 4: (int) sellTriggerMaxDays; >= 1

    @Test
    public void initializationFailsIfSellTriggerMaxDaysParameterNotSpecified() {
        testInitializationFailsForMissingParameter("sellTriggerMaxDays");
    }

    @Test
    public void initializationFailsIfSellTriggerMaxDaysParameterNotAValidInteger() {
        testInitializationFailsForInvalidIntegerParameter("sellTriggerMaxDays");
    }

    @Test
    public void initializationFailsIfSellTriggerMaxDaysParameterNegative() {
        testInitializationFailsForNegativeParameterValue("sellTriggerMaxDays");
    }

    @Test
    public void initializationFailsIfSellTriggerMaxDaysParameterZero() {
        testInitializationFailsForZeroParameterValue("sellTriggerMaxDays");
    }

    // Validate Parameter 5: (int) restartTriggerDecliningDaysInSequence; >= 0

    @Test
    public void initializationFailsIfRestartTriggerDecliningDaysParameterNotSpecified() {
        testInitializationFailsForMissingParameter("restartTriggerDecliningDays");
    }

    @Test
    public void initializationFailsIfRestartTriggerDecliningDaysParameterNotAValidInteger() {
        testInitializationFailsForInvalidIntegerParameter("restartTriggerDecliningDays");
    }

    @Test
    public void initializationFailsIfRestartTriggerDecliningDaysParameterNegative() {
        testInitializationFailsForNegativeParameterValue("restartTriggerDecliningDays");
    }
}
