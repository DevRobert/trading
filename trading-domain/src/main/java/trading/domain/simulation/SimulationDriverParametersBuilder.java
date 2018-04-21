package trading.domain.simulation;

import trading.domain.Amount;
import trading.domain.DayCount;
import trading.domain.broker.CommissionStrategy;
import trading.domain.strategy.TradingStrategyFactory;

public class SimulationDriverParametersBuilder {
    private SimulationMarketDataSource simulationMarketDataSource;
    private DayCount historyDuration;
    private DayCount simulationDuration;
    private Amount seedCapital;
    private TradingStrategyFactory tradingStrategyFactory;
    private CommissionStrategy commissionStrategy;

    public void setSimulationMarketDataSource(SimulationMarketDataSource simulationMarketDataSource) {
        this.simulationMarketDataSource = simulationMarketDataSource;
    }

    public void setHistoryDuration(DayCount historyDuration) {
        this.historyDuration = historyDuration;
    }

    public void setSimulationDuration(DayCount simulationDuration) {
        this.simulationDuration = simulationDuration;
    }

    public void setSeedCapital(Amount seedCapital) {
        this.seedCapital = seedCapital;
    }

    public void setTradingStrategyFactory(TradingStrategyFactory tradingStrategyFactory) {
        this.tradingStrategyFactory = tradingStrategyFactory;
    }

    public void setCommissionStrategy(CommissionStrategy commissionStrategy) {
        this.commissionStrategy = commissionStrategy;
    }

    public SimulationDriverParameters build() {
        return new SimulationDriverParameters(
                this.simulationMarketDataSource,
                this.historyDuration,
                this.simulationDuration,
                this.seedCapital,
                this.tradingStrategyFactory,
                this.commissionStrategy
        );
    }
}
