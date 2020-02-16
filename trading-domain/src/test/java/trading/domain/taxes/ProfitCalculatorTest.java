package trading.domain.taxes;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import trading.domain.Amount;
import trading.domain.DomainException;
import trading.domain.ISIN;
import trading.domain.Quantity;
import trading.domain.account.*;

import java.time.LocalDate;

public class ProfitCalculatorTest {
    private ProfitCalculator profitCalculator;

    @Before
    public void initializeProfitCalculator() {
        this.profitCalculator = new ProfitCalculator();
    }

    // Profit of Buy Transaction

    @Test
    public void doesNotCalculateAnyProfitForBuy() {
        Transaction buyTransaction = new MarketTransactionBuilder()
                .setTransactionType(TransactionType.Buy)
                .setIsin(ISIN.MunichRe)
                .setQuantity(new Quantity(1))
                .setTotalPrice(new Amount(1000.0))
                .setCommission(new Amount(10.0))
                .setDate(LocalDate.of(2000, 1, 1))
                .build();

        Profit profit = this.profitCalculator.registerTransactionAndCalculateTransactionProfit(buyTransaction);

        Assert.assertNull(profit);
    }

    // Profit of Sell Transaction

    @Test
    public void calculatesProfitForSale() {
        Transaction buyTransaction = new MarketTransactionBuilder()
            .setTransactionType(TransactionType.Buy)
            .setIsin(ISIN.MunichRe)
            .setQuantity(new Quantity(1))
            .setTotalPrice(new Amount(1000.0))
            .setCommission(new Amount(10.0))
            .setDate(LocalDate.of(2000, 1, 1))
            .build();

        this.profitCalculator.registerTransactionAndCalculateTransactionProfit(buyTransaction);

        Transaction sellTransaction = new MarketTransactionBuilder()
            .setTransactionType(TransactionType.Sell)
            .setIsin(ISIN.MunichRe)
            .setQuantity(new Quantity(1))
            .setTotalPrice(new Amount(2000.0))
            .setCommission(new Amount(20.0))
            .setDate(LocalDate.of(2000, 1, 2))
            .build();

        // Profit: 2,000.0 - 1,000.0  - 10.0 - 20.0 = 970.0

        Profit profit = this.profitCalculator.registerTransactionAndCalculateTransactionProfit(sellTransaction);

        Assert.assertSame(ProfitCategories.Sale, profit.getProfitCategory());
        Assert.assertEquals(new Amount(970.0), profit.getAmount());
    }

    @Test
    public void rejectsConsecutiveBuyTransaction_currentLimitation() {
        Transaction buyTransaction1 = new MarketTransactionBuilder()
                .setTransactionType(TransactionType.Buy)
                .setIsin(ISIN.MunichRe)
                .setQuantity(new Quantity(1))
                .setTotalPrice(new Amount(1000.0))
                .setCommission(new Amount(10.0))
                .setDate(LocalDate.of(2000, 1, 1))
                .build();

        this.profitCalculator.registerTransactionAndCalculateTransactionProfit(buyTransaction1);

        Transaction buyTransaction2 = new MarketTransactionBuilder()
                .setTransactionType(TransactionType.Buy)
                .setIsin(ISIN.MunichRe)
                .setQuantity(new Quantity(1))
                .setTotalPrice(new Amount(1000.0))
                .setCommission(new Amount(10.0))
                .setDate(LocalDate.of(2000, 1, 1))
                .build();

        try {
            this.profitCalculator.registerTransactionAndCalculateTransactionProfit(buyTransaction2);
        }
        catch(DomainException e) {
            Assert.assertEquals("Consecutive buy transactions are not supported.", e.getMessage());
            return;
        }

        Assert.fail("DomainException expected.");
    }

    @Test
    public void rejectsPartialSellTransaction_currentLimitation() {
        Transaction buyTransaction = new MarketTransactionBuilder()
                .setTransactionType(TransactionType.Buy)
                .setIsin(ISIN.MunichRe)
                .setQuantity(new Quantity(2))
                .setTotalPrice(new Amount(1000.0))
                .setCommission(new Amount(10.0))
                .setDate(LocalDate.of(2000, 1, 1))
                .build();

        this.profitCalculator.registerTransactionAndCalculateTransactionProfit(buyTransaction);

        Transaction sellTransaction = new MarketTransactionBuilder()
                .setTransactionType(TransactionType.Sell)
                .setIsin(ISIN.MunichRe)
                .setQuantity(new Quantity(1))
                .setTotalPrice(new Amount(2000.0))
                .setCommission(new Amount(20.0))
                .setDate(LocalDate.of(2000, 1, 2))
                .build();

        try {
            this.profitCalculator.registerTransactionAndCalculateTransactionProfit(sellTransaction);
        }
        catch(DomainException e) {
            Assert.assertEquals("Partial sell transactions are not supported.", e.getMessage());
            return;
        }

        Assert.fail("DomainException expected.");
    }

