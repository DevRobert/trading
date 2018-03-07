package trading.simulation;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import trading.Amount;
import trading.DayCount;
import trading.ISIN;
import trading.Quantity;
import trading.broker.OrderRequest;
import trading.broker.OrderType;
import trading.market.HistoricalStockData;
import trading.strategy.TradingStrategy;
import trading.strategy.TradingStrategyFactory;
import trading.strategy.manual.ManualTradingStrategy;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The simulation driver tests heavily depend on the simulation class.
 *
 * Make sure the tests for the simulation class work before fixing issues
 * in this unit test.
 */
public class SimulationDriverTest {
    SimulationDriverParametersBuilder parametersBuilder;

    @Before
    public void before() {
        parametersBuilder = new SimulationDriverParametersBuilder();

        List<Amount> closingMarketPrices = Arrays.asList(
                new Amount(1000.0),
                new Amount(1100.0),
                new Amount(1200.0),
                new Amount(1300.0));

        SimulationMarketDataSource marketDataSource = new SingleStockListDataSource(ISIN.MunichRe, closingMarketPrices);
        parametersBuilder.setSimulationMarketDataSource(marketDataSource);

        parametersBuilder.setSeedCapital(new Amount(50000.0));

        parametersBuilder.setHistoryDuration(new DayCount(1));
        parametersBuilder.setSimulationDuration(new DayCount(1));

        parametersBuilder.setTradingStrategyFactory((account, broker, historicalMarketData) -> null);
    }

    // Initialization

    @Test
    public void initializationFails_ifNoMarketDataSourceSpecified() {
        parametersBuilder.setSimulationMarketDataSource(null);

        try {
            new SimulationDriver(parametersBuilder.build());
        }
        catch(SimulationDriverInitializationException ex) {
            Assert.assertEquals("The simulation market data source must be specified.", ex.getMessage());
            return;
        }

        Assert.fail("SimulationDriverInitializationException expected.");
    }

    @Test
    public void initializationFails_ifNoSeedCapitalSpecified() {
        parametersBuilder.setSeedCapital(null);

        try {
            new SimulationDriver(parametersBuilder.build());
        }
        catch(SimulationDriverInitializationException ex) {
            Assert.assertEquals("The seed capital must be specified.", ex.getMessage());
            return;
        }

        Assert.fail("SimulationDriverInitializationException expected.");
    }

    @Test
    public void initializationFails_ifNoHistoryDurationSpecified() {
        parametersBuilder.setHistoryDuration(null);

        try {
            new SimulationDriver(parametersBuilder.build());
        }
        catch(SimulationDriverInitializationException ex) {
            Assert.assertEquals("The history duration must be specified.", ex.getMessage());
            return;
        }

        Assert.fail("SimulationDriverInitializationException expected.");
    }

    @Test
    public void initializationFails_ifNegativeHistoryDurationSpecified() {
        parametersBuilder.setHistoryDuration(new DayCount(-1));

        try {
            new SimulationDriver(parametersBuilder.build());
        }
        catch(SimulationDriverInitializationException ex) {
            Assert.assertEquals("The history duration must not be negative.", ex.getMessage());
            return;
        }

        Assert.fail("SimulationDriverInitializationException expected.");
    }

    @Test
    public void initializationFails_ifZeroHistoryDurationSpecified() {
        parametersBuilder.setHistoryDuration(new DayCount(0));

        try {
            new SimulationDriver(parametersBuilder.build());
        }
        catch(SimulationDriverInitializationException ex) {
            Assert.assertEquals("The history duration must not be zero.", ex.getMessage());
            return;
        }

        Assert.fail("SimulationDriverInitializationException expected.");
    }

    @Test
    public void initializationFails_ifNoSimulationDurationSpecified() {
        parametersBuilder.setSimulationDuration(null);

        try {
            new SimulationDriver(parametersBuilder.build());
        }
        catch(SimulationDriverInitializationException ex) {
            Assert.assertEquals("The simulation duration must be specified.", ex.getMessage());
            return;
        }

        Assert.fail("SimulationDriverInitializationException expected.");
    }

