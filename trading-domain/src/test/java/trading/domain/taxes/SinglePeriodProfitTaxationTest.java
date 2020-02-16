package trading.domain.taxes;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import trading.domain.Amount;
import trading.domain.DomainException;

/**
 * For various scenarios key figures are tested.
 *
 * Key Figures:
 *
 * - TaxableProfit: The profit to be taxed
 * - TaxedProfit: The profit that has been taxed (taxes have been paid)
 * - ReservedTaxes: The amount of money to be reserved for taxable profit
 * - PaidTaxes: The amount of taxes that have been paid
 *
 * Scenarios:
 *
 * 1) Simple Profit (taxable profit and reserved taxes should increase)
 * 2) Simple Loss (loss should be captured as negative taxable profit, no tax reservation)
 * 3) Profit after Loss (taxable profit should increase, tax reservation should only be established for amount over loss compensation)
 * 4) Loss after Profit (loss should be subtracted from taxable profit, tax reservation should be decreased)
 * 5) Tax Payment (taxable profit should shift to taxed profit, reserved taxes should shift to paid taxes)
 * 6) Loss after Taxed Profit (loss should be captured as negative taxable profit, no tax reservation)
 */
public class SinglePeriodProfitTaxationTest {
    private ProfitTaxation profitTaxation;

    @Before
    public void initialize() {
        TaxCalculator taxCalculator = new LinearTaxCalculator(0.25);
        this.profitTaxation = new ProfitTaxation(taxCalculator, null);
    }

    // Simple Profit

    private void registerSimpleProfit() {
        Amount profit = new Amount(1000.0);
        this.profitTaxation.registerProfit(profit);
    }

    @Test
    public void profitIncreasesTaxableProfit() {
        this.registerSimpleProfit();
        Assert.assertEquals(new Amount(1000.0), this.profitTaxation.getUntaxedTaxableProfitConsideringLossCarryforward());
    }

    @Test
    public void profitDoesNotAffectTaxedProfit() {
        this.registerSimpleProfit();
        Assert.assertEquals(Amount.Zero, this.profitTaxation.getTaxedProfit());
    }

    @Test
    public void profitIncreasesReservedTaxes() {
        this.registerSimpleProfit();
        Assert.assertEquals(new Amount(250.0), this.profitTaxation.getReservedTaxes());
    }

    @Test
    public void profitDoesNotAffectPaidTaxes() {
        this.registerSimpleProfit();
        Assert.assertEquals(Amount.Zero, this.profitTaxation.getPaidTaxes());
    }

    // Simple Loss

    private void registerSimpleLoss() {
        Amount loss = new Amount(-1000.0);
        this.profitTaxation.registerProfit(loss);
    }

    @Test
    public void lossDecreasesTaxableProfit() {
        this.registerSimpleLoss();
        Assert.assertEquals(new Amount(-1000.0), this.profitTaxation.getUntaxedTaxableProfitConsideringLossCarryforward());
    }

    @Test
    public void lossDoesNotAffectTaxedProfit() {
        this.registerSimpleLoss();
        Assert.assertEquals(Amount.Zero, this.profitTaxation.getTaxedProfit());
    }

    @Test
    public void noTaxReservationGiven_lossDoesNotAffectReservedTaxes() {
        this.registerSimpleLoss();
        Assert.assertEquals(Amount.Zero, this.profitTaxation.getReservedTaxes());
    }

    @Test
    public void lossDoesNotAffectPaidTaxes() {
        this.registerSimpleLoss();
        Assert.assertEquals(Amount.Zero, this.profitTaxation.getPaidTaxes());
    }

    // Profit after Loss

    private void registerLossAndProfit() {
        Amount loss = new Amount(-1000.0);
        this.profitTaxation.registerProfit(loss);

        Amount profit = new Amount(1500.0);
        this.profitTaxation.registerProfit(profit);
    }

    @Test
    public void profitIncreasesTaxableProfitAfterLoss() {
        this.registerLossAndProfit();
        Assert.assertEquals(new Amount(500.0), this.profitTaxation.getUntaxedTaxableProfitConsideringLossCarryforward());
    }

    @Test
    public void profitAfterLossDoesNotAffectTaxedProfit() {
        this.registerLossAndProfit();
        Assert.assertEquals(Amount.Zero, this.profitTaxation.getTaxedProfit());
    }

    @Test
    public void profitIncreasesReservedTaxesAfterLoss() {
        // Total profit: 500.00
        // Reserved Taxes 25% * 500.00 = 125.00

        this.registerLossAndProfit();
        Assert.assertEquals(new Amount(125.00), this.profitTaxation.getReservedTaxes());
    }

    @Test
    public void profitAfterLossDoesNotAffectPaidTaxes() {
        this.registerLossAndProfit();
        Assert.assertEquals(Amount.Zero, this.profitTaxation.getPaidTaxes());
    }

    // Loss after Profit

    private void registerProfitAndLoss() {
        Amount profit = new Amount(1500.0);
        this.profitTaxation.registerProfit(profit);

        Amount loss = new Amount(-2000.0);
        this.profitTaxation.registerProfit(loss);
    }

    @Test
    public void lossDecreasesTaxableProfitAfterLoss() {
        this.registerProfitAndLoss();
        Assert.assertEquals(new Amount(-500.0), this.profitTaxation.getUntaxedTaxableProfitConsideringLossCarryforward());
    }

