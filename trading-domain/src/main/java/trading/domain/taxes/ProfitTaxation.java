package trading.domain.taxes;

import trading.domain.Amount;

public class ProfitTaxation {
    private final TaxCalculator taxCalculator;

    private Amount taxableProfit = Amount.Zero;
    private Amount taxedProfit = Amount.Zero;
    private Amount reservedTaxes = Amount.Zero;
    private Amount confirmedTaxes = Amount.Zero;

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

    public Amount getConfirmedTaxes() {
        return confirmedTaxes;
    }

    public void registerProfit(Profit profit) {

    }
}
