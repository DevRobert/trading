package trading.domain.taxes;

import trading.domain.Amount;
import trading.domain.account.TaxStrategy;

import java.util.ArrayList;

public class TaxPeriod
{
    private final int year;
    private final ProfitTaxation saleProfitTaxation;
    private final ProfitTaxation dividendProfitTaxation;

    public TaxPeriod(int year, TaxPeriod previousTaxPeriod, TaxStrategy taxStrategy) {
        this.year = year;

        ProfitTaxation previousTaxPeriodSaleProfitTaxation = null;
        ProfitTaxation previousTaxPeriodDividendProfitTaxation = null;

        if(previousTaxPeriod != null) {
            previousTaxPeriodSaleProfitTaxation = previousTaxPeriod.saleProfitTaxation;
            previousTaxPeriodDividendProfitTaxation = previousTaxPeriod.dividendProfitTaxation;
        }

        this.saleProfitTaxation = new ProfitTaxation(
                taxStrategy.getSaleTaxCalculator(),
                previousTaxPeriodSaleProfitTaxation
        );

        this.dividendProfitTaxation = new ProfitTaxation(
                taxStrategy.getDividendTaxCalculator(),
                previousTaxPeriodDividendProfitTaxation
        );
    }

    public int getYear() {
        return this.year;
    }

    public void registerProfit(Profit profit) {
        if(profit.getProfitCategory() == ProfitCategories.Sale) {
            this.saleProfitTaxation.registerProfit(profit.getAmount());
        }
        else if(profit.getProfitCategory() == ProfitCategories.Dividends) {
            this.dividendProfitTaxation.registerProfit(profit.getAmount());
        }
        else {
            throw new RuntimeException("Unknown profit category.");
        }
    }

    public void registerTaxPayment(ProfitCategory profitCategory, Amount taxedProfit, Amount paidTaxes) {
        if(profitCategory == ProfitCategories.Sale) {
            this.saleProfitTaxation.registerTaxPayment(taxedProfit, paidTaxes);
        }
        else if(profitCategory == ProfitCategories.Dividends) {
            this.dividendProfitTaxation.registerTaxPayment(taxedProfit, paidTaxes);
        }
        else {
            throw new RuntimeException("Unknown profit category.");
        }
    }

    public Amount getReservedTaxes() {
        return this.saleProfitTaxation.getReservedTaxes()
                .add(this.dividendProfitTaxation.getReservedTaxes());
    }

    public Amount getPaidTaxes() {
        return this.saleProfitTaxation.getPaidTaxes()
                .add(this.dividendProfitTaxation.getPaidTaxes());
    }

    public TaxPeriodReport buildTaxPeriodReport() {
        TaxPeriodReport taxPeriodReport = new TaxPeriodReport();

        taxPeriodReport.setYear(this.year);

        taxPeriodReport.setProfitCategories(new ArrayList<>());
        taxPeriodReport.getProfitCategories().add(this.saleProfitTaxation.buildTaxPeriodProfitCategoryReport(ProfitCategories.Sale));
        taxPeriodReport.getProfitCategories().add(this.dividendProfitTaxation.buildTaxPeriodProfitCategoryReport(ProfitCategories.Dividends));

        return taxPeriodReport;
    }
}
