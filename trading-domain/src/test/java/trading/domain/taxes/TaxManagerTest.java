package trading.domain.taxes;

import org.junit.Before;
import org.junit.Test;

public class TaxManagerTest {
    private TaxManager taxManager;

    @Before
    public void initializeTaxManager() {
        double dividendsTaxRate = 0.10;
        double saleTaxRate = 0.20;

        this.taxManager = new TaxManager();
    }

    @Test
    public void taxManagerReservesTaxesForSaleTransaction() {

    }

    @Test
    public void taxManagerReservesTaxesForDividendTransaction() {

    }

    @Test
    public void taxManagerAdjustsTaxReservationForTaxPaymentTransaction() {

    }
}
