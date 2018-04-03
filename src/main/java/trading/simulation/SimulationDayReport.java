package trading.simulation;

import trading.Amount;

public class SimulationDayReport {
    private final Amount availableMoney;
    private final Amount accountBalance;
    private final double averageMarketRateOfReturn;
    private final double realizedRateOfReturn;

    public SimulationDayReport(Amount availableMoney, Amount accountBalance, double averageMarketRateOfReturn, double realizedRateOfReturn) {
        this.availableMoney = availableMoney;
        this.accountBalance = accountBalance;
        this.averageMarketRateOfReturn = averageMarketRateOfReturn;
        this.realizedRateOfReturn = realizedRateOfReturn;
    }

    public Amount getAvailableMoney() {
        return this.availableMoney;
    }

    public Amount getAccountBalance() {
        return this.accountBalance;
    }

    public double getAverageMarketRateOfReturn() {
        return this.averageMarketRateOfReturn;
    }

    public double getRealizedRateOfReturn() {
        return this.realizedRateOfReturn;
    }
}
