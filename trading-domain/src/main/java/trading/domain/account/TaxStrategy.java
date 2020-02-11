package trading.domain.account;

import trading.domain.Amount;

public interface TaxStrategy {
    Amount calculateTaxImpact(Account account, Transaction transaction);
}
