package trading.domain.account;

import org.junit.Assert;
import org.junit.Test;
import trading.domain.Amount;
import trading.domain.DomainException;
import trading.domain.taxes.ProfitCategories;

import java.time.LocalDate;

public class TaxPaymentTransactionTest {
    @Test
    public void initializationFails_ifTaxPeriodYearNotSpecified() {
        try {
            new TaxPaymentTransaction(
                    LocalDate.of(2000, 1, 1),
                    0,
                    ProfitCategories.Sale,
                    new Amount(100.0),
                    new Amount(10.0)
            );
        }
        catch(DomainException e) {
            Assert.assertEquals("The tax period year must be specified.", e.getMessage());
            return;
        }

        Assert.fail("DomainException expected.");
    }

    @Test
    public void initializationFails_ifProfitCategoryNotSpecified() {
        try {
            new TaxPaymentTransaction(
                    LocalDate.of(2000, 1, 1),
                    1999,
                    null,
                    new Amount(100.0),
                    new Amount(10.0)
            );
        }
        catch(DomainException e) {
            Assert.assertEquals("The profit category must be specified.", e.getMessage());
            return;
        }

        Assert.fail("DomainException expected.");
    }

    @Test
    public void initializationFails_ifTaxedProfitNotSpecified() {
        try {
            new TaxPaymentTransaction(
                    LocalDate.of(2000, 1, 1),
                    1999,
                    ProfitCategories.Sale,
                    null,
                    new Amount(10.0)
            );
        }
        catch(DomainException e) {
            Assert.assertEquals("The taxed profit must be specified.", e.getMessage());
            return;
        }

        Assert.fail("DomainException expected.");
    }

    @Test
    public void initializationFails_ifPaidTaxesNotSpecified() {
        try {
            new TaxPaymentTransaction(
                    LocalDate.of(2000, 1, 1),
                    1999,
                    ProfitCategories.Sale,
                    new Amount(100.0),
                    null
            );
        }
        catch(DomainException e) {
            Assert.assertEquals("The paid taxes must be specified.", e.getMessage());
            return;
        }

        Assert.fail("DomainException expected.");
    }
}
