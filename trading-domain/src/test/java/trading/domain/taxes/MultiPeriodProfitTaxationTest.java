package trading.domain.taxes;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import trading.domain.Amount;
import trading.domain.DomainException;

public class MultiPeriodProfitTaxationTest {
    private ProfitTaxation profitTaxation_period1;
    private ProfitTaxation profitTaxation_period2;
    private ProfitTaxation profitTaxation_period3;

    @Before
    public void initialize() {
        TaxCalculator taxCalculator = new LinearTaxCalculator(0.10);

        this.profitTaxation_period1 = new ProfitTaxation(taxCalculator, null);
        this.profitTaxation_period2 = new ProfitTaxation(taxCalculator, profitTaxation_period1);
        this.profitTaxation_period3 = new ProfitTaxation(taxCalculator, profitTaxation_period2);
    }

    @Test
    public void lossCarryforwardForNextPeriodIsZero_forProfit() {
        this.profitTaxation_period1.registerProfit(new Amount(1000.0));
        Assert.assertEquals(Amount.Zero, this.profitTaxation_period1.getLossCarryforwardForNextPeriod());
    }

    @Test
    public void lossCarryforwardForNextPeriodEqualsAmountOfLoss_forLoss() {
        this.profitTaxation_period1.registerProfit(new Amount(-1000.0));
        Assert.assertEquals(new Amount(1000.0), this.profitTaxation_period1.getLossCarryforwardForNextPeriod());
    }

    @Test
    public void lossCarryforwardIsAppliedToTaxableProfitAfterLossCarryforward() {
        this.profitTaxation_period1.registerProfit(new Amount(-1000.0));
        this.profitTaxation_period2.registerProfit(new Amount(500.0));
        Assert.assertEquals(new Amount(-500.0), this.profitTaxation_period2.getUntaxedTaxableProfitConsideringLossCarryforward());
    }

    @Test
    public void lossIsCarriedForwardOverMultiplePeriods() {
        this.profitTaxation_period1.registerProfit(new Amount(-1000.0));
        this.profitTaxation_period2.registerProfit(new Amount(-500.0));
        Assert.assertEquals(new Amount(1500.0), this.profitTaxation_period3.getLossCarryforward());
    }

    @Test
    public void taxesAreReservedConsideringLossCarryforward() {
        this.profitTaxation_period1.registerProfit(new Amount(-1000.0));
        this.profitTaxation_period2.registerProfit(new Amount(2000.0));
        Assert.assertEquals(new Amount(100.0), this.profitTaxation_period2.getReservedTaxes());
    }

    @Test
    public void taxPaymentFailsIfTaxedProfitExceedsTaxableProfitAfterCarryforward() {
        this.profitTaxation_period1.registerProfit(new Amount(-1000.0));
        this.profitTaxation_period2.registerProfit(new Amount(500.0));

        Amount taxedProfit = new Amount(500.0);
        Amount paidTaxes = new Amount(50.0);

        try {
            this.profitTaxation_period2.registerTaxPayment(taxedProfit, paidTaxes);
        }
        catch(DomainException e) {
            Assert.assertEquals("The specified taxed profit must not exceed the remaining untaxed taxable profit/ considering loss carryforward.", e.getMessage());
            return;
        }

        Assert.fail("DomainException expected.");
    }
}
