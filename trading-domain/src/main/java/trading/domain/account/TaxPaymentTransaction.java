package trading.domain.account;

import trading.domain.Amount;
import trading.domain.taxes.ProfitCategory;

import java.time.LocalDate;

public class TaxPaymentTransaction extends Transaction {
    private final ProfitCategory profitCategory;
    private final Amount taxedProfit;
    private final Amount paidTaxes;

    public TaxPaymentTransaction(LocalDate date, ProfitCategory profitCategory, Amount taxedProfit, Amount paidTaxes) {
        super(date);

        this.profitCategory = profitCategory;
        this.taxedProfit = taxedProfit;
        this.paidTaxes = paidTaxes;
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
