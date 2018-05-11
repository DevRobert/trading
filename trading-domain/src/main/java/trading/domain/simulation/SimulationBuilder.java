package trading.domain.simulation;

import trading.domain.account.Account;
import trading.domain.broker.Broker;
import trading.domain.market.HistoricalMarketData;
import trading.domain.strategy.TradingStrategy;

public class SimulationBuilder {
    private HistoricalMarketData historicalMarketData;
    private Account account;
    private Broker broker;
    private TradingStrategy tradingStrategy;

    public SimulationBuilder setHistoricalMarketData(HistoricalMarketData historicalMarketData) {
        this.historicalMarketData = historicalMarketData;
        return this;
    }

    public SimulationBuilder setAccount(Account account) {
        this.account = account;
        return this;
    }

    public SimulationBuilder setBroker(Broker broker) {
        this.broker = broker;
        return this;
    }

    public SimulationBuilder setTradingStrategy(TradingStrategy tradingStrategy) {
        this.tradingStrategy = tradingStrategy;
        return this;
    }

    public Simulation beginSimulation() {
        return new Simulation(this.historicalMarketData, this.account, this.broker, this.tradingStrategy);
    }
}
