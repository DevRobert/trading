package trading.domain.strategy;

import org.junit.Assert;
import org.junit.Before;
import trading.domain.Amount;
import trading.domain.ISIN;
import trading.domain.account.Account;
import trading.domain.account.Position;
import trading.domain.broker.CommissionStrategy;
import trading.domain.broker.VirtualBroker;
import trading.domain.broker.ZeroCommissionStrategy;
import trading.domain.market.HistoricalMarketData;
import trading.domain.market.MarketPriceSnapshot;
import trading.domain.simulation.Simulation;
import trading.domain.simulation.SimulationBuilder;

import java.time.LocalDate;

public abstract class TradingStrategyTestBase {
    protected Account account;
    private VirtualBroker broker;
    private TradingStrategy tradingStrategy;

    protected HistoricalMarketData historicalMarketData;
    private Simulation simulation;
    protected CommissionStrategy commissionStrategy;

    protected abstract TradingStrategy initializeTradingStrategy(TradingStrategyContext context);

    @Before
    public void tradingStrategyTestBaseBefore() {
        this.account = new Account(new Amount(50000.0));
        this.commissionStrategy = new ZeroCommissionStrategy();
    }

    private void ensureBeginHistoryPreConditions() {
        if(historicalMarketData != null) {
            throw new RuntimeException("The history has already been started.");
        }
    }

    protected void beginHistory(ISIN isin, Amount initialClosingMarketPrice, LocalDate date) {
        ensureBeginHistoryPreConditions();
        historicalMarketData = new HistoricalMarketData(isin, initialClosingMarketPrice, date);
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

    protected void addHistory(Amount closingMarketPrice, LocalDate date) {
        ensureAddHistoryPreconditions();
        historicalMarketData.registerClosedDay(closingMarketPrice, date);
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

        this.broker = new VirtualBroker(this.account, this.historicalMarketData, this.commissionStrategy);
        this.tradingStrategy = this.initializeTradingStrategy(new TradingStrategyContext(this.account, this.broker, this.historicalMarketData));

        SimulationBuilder simulationBuilder = new SimulationBuilder();
        simulationBuilder.setTradingStrategy(this.tradingStrategy);
        simulationBuilder.setHistoricalMarketData(this.historicalMarketData);
        simulationBuilder.setAccount(this.account);
        simulationBuilder.setBroker(this.broker);
        this.simulation = simulationBuilder.beginSimulation();
    }

    protected void openDay(LocalDate date) {
        if(simulation == null) {
            throw new RuntimeException("The simulation has not been started yet.");
        }

        simulation.openDay(date);
    }

    private void ensureCloseDayPreconditions() {
        if(simulation == null) {
            throw new RuntimeException("The simulation has not been started yet.");
        }
    }

    protected void closeDay(Amount closingMarketPrice, LocalDate date) {
        ensureCloseDayPreconditions();
        simulation.closeDay(closingMarketPrice, date);
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
