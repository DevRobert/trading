package trading.domain.account;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import trading.domain.Amount;
import trading.domain.DomainException;
import trading.domain.ISIN;

import java.time.LocalDate;

public class DividendTransactionTest {
    private DividendTransactionBuilder dividendTransactionBuilder;

    @Before
    public void before() {
        this.dividendTransactionBuilder = new DividendTransactionBuilder()
                .setDate(LocalDate.of(2000, 1, 1))
                .setIsin(new ISIN("A"))
                .setAmount(new Amount(1000.0));
    }

    // Initialization

    @Test
    public void createDividendTransaction() {
        DividendTransaction dividendTransaction = this.dividendTransactionBuilder.build();

        Assert.assertEquals(LocalDate.of(2000, 1, 1), dividendTransaction.getDate());
        Assert.assertEquals(new ISIN("A"), dividendTransaction.getIsin());
        Assert.assertEquals(new Amount(1000.0), dividendTransaction.getAmount());
    }

    @Test
    public void initializationFails_ifDateNotSpecified() {
        this.dividendTransactionBuilder.setDate(null);

        try {
            this.dividendTransactionBuilder.build();
        }
        catch(DomainException e) {
            Assert.assertEquals("The date must be specified.", e.getMessage());
            return;
        }

        Assert.fail("DomainException expected.");
    }

    @Test
    public void initializationFails_ifIsinNotSpecified() {
        this.dividendTransactionBuilder.setIsin(null);

        try {
            this.dividendTransactionBuilder.build();
        }
        catch(DomainException e) {
            Assert.assertEquals("The ISIN must be specified.", e.getMessage());
            return;
        }

        Assert.fail("DomainException expected.");
    }

    @Test
    public void initializationFails_ifAmountNotSpecified() {
        this.dividendTransactionBuilder.setAmount(null);

        try {
            this.dividendTransactionBuilder.build();
        }
        catch(DomainException e) {
            Assert.assertEquals("The amount must be specified.", e.getMessage());
            return;
        }

        Assert.fail("DomainException expected.");
    }
}
