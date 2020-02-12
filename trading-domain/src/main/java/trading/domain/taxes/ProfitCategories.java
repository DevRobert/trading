package trading.domain.taxes;

public abstract class ProfitCategories {
    public static ProfitCategory Sale = new ProfitCategory("Sale");
    public static ProfitCategory Dividends = new ProfitCategory("Dividends");
}
