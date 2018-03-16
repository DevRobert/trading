package trading.simulation;

import trading.Amount;
import trading.ISIN;
import trading.account.Account;
import trading.broker.Broker;
import trading.broker.VirtualBroker;
import trading.market.HistoricalMarketData;
import trading.market.MarketPriceSnapshot;
import trading.strategy.TradingStrategy;
import trading.strategy.TradingStrategyContext;

import java.util.Set;

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

        MarketPriceSnapshot initialClosingMarketPrices = historicalMarketData.getLastClosingMarketPrices();

        Simulation simulation = new Simulation(historicalMarketData, account, broker, tradingStrategy);
        SimulationMarketDataSource simulationMarketDataSource = this.parameters.getSimulationMarketDataSource();

        int numSimulationDays = this.parameters.getSimulationDuration().getValue();

        for(int simulationDayIndex = 0; simulationDayIndex < numSimulationDays; simulationDayIndex++) {
            simulation.openDay();
            simulation.closeDay(simulationMarketDataSource.getNextClosingMarketPrices());
        }

        Amount finalAccountBalance = account.getBalance();

        MarketPriceSnapshot finalClosingMarketPrices = historicalMarketData.getLastClosingMarketPrices();
        double averageMarketRateOfReturn = this.calculateAverageMarketRateOfReturn(initialClosingMarketPrices, finalClosingMarketPrices);

        double realizedRateOfReturn = account.getBalance().getValue() / this.parameters.getSeedCapital().getValue() - 1;

        SimulationReport simulationReport = new SimulationReport(
                this.parameters.getSeedCapital(),
                finalAccountBalance,
                account.getProcessedTransactions(),
                averageMarketRateOfReturn,
                realizedRateOfReturn);

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

    private double calculateAverageMarketRateOfReturn(MarketPriceSnapshot initialClosingMarketPrices, MarketPriceSnapshot finalClosingMarketPrices) {
        Set<ISIN> isins = initialClosingMarketPrices.getISINs();
        double sumMarketRateOfReturn = 0.0;

        for(ISIN isin: isins) {
            double initialMarketPrice = initialClosingMarketPrices.getMarketPrice(isin).getValue();
            double finalMarketPrice = finalClosingMarketPrices.getMarketPrice(isin).getValue();
            double marketRateOfReturn = finalMarketPrice / initialMarketPrice - 1;
            sumMarketRateOfReturn += marketRateOfReturn;
        }

        return sumMarketRateOfReturn / (double) isins.size();
    }
}
