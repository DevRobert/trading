package trading.simulation;

import trading.Amount;

public class SimulationReport {
    private final Amount finalAccountBalance;

    public SimulationReport(Amount finalAccountBalance) {
        this.finalAccountBalance = finalAccountBalance;
    }

    public Amount getFinalAccountBalance() {
        return this.finalAccountBalance;
    }
}
