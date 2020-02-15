package trading.domain.account;

import trading.domain.taxes.LinearTaxCalculator;
import trading.domain.taxes.TaxCalculator;

public class TaxStrategyImpl implements TaxStrategy {
    private final TaxCalculator taxCalculator;

    public TaxStrategyImpl(double taxRate) {
        this.taxCalculator = new LinearTaxCalculator(taxRate);
    }


    @Override
    public TaxCalculator getSaleTaxCalculator() {
        return this.taxCalculator;
    }

    @Override
    public TaxCalculator getDividendTaxCalculator() {
        return this.taxCalculator;
    }
}
