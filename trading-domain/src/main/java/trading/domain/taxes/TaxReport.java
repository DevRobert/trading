package trading.domain.taxes;

import java.util.List;

public class TaxReport {
    private List<TaxPeriodReport> taxPeriods;

    public List<TaxPeriodReport> getTaxPeriods() {
        return taxPeriods;
    }

    public void setTaxPeriods(List<TaxPeriodReport> taxPeriods) {
        this.taxPeriods = taxPeriods;
    }
}
