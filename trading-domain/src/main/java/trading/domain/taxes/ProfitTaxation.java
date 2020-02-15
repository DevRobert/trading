package trading.domain.taxes;

import trading.domain.Amount;
import trading.domain.DomainException;

public class ProfitTaxation {
    private final TaxCalculator taxCalculator;

    private Amount taxableProfit = Amount.Zero;
    private Amount taxedProfit = Amount.Zero;
    private Amount reservedTaxes = Amount.Zero;
    private Amount paidTaxes = Amount.Zero;

    public ProfitTaxation(TaxCalculator taxCalculator) {
        this.taxCalculator = taxCalculator;
    }

    public Amount getTaxableProfit() {
        return taxableProfit;
    }

    public Amount getTaxedProfit() {
        return taxedProfit;
    }

    public Amount getReservedTaxes() {
        return reservedTaxes;
    }

    public Amount getPaidTaxes() {
        return paidTaxes;
    }

    public void registerProfit(Amount profit) {
        this.taxableProfit = this.taxableProfit.add(profit);
        this.recalculateReservedTaxes();
    }

    public void registerTaxPayment(Amount taxedProfit, Amount paidTaxes) {
        if(taxedProfit.getValue() > this.taxableProfit.getValue()) {
            System.out.println(taxedProfit + " > " + this.taxableProfit);
            throw new DomainException("The specified taxed profit must not exceed the accrued taxable profit.");
        }

        this.taxableProfit = this.taxableProfit.subtract(taxedProfit);
        this.taxedProfit = this.taxedProfit.add(taxedProfit);
        this.paidTaxes = this.paidTaxes.add(paidTaxes);
        this.recalculateReservedTaxes();
    }

    private void recalculateReservedTaxes() {
        if(this.taxableProfit.getValue() >= 0.0) {
            this.reservedTaxes = this.taxCalculator.calculateTaxes(this.taxableProfit);
        }
        else {
            this.reservedTaxes = Amount.Zero;
        }
    }
}
