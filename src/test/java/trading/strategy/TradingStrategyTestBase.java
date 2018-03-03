package trading.strategy;

import org.junit.Assert;
import org.junit.Before;
import trading.Amount;
import trading.AvailableStocks;
import trading.ISIN;
import trading.account.Account;
import trading.market.MarketPriceSnapshot;
import trading.order.Broker;

import java.util.HashSet;
import java.util.Set;

public abstract class TradingStrategyTestBase {
    private TradingStrategyFactory strategyFactory;
    private Account account;
    private Broker broker;

    protected AvailableStocks availableStocks;
    protected TradingStrategy strategy;
    protected TradingStrategyParametersBuilder parametersBuilder;

    protected abstract TradingStrategyFactory getStrategyFactory();

    @Before()
    public void baseBefore() {
        this.account = new Account(new Amount(50000.0));
        this.parametersBuilder = new TradingStrategyParametersBuilder();
    }

    protected void beginTestCase() {
        if(this.availableStocks == null) {
            Set<ISIN> isins = new HashSet<>();
            isins.add(ISIN.MunichRe);
            this.availableStocks = new AvailableStocks(isins);
        }

        TradingStrategyFactory strategyFactory = this.getStrategyFactory();
        TradingStrategyParameters parameters = this.parametersBuilder.build();

        this.strategy = strategyFactory.initializeTradingStrategy(parameters, this.account, this.broker, this.availableStocks);
    }

    protected void testInitializationFailsForMissingParameter(String parameterName) {
        this.parametersBuilder.setParameter(parameterName, null);

        try {
            beginTestCase();
        }
        catch(StrategyInitializationException ex) {
            Assert.assertEquals(String.format("The parameter '%s' has not been specified.", parameterName), ex.getMessage());
            return;
        }

        Assert.fail("StrategyInitializationException expected.");
    }

    protected void testInitializationFailsForInvalidIntegerParameter(String parameterName) {
        this.parametersBuilder.setParameter(parameterName, "XX");

        try {
            beginTestCase();
        }
        catch(StrategyInitializationException ex) {
            Assert.assertEquals(String.format("The parameter '%s' is not a valid integer.", parameterName), ex.getMessage());
            return;
        }

        Assert.fail("StrategyInitializationException expected.");
    }

    protected void testInitializationFailsForZeroParameterValue(String parameterName) {
        this.parametersBuilder.setParameter(parameterName, "0");

        try {
            beginTestCase();
        }
        catch(StrategyInitializationException ex) {
            Assert.assertEquals(String.format("The parameter '%s' must not be zero.", parameterName), ex.getMessage());
            return;
        }

        Assert.fail("StrategyInitializationException expected.");
    }

    protected void testInitializationFailsForNegativeParameterValue(String parameterName) {
        this.parametersBuilder.setParameter(parameterName, "-1");

        try {
            beginTestCase();
        }
        catch(StrategyInitializationException ex) {
            Assert.assertEquals(String.format("The parameter '%s' must not be negative.", parameterName), ex.getMessage());
            return;
        }

        Assert.fail("StrategyInitializationException expected.");
    }

    protected void dayPassesWithQuote(Amount amount) {

        // dayPassesWithQuotes(new MarketPriceSnapshot());
    }

    protected void dayPassesWithQuotes(MarketPriceSnapshot marketPriceSnapshot) {

    }

//    expectNoTrades();
//    dayPassesWithQuote(new Amount(1000.0));
//    expectNoTrades();
//    dayPassesWithQuote(new Amount(1000.0));
//    expectNoTrades();
//    dayPassesWithQuote(new Amount(1000.0)
}
