package trading.domain.taxes;

import org.junit.Assert;
import org.junit.Test;
import trading.domain.Amount;
import trading.domain.DomainException;

public class LinearTaxCalculatorTest {
    @Test
    public void calculatesZeroTaxes() {
        double taxRate = 0.0;
        TaxCalculator taxCalculator = new LinearTaxCalculator(taxRate);

        Amount taxableProfit = new Amount(1000.0);
        Amount actualTaxes = taxCalculator.calculateTaxes(taxableProfit);

        Assert.assertEquals(new Amount(0.0), actualTaxes);
    }

    @Test
    public void calculatesTaxes() {
        double taxRate = 0.25;
        TaxCalculator taxCalculator = new LinearTaxCalculator(taxRate);

        Amount taxableProfit = new Amount(1000.0);
        Amount actualTaxes = taxCalculator.calculateTaxes(taxableProfit);

        Assert.assertEquals(new Amount(250.0), actualTaxes);
    }

    @Test
    public void initializationWithNegativeTaxRateFails() {
        double taxRate = -0.1;

        try {
            new LinearTaxCalculator(taxRate);
        }
        catch(DomainException e) {
            Assert.assertEquals("The tax rate must not be negative.", e.getMessage());
            return;
        }

        Assert.fail("DomainException expected.");
    }

    @Test
    public void refusesTaxCalculationForNegativeProfit() {
        double taxRate = 0.25;
        TaxCalculator taxCalculator = new LinearTaxCalculator(taxRate);

        Amount taxableProfit = new Amount(-1000.0);

        try{
            taxCalculator.calculateTaxes(taxableProfit);
        }
        catch(DomainException e) {
            Assert.assertEquals("The taxable profit must not be negative.", e.getMessage());
            return;
        }

        Assert.fail("DomainException expected.");
    }
}
