package trading.strategy;

import org.junit.Assert;
import org.junit.Before;
import trading.Amount;
import trading.ISIN;
import trading.account.Account;
import trading.account.Position;
import trading.broker.Broker;
import trading.broker.VirtualBroker;
import trading.market.HistoricalMarketData;
import trading.market.MarketPriceSnapshot;
import trading.simulation.Simulation;
import trading.simulation.SimulationBuilder;

public abstract class TradingStrategyTestBase {
    protected Account account;
    private VirtualBroker broker;
    private TradingStrategy tradingStrategy;

    private HistoricalMarketData historicalMarketData;
    private Simulation simulation;

    protected abstract TradingStrategy initializeTradingStrategy(Account account, Broker broker, HistoricalMarketData historicalMarketData);

    @Before()
    public void tradingStrategyTestBaseBefore() {
        this.account = new Account(new Amount(50000.0));
    }

    private void ensureBeginHistoryPreConditions() {
        if(historicalMarketData != null) {
            throw new RuntimeException("The history has already been started.");
        }
    }

    protected void beginHistory(ISIN isin, Amount initialClosingMarketPrice) {
        ensureBeginHistoryPreConditions();
        historicalMarketData = new HistoricalMarketData(isin, initialClosingMarketPrice);
    }

    protected void beginHistory(MarketPriceSnapshot initialClosingMarketPrices) {
        ensureBeginHistoryPreConditions();
        historicalMarketData = new HistoricalMarketData(initialClosingMarketPrices);
    }

    private void ensureAddHistoryPreconditions() {
        if(historicalMarketData == null) {
            throw new RuntimeException("The history has not been started.");
        }

        if(simulation != null) {
            throw new RuntimeException("The simulation has already been started.");
        }
    }

    protected void addHistory(Amount closingMarketPrice) {
        ensureAddHistoryPreconditions();
        historicalMarketData.registerClosedDay(closingMarketPrice);
    }

    protected void addHistory(MarketPriceSnapshot closingMarketPrices) {
        ensureAddHistoryPreconditions();
        historicalMarketData.registerClosedDay(closingMarketPrices);
    }

    protected void beginSimulation() {
        if(this.historicalMarketData == null) {
            throw new RuntimeException("The history has to be started before starting the simulation.");
        }

        if(this.simulation != null) {
            throw new RuntimeException("The simulation has already been started.");
        }

        this.broker = new VirtualBroker(this.account, this.historicalMarketData);
        this.tradingStrategy = this.initializeTradingStrategy(this.account, this.broker, historicalMarketData);

        SimulationBuilder simulationBuilder = new SimulationBuilder();
        simulationBuilder.setTradingStrategy(this.tradingStrategy);
        simulationBuilder.setHistoricalMarketData(this.historicalMarketData);
        simulationBuilder.setAccount(this.account);
        simulationBuilder.setBroker(this.broker);
        this.simulation = simulationBuilder.beginSimulation();
    }

    protected void openDay() {
        if(simulation == null) {
            throw new RuntimeException("The simulation has not been started yet.");
        }

        simulation.openDay();
    }

    private void ensureCloseDayPreconditions() {
        if(simulation == null) {
            throw new RuntimeException("The simulation has not been started yet.");
        }
    }

    protected void closeDay(Amount closingMarketPrice) {
        ensureCloseDayPreconditions();
        simulation.closeDay(closingMarketPrice);
    }

    protected void closeDay(MarketPriceSnapshot closingMarketPrices) {
        ensureCloseDayPreconditions();
        simulation.closeDay(closingMarketPrices);
    }

    protected void assertPositionHasPositiveQuantity(ISIN isin) {
        if(!account.hasPosition(isin)) {
            Assert.fail(String.format("Position for ISIN '%s' expected, but no position found.", isin.getText()));
        }

        Assert.assertFalse("Position with positive quantity expected, but zero quantity found.", account.getPosition(isin).getQuantity().isZero());
    }

    protected void assertNoneOrEmptyPosition(ISIN isin) {
        if(!account.hasPosition(isin)) {
            return;
        }

        Position position = account.getPosition(isin);

        Assert.assertTrue("None or empty position expected, but position with non-zero quantity found.", position.getQuantity().isZero());
    }
}
