package trading.strategy;

import org.junit.Assert;
import org.junit.Before;

public abstract class TradingStrategyParseParametersTestBase<TParsedTradingStrategyParameters> {
    private TradingStrategyParametersBuilder parametersBuilder;

    @Before
    public void before() {
        this.parametersBuilder = new TradingStrategyParametersBuilder();
        this.setDefaultParameters(parametersBuilder);
    }

    protected abstract void setDefaultParameters(TradingStrategyParametersBuilder tradingStrategyParametersBuilder);

    protected abstract TParsedTradingStrategyParameters parseParameters(TradingStrategyParameters tradingStrategyParameters);

    protected TParsedTradingStrategyParameters parseDefaultParameters() {
        TradingStrategyParametersBuilder tradingStrategyParametersBuilder = new TradingStrategyParametersBuilder();
        setDefaultParameters(tradingStrategyParametersBuilder);
        return parseParameters(tradingStrategyParametersBuilder.build());
    }

    protected void testFailsForMissingParameter(String parameterName) {
        this.parametersBuilder.setParameter(parameterName, null);

        try {
            parseParameters(parametersBuilder.build());
        }
        catch(StrategyInitializationException ex) {
            Assert.assertEquals(String.format("The parameter '%s' has not been specified.", parameterName), ex.getMessage());
            return;
        }

        Assert.fail("StrategyInitializationException expected.");
    }

    protected void testFailsForInvalidIntegerParameter(String parameterName) {
        this.parametersBuilder.setParameter(parameterName, "XX");

        try {
            parseParameters(parametersBuilder.build());
        }
        catch(StrategyInitializationException ex) {
            Assert.assertEquals(String.format("The parameter '%s' is not a valid integer.", parameterName), ex.getMessage());
            return;
        }

        Assert.fail("StrategyInitializationException expected.");
    }

    protected void testFailsForZeroIntegerParameter(String parameterName) {
        this.parametersBuilder.setParameter(parameterName, "0");

        try {
            parseParameters(parametersBuilder.build());
        }
        catch(StrategyInitializationException ex) {
            Assert.assertEquals(String.format("The parameter '%s' must not be zero.", parameterName), ex.getMessage());
            return;
        }

        Assert.fail("StrategyInitializationException expected.");
    }

    protected void testFailsForNegativeIntegerParameter(String parameterName) {
        this.parametersBuilder.setParameter(parameterName, "-1");

        try {
            parseParameters(parametersBuilder.build());
        }
        catch(StrategyInitializationException ex) {
            Assert.assertEquals(String.format("The parameter '%s' must not be negative.", parameterName), ex.getMessage());
            return;
        }

        Assert.fail("StrategyInitializationException expected.");
    }
}
