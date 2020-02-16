package trading.api.account;

import trading.domain.taxes.TaxPeriodReport;
import trading.domain.taxes.TaxReport;

import java.util.ArrayList;
import java.util.List;

public class GetAccountTaxReportResponse {
    private List<TaxPeriodDto> taxPeriods;

    public GetAccountTaxReportResponse() {

    }

    public GetAccountTaxReportResponse(TaxReport taxReport) {
        this.taxPeriods = new ArrayList<>();

        for(TaxPeriodReport taxPeriodReport: taxReport.getTaxPeriods()) {
            this.taxPeriods.add(new TaxPeriodDto(taxPeriodReport));
        }
    }

    public List<TaxPeriodDto> getTaxPeriods() {
        return taxPeriods;
    }

    public void setTaxPeriods(List<TaxPeriodDto> taxPeriods) {
        this.taxPeriods = taxPeriods;
    }
}
