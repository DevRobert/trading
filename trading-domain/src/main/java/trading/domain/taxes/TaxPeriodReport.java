package trading.domain.taxes;

import java.util.List;

public class TaxPeriodReport {
    private int year;
    private List<TaxPeriodProfitCategoryReport> profitCategories;

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public List<TaxPeriodProfitCategoryReport> getProfitCategories() {
        return profitCategories;
    }

    public void setProfitCategories(List<TaxPeriodProfitCategoryReport> profitCategories) {
        this.profitCategories = profitCategories;
    }
}
