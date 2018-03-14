package trading.simulation;

import trading.Amount;
import trading.account.Account;
import trading.broker.Broker;
import trading.broker.VirtualBroker;
import trading.market.HistoricalMarketData;
import trading.market.MarketPriceSnapshot;
import trading.strategy.TradingStrategy;
import trading.strategy.TradingStrategyContext;

public class SimulationDriver {
    private final SimulationDriverParameters parameters;

    public SimulationDriver(SimulationDriverParameters parameters) {
        this.parameters = parameters;
    }

    public SimulationReport runSimulation() {
        HistoricalMarketData historicalMarketData = this.buildHistoricalMarketData();
        Account account = new Account(this.parameters.getSeedCapital());
        Broker broker = new VirtualBroker(account, historicalMarketData, this.parameters.getCommissionStrategy());

        TradingStrategyContext tradingStrategyContext = new TradingStrategyContext(account, broker, historicalMarketData);
        TradingStrategy tradingStrategy = parameters.getTradingStrategyFactory().createTradingStrategy(tradingStrategyContext);

        Simulation simulation = new Simulation(historicalMarketData, account, broker, tradingStrategy);
        SimulationMarketDataSource simulationMarketDataSource = this.parameters.getSimulationMarketDataSource();

        int numSimulationDays = this.parameters.getSimulationDuration().getValue();

        for(int simulationDayIndex = 0; simulationDayIndex < numSimulationDays; simulationDayIndex++) {
            simulation.openDay();
            simulation.closeDay(simulationMarketDataSource.getNextClosingMarketPrices());
        }

        Amount finalAccountBalance = account.getBalance();

        SimulationReport simulationReport = new SimulationReport(this.parameters.getSeedCapital(), finalAccountBalance, account.getProcessedTransactions());
        return simulationReport;
    }

    private HistoricalMarketData buildHistoricalMarketData() {
        SimulationMarketDataSource simulationMarketDataSource = this.parameters.getSimulationMarketDataSource();
        int numHistoryDays = this.parameters.getHistoryDuration().getValue();

        MarketPriceSnapshot initialClosingMarketPrices = simulationMarketDataSource.getNextClosingMarketPrices();
        HistoricalMarketData historicalMarketData = new HistoricalMarketData(initialClosingMarketPrices);

        for(int historyDayIndex = 1; historyDayIndex < numHistoryDays; historyDayIndex++) {
            MarketPriceSnapshot closingMarketPrices = simulationMarketDataSource.getNextClosingMarketPrices();
            historicalMarketData.registerClosedDay(closingMarketPrices);
        }

        return historicalMarketData;
    }
}
