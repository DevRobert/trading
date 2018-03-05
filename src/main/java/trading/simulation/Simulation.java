package trading.simulation;

import trading.Amount;
import trading.ISIN;
import trading.account.Account;
import trading.broker.Broker;
import trading.market.HistoricalMarketData;
import trading.market.MarketPriceSnapshot;
import trading.market.MarketPriceSnapshotBuilder;
import trading.strategy.TradingStrategy;

import java.util.Set;

public class Simulation {
    private final HistoricalMarketData historicalMarketData;
    private final Account account;
    private final Broker broker;
    private final TradingStrategy tradingStrategy;
    private final ISIN singleISIN;

    private boolean activeDay;

    protected Simulation(HistoricalMarketData historicalMarketData, Account account, Broker broker, TradingStrategy tradingStrategy) {
        if(historicalMarketData == null) {
            throw new SimulationStartException("The historical market data must be set.");
        }

        if(account == null) {
            throw new SimulationStartException("The account must be set.");
        }

        if(broker == null) {
            throw new SimulationStartException("The broker must be set.");
        }

        if(tradingStrategy == null) {
            throw new SimulationStartException("The trading strategy must be set.");
        }

        this.historicalMarketData = historicalMarketData;
        this.account = account;
        this.broker = broker;
        this.tradingStrategy = tradingStrategy;
        this.activeDay = false;

        Set<ISIN> availableStocks = historicalMarketData.getAvailableStocks();

        if(availableStocks.size() == 1) {
            this.singleISIN = availableStocks.stream().findFirst().get();
        }
        else {
            this.singleISIN = null;
        }

        this.tradingStrategy.prepareOrdersForNextTradingDay();
    }

    public void openDay() {
        if(this.activeDay) {
            throw new SimulationStateException("A new day cannot be opened because the active day has not been closed yet.");
        }

        this.activeDay = true;

        this.broker.notifyDayOpened();
    }

    public void closeDay(MarketPriceSnapshot closingMarketPrices) {
        if(!this.activeDay) {
            throw new SimulationStateException("There is no active day to be closed.");
        }

        this.activeDay = false;

        this.historicalMarketData.registerClosedDay(closingMarketPrices);
        this.tradingStrategy.prepareOrdersForNextTradingDay();
    }

    public void closeDay(Amount closingMarketPrice) {
        if(this.singleISIN == null) {
            throw new SimulationStateException("The single-stock close day function must not be used when multiple stocks registered.");
        }

        MarketPriceSnapshotBuilder marketPriceSnapshotBuilder = new MarketPriceSnapshotBuilder();
        marketPriceSnapshotBuilder.setMarketPrice(this.singleISIN, closingMarketPrice);
        MarketPriceSnapshot closingMarketPrices = marketPriceSnapshotBuilder.build();

        this.closeDay(closingMarketPrices);
    }
}
