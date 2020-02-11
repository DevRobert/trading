package trading.domain.simulation;

import trading.domain.Amount;
import trading.domain.DayCount;
import trading.domain.account.TaxStrategy;
import trading.domain.broker.CommissionStrategy;
import trading.domain.strategy.TradingStrategyFactory;

public class SimulationDriverParameters {
    private final SimulationMarketDataSource simulationMarketDataSource;
    private final DayCount historyDuration;
    private final DayCount simulationDuration;
    private final Amount seedCapital;
    private final TradingStrategyFactory tradingStrategyFactory;
    private final CommissionStrategy commissionStrategy;
    private final TaxStrategy taxStrategy;

    public SimulationDriverParameters(
            SimulationMarketDataSource simulationMarketDataSource,
            DayCount historyDuration,
            DayCount simulationDuration,
            Amount seedCapital,
            TradingStrategyFactory tradingStrategyFactory,
            CommissionStrategy commissionStrategy,
            TaxStrategy taxStrategy
    ){
        if(simulationMarketDataSource == null) {
            throw new SimulationDriverInitializationException("The simulation market data source must be specified.");
        }

        if(historyDuration == null) {
            throw new SimulationDriverInitializationException("The history duration must be specified.");
        }

        if(historyDuration.getValue() < 0) {
            throw new SimulationDriverInitializationException("The history duration must not be negative.");
        }

        if(historyDuration.getValue() == 0) {
            throw new SimulationDriverInitializationException("The history duration must not be zero.");
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

        if(commissionStrategy == null) {
            throw new SimulationDriverInitializationException("The commission strategy must be specified.");
        }

        if(taxStrategy == null) {
            throw new SimulationDriverInitializationException("The tax strategy must be specified.");
        }

        this.simulationMarketDataSource = simulationMarketDataSource;
        this.historyDuration = historyDuration;
        this.simulationDuration = simulationDuration;
        this.seedCapital = seedCapital;
        this.tradingStrategyFactory = tradingStrategyFactory;
        this.commissionStrategy = commissionStrategy;
        this.taxStrategy = taxStrategy;
    }

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

    public CommissionStrategy getCommissionStrategy() {
         return this.commissionStrategy;
    }

    public TaxStrategy getTaxStrategy() {
        return this.taxStrategy;
    }
}
