package trading.broker;

import trading.Amount;

public class ZeroCommissionStrategy implements CommissionStrategy {
    @Override
    public Amount calculateCommission(Amount totalPrice) {
        return Amount.Zero;
    }
}