    @Test
    public void calculatesProfitForSale_basedOnLatestBuyTransaction() {
        Transaction buyTransaction1 = new MarketTransactionBuilder()
                .setTransactionType(TransactionType.Buy)
                .setIsin(ISIN.MunichRe)
                .setQuantity(new Quantity(1))
                .setTotalPrice(new Amount(1000.0))
                .setCommission(Amount.Zero)
                .setDate(LocalDate.of(2000, 1, 1))
                .build();

        this.profitCalculator.registerTransactionAndCalculateTransactionProfit(buyTransaction1);

        Transaction sellTransaction1 = new MarketTransactionBuilder()
                .setTransactionType(TransactionType.Sell)
                .setIsin(ISIN.MunichRe)
                .setQuantity(new Quantity(1))
                .setTotalPrice(new Amount(2000.0))
                .setCommission(Amount.Zero)
                .setDate(LocalDate.of(2000, 1, 1))
                .build();

        this.profitCalculator.registerTransactionAndCalculateTransactionProfit(sellTransaction1);

        Transaction buyTransaction2 = new MarketTransactionBuilder()
                .setTransactionType(TransactionType.Buy)
                .setIsin(ISIN.MunichRe)
                .setQuantity(new Quantity(1))
                .setTotalPrice(new Amount(1500.0))
                .setCommission(Amount.Zero)
                .setDate(LocalDate.of(2000, 1, 1))
                .build();

        this.profitCalculator.registerTransactionAndCalculateTransactionProfit(buyTransaction2);

        Transaction sellTransaction2 = new MarketTransactionBuilder()
                .setTransactionType(TransactionType.Sell)
                .setIsin(ISIN.MunichRe)
                .setQuantity(new Quantity(1))
                .setTotalPrice(new Amount(3000.0))
                .setCommission(Amount.Zero)
                .setDate(LocalDate.of(2000, 1, 1))
                .build();

        Profit profit = this.profitCalculator.registerTransactionAndCalculateTransactionProfit(sellTransaction2);

        Assert.assertEquals(new Amount(1500.0), profit.getAmount());
    }

    @Test
    public void calculatesProfitForSale_basedOnRespectiveIsinBuyTransaction() {
        Transaction buyTransaction1 = new MarketTransactionBuilder()
                .setTransactionType(TransactionType.Buy)
                .setIsin(ISIN.MunichRe)
                .setQuantity(new Quantity(1))
                .setTotalPrice(new Amount(1000.0))
                .setCommission(Amount.Zero)
                .setDate(LocalDate.of(2000, 1, 1))
                .build();

        this.profitCalculator.registerTransactionAndCalculateTransactionProfit(buyTransaction1);

        Transaction buyTransaction2 = new MarketTransactionBuilder()
                .setTransactionType(TransactionType.Buy)
                .setIsin(ISIN.Allianz)
                .setQuantity(new Quantity(1))
                .setTotalPrice(new Amount(2000.0))
                .setCommission(Amount.Zero)
                .setDate(LocalDate.of(2000, 1, 1))
                .build();

        this.profitCalculator.registerTransactionAndCalculateTransactionProfit(buyTransaction2);

        Transaction sellTransaction = new MarketTransactionBuilder()
                .setTransactionType(TransactionType.Sell)
                .setIsin(ISIN.MunichRe)
                .setQuantity(new Quantity(1))
                .setTotalPrice(new Amount(1200.0))
                .setCommission(Amount.Zero)
                .setDate(LocalDate.of(2000, 1, 1))
                .build();

        Profit profit = this.profitCalculator.registerTransactionAndCalculateTransactionProfit(sellTransaction);

        Assert.assertEquals(new Amount(200.0), profit.getAmount());
    }

    // Profit of Dividend Transaction

    @Test
    public void calculatesProfitForDividend() {
        Transaction dividendTransaction = new DividendTransactionBuilder()
                .setIsin(ISIN.MunichRe)
                .setAmount(new Amount(1000.0))
                .setDate(LocalDate.of(2000, 1, 1))
                .build();

        Profit profit = this.profitCalculator.registerTransactionAndCalculateTransactionProfit(dividendTransaction);

        Assert.assertSame(ProfitCategories.Dividends, profit.getProfitCategory());
        Assert.assertEquals(new Amount(1000.0), profit.getAmount());
    }

    // Profit of Tax Payment Transaction

    @Test
    public void doesNotCalculateAnyProfitForTaxPayment() {
        TaxPaymentTransaction taxPaymentTransaction = new TaxPaymentTransactionBuilder()
                .setProfitCategory(ProfitCategories.Sale)
                .setTaxPeriodYear(2000)
                .setTaxedProfit(new Amount(1000.0))
                .setPaidTaxes(new Amount(100.0))
                .setDate(LocalDate.of(2000, 1, 1))
                .build();

        Profit profit = this.profitCalculator.registerTransactionAndCalculateTransactionProfit(taxPaymentTransaction);

        Assert.assertNull(profit);
    }
}