    @Test
    public void initializationFails_ifNegativeSimulationDurationSpecified() {
        parametersBuilder.setSimulationDuration(new DayCount(-1));

        try {
            new SimulationDriver(parametersBuilder.build());
        }
        catch(SimulationDriverInitializationException ex) {
            Assert.assertEquals("The simulation duration must not be negative.", ex.getMessage());
            return;
        }

        Assert.fail("SimulationDriverInitializationException expected.");
    }

    @Test
    public void initializationFails_ifZeroSimulationDurationSpecified() {
        parametersBuilder.setSimulationDuration(new DayCount(0));

        try {
            new SimulationDriver(parametersBuilder.build());
        }
        catch(SimulationDriverInitializationException ex) {
            Assert.assertEquals("The simulation duration must not be zero.", ex.getMessage());
            return;
        }

        Assert.fail("SimulationDriverInitializationException expected.");
    }

    @Test
    public void initializationFails_ifNoTradingStrategyFactorySpecified() {
        parametersBuilder.setTradingStrategyFactory(null);

        try {
            new SimulationDriver(parametersBuilder.build());
        }
        catch(SimulationDriverInitializationException ex) {
            Assert.assertEquals("The trading strategy factory must be specified.", ex.getMessage());
            return;
        }

        Assert.fail("SimulationDriverInitializationException expected.");
    }

    // Build historical market data

    @Test
    public void buildHistoricalMarketData_forOneDayHistoryDuration() {
        this.parametersBuilder.setHistoryDuration(new DayCount(1));

        AtomicBoolean assertDone = new AtomicBoolean(false);

        TradingStrategyFactory tradingStrategyFactory = (account, broker, historicalMarketData) -> {
            Assert.assertEquals(new DayCount(1), historicalMarketData.getDuration());
            Assert.assertEquals(new Amount(1000.0), historicalMarketData.getStockData(ISIN.MunichRe).getLastClosingMarketPrice());
            assertDone.set(true);
            return new ManualTradingStrategy(broker);
        };

        this.parametersBuilder.setTradingStrategyFactory(tradingStrategyFactory);

        SimulationDriver simulationDriver = new SimulationDriver(this.parametersBuilder.build());
        simulationDriver.runSimulation();

        Assert.assertTrue("The trading strategy factory has not been called.", assertDone.get());
    }

    @Test
    public void buildHistoricalMarketData_forTwoDaysHistoryDuration() {
        this.parametersBuilder.setHistoryDuration(new DayCount(2));

        AtomicBoolean assertDone = new AtomicBoolean(false);

        TradingStrategyFactory tradingStrategyFactory = (account, broker, historicalMarketData) -> {
            Assert.assertEquals(new DayCount(2), historicalMarketData.getDuration());
            Assert.assertEquals(new Amount(1100.0), historicalMarketData.getStockData(ISIN.MunichRe).getLastClosingMarketPrice());
            assertDone.set(true);
            return new ManualTradingStrategy(broker);
        };

        this.parametersBuilder.setTradingStrategyFactory(tradingStrategyFactory);

        SimulationDriver simulationDriver = new SimulationDriver(this.parametersBuilder.build());
        simulationDriver.runSimulation();

        Assert.assertTrue("The trading strategy factory has not been called.", assertDone.get());
    }

    // Account seed capital

    @Test
    public void accountIsCreatedWithSpecifiedSeedCapital() {
        final Amount seedCapital = new Amount(80000.0);
        this.parametersBuilder.setSeedCapital(seedCapital);

        AtomicBoolean assertDone = new AtomicBoolean(false);

        TradingStrategyFactory tradingStrategyFactory = (account, broker, historicalMarketData) -> {
            Assert.assertEquals(seedCapital, account.getAvailableMoney());
            assertDone.set(true);
            return new ManualTradingStrategy(broker);
        };

        this.parametersBuilder.setTradingStrategyFactory(tradingStrategyFactory);

        SimulationDriver simulationDriver = new SimulationDriver(this.parametersBuilder.build());
        simulationDriver.runSimulation();

        Assert.assertTrue("The trading strategy factory has not been called.", assertDone.get());
    }

    // Call of trading strategy

    // If the simulation duration is N, the trading strategy is asked N + 1 times
    // to prepare new orders, but the last prepared orders are not executed

