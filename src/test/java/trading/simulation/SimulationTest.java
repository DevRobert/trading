package trading.simulation;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import trading.Amount;
import trading.ISIN;
import trading.account.Account;
import trading.broker.Broker;
import trading.broker.OrderRequest;
import trading.broker.VirtualBroker;
import trading.market.HistoricalMarketData;
import trading.market.MarketPriceSnapshot;
import trading.market.MarketPriceSnapshotBuilder;
import trading.strategy.manual.ManualTradingStrategy;
import trading.strategy.TradingStrategy;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class SimulationTest {
    private Simulation simulation;
    private HistoricalMarketData historicalMarketData;
    private Account account;
    private Broker broker;
    private TradingStrategy tradingStrategy;

    @Before
    public void before() {
        MarketPriceSnapshotBuilder marketPriceSnapshotBuilder = new MarketPriceSnapshotBuilder();
        marketPriceSnapshotBuilder.setMarketPrice(ISIN.MunichRe, new Amount(1000.0));
        historicalMarketData = new HistoricalMarketData(marketPriceSnapshotBuilder.build());

        Amount availableMoney = new Amount(50000.0);
        account = new Account(availableMoney);
        broker = new VirtualBroker(account, historicalMarketData);
        tradingStrategy = new ManualTradingStrategy(broker);
    }

    protected void startSimulation() {
        SimulationBuilder simulationBuilder = new SimulationBuilder();
        simulationBuilder.setHistoricalMarketData(historicalMarketData);
        simulationBuilder.setAccount(account);
        simulationBuilder.setBroker(broker);
        simulationBuilder.setTradingStrategy(tradingStrategy);
        simulation = simulationBuilder.beginSimulation();
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
    public void tradingStrategyIsAskedToPrepareOrdersForNextTradingDayWhenSimulationStarted() {
        AtomicBoolean prepareOrdersSignalReceived = new AtomicBoolean(false);

        this.tradingStrategy = () -> prepareOrdersSignalReceived.set(true);

        startSimulation();

        Assert.assertTrue(prepareOrdersSignalReceived.get());
    }

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

        MarketPriceSnapshotBuilder marketPriceSnapshotBuilder = new MarketPriceSnapshotBuilder();
        marketPriceSnapshotBuilder.setMarketPrice(ISIN.MunichRe, new Amount(1100.0));
        MarketPriceSnapshot closingMarketPrices = marketPriceSnapshotBuilder.build();

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
    public void forwardDayOpenedSignalToBroker() {
        final AtomicBoolean dayOpenedSignalReceived = new AtomicBoolean(false);

        broker = new Broker() {
            @Override
            public void setOrder(OrderRequest orderRequest) {

            }

            @Override
            public void notifyDayOpened() {
                dayOpenedSignalReceived.set(true);
            }
        };

        startSimulation();

        simulation.openDay();

        Assert.assertTrue(dayOpenedSignalReceived.get());
    }

    @Test
    public void updateHistoricalMarketDataWhenDayClosed() {
        startSimulation();
        simulation.openDay();

        Amount newMarketPriceMunichRe = new Amount(1100.0);
        MarketPriceSnapshotBuilder marketPriceSnapshotBuilder = new MarketPriceSnapshotBuilder();
        marketPriceSnapshotBuilder.setMarketPrice(ISIN.MunichRe, newMarketPriceMunichRe);
        simulation.closeDay(marketPriceSnapshotBuilder.build());

        Assert.assertEquals(newMarketPriceMunichRe, historicalMarketData.getStockData(ISIN.MunichRe).getLastClosingMarketPrice());
    }

    @Test
    public void updateHistoricalMarketDataWhenDayClosed_forSingleStock() {
        startSimulation();
        simulation.openDay();

        Amount newMarketPriceMunichRe = new Amount(1100.0);
        simulation.closeDay(newMarketPriceMunichRe);

        Assert.assertEquals(newMarketPriceMunichRe, historicalMarketData.getStockData(ISIN.MunichRe).getLastClosingMarketPrice());
    }

    @Test
    public void updateHistoricalMarketDataFails_ifForSingleStockCalled_butMultipleStocksAvailable() {
        MarketPriceSnapshotBuilder marketPriceSnapshotBuilder = new MarketPriceSnapshotBuilder();
        marketPriceSnapshotBuilder.setMarketPrice(ISIN.MunichRe, new Amount(1000.0));
        marketPriceSnapshotBuilder.setMarketPrice(ISIN.Allianz, new Amount(500.0));
        historicalMarketData = new HistoricalMarketData(marketPriceSnapshotBuilder.build());

        startSimulation();
        simulation.openDay();

        Amount newMarketPriceMunichRe = new Amount(1100.0);

        try {
            simulation.closeDay(newMarketPriceMunichRe);
        }
        catch(SimulationStateException ex) {
            Assert.assertEquals("The single-stock close day function must not be used when multiple stocks registered.", ex.getMessage());
            return;
        }

        Assert.fail("SimulationStateException expected.");
    }

    @Test
    public void forwardDayClosedSignalToTradingStrategy() {
        AtomicBoolean dayClosedSignalReceived = new AtomicBoolean(false);

        this.tradingStrategy = () -> dayClosedSignalReceived.set(true);

        startSimulation();
        simulation.openDay();

        MarketPriceSnapshotBuilder marketPriceSnapshotBuilder = new MarketPriceSnapshotBuilder();
        marketPriceSnapshotBuilder.setMarketPrice(ISIN.MunichRe, new Amount(1100.0));
        simulation.closeDay(marketPriceSnapshotBuilder.build());

        Assert.assertTrue(dayClosedSignalReceived.get());
    }

    @Test
    public void historicalMarketDataAreUpdatedBeforeDayClosedSignalIsForwardedToTradingStrategy() {

    }
}
