package trading.api.account;

import trading.domain.taxes.TaxPeriodProfitCategoryReport;
import trading.domain.taxes.TaxPeriodReport;

import java.util.ArrayList;
import java.util.List;

public class TaxPeriodDto {
    private int year;
    private List<TaxPeriodProfitCategoryDto> profitCategories;

    public TaxPeriodDto() {

    }

    public TaxPeriodDto(TaxPeriodReport taxPeriodReport) {
        this.year = taxPeriodReport.getYear();
        this.profitCategories = new ArrayList<>();

        for(TaxPeriodProfitCategoryReport taxPeriodProfitCategoryReport: taxPeriodReport.getProfitCategories()) {
            this.profitCategories.add(new TaxPeriodProfitCategoryDto(taxPeriodProfitCategoryReport));
        }
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public List<TaxPeriodProfitCategoryDto> getProfitCategories() {
        return profitCategories;
    }

    public void setProfitCategories(List<TaxPeriodProfitCategoryDto> profitCategories) {
        this.profitCategories = profitCategories;
    }
}
