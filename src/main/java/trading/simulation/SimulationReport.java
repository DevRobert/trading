package trading.simulation;

import trading.Amount;
import trading.Transaction;

import java.util.List;

public class SimulationReport {
    private final Amount initialAccountBalance;
    private final Amount finalAccountBalance;
    private final List<Transaction> transactions;
    private final double averageMarketRateOfReturn;
    private final double realizedRateOfReturn;
    private final List<SimulationDayReport> dayReports;

    public SimulationReport(Amount initialAccountBalance, Amount finalAccountBalance, List<Transaction> transactions, double averageMarketRateOfReturn, double realizedRateOfReturn, List<SimulationDayReport> dayReports) {
        this.initialAccountBalance = initialAccountBalance;
        this.finalAccountBalance = finalAccountBalance;
        this.transactions = transactions;
        this.averageMarketRateOfReturn = averageMarketRateOfReturn;
        this.realizedRateOfReturn = realizedRateOfReturn;
        this.dayReports = dayReports;
    }

    public Amount getInitialAccountBalance() {
        return this.initialAccountBalance;
    }

    public Amount getFinalAccountBalance() {
        return this.finalAccountBalance;
    }

    public List<Transaction> getTransactions() {
        return this.transactions;
    }

    public double getAverageMarketRateOfReturn() {
        return this.averageMarketRateOfReturn;
    }

    public double getRealizedRateOfReturn() {
        return this.realizedRateOfReturn;
    }

    public double getAddedRateOfReturn() {
        return this.getRealizedRateOfReturn() - this.getAverageMarketRateOfReturn();
    }

    public List<SimulationDayReport> getDayReports() {
        return this.dayReports;
    }
}
