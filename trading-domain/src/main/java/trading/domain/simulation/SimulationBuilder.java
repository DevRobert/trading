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

    public Simulation beginSimulation() {
        return new Simulation(this.historicalMarketData, this.account, this.broker, this.tradingStrategy);
    }
}
