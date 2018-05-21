package trading.domain.simulation;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import trading.domain.Amount;
import trading.domain.DomainException;
import trading.domain.ISIN;
import trading.domain.Quantity;
import trading.domain.account.Account;
import trading.domain.account.TransactionBuilder;
import trading.domain.account.TransactionType;
import trading.domain.broker.*;
import trading.domain.market.HistoricalMarketData;
import trading.domain.market.MarketPriceSnapshot;
import trading.domain.market.MarketPriceSnapshotBuilder;
import trading.domain.strategy.TradingStrategy;
import trading.domain.strategy.manual.ManualTradingStrategy;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class SimulationTest {
    private Simulation simulation;
    private HistoricalMarketData historicalMarketData;
    private Account account;
    private Broker broker;
    private TradingStrategy tradingStrategy;

    @Before
    public void before() {
        MarketPriceSnapshot marketPriceSnapshot = new MarketPriceSnapshotBuilder()
                .setMarketPrice(ISIN.MunichRe, new Amount(1000.0))
                .setDate(LocalDate.now())
                .build();

        historicalMarketData = new HistoricalMarketData(marketPriceSnapshot);

        Amount availableMoney = new Amount(50000.0);
        account = new Account(availableMoney);
        broker = new VirtualBroker(account, historicalMarketData, new ZeroCommissionStrategy());
        tradingStrategy = new ManualTradingStrategy(broker);
    }

    protected void startSimulation() {
        simulation = new SimulationBuilder()
                .setHistoricalMarketData(historicalMarketData)
                .setAccount(account)
                .setBroker(broker)
                .setTradingStrategy(tradingStrategy)
                .beginSimulation();
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
            MarketPriceSnapshot closingMarketPrices = new MarketPriceSnapshot(new HashMap<>(), historicalMarketData.getDate().plusDays(1));
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

        MarketPriceSnapshot closingMarketPrices = new MarketPriceSnapshotBuilder()
                .setMarketPrice(ISIN.MunichRe, new Amount(1100.0))
                .setDate(historicalMarketData.getDate().plusDays(1))
                .build();

        simulation.openDay(closingMarketPrices.getDate());
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

        simulation.openDay(historicalMarketData.getDate().plusDays(1));

        try {
            simulation.openDay(historicalMarketData.getDate().plusDays(2));
        }
        catch(SimulationStateException ex) {
            Assert.assertEquals("A new day cannot be opened because the active day has not been closed yet.", ex.getMessage());
            return;
        }

        Assert.fail("SimulationStateException expected.");
    }

    @Test
    public void openDayFailsIfDateNotSpecified() {
        startSimulation();

        LocalDate date = null;

        try {
            simulation.openDay(date);
        }
        catch(DomainException e) {
            Assert.assertEquals("The date must be specified.", e.getMessage());
            return;
        }

        Assert.fail("DomainException expected.");
    }

    @Test
    public void openDayFailsIfDateEqualsLastDate() {
        startSimulation();

        try {
            simulation.openDay(historicalMarketData.getDate());
        }
        catch(DomainException e) {
            Assert.assertEquals("The date must lie after the date of the last closed market day.", e.getMessage());
            return;
        }

        Assert.fail("DomainException expected.");
    }

    @Test
    public void openDayFailsIfDateBeforeLastDate() {
        startSimulation();

        try {
            simulation.openDay(historicalMarketData.getDate().minusDays(1));
        }
        catch(DomainException e) {
            Assert.assertEquals("The date must lie after the date of the last closed market day.", e.getMessage());
            return;
        }

        Assert.fail("DomainException expected.");
    }


    // Redirect signals

    @Test
    public void forwardDayOpenedSignalToBroker() {
        final AtomicBoolean dayOpenedSignalReceived = new AtomicBoolean(false);

        broker = new Broker() {
            @Override
            public void setOrder(OrderRequest orderRequest) {

            }

            @Override
            public void notifyDayOpened(LocalDate date) {
                dayOpenedSignalReceived.set(true);
            }

            @Override
            public CommissionStrategy getCommissionStrategy() {
                return null;
            }
        };

        startSimulation();

        simulation.openDay(historicalMarketData.getDate().plusDays(1));

        Assert.assertTrue(dayOpenedSignalReceived.get());
    }

    @Test
    public void updateHistoricalMarketDataWhenDayClosed() {
        startSimulation();

        Amount newMarketPriceMunichRe = new Amount(1100.0);

        MarketPriceSnapshot marketPriceSnapshot = new MarketPriceSnapshotBuilder()
                .setMarketPrice(ISIN.MunichRe, newMarketPriceMunichRe)
                .setDate(historicalMarketData.getDate().plusDays(1))
                .build();

        simulation.openDay(marketPriceSnapshot.getDate());
        simulation.closeDay(marketPriceSnapshot);

        Assert.assertEquals(newMarketPriceMunichRe, historicalMarketData.getStockData(ISIN.MunichRe).getLastClosingMarketPrice());
    }

    @Test
    public void updateHistoricalMarketDataWhenDayClosed_forSingleStock() {
        startSimulation();

        simulation.openDay(historicalMarketData.getDate().plusDays(1));

        Amount newMarketPriceMunichRe = new Amount(1100.0);
        simulation.closeDay(newMarketPriceMunichRe, historicalMarketData.getDate().plusDays(1));

        Assert.assertEquals(newMarketPriceMunichRe, historicalMarketData.getStockData(ISIN.MunichRe).getLastClosingMarketPrice());
    }

    @Test
    public void closeDayFails_ifDateBeforeOpenDayDate() {
        startSimulation();

        LocalDate openDayDate = historicalMarketData.getDate().plusDays(4);
        simulation.openDay(openDayDate);

        try {
            simulation.closeDay(new Amount(1100.0), openDayDate.minusDays(1));
        }
        catch(DomainException e) {
            Assert.assertEquals("The market price date must equal the date given when the day was opened.", e.getMessage());
            return;
        }

        Assert.fail("DomainException expected.");
    }

    @Test
    public void closeDayFails_ifDateAfterOpenDayDate() {
        startSimulation();

        LocalDate openDayDate = historicalMarketData.getDate().plusDays(4);
        simulation.openDay(openDayDate);

        try {
            simulation.closeDay(new Amount(1100.0), openDayDate.plusDays(1));
        }
        catch(DomainException e) {
            Assert.assertEquals("The market price date must equal the date given when the day was opened.", e.getMessage());
            return;
        }

        Assert.fail("DomainException expected.");
    }

    @Test
    public void closeDayFails_ifForSingleStockCalled_butMultipleStocksAvailable() {
        MarketPriceSnapshot marketPriceSnapshot = new MarketPriceSnapshotBuilder()
                .setMarketPrice(ISIN.MunichRe, new Amount(1000.0))
                .setMarketPrice(ISIN.Allianz, new Amount(500.0))
                .setDate(LocalDate.now())
                .build();

        historicalMarketData = new HistoricalMarketData(marketPriceSnapshot);

        startSimulation();
        simulation.openDay(historicalMarketData.getDate().plusDays(1));

        Amount newMarketPriceMunichRe = new Amount(1100.0);

        try {
            simulation.closeDay(newMarketPriceMunichRe, historicalMarketData.getDate().plusDays(1));
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
        simulation.openDay(historicalMarketData.getDate().plusDays(1));

        MarketPriceSnapshot marketPriceSnapshot = new MarketPriceSnapshotBuilder()
                .setMarketPrice(ISIN.MunichRe, new Amount(1100.0))
                .setDate(historicalMarketData.getDate().plusDays(1))
                .build();

        simulation.closeDay(marketPriceSnapshot);

        Assert.assertTrue(dayClosedSignalReceived.get());
    }

    @Test
    public void accountIsUpdatedWithLatestMarketPriceWhenDayClosed() {
        Amount buyStocksTotalPrice = new Amount(200.0);
        Amount buyStocksComission = new Amount(0.0);

        account.registerTransaction(new TransactionBuilder()
                .setTransactionType(TransactionType.Buy)
                .setIsin(ISIN.MunichRe)
                .setQuantity(new Quantity(2))
                .setTotalPrice(buyStocksTotalPrice)
                .setCommission(buyStocksComission)
                .setDate(historicalMarketData.getDate())
                .build());

        startSimulation();
        simulation.openDay(historicalMarketData.getDate().plusDays(1));
        simulation.closeDay(new Amount(110.0), historicalMarketData.getDate().plusDays(1));

        Assert.assertEquals(new Amount(220.0), account.getPosition(ISIN.MunichRe).getFullMarketPrice());
    }

    @Test
    public void historicalMarketDataAreUpdatedBeforeDayClosedSignalIsForwardedToTradingStrategy() {
        AtomicReference<Boolean> ignoreCall = new AtomicReference<>(true);
        AtomicReference<Boolean> tradingStrategyCalled = new AtomicReference<>(false);

        this.tradingStrategy = () -> {
            if(ignoreCall.get()) {
                return;
            }

            Assert.assertEquals(new Amount(200.0), historicalMarketData.getStockData(ISIN.MunichRe).getLastClosingMarketPrice());
            tradingStrategyCalled.set(true);
        };

        this.startSimulation();

        this.simulation.openDay(this.historicalMarketData.getDate().plusDays(1));

        ignoreCall.set(false);

        this.simulation.closeDay(new MarketPriceSnapshotBuilder()
                .setMarketPrice(ISIN.MunichRe, new Amount(200.0))
                .setDate(this.historicalMarketData.getDate().plusDays(1))
                .build());

        this.simulation.openDay(this.historicalMarketData.getDate().plusDays(1));

        Assert.assertTrue(tradingStrategyCalled.get());
    }
}
