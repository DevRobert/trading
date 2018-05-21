package trading.domain.simulation;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import trading.domain.*;
import trading.domain.account.TransactionType;
import trading.domain.broker.CommissionStrategy;
import trading.domain.broker.OrderRequest;
import trading.domain.broker.OrderType;
import trading.domain.broker.ZeroCommissionStrategy;
import trading.domain.market.HistoricalMarketData;
import trading.domain.market.HistoricalStockData;
import trading.domain.market.MarketPriceSnapshot;
import trading.domain.market.MarketPriceSnapshotBuilder;
import trading.domain.strategy.TradingStrategy;
import trading.domain.strategy.TradingStrategyFactory;
import trading.domain.strategy.manual.ManualTradingStrategy;

import java.time.LocalDate;
import java.util.ArrayList;
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

        List<LocalDate> dates = new DateSequenceGenerator(LocalDate.now()).nextDates(4);

        SimulationMarketDataSource marketDataSource = new SingleStockListDataSource(ISIN.MunichRe, closingMarketPrices, dates);
        parametersBuilder.setSimulationMarketDataSource(marketDataSource);

        parametersBuilder.setSeedCapital(new Amount(50000.0));

        parametersBuilder.setHistoryDuration(new DayCount(1));
        parametersBuilder.setSimulationDuration(new DayCount(1));

        parametersBuilder.setTradingStrategyFactory(context -> null);

        parametersBuilder.setCommissionStrategy(new ZeroCommissionStrategy());
    }

    // Initialization

    @Test
    public void initializationFails_ifMarketDataSourceNotSpecified() {
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
    public void initializationFails_ifSeedCapitalNotSpecified() {
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
    public void initializationFails_ifHistoryDurationNotSpecified() {
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
    public void initializationFails_ifSimulationDurationNotSpecified() {
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
    public void initializationFails_ifTradingStrategyFactoryNotSpecified() {
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

    @Test
    public void initializationFails_ifCommissionStrategyNotSpecified() {
        parametersBuilder.setCommissionStrategy(null);

        try {
            new SimulationDriver(parametersBuilder.build());
        }
        catch(SimulationDriverInitializationException ex) {
            Assert.assertEquals("The commission strategy must be specified.", ex.getMessage());
            return;
        }

        Assert.fail("SimulationDriverInitializationException expected.");
    }

    // Build historical market data

    @Test
    public void buildHistoricalMarketData_forOneDayHistoryDuration() {
        this.parametersBuilder.setHistoryDuration(new DayCount(1));

        AtomicBoolean assertDone = new AtomicBoolean(false);

        TradingStrategyFactory tradingStrategyFactory = context -> {
            Assert.assertEquals(new DayCount(1), context.getHistoricalMarketData().getDuration());
            Assert.assertEquals(new Amount(1000.0), context.getHistoricalMarketData().getStockData(ISIN.MunichRe).getLastClosingMarketPrice());
            assertDone.set(true);
            return new ManualTradingStrategy(context.getBroker());
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

        TradingStrategyFactory tradingStrategyFactory = context -> {
            Assert.assertEquals(new DayCount(2), context.getHistoricalMarketData().getDuration());
            Assert.assertEquals(new Amount(1100.0), context.getHistoricalMarketData().getStockData(ISIN.MunichRe).getLastClosingMarketPrice());
            assertDone.set(true);
            return new ManualTradingStrategy(context.getBroker());
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

        TradingStrategyFactory tradingStrategyFactory = context -> {
            Assert.assertEquals(seedCapital, context.getAccount().getAvailableMoney());
            assertDone.set(true);
            return new ManualTradingStrategy(context.getBroker());
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

        TradingStrategyFactory tradingStrategyFactory = tradingStrategyContext -> {
            final HistoricalMarketData historicalMarketData = tradingStrategyContext.getHistoricalMarketData();

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

    // Call of commission strategy

    @Test
    public void specifiedCommissionStrategyUsed() {
        AtomicBoolean commissionStrategyCalled = new AtomicBoolean(false);

        this.parametersBuilder.setTradingStrategyFactory(context -> new TradingStrategy() {
            @Override
            public void prepareOrdersForNextTradingDay() {
                context.getBroker().setOrder(new OrderRequest(OrderType.BuyMarket, ISIN.MunichRe, new Quantity(1)));
            }
        });

        CommissionStrategy commissionStrategy = totalPrice -> {
            Assert.assertEquals(new Amount(1000.0), totalPrice);
            commissionStrategyCalled.set(true);
            return Amount.Zero;
        };

        this.parametersBuilder.setCommissionStrategy(commissionStrategy);

        SimulationDriver simulationDriver = new SimulationDriver(this.parametersBuilder.build());
        simulationDriver.runSimulation();

        Assert.assertTrue("The commission strategy has not been called.", commissionStrategyCalled.get());
    }

    @Test
    public void tradingStrategyIsAskedForNewOrders_threeTimes_ifSimulationDurationIsTwoDays() {
        this.parametersBuilder.setSimulationDuration(new DayCount(2));

        AtomicInteger numAskedForNewOrders = new AtomicInteger(0);

        TradingStrategyFactory tradingStrategyFactory = context -> {
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

        TradingStrategyFactory tradingStrategyFactory = context -> new ManualTradingStrategy(context.getBroker());
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

        TradingStrategyFactory tradingStrategyFactory = context -> {
            ManualTradingStrategy manualTradingStrategy = new ManualTradingStrategy(context.getBroker());

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

    // Report correct initial account balance in the end

    @Test
    public void reportsCorrectInitialBalance_ifDifferentFromFinalBalance() {
        Amount seedCapital = new Amount(50000.0);
        this.parametersBuilder.setSeedCapital(seedCapital);

        TradingStrategyFactory tradingStrategyFactory = context -> {
            // The transaction ensures that initial and final balance are not equal
            ManualTradingStrategy manualTradingStrategy = new ManualTradingStrategy(context.getBroker());
            manualTradingStrategy.registerOrderRequest(new OrderRequest(OrderType.BuyMarket, ISIN.MunichRe, new Quantity(1)));
            return manualTradingStrategy;
        };

        this.parametersBuilder.setTradingStrategyFactory(tradingStrategyFactory);

        SimulationDriver simulationDriver = new SimulationDriver(this.parametersBuilder.build());
        SimulationReport simulationReport = simulationDriver.runSimulation();

        Assert.assertEquals(seedCapital, simulationReport.getInitialAccountBalance());
    }

    // Report transactions in the end

    @Test
    public void reportsTransactions() {
        TradingStrategyFactory tradingStrategyFactory = context -> {
            ManualTradingStrategy manualTradingStrategy = new ManualTradingStrategy(context.getBroker());
            manualTradingStrategy.registerOrderRequest(new OrderRequest(OrderType.BuyMarket, ISIN.MunichRe, new Quantity(1)));
            return manualTradingStrategy;
        };

        this.parametersBuilder.setTradingStrategyFactory(tradingStrategyFactory);

        SimulationDriver simulationDriver = new SimulationDriver(this.parametersBuilder.build());
        SimulationReport simulationReport = simulationDriver.runSimulation();

        Assert.assertEquals(1, simulationReport.getTransactions().size());
        Assert.assertEquals(ISIN.MunichRe, simulationReport.getTransactions().get(0).getIsin());
        Assert.assertEquals(new Quantity(1), simulationReport.getTransactions().get(0).getQuantity());
        Assert.assertEquals(TransactionType.Buy, simulationReport.getTransactions().get(0).getTransactionType());
    }

    // Average market rate of return

    @Test
    public void reportsAverageMarketRateOfReturn_ifOneStock_andOneDayHistory_andOneDaySimulation() {
        List<Amount> closingMarketPrices = Arrays.asList(
                new Amount(1000.0),
                new Amount(1500.0));

        List<LocalDate> dates = new DateSequenceGenerator(LocalDate.now()).nextDates(2);

        SimulationMarketDataSource marketDataSource = new SingleStockListDataSource(ISIN.MunichRe, closingMarketPrices, dates);
        this.parametersBuilder.setSimulationMarketDataSource(marketDataSource);

        this.parametersBuilder.setTradingStrategyFactory(context -> new ManualTradingStrategy(context.getBroker()));

        this.parametersBuilder.setHistoryDuration(new DayCount(1));
        this.parametersBuilder.setSimulationDuration(new DayCount(1));

        SimulationDriver simulationDriver = new SimulationDriver(this.parametersBuilder.build());
        SimulationReport simulationReport = simulationDriver.runSimulation();

        Assert.assertEquals(0.5, simulationReport.getAverageMarketRateOfReturn(), 0.0);
    }

    @Test
    public void reportsAverageMarketRateOfReturn_ifOneStock_andTwoDaysHistory_andOneDaySimulation() {
        List<Amount> closingMarketPrices = Arrays.asList(
                new Amount(500.0),
                new Amount(1000.0),
                new Amount(2000.0));

        List<LocalDate> dates = new DateSequenceGenerator(LocalDate.now()).nextDates(3);

        SimulationMarketDataSource marketDataSource = new SingleStockListDataSource(ISIN.MunichRe, closingMarketPrices, dates);
        this.parametersBuilder.setSimulationMarketDataSource(marketDataSource);

        this.parametersBuilder.setTradingStrategyFactory(context -> new ManualTradingStrategy(context.getBroker()));

        this.parametersBuilder.setHistoryDuration(new DayCount(2));
        this.parametersBuilder.setSimulationDuration(new DayCount(1));

        SimulationDriver simulationDriver = new SimulationDriver(this.parametersBuilder.build());
        SimulationReport simulationReport = simulationDriver.runSimulation();

        Assert.assertEquals(1.0, simulationReport.getAverageMarketRateOfReturn(), 0.0);
    }

    @Test
    public void reportsAverageMarketRateOfReturn_ifOneStock_andOneDayHistory_andTwoDaySimulation() {
        List<Amount> closingMarketPrices = Arrays.asList(
                new Amount(500.0),
                new Amount(1000.0),
                new Amount(2000.0));

        List<LocalDate> dates = new DateSequenceGenerator(LocalDate.now()).nextDates(3);

        SimulationMarketDataSource marketDataSource = new SingleStockListDataSource(ISIN.MunichRe, closingMarketPrices, dates);
        this.parametersBuilder.setSimulationMarketDataSource(marketDataSource);

        this.parametersBuilder.setTradingStrategyFactory(context -> new ManualTradingStrategy(context.getBroker()));

        this.parametersBuilder.setHistoryDuration(new DayCount(1));
        this.parametersBuilder.setSimulationDuration(new DayCount(2));

        SimulationDriver simulationDriver = new SimulationDriver(this.parametersBuilder.build());
        SimulationReport simulationReport = simulationDriver.runSimulation();

        Assert.assertEquals(3.0, simulationReport.getAverageMarketRateOfReturn(), 0.0);
    }

    @Test
    public void reportsAverageMarketRateOfReturn_ifTwoStocks_andOneDayHistory_andOneDaySimulation() {
        List<MarketPriceSnapshot> marketPriceSnapshots = new ArrayList<>();

        marketPriceSnapshots.add(new MarketPriceSnapshotBuilder()
                .setMarketPrice(ISIN.MunichRe, new Amount(100.0))
                .setMarketPrice(ISIN.Allianz, new Amount(200.0))
                .setDate(LocalDate.of(2018, 1, 1))
                .build());

        marketPriceSnapshots.add(new MarketPriceSnapshotBuilder()
                .setMarketPrice(ISIN.MunichRe, new Amount(150.0)) // rate of return is 0.5
                .setMarketPrice(ISIN.Allianz, new Amount(400.0)) // rate of return is 1.0
                .setDate(LocalDate.of(2018, 1, 2))
                .build());

        SimulationMarketDataSource marketDataSource = new MultiStockListDataSource(marketPriceSnapshots);
        this.parametersBuilder.setSimulationMarketDataSource(marketDataSource);

        this.parametersBuilder.setTradingStrategyFactory(context -> new ManualTradingStrategy(context.getBroker()));

        this.parametersBuilder.setHistoryDuration(new DayCount(1));
        this.parametersBuilder.setSimulationDuration(new DayCount(1));

        SimulationDriver simulationDriver = new SimulationDriver(this.parametersBuilder.build());
        SimulationReport simulationReport = simulationDriver.runSimulation();

        Assert.assertEquals(0.75, simulationReport.getAverageMarketRateOfReturn(), 0.0);
    }

    // Realized rate of return

    @Test
    public void reportsRealizedRateOfReturn() {
        this.parametersBuilder.setTradingStrategyFactory(context -> {
            ManualTradingStrategy tradingStrategy = new ManualTradingStrategy(context.getBroker());

            tradingStrategy.registerOrderRequest(new OrderRequest(OrderType.BuyMarket, ISIN.MunichRe, new Quantity(1)));

            return tradingStrategy;
        });

        this.parametersBuilder.setSeedCapital(new Amount(2000.0));
        this.parametersBuilder.setHistoryDuration(new DayCount(1));
        this.parametersBuilder.setSimulationDuration(new DayCount(1));

        SimulationDriver simulationDriver = new SimulationDriver(this.parametersBuilder.build());
        SimulationReport simulationReport = simulationDriver.runSimulation();

        // Seed capital: 2,000
        // Buy one stock for 1,000
        // Stock market price rises to 1,100
        // Final balance is: 2,100
        // Rate of return is: 2,100 / 2,000 - 1 = 0.05

        Assert.assertEquals(0.05, simulationReport.getRealizedRateOfReturn(), 0.000000001);
    }
}
