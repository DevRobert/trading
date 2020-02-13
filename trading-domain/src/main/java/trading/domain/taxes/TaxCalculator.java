package trading.domain.taxes;

import trading.domain.Amount;

public interface TaxCalculator {
    Amount calculateTaxes(Amount taxableProfit);
}
