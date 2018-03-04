package trading.strategy.progressive;

import org.junit.Assert;
import org.junit.Test;
import trading.ISIN;
import trading.strategy.*;

public class ProgressiveTradingStrategyParseParametersTest extends TradingStrategyParseParametersTestBase<ProgressiveTradingStrategyParameters> {
    @Override
    protected void setDefaultParameters(TradingStrategyParametersBuilder tradingStrategyParametersBuilder) {
        tradingStrategyParametersBuilder.setParameter("isin", ISIN.MunichRe.getText());
        tradingStrategyParametersBuilder.setParameter("buyTriggerRisingDaysInSequence", "1");
        tradingStrategyParametersBuilder.setParameter("sellTriggerDecliningDays", "1");
        tradingStrategyParametersBuilder.setParameter("sellTriggerMaxDays", "3");
        tradingStrategyParametersBuilder.setParameter("restartTriggerDecliningDays", "0");
    }

    @Override
    protected ProgressiveTradingStrategyParameters parseParameters(TradingStrategyParameters tradingStrategyParameters) {
        return ProgressiveTradingStrategyParameters.parse(tradingStrategyParameters);
    }

    // Parse Parameter 1: (ISIN) isin

    @Test
    public void parseIsin() {
        ProgressiveTradingStrategyParameters parameters = parseDefaultParameters();
        Assert.assertEquals(ISIN.MunichRe, parameters.getISIN());
    }

    @Test
    public void failsIfISINParameterNotSpecified() {
        testFailsForMissingParameter("isin");
    }

    // Parse Parameter 2: (int) buyTriggerRisingDaysInSequence; >= 0

    @Test
    public void parseBuyTriggerRisingDaysInSequence() {
        ProgressiveTradingStrategyParameters parameters = parseDefaultParameters();
        Assert.assertEquals(1, parameters.getBuyTriggerRisingDaysInSequence());
    }

    @Test
    public void failsIfBuyTriggerRisingDaysInSequenceParameterNotSpecified() {
        testFailsForMissingParameter("buyTriggerRisingDaysInSequence");
    }

    @Test
    public void failsIfBuyTriggerRisingDaysInSequenceParameterNotAValidInteger() {
        testFailsForInvalidIntegerParameter("buyTriggerRisingDaysInSequence");
    }

    @Test
    public void failsIfBuyTriggerRisingDaysInSequenceParameterNegative() {
        testFailsForNegativeIntegerParameter("buyTriggerRisingDaysInSequence");
    }

    // Parse Parameter 3: (int) sellTriggerDecliningDays; >= 0

    @Test
    public void parseSellTriggerDecliningDays() {
        ProgressiveTradingStrategyParameters parameters = parseDefaultParameters();
        Assert.assertEquals(1, parameters.getSellTriggerDecliningDays());
    }

    @Test
    public void failsIfSellTriggerDecliningDaysParameterNotSpecified() {
        testFailsForMissingParameter("sellTriggerDecliningDays");
    }

    @Test
    public void failsIfSellTriggerDecliningDaysParameterNotAValidInteger() {
        testFailsForInvalidIntegerParameter("sellTriggerDecliningDays");
    }

    @Test
    public void failsIfSellTriggerDecliningDaysParameterNegative() {
        testFailsForNegativeIntegerParameter("sellTriggerDecliningDays");
    }

    // Parse Parameter 4: (int) sellTriggerMaxDays; >= 1

    @Test
    public void parseSellTriggerMaxDays() {
        ProgressiveTradingStrategyParameters parameters = parseDefaultParameters();
        Assert.assertEquals(3, parameters.getSellTriggerMaxDays());
    }

    @Test
    public void failsIfSellTriggerMaxDaysParameterNotSpecified() {
        testFailsForMissingParameter("sellTriggerMaxDays");
    }

    @Test
    public void failsIfSellTriggerMaxDaysParameterNotAValidInteger() {
        testFailsForInvalidIntegerParameter("sellTriggerMaxDays");
    }

    @Test
    public void failsIfSellTriggerMaxDaysParameterNegative() {
        testFailsForNegativeIntegerParameter("sellTriggerMaxDays");
    }

    @Test
    public void failsIfSellTriggerMaxDaysParameterZero() {
        testFailsForZeroIntegerParameter("sellTriggerMaxDays");
    }

    // Parse Parameter 5: (int) restartTriggerDecliningDays; >= 0

    @Test
    public void parseRestartTriggerDecliningDays() {
        ProgressiveTradingStrategyParameters parameters = parseDefaultParameters();
        Assert.assertEquals(0, parameters.getRestartTriggerDecliningDays());
    }

    @Test
    public void failsIfRestartTriggerDecliningDaysParameterNotSpecified() {
        testFailsForMissingParameter("restartTriggerDecliningDays");
    }

    @Test
    public void failsIfRestartTriggerDecliningDaysParameterNotAValidInteger() {
        testFailsForInvalidIntegerParameter("restartTriggerDecliningDays");
    }

    @Test
    public void failsIfRestartTriggerDecliningDaysParameterNegative() {
        testFailsForNegativeIntegerParameter("restartTriggerDecliningDays");
    }
}
