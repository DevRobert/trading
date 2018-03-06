package trading.simulation;

import trading.Amount;
import trading.DayCount;
import trading.strategy.TradingStrategyFactory;

public class SimulationDriverParameters {
    private final SimulationMarketDataSource simulationMarketDataSource;
    private final DayCount historyDuration;
    private final DayCount simulationDuration;
    private final Amount seedCapital;
    private final TradingStrategyFactory tradingStrategyFactory;

    public SimulationMarketDataSource getSimulationMarketDataSource() {
        return this.simulationMarketDataSource;
    }

    public DayCount getHistoryDuration() {
        return this.historyDuration;
    }

    public DayCount getSimulationDuration() {
        return this.simulationDuration;
    }

    public Amount getSeedCapital() {
        return this.seedCapital;
    }

    public TradingStrategyFactory getTradingStrategyFactory() {
        return this.tradingStrategyFactory;
    }

    public SimulationDriverParameters(SimulationMarketDataSource simulationMarketDataSource, DayCount historyDuration, DayCount simulationDuration, Amount seedCapital, TradingStrategyFactory tradingStrategyFactory) {
        if(simulationMarketDataSource == null) {
            throw new SimulationDriverInitializationException("The simulation market data source must be specified.");
        }

        if(historyDuration == null) {
            throw new SimulationDriverInitializationException("The history duration must be specified.");
        }

        if(historyDuration.getValue() < 0) {
            throw new SimulationDriverInitializationException("The history duration must not be negative.");
        }

        if(simulationDuration == null) {
            throw new SimulationDriverInitializationException("The simulation duration must be specified.");
        }

        if(simulationDuration.isZero()) {
            throw new SimulationDriverInitializationException("The simulation duration must not be zero.");
        }

        if(simulationDuration.getValue() < 0) {
            throw new SimulationDriverInitializationException("The simulation duration must not be negative.");
        }

        if(seedCapital == null) {
            throw new SimulationDriverInitializationException("The seed capital must be specified.");
        }

        if(tradingStrategyFactory == null) {
            throw new SimulationDriverInitializationException("The trading strategy factory must be specified.");
        }

        this.simulationMarketDataSource = simulationMarketDataSource;
        this.historyDuration = historyDuration;
        this.simulationDuration = simulationDuration;
        this.seedCapital = seedCapital;
        this.tradingStrategyFactory = tradingStrategyFactory;
    }
}
