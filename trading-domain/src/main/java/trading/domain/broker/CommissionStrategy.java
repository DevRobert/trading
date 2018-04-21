package trading.domain.broker;

import trading.domain.Amount;

public interface CommissionStrategy {
    Amount calculateCommission(Amount totalPrice);
}
