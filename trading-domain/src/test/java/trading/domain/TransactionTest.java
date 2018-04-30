package trading.domain;

import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDate;

public class TransactionTest {
    // Initialization

    @Test
    public void initializationFails_ifTransactionTypeNotSpecified() {
        try {
            new Transaction(null, ISIN.MunichRe, new Quantity(10), new Amount(1000.0), new Amount(20.0), LocalDate.now());
        }
        catch(DomainException ex) {
            Assert.assertEquals("The transaction type must be specified.", ex.getMessage());
            return;
        }

        Assert.fail("DomainException expected.");
    }

    @Test
    public void initializationFails_ifIsinNotSpecified() {
        try {
            new Transaction(TransactionType.Buy, null, new Quantity(10), new Amount(1000.0), new Amount(20.0), LocalDate.now());
        }
        catch(DomainException ex) {
            Assert.assertEquals("The ISIN must be specified.", ex.getMessage());
            return;
        }

        Assert.fail("DomainException expected.");
    }

    @Test
    public void initializationFails_ifQuantityNotSpecified() {
        try {
            new Transaction(TransactionType.Buy, ISIN.MunichRe, null, new Amount(1000.0), new Amount(20.0), LocalDate.now());
        }
        catch(DomainException ex) {
            Assert.assertEquals("The quantity must be specified.", ex.getMessage());
            return;
        }

        Assert.fail("DomainException expected.");
    }

    @Test
    public void initializationFails_ifQuantityNegative() {
        Amount totalPrice = new Amount(10000.0);
        Amount commission = new Amount(500.0);

        try {
            new Transaction(TransactionType.Buy, ISIN.MunichRe, new Quantity(-1), totalPrice, commission, LocalDate.now());
        }
        catch(DomainException ex) {
            Assert.assertEquals("The quantity must not be negative.", ex.getMessage());
            return;
        }

        Assert.fail("DomainException expected.");
    }

    @Test
    public void initializationFails_ifQuantityZero() {
        Amount totalPrice = new Amount(10000.0);
        Amount commission = new Amount(500.0);

        try {
            new Transaction(TransactionType.Buy, ISIN.MunichRe, new Quantity(0), totalPrice, commission, LocalDate.now());
        }
        catch(DomainException ex) {
            Assert.assertEquals("The quantity must not be zero.", ex.getMessage());
            return;
        }

        Assert.fail("DomainException expected.");
    }

    @Test
    public void initializationFails_ifTotalPriceNotSpecified() {
        try {
            new Transaction(TransactionType.Buy, ISIN.MunichRe, new Quantity(1), null, new Amount(20.0), LocalDate.now());
        }
        catch(DomainException e) {
            Assert.assertEquals("The total price must be specified.", e.getMessage());
            return;
        }

        Assert.fail("DomainException expected.");
    }

    @Test
    public void initialzationFails_ifCommissionNotSpecified() {
        try {
            new Transaction(TransactionType.Buy, ISIN.MunichRe, new Quantity(1), new Amount(1000.0), null, LocalDate.now());
        }
        catch(DomainException e) {
            Assert.assertEquals("The commission must be specified.", e.getMessage());
            return;
        }

        Assert.fail("DomainException expected.");
    }

    // toString

    @Test
    public void toString_ifBuy() {
        ISIN isin = new ISIN("ISIN");
        Amount totalPrice = new Amount(10000.0);
        Amount commission = new Amount(500.0);

        Transaction transaction = new Transaction(TransactionType.Buy, isin, new Quantity(10), totalPrice, commission, LocalDate.now());
        Assert.assertEquals("Buy 10 ISIN for total 10000.0 plus 500.0 commission", transaction.toString());
    }

    @Test
    public void toString_ifSell() {
        ISIN isin = new ISIN("ISIN");
        Amount totalPrice = new Amount(10000.0);
        Amount commission = new Amount(500.0);

        Transaction transaction = new Transaction(TransactionType.Sell, isin, new Quantity(10), totalPrice, commission, LocalDate.now());
        Assert.assertEquals("Sell 10 ISIN for total 10000.0 plus 500.0 commission", transaction.toString());
    }

    // date

    @Test
    public void setDateSucceeds_ifNotSet() {
        Transaction transaction = new TransactionBuilder()
                .setTransactionType(TransactionType.Buy)
                .setIsin(ISIN.MunichRe)
                .setQuantity(new Quantity(1))
                .setTotalPrice(new Amount(100.0))
                .setCommission(new Amount(5.0))
                .build();

        LocalDate date = LocalDate.now();

        transaction.setDate(date);

        Assert.assertEquals(date, transaction.getDate());
    }

    @Test
    public void setDateFails_ifAlreadySet() {
        Transaction transaction = new TransactionBuilder()
                .setTransactionType(TransactionType.Buy)
                .setIsin(ISIN.MunichRe)
                .setQuantity(new Quantity(1))
                .setTotalPrice(new Amount(100.0))
                .setCommission(new Amount(5.0))
                .setDate(LocalDate.now())
                .build();

        try {
            transaction.setDate(LocalDate.now());
        }
        catch(DomainException e) {
            Assert.assertEquals("The transaction date must not be changed.", e.getMessage());
            return;
        }

        Assert.fail("DomainException expected.");
    }
}
