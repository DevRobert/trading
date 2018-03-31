package trading.strategy;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import trading.Amount;
import trading.Quantity;
import trading.broker.CommissionStrategy;
import trading.broker.ZeroCommissionStrategy;

public class AffordableQuantityCalculatorTest {
    private AffordableQuantityCalculator affordableQuantityCalculator;

    @Before
    public void before() {
        this.affordableQuantityCalculator = new AffordableQuantityCalculator();
    }

    // Without commissions

    @Test
    public void zeroQuantity_ifZeroMoney_andZeroCommission() {
        Amount availableMoney = new Amount(0.0);
        CommissionStrategy commissionStrategy = new ZeroCommissionStrategy();
        Amount lastMarketPrice = new Amount(1000.0);

        Quantity quantity = this.affordableQuantityCalculator.calculateAffordableQuantity(availableMoney, lastMarketPrice, commissionStrategy);

        Assert.assertEquals(0, quantity.getValue());
    }

    @Test
    public void zeroQuantity_ifMoneyForHalf_andZeroComission() {
        Amount availableMoney = new Amount(500.0);
        CommissionStrategy commissionStrategy = new ZeroCommissionStrategy();
        Amount lastMarketPrice = new Amount(1000.0);

        Quantity quantity = this.affordableQuantityCalculator.calculateAffordableQuantity(availableMoney, lastMarketPrice, commissionStrategy);

        Assert.assertEquals(0, quantity.getValue());
    }

    @Test
    public void oneQuantity_ifMoneyForExactOne_andZeroCommission() {
        Amount availableMoney = new Amount(1000.0);
        CommissionStrategy commissionStrategy = new ZeroCommissionStrategy();
        Amount lastMarketPrice = new Amount(1000.0);

        Quantity quantity = this.affordableQuantityCalculator.calculateAffordableQuantity(availableMoney, lastMarketPrice, commissionStrategy);

        Assert.assertEquals(1, quantity.getValue());
    }

    @Test
    public void oneQuantity_ifMoneyForOneAndHalf_andZeroCommission() {
        Amount availableMoney = new Amount(1500.0);
        CommissionStrategy commissionStrategy = new ZeroCommissionStrategy();
        Amount lastMarketPrice = new Amount(1000.0);

        Quantity quantity = this.affordableQuantityCalculator.calculateAffordableQuantity(availableMoney, lastMarketPrice, commissionStrategy);

        Assert.assertEquals(1, quantity.getValue());
    }

    @Test
    public void twoQuantity_ifMoneyForExactTwo_andZeroCommission() {
        Amount availableMoney = new Amount(2000.0);
        CommissionStrategy commissionStrategy = new ZeroCommissionStrategy();
        Amount lastMarketPrice = new Amount(1000.0);

        Quantity quantity = this.affordableQuantityCalculator.calculateAffordableQuantity(availableMoney, lastMarketPrice, commissionStrategy);

        Assert.assertEquals(2, quantity.getValue());
    }

    // With commissions

    @Test
    public void zeroQuantity_ifMoneyForExactOne_butNotForCommission() {
        Amount availableMoney = new Amount(1000.0);
        CommissionStrategy commissionStrategy = totalPrice -> new Amount(10.0);
        Amount lastMarketPrice = new Amount(1000.0);

        Quantity quantity = this.affordableQuantityCalculator.calculateAffordableQuantity(availableMoney, lastMarketPrice, commissionStrategy);

        Assert.assertEquals(0, quantity.getValue());
    }

    @Test
    public void oneQuantity_ifMoneyForExactOne_andCommission() {
        Amount availableMoney = new Amount(1010.0);
        CommissionStrategy commissionStrategy = totalPrice -> new Amount(10.0);
        Amount lastMarketPrice = new Amount(1000.0);

        Quantity quantity = this.affordableQuantityCalculator.calculateAffordableQuantity(availableMoney, lastMarketPrice, commissionStrategy);

        Assert.assertEquals(1, quantity.getValue());
    }

    @Test
    public void oneQuantity_ifMoneyForExactThree_butVeryHighCommission() {
        Amount availableMoney = new Amount(3000.0);
        CommissionStrategy commissionStrategy = totalPrice -> new Amount(2000.0);
        Amount lastMarketPrice = new Amount(1000.0);

        Quantity quantity = this.affordableQuantityCalculator.calculateAffordableQuantity(availableMoney, lastMarketPrice, commissionStrategy);

        Assert.assertEquals(1, quantity.getValue());
    }
}
