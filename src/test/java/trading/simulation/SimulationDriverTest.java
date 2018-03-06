package trading.simulation;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import trading.Amount;
import trading.DayCount;
import trading.ISIN;

import java.util.Arrays;
import java.util.List;

public class SimulationDriverTest {
    SimulationDriverParametersBuilder parametersBuilder;

    @Before
    public void before() {
        parametersBuilder = new SimulationDriverParametersBuilder();

        List<Amount> closingMarketPrices = Arrays.asList(new Amount(1000.0), new Amount(1100.0));
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

    // Basic run

    public void runSimulation() {
        SimulationDriver simulationDriver = new SimulationDriver(this.parametersBuilder.build());
        SimulationReport simulationReport = simulationDriver.runSimulation();
    }

    // TODO Tests, see below

    // Build historical market data

    // Account initial available amount

    // Creation of trading strategy

    // Call of trading strategy

    // Report correct account balance in the end

    // TODO commission strategy
}
