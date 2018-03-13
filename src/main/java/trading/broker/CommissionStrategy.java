package trading.broker;

import trading.Amount;

public interface CommissionStrategy {
    Amount calculateCommission(Amount totalPrice);
}
