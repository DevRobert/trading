package trading.domain.taxes;

public abstract class ProfitCategories {
    public final static ProfitCategory Sale = new ProfitCategory("Sale");
    public final static ProfitCategory Dividends = new ProfitCategory("Dividends");
}
