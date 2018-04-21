package trading.domain.broker;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import trading.domain.Amount;

public class DynamicCommissionStrategyTest {
    private DynamicCommissionStrategyParametersBuilder parametersBuilder;

    @Before
    public void before() {
        this.parametersBuilder = new DynamicCommissionStrategyParametersBuilder();
    }

    private CommissionStrategy createCommissionStrategy() {
        return new DynamicCommissionStrategy(this.parametersBuilder.build());
    }

    @Test
    public void constructionFailsIfNoParametersSpecified() {
        DynamicCommissionStrategyParameters parameters = null;

        try {
            new DynamicCommissionStrategy(parameters);
        }
        catch(RuntimeException ex) {
            Assert.assertEquals("The parameters must be specified.", ex.getMessage());
            return;
        }

        Assert.fail("RuntimeException expected.");
    }

    @Test
    public void constructionFailsIfMinimumVariableAmountGreaterThanMaximumVariableAmount() {
        this.parametersBuilder.setMinimumVariableAmount(new Amount(10.0));
        this.parametersBuilder.setMaximumVariableAmount(new Amount(5.0));

        try {
            this.createCommissionStrategy();
        }
        catch(RuntimeException ex) {
            Assert.assertEquals("The minimum variable amount must not be greater than the maximum variable amount.", ex.getMessage());
            return;
        }

        Assert.fail("RuntimeException expected.");
    }

    @Test
    public void zeroCommissions() {
        CommissionStrategy commissionStrategy = this.createCommissionStrategy();

        Amount totalPrice = new Amount(1000.0);

        Amount commission = commissionStrategy.calculateCommission(totalPrice);

        Assert.assertEquals(Amount.Zero, commission);
    }

    @Test
    public void fixedAmount() {
        this.parametersBuilder.setFixedAmount(new Amount(100.0));
        CommissionStrategy commissionStrategy = this.createCommissionStrategy();

        Amount totalPrice = new Amount(1000.0);

        Amount commission = commissionStrategy.calculateCommission(totalPrice);
        Assert.assertEquals(new Amount(100.0), commission);
    }


    @Test
    public void variableAmount_withoutMinimumAndMaximum() {
        this.parametersBuilder.setVariableAmountRate(0.01);
        CommissionStrategy commissionStrategy = this.createCommissionStrategy();

        Amount totalPrice = new Amount(1000.0);

        Amount commission = commissionStrategy.calculateCommission(totalPrice);
        Assert.assertEquals(new Amount(10.0), commission);
    }

    @Test
    public void variableAmount_betweenMinimumAndMaximum() {
        this.parametersBuilder.setVariableAmountRate(0.01);
        this.parametersBuilder.setMinimumVariableAmount(new Amount(5.0));
        this.parametersBuilder.setMaximumVariableAmount(new Amount(15.0));

        CommissionStrategy commissionStrategy = this.createCommissionStrategy();

        Amount totalPrice = new Amount(1000.0);

        Amount commission = commissionStrategy.calculateCommission(totalPrice);
        Assert.assertEquals(new Amount(10.0), commission);
    }

    @Test
    public void variableAmount_lowerThanMinimum() {
        this.parametersBuilder.setVariableAmountRate(0.01);
        this.parametersBuilder.setMinimumVariableAmount(new Amount(5.0));
        this.parametersBuilder.setMaximumVariableAmount(new Amount(15.0));

        CommissionStrategy commissionStrategy = this.createCommissionStrategy();

        Amount totalPrice = new Amount(100.0);

        Amount commission = commissionStrategy.calculateCommission(totalPrice);
        Assert.assertEquals(new Amount(5.0), commission);
    }

    @Test
    public void variableAmount_greaterThanMaximum() {
        this.parametersBuilder.setVariableAmountRate(0.01);
        this.parametersBuilder.setMinimumVariableAmount(new Amount(5.0));
        this.parametersBuilder.setMaximumVariableAmount(new Amount(15.0));

        CommissionStrategy commissionStrategy = this.createCommissionStrategy();

        Amount totalPrice = new Amount(10000.0);

        Amount commission = commissionStrategy.calculateCommission(totalPrice);
        Assert.assertEquals(new Amount(15.0), commission);
    }

    @Test
    public void combinedFixedAndVariableAmount() {
        this.parametersBuilder.setFixedAmount(new Amount(10.0));
        this.parametersBuilder.setVariableAmountRate(0.01);
        CommissionStrategy commissionStrategy = this.createCommissionStrategy();

        Amount totalPrice = new Amount(1000.0);

        Amount commission = commissionStrategy.calculateCommission(totalPrice);
        Assert.assertEquals(new Amount(20.0), commission);
    }
}