    @Test
    public void lossAfterProfitDoesNotAffectTaxedProfit() {
        this.registerProfitAndLoss();
        Assert.assertEquals(Amount.Zero, this.profitTaxation.getTaxedProfit());
    }

    @Test
    public void lossDecreasesReservedTaxesAfterProfit() {
        this.registerProfitAndLoss();
        Assert.assertEquals(Amount.Zero, this.profitTaxation.getReservedTaxes());
    }

    @Test
    public void lossAfterProfitDoesNotAffectConfirmedTaxes() {
        this.registerProfitAndLoss();
        Assert.assertEquals(Amount.Zero, this.profitTaxation.getPaidTaxes());
    }

    private void registerProfitAndPayTaxes() {
        Amount profit = new Amount(1000.0);
        this.profitTaxation.registerProfit(profit);

        Amount taxedProfit = new Amount(600.0);
        Amount paidTaxes = new Amount(150.0);
        this.profitTaxation.registerTaxPayment(taxedProfit, paidTaxes);
    }

    @Test
    public void taxPaymentDecreasesTaxableProfit() {
        this.registerProfitAndPayTaxes();
        Assert.assertEquals(new Amount(400.0), this.profitTaxation.getUntaxedTaxableProfitConsideringLossCarryforward());
    }

    @Test
    public void taxPaymentIncreasesTaxedProfit() {
        this.registerProfitAndPayTaxes();
        Assert.assertEquals(new Amount(600.0), this.profitTaxation.getTaxedProfit());
    }

    @Test
    public void taxPaymentDecreasesReservedTaxes() {
        this.registerProfitAndPayTaxes();
        Assert.assertEquals(new Amount(100.0), this.profitTaxation.getReservedTaxes());
    }

    @Test
    public void taxPaymentIncreasesPaidTaxes() {
        this.registerProfitAndPayTaxes();
        Assert.assertEquals(new Amount(150.0), this.profitTaxation.getPaidTaxes());
    }

    // Loss after Taxed Profit

    private void registerTaxedProfitAndRegisterLoss() {
        Amount profit = new Amount(1000.0);
        this.profitTaxation.registerProfit(profit);

        Amount taxedProfit = new Amount(1000.0);
        Amount paidTaxes = new Amount(250.0);
        this.profitTaxation.registerTaxPayment(taxedProfit, paidTaxes);

        Amount loss = new Amount(-500.0);
        this.profitTaxation.registerProfit(loss);
    }

    @Test
    public void lossDecreasesTaxableProfit_independentFromAlreadyTaxedProfit() {
        this.registerTaxedProfitAndRegisterLoss();
        Assert.assertEquals(new Amount(-500.0), this.profitTaxation.getUntaxedTaxableProfitConsideringLossCarryforward());
    }

    @Test
    public void lossAfterTaxedProfitDoesNotAffectTaxedProfit() {
        this.registerTaxedProfitAndRegisterLoss();
        Assert.assertEquals(new Amount(1000.0), this.profitTaxation.getTaxedProfit());
    }

    @Test
    public void lossAfterTaxedProfitDoesNotAffectReservedTaxes() {
        this.registerTaxedProfitAndRegisterLoss();
        Assert.assertEquals(Amount.Zero, this.profitTaxation.getReservedTaxes());
    }

    @Test
    public void lossAfterTaxedProfitDoesNotAffectPaidTaxes() {
        this.registerTaxedProfitAndRegisterLoss();
        Assert.assertEquals(new Amount(250.0), this.profitTaxation.getPaidTaxes());
    }

    // Tax confirmation special cases

    @Test
    public void taxPaymentFailsIfPaidTaxesExceedReservedTaxes() {
        Amount profit = new Amount(1000.0);
        this.profitTaxation.registerProfit(profit);

        Amount taxedProfit = new Amount(1500.0);
        Amount paidTaxes = new Amount(375.0);

        try {
            this.profitTaxation.registerTaxPayment(taxedProfit, paidTaxes);
        }
        catch(DomainException e) {
            Assert.assertEquals("The specified taxed profit must not exceed the remaining untaxed taxable profit/ considering loss carryforward.", e.getMessage());
            return;
        }

        Assert.fail("DomainException expected.");
    }

    @Test
    public void taxPaymentFailsIfTaxableProfitIsNegative() {
        Amount loss = new Amount(-1000.0);
        this.profitTaxation.registerProfit(loss);

        Amount taxedProfit = new Amount(1000.0);
        Amount paidTaxes = new Amount(250.0);

        try {
            this.profitTaxation.registerTaxPayment(taxedProfit, paidTaxes);
        }
        catch(DomainException e) {
            Assert.assertEquals("The specified taxed profit must not exceed the remaining untaxed taxable profit/ considering loss carryforward.", e.getMessage());
            return;
        }

        Assert.fail("DomainException expected.");
    }

    @Test
    public void reservedTaxesDecreaseMoreThanPaidTaxesIncrease_ifTaxRateOfTaxPaymentIsLowerThanTaxReservationDueToTaxStrategy() {
        Amount profit = new Amount(1000.0); // Reserved taxes are 250.0 (tax rate 25%)
        this.profitTaxation.registerProfit(profit);

        Amount taxedProfit = new Amount(400.0);
        Amount paidTaxes = new Amount(40.0); // tax rate 10%
        this.profitTaxation.registerTaxPayment(taxedProfit, paidTaxes);

        // Reserved taxes for untaxed profit 600.0 are 125.0
        Assert.assertEquals(new Amount(150.0), this.profitTaxation.getReservedTaxes());
    }
}
