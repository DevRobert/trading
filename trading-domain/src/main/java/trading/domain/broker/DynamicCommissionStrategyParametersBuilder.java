package trading.domain.broker;

import trading.domain.Amount;

public class DynamicCommissionStrategyParametersBuilder {
    private Amount fixedAmount;
    private double variableAmountRate;
    private Amount minimumVariableAmount;
    private Amount maximumVariableAmount;

    public void setFixedAmount(Amount fixedAmount) {
        this.fixedAmount = fixedAmount;
    }

    public void setVariableAmountRate(double variableAmountRate) {
        this.variableAmountRate = variableAmountRate;
    }

    public void setMinimumVariableAmount(Amount minimumVariableAmount) {
        this.minimumVariableAmount = minimumVariableAmount;
    }

    public void setMaximumVariableAmount(Amount maximumVariableAmount) {
        this.maximumVariableAmount = maximumVariableAmount;
    }

    public DynamicCommissionStrategyParameters build() {
        return new DynamicCommissionStrategyParameters(
                this.fixedAmount,
                this.variableAmountRate,
                this.minimumVariableAmount,
                this.maximumVariableAmount
        );
    }
}
