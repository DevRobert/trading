package trading.simulation;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import trading.Amount;
import trading.ISIN;
import trading.Quantity;
import trading.account.Account;
import trading.account.Position;
import trading.broker.OrderRequest;
import trading.broker.OrderType;
import trading.broker.VirtualBroker;
import trading.market.HistoricalMarketData;
import trading.market.MarketPriceSnapshot;
import trading.market.MarketPriceSnapshotBuilder;
import trading.strategy.ManualTradingStrategy;

import java.util.HashMap;

/**
 * The simulation is a core component of the trading application. Its role is mainly to integrate
 * the core entities:
 *
 * - Historical data
 * - Trading strategy
 * - Broker
 * - Account
 *
 * In order to get meaningful test results, these dependencies are not replaced but used in this test suite.
 * If a test fails, at first make sure that the tests of the dependent entity classes succeed.
 */
public class SimulationTest {
    private Simulation simulation;
    private HistoricalMarketData historicalMarketData;
    private Account account;
    private VirtualBroker broker;
    private ManualTradingStrategy tradingStrategy;

    @Before
    public void before() {
        MarketPriceSnapshotBuilder marketPriceSnapshotBuilder = new MarketPriceSnapshotBuilder();
        marketPriceSnapshotBuilder.setMarketPrice(ISIN.MunichRe, new Amount(1000.0));
        historicalMarketData = new HistoricalMarketData(marketPriceSnapshotBuilder.build());

        Amount availableMoney = new Amount(50000.0);
        account = new Account(availableMoney);
        broker = new VirtualBroker(account, historicalMarketData);
        tradingStrategy = new ManualTradingStrategy();
    }

    protected void startSimulation() {
        SimulationBuilder simulationBuilder = new SimulationBuilder();
        simulationBuilder.setHistoricalMarketData(historicalMarketData);
        simulationBuilder.setAccount(account);
        simulationBuilder.setBroker(broker);
        simulationBuilder.setTradingStrategy(tradingStrategy);
        simulation = simulationBuilder.startSimulation();
    }

    // Simulation preconditions

    @Test
    public void simulationStartFailsIfNoHistoricalMarketDataSet() {
        historicalMarketData = null;

        try {
            startSimulation();
        } catch (SimulationStartException ex) {
            Assert.assertEquals("The historical market data must be set.", ex.getMessage());
            return;
        }

        Assert.fail("SimulationStartException expected.");
    }

    @Test
    public void simulationStartFailsIfNoTradingStrategySet() {
        tradingStrategy = null;

        try {
            startSimulation();
        }
        catch(SimulationStartException ex) {
            Assert.assertEquals("The trading strategy must be set.", ex.getMessage());
            return;
        }

        Assert.fail("SimulationStartException expected.");
    }

    @Test
    public void simulationStartFailsIfNoBrokerSet() {
        broker = null;

        try {
            startSimulation();
        }
        catch(SimulationStartException ex) {
            Assert.assertEquals("The broker must be set.", ex.getMessage());
            return;
        }

        Assert.fail("SimulationStartException expected.");
    }

    @Test
    public void simulationStartFailsIfNoAccountSet() {
        account = null;

        try {
            startSimulation();
        }
        catch(SimulationStartException ex) {
            Assert.assertEquals("The account must be set.", ex.getMessage());
            return;
        }

        Assert.fail("SimulationStartException expected.");
    }

    // State sequence

    @Test
    public void closeDayFailsInitiallyWhenNoDayStarted() {
        startSimulation();

        try {
            MarketPriceSnapshot closingMarketPrices = new MarketPriceSnapshot(new HashMap<>());
            simulation.closeDay(closingMarketPrices);
        }
        catch(SimulationStateException ex) {
            Assert.assertEquals("There is no active day to be closed.", ex.getMessage());
            return;
        }

        Assert.fail("SimulationStateException expected.");
    }

    @Test
    public void closeDayFailsIfDayWasClosedAndNoNextDayStarted() {
        startSimulation();

        MarketPriceSnapshot closingMarketPrices = new MarketPriceSnapshot(new HashMap<>());

        simulation.openDay();
        simulation.closeDay(closingMarketPrices);

        try {
            simulation.closeDay(closingMarketPrices);
        }
        catch(SimulationStateException ex) {
            Assert.assertEquals("There is no active day to be closed.", ex.getMessage());
            return;
        }

        Assert.fail("SimulationStateException expected.");
    }

    @Test
    public void openDayFailsIfActiveDayNotClosed() {
        startSimulation();

        simulation.openDay();

        try {
            simulation.openDay();
        }
        catch(SimulationStateException ex) {
            Assert.assertEquals("A new day cannot be opened because the active day has not been closed yet.", ex.getMessage());
            return;
        }

        Assert.fail("SimulationStateException expected.");
    }

    // Redirect of signals

    @Test
    public void openDaySignalLeadsToExecutionOfOrderRequestsByBroker() {
        startSimulation();

        Quantity quantity = new Quantity(1);
        OrderRequest orderRequest = new OrderRequest(OrderType.BuyMarket, ISIN.MunichRe, quantity);
        broker.setOrder(orderRequest);

        simulation.openDay();

        Position position = account.getPosition(ISIN.MunichRe);
        Assert.assertEquals(quantity, position.getQuantity());
    }

    @Test
    public void closeDaySignalLeadsToUpdateOfHistoricalMarketData() {
        startSimulation();
        simulation.openDay();

        Amount newMarketPriceMunichRe = new Amount(1100.0);
        MarketPriceSnapshotBuilder marketPriceSnapshotBuilder = new MarketPriceSnapshotBuilder();
        marketPriceSnapshotBuilder.setMarketPrice(ISIN.MunichRe, newMarketPriceMunichRe);
        simulation.closeDay(marketPriceSnapshotBuilder.build());

        Assert.assertEquals(newMarketPriceMunichRe, historicalMarketData.getStockData(ISIN.MunichRe).getLastMarketPrice());
    }

    @Test
    public void closeDaySignalWithMissingMarketPricesFails() {
        startSimulation();
        simulation.openDay();

        try {
            simulation.closeDay(new MarketPriceSnapshotBuilder().build());
        }
        catch(SimulationStateException ex) {
            Assert.assertEquals("The market price snapshot must contains market prices for all available stocks.", ex.getMessage());
            return;
        }

        Assert.fail("SimulationStateException expected.");
    }

    @Test
    public void closeDaySignalLeadsToCalculationOfNextOrdersByTradingStrategy() {

    }

    @Test
    public void historicalMarketDataAreUpdatedBeforeTradingStrategyIsInformedAboutClosingDay() {

    }
}
