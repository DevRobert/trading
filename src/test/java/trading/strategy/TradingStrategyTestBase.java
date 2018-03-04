package trading.strategy;

import org.junit.Assert;
import org.junit.Before;
import trading.Amount;
import trading.ISIN;
import trading.account.Account;
import trading.market.HistoricalMarketData;
import trading.market.MarketPriceSnapshot;
import trading.broker.Broker;
import trading.market.MarketPriceSnapshotBuilder;
import trading.simulation.Simulation;
import trading.simulation.SimulationBuilder;

public abstract class TradingStrategyTestBase {
    private TradingStrategyFactory strategyFactory;
    private Account account;
    private Broker broker;
    private Simulation simulation;
    private SimulationBuilder simulationBuilder;

    protected TradingStrategy strategy;
    protected TradingStrategyParametersBuilder parametersBuilder;

    protected abstract TradingStrategyFactory getStrategyFactory();

    private HistoricalMarketData historicalMarketData;

    @Before()
    public void baseBefore() {
        this.account = new Account(new Amount(50000.0));
        this.parametersBuilder = new TradingStrategyParametersBuilder();
    }

    protected void beginHistory(ISIN isin, Amount initialMarketPrice) {
        MarketPriceSnapshotBuilder marketPriceSnapshotBuilder = new MarketPriceSnapshotBuilder();
        marketPriceSnapshotBuilder.setMarketPrice(isin, initialMarketPrice);
        this.beginHistory(marketPriceSnapshotBuilder.build());
    }

    protected void beginHistory(MarketPriceSnapshot marketPriceSnapshot) {
        if(historicalMarketData != null) {
            throw new RuntimeException("The history has already been started.");
        }

        historicalMarketData = new HistoricalMarketData(marketPriceSnapshot);
    }

    protected void beginSimulation() {
        if(historicalMarketData == null) {
            throw new RuntimeException("The history has to be started before starting the simulation.");
        }

        if(simulation != null) {
            throw new RuntimeException("The simulation has already been started.");
        }

        TradingStrategyFactory strategyFactory = this.getStrategyFactory();
        TradingStrategyParameters parameters = this.parametersBuilder.build();

        this.strategy = strategyFactory.initializeTradingStrategy(parameters, this.account, this.broker, this.historicalMarketData);
    }

    protected void testInitializationFailsForMissingParameter(String parameterName) {
        this.parametersBuilder.setParameter(parameterName, null);

        beginHistory(ISIN.MunichRe, new Amount(1000.0));

        try {
            beginSimulation();
        }
        catch(StrategyInitializationException ex) {
            Assert.assertEquals(String.format("The parameter '%s' has not been specified.", parameterName), ex.getMessage());
            return;
        }

        Assert.fail("StrategyInitializationException expected.");
    }

    protected void testInitializationFailsForInvalidIntegerParameter(String parameterName) {
        this.parametersBuilder.setParameter(parameterName, "XX");

        beginHistory(ISIN.MunichRe, new Amount(1000.0));

        try {
            beginSimulation();
        }
        catch(StrategyInitializationException ex) {
            Assert.assertEquals(String.format("The parameter '%s' is not a valid integer.", parameterName), ex.getMessage());
            return;
        }

        Assert.fail("StrategyInitializationException expected.");
    }

    protected void testInitializationFailsForZeroParameterValue(String parameterName) {
        this.parametersBuilder.setParameter(parameterName, "0");

        beginHistory(ISIN.MunichRe, new Amount(1000.0));

        try {
            beginSimulation();
        }
        catch(StrategyInitializationException ex) {
            Assert.assertEquals(String.format("The parameter '%s' must not be zero.", parameterName), ex.getMessage());
            return;
        }

        Assert.fail("StrategyInitializationException expected.");
    }

    protected void testInitializationFailsForNegativeParameterValue(String parameterName) {
        this.parametersBuilder.setParameter(parameterName, "-1");

        beginHistory(ISIN.MunichRe, new Amount(1000.0));

        try {
            beginSimulation();
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
