package trading.strategy;

import org.junit.Assert;
import org.junit.Before;
import trading.Amount;
import trading.ISIN;
import trading.Quantity;
import trading.account.Account;
import trading.account.Position;
import trading.broker.Broker;
import trading.broker.VirtualBroker;
import trading.market.HistoricalMarketData;
import trading.market.MarketPriceSnapshot;
import trading.market.MarketPriceSnapshotBuilder;
import trading.simulation.Simulation;
import trading.simulation.SimulationBuilder;

public abstract class TradingStrategyTestBase {
    protected Account account;
    private VirtualBroker broker;
    private Simulation simulation;

    protected TradingStrategy tradingStrategy;

    protected abstract TradingStrategy initializeTradingStrategy(Account account, Broker broker, HistoricalMarketData historicalMarketData);

    private HistoricalMarketData historicalMarketData;

    @Before()
    public void tradingStrategyTestBaseBefore() {
        this.account = new Account(new Amount(50000.0));
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

    protected void addHistory(Amount marketPrice) {
        if(historicalMarketData == null) {
            throw new RuntimeException("The history has not been started.");
        }

        if(historicalMarketData.getAvailableStocks().size() != 1) {
            throw new RuntimeException("This method is only allowed when one stock available.");
        }

        ISIN isin = historicalMarketData.getAvailableStocks().stream().findFirst().get();

        MarketPriceSnapshotBuilder marketPriceSnapshotBuilder = new MarketPriceSnapshotBuilder();
        marketPriceSnapshotBuilder.setMarketPrice(isin, marketPrice);
        MarketPriceSnapshot marketPriceSnapshot = marketPriceSnapshotBuilder.build();

        addHistory(marketPriceSnapshot);
    }

    protected void addHistory(MarketPriceSnapshot marketPriceSnapshot) {
        if(historicalMarketData == null) {
            throw new RuntimeException("The history has not been started.");
        }

        if(simulation != null) {
            throw new RuntimeException("The simulation has already been started.");
        }

        historicalMarketData.registerClosedDay(marketPriceSnapshot);
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

    protected void passDay(Amount closingMarketPrice) {
        if(simulation == null) {
            throw new RuntimeException("The simulation has not been started yet.");
        }

        if(historicalMarketData.getAvailableStocks().size() != 1) {
            throw new RuntimeException("This method is only allowed when one stock available.");
        }

        ISIN isin = historicalMarketData.getAvailableStocks().stream().findFirst().get();

        MarketPriceSnapshotBuilder marketPriceSnapshotBuilder = new MarketPriceSnapshotBuilder();
        marketPriceSnapshotBuilder.setMarketPrice(isin, closingMarketPrice);
        MarketPriceSnapshot closingMarketPrices = marketPriceSnapshotBuilder.build();

        passDay(closingMarketPrices);
    }

    protected void passDay(MarketPriceSnapshot closingMarketPrices) {
        openDay();
        closeDay(closingMarketPrices);
    }

    protected void openDay() {
        if(simulation == null) {
            throw new RuntimeException("The simulation has not been started yet.");
        }

        simulation.openDay();
    }

    protected void closeDay(MarketPriceSnapshot closingMarketPrices) {
        if(simulation == null) {
            throw new RuntimeException("The simulation has not been started yet.");
        }

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
