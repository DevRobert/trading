package trading.domain.account;

import trading.domain.Amount;

public abstract class TaxStrategies {
    private static TaxStrategy noTaxesStrategy;
    private static TaxStrategy defaultTaxStrategy;

    static {
        noTaxesStrategy = (account, transaction) -> Amount.Zero;
        defaultTaxStrategy = new TaxStrategyImpl(0.26375);
    }

    public static TaxStrategy getNoTaxesStrategy() {
        return noTaxesStrategy;
    }

    public static TaxStrategy getDefaultTaxStrategy() {
        return defaultTaxStrategy;
    }
}
