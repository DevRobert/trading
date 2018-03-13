package trading.broker;

import trading.Amount;

public class DynamicCommissionStrategyParameters {
    private final Amount fixedAmount;
    private final double variableAmountRate;
    private final Amount minimumVariableAmount;
    private final Amount maximumVariableAmount;

    public Amount getFixedAmount() {
        return fixedAmount;
    }

    public double getVariableAmountRate() {
        return variableAmountRate;
    }

    public Amount getMinimumVariableAmount() {
        return minimumVariableAmount;
    }

    public Amount getMaximumVariableAmount() {
        return maximumVariableAmount;
    }

    public DynamicCommissionStrategyParameters(Amount fixedAmount, double variableAmountRate, Amount minimumVariableAmount, Amount maximumVariableAmount) {
        if(minimumVariableAmount != null && maximumVariableAmount != null && minimumVariableAmount.getValue() > maximumVariableAmount.getValue()) {
            throw new RuntimeException("The minimum variable amount must not be greater than the maximum variable amount.");
        }

        this.fixedAmount = fixedAmount;
        this.variableAmountRate = variableAmountRate;
        this.minimumVariableAmount = minimumVariableAmount;
        this.maximumVariableAmount = maximumVariableAmount;
    }
}
