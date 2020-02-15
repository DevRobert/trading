package trading.domain.account;

import trading.domain.taxes.TaxCalculator;

public interface TaxStrategy {
    TaxCalculator getSaleTaxCalculator();
    TaxCalculator getDividendTaxCalculator();
}
