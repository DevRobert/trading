package trading.domain.broker;

import trading.domain.Amount;

public class DynamicCommissionStrategyParametersBuilder {
    private Amount fixedAmount;
    private double variableAmountRate;
    private Amount minimumVariableAmount;
    private Amount maximumVariableAmount;

    public DynamicCommissionStrategyParametersBuilder setFixedAmount(Amount fixedAmount) {
        this.fixedAmount = fixedAmount;
        return this;
    }

    public DynamicCommissionStrategyParametersBuilder setVariableAmountRate(double variableAmountRate) {
        this.variableAmountRate = variableAmountRate;
        return this;
    }

    public DynamicCommissionStrategyParametersBuilder setMinimumVariableAmount(Amount minimumVariableAmount) {
        this.minimumVariableAmount = minimumVariableAmount;
        return this;
    }

    public DynamicCommissionStrategyParametersBuilder setMaximumVariableAmount(Amount maximumVariableAmount) {
        this.maximumVariableAmount = maximumVariableAmount;
        return this;
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
