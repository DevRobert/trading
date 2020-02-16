package trading.api.account;

import trading.domain.taxes.TaxPeriodProfitCategoryReport;

public class TaxPeriodProfitCategoryDto {
    private String name;
    private double lossCarryforward;
    private double accruedProfit;
    private double clearedProfit;
    private double reservedTaxes;
    private double paidTaxes;

    public TaxPeriodProfitCategoryDto() {

    }

    public TaxPeriodProfitCategoryDto(TaxPeriodProfitCategoryReport taxPeriodProfitCategoryReport) {
        this.name = taxPeriodProfitCategoryReport.getProfitCategory().getName();
        this.lossCarryforward = taxPeriodProfitCategoryReport.getLossCarryforward().getValue();
        this.accruedProfit = taxPeriodProfitCategoryReport.getAccruedProfit().getValue();
        this.clearedProfit = taxPeriodProfitCategoryReport.getClearedProfit().getValue();
        this.reservedTaxes = taxPeriodProfitCategoryReport.getReservedTaxes().getValue();
        this.paidTaxes = taxPeriodProfitCategoryReport.getPaidTaxes().getValue();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLossCarryforward() {
        return lossCarryforward;
    }

    public void setLossCarryforward(double lossCarryforward) {
        this.lossCarryforward = lossCarryforward;
    }

    public double getAccruedProfit() {
        return accruedProfit;
    }

    public void setAccruedProfit(double accruedProfit) {
        this.accruedProfit = accruedProfit;
    }

    public double getClearedProfit() {
        return clearedProfit;
    }

    public void setClearedProfit(double clearedProfit) {
        this.clearedProfit = clearedProfit;
    }

    public double getReservedTaxes() {
        return reservedTaxes;
    }

    public void setReservedTaxes(double reservedTaxes) {
        this.reservedTaxes = reservedTaxes;
    }

    public double getPaidTaxes() {
        return paidTaxes;
    }

    public void setPaidTaxes(double paidTaxes) {
        this.paidTaxes = paidTaxes;
    }
}
