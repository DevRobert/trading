package trading.domain.taxes;

import trading.domain.Amount;

public class Profit {
    private final ProfitCategory profitCategory;
    private final Amount amount;

    public Profit(ProfitCategory profitCategory, Amount amount) {
        this.profitCategory = profitCategory;
        this.amount = amount;
    }

    public ProfitCategory getProfitCategory() {
        return profitCategory;
    }

    public Amount getAmount() {
        return amount;
    }
}
