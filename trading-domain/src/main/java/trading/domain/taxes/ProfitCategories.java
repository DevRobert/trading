package trading.domain.taxes;

public abstract class ProfitCategories {
    public final static ProfitCategory Sale = new ProfitCategory(1, "Sale");
    public final static ProfitCategory Dividends = new ProfitCategory(2, "Dividends");

    public static ProfitCategory fromId(int profitCategoryId) {
        if(profitCategoryId == Sale.getId()) {
            return Sale;
        }

        if(profitCategoryId == Dividends.getId()) {
            return Dividends;
        }

        throw new RuntimeException("Unknown profit category.");
    }
}
