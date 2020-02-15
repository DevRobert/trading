package trading.domain.account;

import trading.domain.Amount;
import trading.domain.taxes.ProfitCategory;

import java.time.LocalDate;

public class TaxPaymentTransactionBuilder
{
    private LocalDate date;
    private ProfitCategory profitCategory;
    private Amount taxedProfit;
    private Amount paidTaxes;

    public TaxPaymentTransactionBuilder setDate(LocalDate date) {
        this.date = date;
        return this;
    }

    public TaxPaymentTransactionBuilder setProfitCategory(ProfitCategory profitCategory) {
        this.profitCategory = profitCategory;
        return this;
    }

    public TaxPaymentTransactionBuilder setTaxedProfit(Amount taxedProfit) {
        this.taxedProfit = taxedProfit;
        return this;
    }

    public TaxPaymentTransactionBuilder setPaidTaxes(Amount paidTaxes) {
        this.paidTaxes = paidTaxes;
        return this;
    }

    public TaxPaymentTransaction build() {
        return new TaxPaymentTransaction(
                this.date,
                this.profitCategory,
                this.taxedProfit,
                this.paidTaxes
        );
    }
}
