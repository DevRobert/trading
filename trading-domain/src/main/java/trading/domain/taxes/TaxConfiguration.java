package trading.domain.taxes;

public interface TaxConfiguration
{
    TaxCalculator getSaleTaxCalculator();
    TaxCalculator getDividendTaxCalculator();
}
