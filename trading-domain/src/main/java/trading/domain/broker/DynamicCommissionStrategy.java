package trading.domain.broker;

import trading.domain.Amount;

public class DynamicCommissionStrategy implements CommissionStrategy {
    private final DynamicCommissionStrategyParameters parameters;

    public DynamicCommissionStrategy(DynamicCommissionStrategyParameters parameters) {
        if(parameters == null) {
            throw new RuntimeException("The parameters must be specified.");
        }

        this.parameters = parameters;
    }

    @Override
    public Amount calculateCommission(Amount totalPrice) {
        Amount result = Amount.Zero;

        if(this.parameters.getFixedAmount() != null) {
            result = result.add(this.parameters.getFixedAmount());
        }

        if(this.parameters.getVariableAmountRate() != 0.0) {
            double variableAmount = totalPrice.getValue() * this.parameters.getVariableAmountRate();

            if(this.parameters.getMinimumVariableAmount() != null && variableAmount < this.parameters.getMinimumVariableAmount().getValue()) {
                variableAmount = this.parameters.getMinimumVariableAmount().getValue();
            }

            if(this.parameters.getMaximumVariableAmount() != null && variableAmount > this.parameters.getMaximumVariableAmount().getValue()) {
                variableAmount = this.parameters.getMaximumVariableAmount().getValue();
            }

            result = result.add(new Amount(variableAmount));
        }

        return result;
    }
}
