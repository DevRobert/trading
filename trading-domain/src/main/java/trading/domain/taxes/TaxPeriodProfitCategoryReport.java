package trading.domain.taxes;

import trading.domain.Amount;

public class TaxPeriodProfitCategoryReport {
    private ProfitCategory profitCategory;
    private Amount lossCarryforward;
    private Amount accruedProfit;
    private Amount clearedProfit;
    private Amount reservedTaxes;
    private Amount paidTaxes;

    public ProfitCategory getProfitCategory() {
        return profitCategory;
    }

    public void setProfitCategory(ProfitCategory profitCategory) {
        this.profitCategory = profitCategory;
    }

    public Amount getLossCarryforward() {
        return lossCarryforward;
    }

    public void setLossCarryforward(Amount lossCarryforward) {
        this.lossCarryforward = lossCarryforward;
    }

    public Amount getAccruedProfit() {
        return accruedProfit;
    }

    public void setAccruedProfit(Amount accruedProfit) {
        this.accruedProfit = accruedProfit;
    }

    public Amount getClearedProfit() {
        return clearedProfit;
    }

    public void setClearedProfit(Amount clearedProfit) {
        this.clearedProfit = clearedProfit;
    }

    public Amount getReservedTaxes() {
        return reservedTaxes;
    }

    public void setReservedTaxes(Amount reservedTaxes) {
        this.reservedTaxes = reservedTaxes;
    }

    public Amount getPaidTaxes() {
        return paidTaxes;
    }

    public void setPaidTaxes(Amount paidTaxes) {
        this.paidTaxes = paidTaxes;
    }
}
