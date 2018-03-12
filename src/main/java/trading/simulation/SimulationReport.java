package trading.simulation;

import trading.Amount;
import trading.Transaction;

import java.util.List;

public class SimulationReport {
    private final Amount initialAccountBalance;
    private final Amount finalAccountBalance;
    private final List<Transaction> transactions;

    public SimulationReport(Amount initialAccountBalance, Amount finalAccountBalance, List<Transaction> transactions) {
        this.initialAccountBalance = initialAccountBalance;
        this.finalAccountBalance = finalAccountBalance;
        this.transactions = transactions;
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
}
