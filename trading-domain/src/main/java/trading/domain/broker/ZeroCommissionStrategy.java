package trading.domain.broker;

import trading.domain.Amount;

public class ZeroCommissionStrategy implements CommissionStrategy {
    @Override
    public Amount calculateCommission(Amount totalPrice) {
        return Amount.Zero;
    }
}
