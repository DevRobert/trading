package trading.domain.taxes;

import trading.domain.Amount;

public class TaxImpact {
    private final Amount addedReservedTaxes;
    private final Amount addedPaidTaxes;

    public TaxImpact(Amount addedReservedTaxes, Amount addedPaidTaxes) {
        this.addedReservedTaxes = addedReservedTaxes;
        this.addedPaidTaxes = addedPaidTaxes;
    }

    public Amount getAddedReservedTaxes() {
        return this.addedReservedTaxes;
    }

    public Amount getAddedPaidTaxes() {
        return this.addedPaidTaxes;
    }
}
