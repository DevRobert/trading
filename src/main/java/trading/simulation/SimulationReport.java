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

    public SimulationReport(Amount initialAccountBalance, Amount finalAccountBalance, List<Transaction> transactions, double averageMarketRateOfReturn, double realizedRateOfReturn) {
        this.initialAccountBalance = initialAccountBalance;
        this.finalAccountBalance = finalAccountBalance;
        this.transactions = transactions;
        this.averageMarketRateOfReturn = averageMarketRateOfReturn;
        this.realizedRateOfReturn = realizedRateOfReturn;
    }

    public Amount getInitialAccountBalance() {
        return this.initialAccountBalance;
    }

    public Amount getFinalAccountBalance() {
        return this.finalAccountBalance;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public double getAverageMarketRateOfReturn() {
        return this.averageMarketRateOfReturn;
    }

    public double getRealizedRateOfReturn() {
        return this.realizedRateOfReturn;
    }
}
