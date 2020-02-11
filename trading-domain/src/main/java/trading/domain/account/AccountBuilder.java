package trading.domain.account;

import trading.domain.Amount;

public class AccountBuilder {
    private Amount availableMoney;
    private TaxStrategy taxStrategy;

    public AccountBuilder setAvailableMoney(Amount availableMoney) {
        this.availableMoney = availableMoney;
        return this;
    }

    public AccountBuilder setTaxStrategy(TaxStrategy taxStrategy) {
        this.taxStrategy = taxStrategy;
        return this;
    }

    public Account build() {
        return new Account(this.availableMoney, this.taxStrategy);
    }
}
