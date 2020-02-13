package trading.domain.taxes;

import trading.domain.Amount;
import trading.domain.DomainException;

public class LinearTaxCalculator implements TaxCalculator {
    private final double taxRate;

    public LinearTaxCalculator(double taxRate) {
        if(taxRate < 0) {
            throw new DomainException("The tax rate must not be negative.");
        }

        this.taxRate = taxRate;
    }

    @Override
    public Amount calculateTaxes(Amount taxableProfit) {
        if(taxableProfit.getValue() < 0.0) {
            throw new DomainException("The taxable profit must not be negative.");
        }

        return new Amount(taxableProfit.getValue() * this.taxRate);
    }
}
