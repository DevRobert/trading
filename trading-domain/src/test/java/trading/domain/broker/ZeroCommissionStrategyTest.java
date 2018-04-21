package trading.domain.broker;

import org.junit.Assert;
import org.junit.Test;
import trading.domain.Amount;

public class ZeroCommissionStrategyTest {
    @Test
    public void calculatesZeroCommissions() {
        CommissionStrategy commissionStrategy = new ZeroCommissionStrategy();
        Amount totalPrice = new Amount(1000.0);
        Amount commission = commissionStrategy.calculateCommission(totalPrice);
        Assert.assertEquals(Amount.Zero, commission);
    }
}
