package trading.domain.simulation;

import trading.domain.Amount;
import trading.domain.ISIN;
import trading.domain.account.Account;
import trading.domain.broker.Broker;
import trading.domain.broker.VirtualBroker;
import trading.domain.market.HistoricalMarketData;
import trading.domain.market.MarketPriceSnapshot;
import trading.domain.strategy.TradingStrategy;
import trading.domain.strategy.TradingStrategyContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SimulationDriver {
    private final SimulationDriverParameters parameters;
    private boolean dailyReporting = false;

    public SimulationDriver(SimulationDriverParameters parameters) {
        this.parameters = parameters;
    }

    public void setDailyReporting(boolean dailyReporting) {
        this.dailyReporting = dailyReporting;
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

        List<SimulationDayReport> simulationDayReports = null;

        if(this.dailyReporting) {
            simulationDayReports = new ArrayList<>();
        }

        for(int simulationDayIndex = 0; simulationDayIndex < numSimulationDays; simulationDayIndex++) {
            simulation.openDay();

            MarketPriceSnapshot nextClosingMarketPrices = simulationMarketDataSource.getNextClosingMarketPrices();
            simulation.closeDay(nextClosingMarketPrices);

            if(this.dailyReporting) {
                Amount availableMoney = account.getAvailableMoney();
                Amount accountBalance = account.getBalance();

                double averageMarketRateOfReturn = this.calculateAverageMarketRateOfReturn(
                        initialClosingMarketPrices, nextClosingMarketPrices);

                double realizedRateOfReturn = account.getBalance().getValue() / this.parameters.getSeedCapital().getValue() - 1;

                simulationDayReports.add(new SimulationDayReport(availableMoney, accountBalance, averageMarketRateOfReturn, realizedRateOfReturn));
            }
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
                realizedRateOfReturn,
                simulationDayReports);

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
            double marketRateOfReturn = finalMarketPrice / initialMarketPrice - 1.0;
            sumMarketRateOfReturn += marketRateOfReturn;
        }

        return sumMarketRateOfReturn / (double) isins.size();
    }
}
