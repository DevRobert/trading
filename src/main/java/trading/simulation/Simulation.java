package trading.simulation;

import trading.account.Account;
import trading.broker.Broker;
import trading.market.HistoricalMarketData;
import trading.market.MarketPriceSnapshot;
import trading.strategy.TradingStrategy;

public class Simulation {
    private final HistoricalMarketData historicalMarketData;
    private final Account account;
    private final Broker broker;
    private final TradingStrategy tradingStrategy;

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
}