    @Test
    public void tradingStrategyIsAskedForNewOrders_twoTimes_withCorrectClosingPrices_ifSimulationDurationIsOneDay() {
        this.parametersBuilder.setSimulationDuration(new DayCount(1));

        AtomicInteger numAskedForNewOrders = new AtomicInteger(0);

        TradingStrategyFactory tradingStrategyFactory = (account, broker, historicalMarketData) -> {
            return (TradingStrategy) () -> {
                int simulationDayIndex = numAskedForNewOrders.get();

                HistoricalStockData historicalStockData = historicalMarketData.getStockData(ISIN.MunichRe);

                if(simulationDayIndex == 0) {
                    // First call - before first trading day
                    Assert.assertEquals(new Amount(1000.0), historicalStockData.getLastClosingMarketPrice());
                }
                else if (simulationDayIndex == 1) {
                    // Second call - after first trading day
                    Assert.assertEquals(new Amount(1100.0), historicalStockData.getLastClosingMarketPrice());
                }

                numAskedForNewOrders.incrementAndGet();
            };
        };

        this.parametersBuilder.setTradingStrategyFactory(tradingStrategyFactory);

        SimulationDriver simulationDriver = new SimulationDriver(this.parametersBuilder.build());
        simulationDriver.runSimulation();

        Assert.assertEquals(2, numAskedForNewOrders.get());
    }

    @Test
    public void tradingStrategyIsAskedForNewOrders_threeTimes_ifSimulationDurationIsTwoDays() {
        this.parametersBuilder.setSimulationDuration(new DayCount(2));

        AtomicInteger numAskedForNewOrders = new AtomicInteger(0);

        TradingStrategyFactory tradingStrategyFactory = (account, broker, historicalMarketData) -> {
            return (TradingStrategy) () -> numAskedForNewOrders.incrementAndGet();
        };

        this.parametersBuilder.setTradingStrategyFactory(tradingStrategyFactory);

        SimulationDriver simulationDriver = new SimulationDriver(this.parametersBuilder.build());
        simulationDriver.runSimulation();

        Assert.assertEquals(3, numAskedForNewOrders.get());
    }

    // Report correct account balance in the end

    @Test
    public void reportsCorrectAccountBalance_ifNoOrdersSet() {
        Amount seedCapital = new Amount(80000.0);
        this.parametersBuilder.setSeedCapital(seedCapital);

        TradingStrategyFactory tradingStrategyFactory = (account, broker, historicalMarketData) -> new ManualTradingStrategy(broker);
        this.parametersBuilder.setTradingStrategyFactory(tradingStrategyFactory);

        SimulationDriver simulationDriver = new SimulationDriver(this.parametersBuilder.build());
        SimulationReport simulationReport = simulationDriver.runSimulation();

        Assert.assertEquals(seedCapital, simulationReport.getFinalAccountBalance());
    }

    @Test
    public void reportsCorrectAccountBalance_ifOrderSet() {
        this.parametersBuilder.setHistoryDuration(new DayCount(1));
        this.parametersBuilder.setSimulationDuration(new DayCount(1));

        Amount seedCapital = new Amount(80000.0);
        this.parametersBuilder.setSeedCapital(seedCapital);

        TradingStrategyFactory tradingStrategyFactory = (account, broker, historicalMarketData) -> {
            ManualTradingStrategy manualTradingStrategy = new ManualTradingStrategy(broker);

            // Market price should be 1,000 (after first history trading day = before first simulation trading day)
            // Total amount should be 5 x 1,000 = 5,000

            OrderRequest orderRequest = new OrderRequest(OrderType.BuyMarket, ISIN.MunichRe, new Quantity(5));
            manualTradingStrategy.registerOrderRequest(orderRequest);

            return manualTradingStrategy;
        };

        this.parametersBuilder.setTradingStrategyFactory(tradingStrategyFactory);

        SimulationDriver simulationDriver = new SimulationDriver(this.parametersBuilder.build());
        SimulationReport simulationReport = simulationDriver.runSimulation();

        // Market price of stocks should be 1,100 (after first simulation trading day)
        // Total market price of stocks should be 5 x 1,100 = 5,500

        // Expected balance = 75,000 (available amount) + 5,500 = 80,500

        Assert.assertEquals(new Amount(80500.0), simulationReport.getFinalAccountBalance());
    }

    // TODO commission strategy
}
