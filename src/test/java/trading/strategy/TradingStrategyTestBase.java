package trading.strategy;

import org.junit.Before;
import trading.Amount;
import trading.ISIN;
import trading.account.Account;
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
    private SimulationBuilder simulationBuilder;

    protected TradingStrategy strategy;

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
        this.strategy = this.initializeTradingStrategy(this.account, this.broker, historicalMarketData);
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
