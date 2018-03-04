package trading.simulation;

import trading.account.Account;
import trading.broker.Broker;
import trading.market.HistoricalMarketData;
import trading.strategy.TradingStrategy;

public class SimulationBuilder {
    private HistoricalMarketData historicalMarketData;
    private Account account;
    private Broker broker;
    private TradingStrategy tradingStrategy;

    public void setHistoricalMarketData(HistoricalMarketData historicalMarketData) {
        this.historicalMarketData = historicalMarketData;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public void setBroker(Broker broker) {
        this.broker = broker;
    }

    public void setTradingStrategy(TradingStrategy tradingStrategy) {
        this.tradingStrategy = tradingStrategy;
    }

    public Simulation startSimulation() {
        if(this.historicalMarketData == null) {
            throw new SimulationStartException("The historical market data must be set.");
        }

        if(this.account == null) {
            throw new SimulationStartException("The account must be set.");
        }

        if(this.broker == null) {
            throw new SimulationStartException("The broker must be set.");
        }

        if(this.tradingStrategy == null) {
            throw new SimulationStartException("The trading strategy must be set.");
        }

        return new Simulation(this.historicalMarketData, this.account, this.broker, this.tradingStrategy);
    }
}
