package trading.domain.taxes;

public class TaxReservation {
    private final ProfitCategory taxableProfitCategory;

    public TaxReservation(ProfitCategory taxableProfitCategory) {
        this.taxableProfitCategory = taxableProfitCategory;
    }
}
