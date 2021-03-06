package trading.domain.simulation;

import trading.domain.Amount;
import trading.domain.DomainException;
import trading.domain.ISIN;
import trading.domain.account.Account;
import trading.domain.broker.Broker;
import trading.domain.market.HistoricalMarketData;
import trading.domain.market.MarketPriceSnapshot;
import trading.domain.market.MarketPriceSnapshotBuilder;
import trading.domain.strategy.TradingStrategy;

import java.time.LocalDate;
import java.util.Set;

public class Simulation {
    private final HistoricalMarketData historicalMarketData;
    private final Account account;
    private final Broker broker;
    private final TradingStrategy tradingStrategy;
    private final ISIN singleISIN;

    private boolean activeDay;
    private LocalDate openedDayDate;

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

    public void openDay(LocalDate date) {
        if(date == null) {
            throw new DomainException("The date must be specified.");
        }

        if(!date.isAfter(this.historicalMarketData.getDate())) {
            throw new DomainException("The date must lie after the date of the last closed market day.");
        }

        if(this.activeDay) {
            throw new SimulationStateException("A new day cannot be opened because the active day has not been closed yet.");
        }

        this.activeDay = true;

        this.broker.notifyDayOpened(date);
        this.openedDayDate = date;
    }

    public void closeDay(MarketPriceSnapshot closingMarketPrices) {
        if(!this.activeDay) {
            throw new SimulationStateException("There is no active day to be closed.");
        }

        if(!closingMarketPrices.getDate().equals(this.openedDayDate)) {
            throw new DomainException("The market price date must equal the date given when the day was opened.");
        }

        this.activeDay = false;

        this.historicalMarketData.registerClosedDay(closingMarketPrices);

        this.account.reportMarketPrices(closingMarketPrices);

        this.tradingStrategy.prepareOrdersForNextTradingDay();
    }

    public void closeDay(Amount closingMarketPrice, LocalDate date) {
        if(this.singleISIN == null) {
            throw new SimulationStateException("The single-stock close day function must not be used when multiple stocks registered.");
        }

        MarketPriceSnapshot closingMarketPrices = new MarketPriceSnapshotBuilder()
                .setMarketPrice(this.singleISIN, closingMarketPrice)
                .setDate(date)
                .build();

        this.closeDay(closingMarketPrices);
    }
}
