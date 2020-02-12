package trading.domain.taxes;

import trading.domain.Amount;

public class TaxabaleProfit {
    private final ProfitCategory taxableProfitCategory;
    private final Amount amount;

    public TaxabaleProfit(ProfitCategory taxableProfitCategory, Amount amount) {
        this.taxableProfitCategory = taxableProfitCategory;
        this.amount = amount;
    }

    public ProfitCategory getTaxableProfitCategory() {
        return this.taxableProfitCategory;
    }

    public Amount getAmount() {
        return this.amount;
    }
}
