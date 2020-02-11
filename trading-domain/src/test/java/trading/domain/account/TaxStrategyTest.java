package trading.domain.account;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import trading.domain.Amount;
import trading.domain.DomainException;
import trading.domain.ISIN;
import trading.domain.Quantity;

import java.time.LocalDate;

public class TaxStrategyTest {
    private Account account;

    @Before
    public void before() {
        this.account = new AccountBuilder()
                .setAvailableMoney(new Amount(10000.0))
                .setTaxStrategy(new TaxStrategyImpl(0.1))
                .build();
    }

    @Test
    public void retrieveTaxImpactOfUnknownTransactionFails() {
        Transaction buyTransaction = new MarketTransactionBuilder()
                .setTransactionType(TransactionType.Buy)
                .setDate(LocalDate.of(2000, 1, 1))
                .setIsin(ISIN.MunichRe)
                .setQuantity(new Quantity(1))
                .setTotalPrice(new Amount(1000.0))
                .setCommission(new Amount(0.0))
                .build();

        try {
            this.account.getTaxImpact(buyTransaction);
        }
        catch(DomainException e) {
            Assert.assertEquals("The given transaction is unknown to the account.", e.getMessage());
            return;
        }

        Assert.fail("DomainException expected.");
    }

    @Test
    public void taxImpactOfBuyTransactionsIsZero() {
        Transaction buyTransaction = new MarketTransactionBuilder()
                .setTransactionType(TransactionType.Buy)
                .setDate(LocalDate.of(2000, 1, 1))
                .setIsin(ISIN.MunichRe)
                .setQuantity(new Quantity(1))
                .setTotalPrice(new Amount(1000.0))
                .setCommission(new Amount(0.0))
                .build();

        this.account.registerTransaction(buyTransaction);

        Amount taxImpact = this.account.getTaxImpact(buyTransaction);

        Assert.assertEquals(Amount.Zero, taxImpact);
    }

    @Test
    public void taxImpactOfSellTransactionIsPositive_forPositiveSell() {
        // Buy for 1,000
        // Sell for 2,000
        // Difference: 1,000
        // Makes 100 tax impact given a tax rate of 10%

        Transaction buyTransaction = new MarketTransactionBuilder()
                .setTransactionType(TransactionType.Buy)
                .setDate(LocalDate.of(2000, 1, 1))
                .setIsin(ISIN.MunichRe)
                .setQuantity(new Quantity(1))
                .setTotalPrice(new Amount(1000.0))
                .setCommission(new Amount(0.0))
                .build();

        account.registerTransaction(buyTransaction);

        Transaction sellTransaction = new MarketTransactionBuilder()
                .setTransactionType(TransactionType.Sell)
                .setDate(LocalDate.of(2000, 1, 1))
                .setIsin(ISIN.MunichRe)
                .setQuantity(new Quantity(1))
                .setTotalPrice(new Amount(2000.0))
                .setCommission(new Amount(0.0))
                .build();

        account.registerTransaction(sellTransaction);

        Amount taxImpact = account.getTaxImpact(sellTransaction);

        Assert.assertEquals(new Amount(100.0), taxImpact);
    }

    @Test
    public void taxImpactOfBuyTransactionIsNegative_forNegativeSell() {
        // Buy for 1,000
        // Sell for 500
        // Difference: -500
        // Makes -50 tax impact given a tax rate of 10%

        Transaction buyTransaction = new MarketTransactionBuilder()
                .setTransactionType(TransactionType.Buy)
                .setDate(LocalDate.of(2000, 1, 1))
                .setIsin(ISIN.MunichRe)
                .setQuantity(new Quantity(1))
                .setTotalPrice(new Amount(1000.0))
                .setCommission(new Amount(0.0))
                .build();

        account.registerTransaction(buyTransaction);

        Transaction sellTransaction = new MarketTransactionBuilder()
                .setTransactionType(TransactionType.Sell)
                .setDate(LocalDate.of(2000, 1, 1))
                .setIsin(ISIN.MunichRe)
                .setQuantity(new Quantity(1))
                .setTotalPrice(new Amount(500.0))
                .setCommission(new Amount(0.0))
                .build();

        account.registerTransaction(sellTransaction);

        Amount taxImpact = account.getTaxImpact(sellTransaction);

        Assert.assertEquals(new Amount(-50.0), taxImpact);
    }

    @Test
    public void taxImpactOfSellTransactionIncludesBothBuyAndSellTransactionFee() {
        // Buy for 1,000 - 10 fees
        // Sell for 2,000 - 10 fees
        // Difference: 1,000 - 20 fees = 980
        // Makes 98 tax impact given a tax rate of 10%

        Transaction buyTransaction = new MarketTransactionBuilder()
                .setTransactionType(TransactionType.Buy)
                .setDate(LocalDate.of(2000, 1, 1))
                .setIsin(ISIN.MunichRe)
                .setQuantity(new Quantity(1))
                .setTotalPrice(new Amount(1000.0))
                .setCommission(new Amount(10.0))
                .build();

        account.registerTransaction(buyTransaction);

        Transaction sellTransaction = new MarketTransactionBuilder()
                .setTransactionType(TransactionType.Sell)
                .setDate(LocalDate.of(2000, 1, 1))
                .setIsin(ISIN.MunichRe)
                .setQuantity(new Quantity(1))
                .setTotalPrice(new Amount(2000.0))
                .setCommission(new Amount(10.0))
                .build();

        account.registerTransaction(sellTransaction);

        Amount taxImpact = account.getTaxImpact(sellTransaction);

        Assert.assertEquals(new Amount(98.0), taxImpact);
    }

    @Test
    public void taxImpactOfDividend() {
        // 100 dividend
        // makes 10 tax impact given 10% tax rate

        Transaction buyTransaction = new MarketTransactionBuilder()
                .setTransactionType(TransactionType.Buy)
                .setDate(LocalDate.of(2000, 1, 1))
                .setIsin(ISIN.MunichRe)
                .setQuantity(new Quantity(1))
                .setTotalPrice(new Amount(1000.0))
                .setCommission(new Amount(10.0))
                .build();

        this.account.registerTransaction(buyTransaction);

        Transaction dividendTransaction = new DividendTransactionBuilder()
                .setDate(LocalDate.of(2000, 1, 1))
                .setIsin(ISIN.MunichRe)
                .setAmount(new Amount(100.0))
                .build();

        this.account.registerTransaction(dividendTransaction);

        Amount taxImpact = this.account.getTaxImpact(dividendTransaction);

        Assert.assertEquals(new Amount(10.0), taxImpact);
    }
}
