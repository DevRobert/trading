package trading.domain.account;

import trading.domain.Amount;
import trading.domain.DomainException;
import trading.domain.taxes.ProfitCategory;

import java.time.LocalDate;

public class TaxPaymentTransaction extends Transaction {
    private final int taxPeriodYear;
    private final ProfitCategory profitCategory;
    private final Amount taxedProfit;
    private final Amount paidTaxes;

    TaxPaymentTransaction(LocalDate date, int taxPeriodYear, ProfitCategory profitCategory, Amount taxedProfit, Amount paidTaxes) {
        super(date);

        if(taxPeriodYear == 0) {
            throw new DomainException("The tax period year must be specified.");
        }

        if(profitCategory == null) {
            throw new DomainException("The profit category must be specified.");
        }

        if(taxedProfit == null) {
            throw new DomainException("The taxed profit must be specified.");
        }

        if(paidTaxes == null) {
            throw new DomainException("The paid taxes must be specified.");
        }

        this.taxPeriodYear = taxPeriodYear;
        this.profitCategory = profitCategory;
        this.taxedProfit = taxedProfit;
        this.paidTaxes = paidTaxes;
    }

    public int getTaxPeriodYear() {
        return this.taxPeriodYear;
    }

    public ProfitCategory getProfitCategory() {
        return this.profitCategory;
    }

    public Amount getTaxedProfit() {
        return this.taxedProfit;
    }

    public Amount getPaidTaxes() {
        return this.paidTaxes;
    }
}
