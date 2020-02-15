package trading.domain.account;

public abstract class TaxStrategies {
    private static TaxStrategy noTaxesStrategy;
    private static TaxStrategy defaultTaxStrategy;

    static {
        noTaxesStrategy = new TaxStrategyImpl(0.0);
        defaultTaxStrategy = new TaxStrategyImpl(0.26375);
    }

    public static TaxStrategy getNoTaxesStrategy() {
        return noTaxesStrategy;
    }

    public static TaxStrategy getDefaultTaxStrategy() {
        return defaultTaxStrategy;
    }
}
