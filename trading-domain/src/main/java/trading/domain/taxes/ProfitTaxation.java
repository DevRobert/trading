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

    public Amount getUntaxedTaxableProfitConsideringLossCarryforward() {
        return this.accruedProfit
                .subtract(this.taxedProfit)
                .subtract(this.getLossCarryforward());
    }

    public Amount getLossCarryforward() {
        if(this.previousTaxPeriodProfitTaxation == null) {
            return Amount.Zero;
        }

        return this.previousTaxPeriodProfitTaxation.getLossCarryforwardForNextPeriod();
    }

    public Amount getLossCarryforwardForNextPeriod() {
        if(this.getUntaxedTaxableProfitConsideringLossCarryforward().getValue() >= 0) {
            return Amount.Zero;
        }

        return this.getUntaxedTaxableProfitConsideringLossCarryforward().multiply(new Quantity(-1));
    }

    public Amount getTaxedProfit() {
        return this.taxedProfit;
    }

    public Amount getReservedTaxes() {
        Amount taxableProfit = this.getUntaxedTaxableProfitConsideringLossCarryforward();

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
        if(taxedProfit.getValue() > this.getUntaxedTaxableProfitConsideringLossCarryforward().getValue()) {
            throw new DomainException("The specified taxed profit must not exceed the remaining untaxed taxable profit/ considering loss carryforward.");
        }

        this.taxedProfit = this.taxedProfit.add(taxedProfit);
        this.paidTaxes = this.paidTaxes.add(paidTaxes);
    }

    public TaxPeriodProfitCategoryReport buildTaxPeriodProfitCategoryReport(ProfitCategory profitCategory) {
        TaxPeriodProfitCategoryReport taxPeriodProfitCategoryReport = new TaxPeriodProfitCategoryReport();

        taxPeriodProfitCategoryReport.setProfitCategory(profitCategory);
        taxPeriodProfitCategoryReport.setLossCarryforward(this.getLossCarryforward());
        taxPeriodProfitCategoryReport.setAccruedProfit(this.accruedProfit);
        taxPeriodProfitCategoryReport.setClearedProfit(this.accruedProfit.subtract(this.getLossCarryforward()));
        taxPeriodProfitCategoryReport.setReservedTaxes(this.getReservedTaxes());
        taxPeriodProfitCategoryReport.setPaidTaxes(this.getPaidTaxes());

        return taxPeriodProfitCategoryReport;
    }
}
