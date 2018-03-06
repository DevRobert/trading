package trading.simulation;

import trading.Amount;
import trading.DayCount;
import trading.strategy.TradingStrategyFactory;

public class SimulationDriverParametersBuilder {
    private SimulationMarketDataSource simulationMarketDataSource;
    private DayCount historyDuration;
    private DayCount simulationDuration;
    private Amount seedCapital;
    private TradingStrategyFactory tradingStrategyFactory;

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

    public SimulationDriverParameters build() {
        return new SimulationDriverParameters(
                this.simulationMarketDataSource,
                this.historyDuration,
                this.simulationDuration,
                this.seedCapital,
                this.tradingStrategyFactory
        );
    }
}
