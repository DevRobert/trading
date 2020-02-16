package trading.domain.taxes;

import trading.domain.Amount;
import trading.domain.DomainException;
import trading.domain.Quantity;

/**
 * Profit taxation for one profit category in one tax period.
 */
public class ProfitTaxation {
    private final TaxCalculator taxCalculator;
    private final ProfitTaxation previousTaxPeriodProfitTaxation;

    private Amount accruedProfit = Amount.Zero;
    private Amount taxedProfit = Amount.Zero;
    private Amount paidTaxes = Amount.Zero;

    public ProfitTaxation(TaxCalculator taxCalculator, ProfitTaxation previousTaxPeriodProfitTaxation) {
        this.taxCalculator = taxCalculator;
        this.previousTaxPeriodProfitTaxation = previousTaxPeriodProfitTaxation;
    }

    public Amount getTaxableProfitBeforeLossCarryforward() {
        return this.accruedProfit.subtract(this.taxedProfit);
    }

    public Amount getTaxableProfitAfterLossCarryforward() {
        return this.getTaxableProfitBeforeLossCarryforward().subtract(this.getLossCarryforward());
    }

    public Amount getLossCarryforward() {
        if(this.previousTaxPeriodProfitTaxation == null) {
            return Amount.Zero;
        }

        return this.previousTaxPeriodProfitTaxation.getLossCarryforwardForNextPeriod();
    }

    public Amount getLossCarryforwardForNextPeriod() {
        if(this.getTaxableProfitAfterLossCarryforward().getValue() >= 0) {
            return Amount.Zero;
        }

        return this.getTaxableProfitAfterLossCarryforward().multiply(new Quantity(-1));
    }

    public Amount getTaxedProfit() {
        return this.taxedProfit;
    }

    public Amount getReservedTaxes() {
        Amount taxableProfit = this.getTaxableProfitAfterLossCarryforward();

        if(taxableProfit.getValue() > 0) {
            return this.taxCalculator.calculateTaxes(taxableProfit);
        }

        return Amount.Zero;
    }

    public Amount getPaidTaxes() {
        return this.paidTaxes;
    }

    public void registerProfit(Amount profit) {
        this.accruedProfit = this.accruedProfit.add(profit);
    }

    public void registerTaxPayment(Amount taxedProfit, Amount paidTaxes) {
        if(taxedProfit.getValue() > this.getTaxableProfitAfterLossCarryforward().getValue()) {
            throw new DomainException("The specified taxed profit must not exceed the remaining taxable profit.");
            // todo loss carryover
        }

        this.taxedProfit = this.taxedProfit.add(taxedProfit);
        this.paidTaxes = this.paidTaxes.add(paidTaxes);
    }
}
