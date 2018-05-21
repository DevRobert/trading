package trading.domain.account;

import org.junit.Assert;
import org.junit.Test;
import trading.domain.Amount;
import trading.domain.DomainException;
import trading.domain.ISIN;
import trading.domain.Quantity;

import java.time.LocalDate;

public class MarketTransactionTest {
    // Initialization

    @Test
    public void initializationFails_ifTransactionTypeNotSpecified() {
        try {
            new MarketTransaction(null, ISIN.MunichRe, new Quantity(10), new Amount(1000.0), new Amount(20.0), LocalDate.now());
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
            new MarketTransaction(TransactionType.Buy, null, new Quantity(10), new Amount(1000.0), new Amount(20.0), LocalDate.now());
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
            new MarketTransaction(TransactionType.Buy, ISIN.MunichRe, null, new Amount(1000.0), new Amount(20.0), LocalDate.now());
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
            new MarketTransaction(TransactionType.Buy, ISIN.MunichRe, new Quantity(-1), totalPrice, commission, LocalDate.now());
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
            new MarketTransaction(TransactionType.Buy, ISIN.MunichRe, new Quantity(0), totalPrice, commission, LocalDate.now());
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
            new MarketTransaction(TransactionType.Buy, ISIN.MunichRe, new Quantity(1), null, new Amount(20.0), LocalDate.now());
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
            new MarketTransaction(TransactionType.Buy, ISIN.MunichRe, new Quantity(1), new Amount(1000.0), null, LocalDate.now());
        }
        catch(DomainException e) {
            Assert.assertEquals("The commission must be specified.", e.getMessage());
            return;
        }

        Assert.fail("DomainException expected.");
    }

    @Test
    public void initializationFails_ifDateNotSpecified() {
        try {
            new MarketTransaction(TransactionType.Buy, ISIN.MunichRe, new Quantity(1), new Amount(1000.0), new Amount(10.0), null);
        }
        catch(DomainException e) {
            Assert.assertEquals("The date must be specified.", e.getMessage());
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

        MarketTransaction transaction = new MarketTransaction(TransactionType.Buy, isin, new Quantity(10), totalPrice, commission, LocalDate.now());
        Assert.assertEquals("Buy 10 ISIN for total 10000.0 plus 500.0 commission", transaction.toString());
    }

    @Test
    public void toString_ifSell() {
        ISIN isin = new ISIN("ISIN");
        Amount totalPrice = new Amount(10000.0);
        Amount commission = new Amount(500.0);

        MarketTransaction transaction = new MarketTransaction(TransactionType.Sell, isin, new Quantity(10), totalPrice, commission, LocalDate.now());
        Assert.assertEquals("Sell 10 ISIN for total 10000.0 plus 500.0 commission", transaction.toString());
    }
}
