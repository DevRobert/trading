package trading.domain.taxes;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import trading.domain.Amount;

/**
 * For various scenarios key figures are tested.
 *
 * Key Figures:
 *
 * - TaxableProfit: The profit to be taxed
 * - TaxedProfit: The profit that has been taxed (taxes have been confirmed)
 * - ReservedTaxes: The amount of money to be reserved for taxable profit
 * - ConfirmedTaxes: The amount of money that has been confirmed for paying taxes of taxed profit
 *
 * Scenarios:
 *
 * 1) Simple Profit (taxable profit and reserved taxes should increase)
 * 2) Simple Loss (loss should be captured as negative taxable profit, no tax reservation)
 * 3) Profit after Loss (taxable profit should increase, tax reservation should only be established for amount over loss compensation)
 * 4) Loss after Profit (loss should be subtracted from taxable profit, tax reservation should be decreased)
 * 5) Tax Confirmation (taxable profit should shift to taxed profit, reserved taxes should shift to confirmed taxes)
 * 6) Loss after Taxed Profit (loss should be captured as negative taxable profit, no tax reservation)
 */
public class ProfitTaxationTest {
    private ProfitTaxation profitTaxation;

    @Before
    public void initialize() {
        TaxCalculator taxCalculator = new LinearTaxCalculator(0.25);
        this.profitTaxation = new ProfitTaxation(taxCalculator);
    }

    // Simple Profit

    private void registerSimpleProfit() {
        Profit profit = new Profit(ProfitCategories.Sale, new Amount(1000.0));
        this.profitTaxation.registerProfit(profit);
    }

    @Test
    public void profitIncreasesTaxableProfit() {
        this.registerSimpleProfit();
        Assert.assertEquals(new Amount(250.0), this.profitTaxation.getTaxableProfit());
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
    public void profitDoesNotAffectConfirmedTaxes() {
        this.registerSimpleProfit();
        Assert.assertEquals(Amount.Zero, this.profitTaxation.getConfirmedTaxes());
    }

    // Simple Loss

    private void registerSimpleLoss() {
        Profit profit = new Profit(ProfitCategories.Sale, new Amount(-1000.0));
        this.profitTaxation.registerProfit(profit);
    }

    @Test
    public void lossDecreasesTaxableProfit() {
        this.registerSimpleLoss();
        Assert.assertEquals(new Amount(-1000.0), this.profitTaxation.getTaxableProfit());
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
    public void lossDoesNotAffectConfirmedTaxes() {
        this.registerSimpleLoss();
        Assert.assertEquals(Amount.Zero, this.profitTaxation.getConfirmedTaxes());
    }

    // Profit after Loss

    private void registerLossAndProfit() {

    }

    // Loss after Profit

    private void registerProfitAndLoss() {

    }

    // Tax confirmation

    private void registerProfitAndConfirmTaxes() {

    }

    // Loss after Taxed Profit

    private void registerProfitAndConfirmTaxesAndRegisterLoss() {

    }
}
