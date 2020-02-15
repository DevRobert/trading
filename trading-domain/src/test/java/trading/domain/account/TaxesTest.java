package trading.domain.account;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import trading.domain.Amount;
import trading.domain.ISIN;
import trading.domain.Quantity;
import trading.domain.taxes.ProfitCategories;

import java.time.LocalDate;

public class TaxesTest extends AccountTestBase {
    @Before
    public void initialize() {
        // TODO set tax rate 10% sale, 20% dividend
    }

    // Sale

    private void registerBuyAndSale_withProfit970() {
        Transaction buyTransaction = new MarketTransactionBuilder()
                .setTransactionType(TransactionType.Buy)
                .setIsin(ISIN.MunichRe)
                .setQuantity(new Quantity(1))
                .setTotalPrice(new Amount(1000.0))
                .setCommission(new Amount(10.0))
                .setDate(LocalDate.of(2000, 1, 1))
                .build();

        this.account.registerTransaction(buyTransaction);

        Transaction sellTransaction = new MarketTransactionBuilder()
                .setTransactionType(TransactionType.Sell)
                .setIsin(ISIN.MunichRe)
                .setQuantity(new Quantity(1))
                .setTotalPrice(new Amount(2000.0))
                .setCommission(new Amount(20.0))
                .setDate(LocalDate.of(2000, 1, 1))
                .build();

        this.account.registerTransaction(sellTransaction);

        // Seed capital: 10,000.0
        // Sale profit: 2,000.0 - 1,000.0 - 10.0 - 20.0 = 970.0
        // Available money after sale/ before taxes: 10,970.0
        // Taxes: 970.0 * 10% = 97.0
        // Available money after sale/ after taxes: 10,873.0
    }

    @Test
    public void forSale_taxesAreReserved() {
        this.registerBuyAndSale_withProfit970();
        Assert.assertEquals(new Amount(97.0), account.getReservedTaxes());
    }

    @Test
    public void forSale_noTaxPaymentIsRegistered() {
        this.registerBuyAndSale_withProfit970();
        Assert.assertEquals(Amount.Zero, account.getPaidTaxes());
    }

    @Test
    public void forSale_availableMoneyDecreasesByReservedTaxes() {
        this.registerBuyAndSale_withProfit970();
        Assert.assertEquals(new Amount(10873.0), account.getAvailableMoney());
    }

    @Test
    public void forSale_balanceDecreasesByReservedTaxes() {
        this.registerBuyAndSale_withProfit970();
        Assert.assertEquals(new Amount(10873.0), account.getBalance());
    }

    // Dividend

    private void registerBuy_for1000_commission10_andDividend100() {
        Transaction buyTransaction = new MarketTransactionBuilder()
                .setTransactionType(TransactionType.Buy)
                .setIsin(ISIN.MunichRe)
                .setQuantity(new Quantity(1))
                .setTotalPrice(new Amount(1000.0))
                .setCommission(new Amount(10.0))
                .setDate(LocalDate.of(2000, 1, 1))
                .build();

        this.account.registerTransaction(buyTransaction);

        Transaction dividendTransaction = new DividendTransactionBuilder()
                .setIsin(ISIN.MunichRe)
                .setAmount(new Amount(100.0))
                .setDate(LocalDate.of(2000, 1, 1))
                .build();

        this.account.registerTransaction(dividendTransaction);

        // Seed capital: 10,000.0
        // Dividend: 100.0
        // Dividend taxes: 100.0 * 20% = 20.0
        // Available money: 10,000.0 - 1,000.0 - 10.0 + 100.0 - 20.0 = 9,070.0
        // Balance: 10,000 - 10.0 + 100.0 - 20.0 = 10,070.0
    }

    @Test
    public void forDividend_taxesAreReserved() {
        this.registerBuy_for1000_commission10_andDividend100();
        Assert.assertEquals(new Amount(20.0), this.account.getReservedTaxes());
    }

    @Test
    public void forDividend_noTaxPaymentIsRegistered() {
        this.registerBuy_for1000_commission10_andDividend100();
        Assert.assertEquals(Amount.Zero, this.account.getPaidTaxes());
    }

    @Test
    public void forDividend_availableMoneyDecreasesByReservedTaxes() {
        this.registerBuy_for1000_commission10_andDividend100();
        Assert.assertEquals(new Amount(9070.0), this.account.getAvailableMoney());
    }

    @Test
    public void forDividend_balanceDecreasesByReservedTaxes() {
        this.registerBuy_for1000_commission10_andDividend100();
        Assert.assertEquals(new Amount(10070.0), this.account.getBalance());
    }

    // Profit Taxes are decreased by Loss

    @Test
    public void forSaleLossAfterSaleProfit_reservedTaxesDecrease() {
        this.registerBuyAndSale_withProfit970();

        // Profit: 970.0
        // Sale tax rate: 10%
        // Reserved taxes: 97.0

        Transaction buyTransaction2 = new MarketTransactionBuilder()
                .setTransactionType(TransactionType.Buy)
                .setIsin(ISIN.MunichRe)
                .setQuantity(new Quantity(1))
                .setTotalPrice(new Amount(1000.0))
                .setCommission(new Amount(10.0))
                .setDate(LocalDate.of(2000, 1, 3))
                .build();

        this.account.registerTransaction(buyTransaction2);

        Transaction sellTransaction2 = new MarketTransactionBuilder()
                .setTransactionType(TransactionType.Sell)
                .setIsin(ISIN.MunichRe)
                .setQuantity(new Quantity(1))
                .setTotalPrice(new Amount(500.0))
                .setCommission(new Amount(5.0))
                .setDate(LocalDate.of(2000, 1, 4))
                .build();

        this.account.registerTransaction(sellTransaction2);

        // Loss: 500.0 - 1,000.0 - 10.0 - 5.0 = -515.0
        // Total profit: 455.0
        // Sale tax rate: 10%
        // Reserved taxes: 45.5

        Assert.assertEquals(new Amount(45.0), this.account.getReservedTaxes());
    }

    // Dividend Taxes are not decreases by Loss

    @Test
    public void forSaleLossAfterDividend_reservedTaxesDoNotDecrease() {
        this.registerBuy_for1000_commission10_andDividend100();
        // Dividends tax rate: 20%
        // Reserved taxes: 20% * 100.0 = 20.0

        Transaction sellTransaction = new MarketTransactionBuilder()
                .setTransactionType(MarketTransactionType.Sell)
                .setIsin(ISIN.MunichRe)
                .setQuantity(new Quantity(1))
                .setTotalPrice(new Amount(500.0))
                .setCommission(new Amount(5.0))
                .setDate(LocalDate.of(2000, 1, 5))
                .build();

        this.account.registerTransaction(sellTransaction);

        // Loss: 500.0 - 1,000.0 - 10.0 - 5.0 = -515.0

        Assert.assertEquals(new Amount(20.0), this.account.getReservedTaxes());
    }

    // Tax Payment - Paid Taxes match Reserved Taxes

    private void registerBuyAndSale_withProfit970_andRegisterMatchingTaxPayment97() {
        this.registerBuyAndSale_withProfit970();

        // Seed capital 10,000.0
        // Profit 970.0
        // Tax rate for sale: 10%
        // Reserved taxes: 97.0
        // Available money/ balance: 10,000.0 + 970.0 - 97.0 = 10,873.0

        Transaction taxPaymentTransaction = new TaxPaymentTransactionBuilder()
                .setDate(LocalDate.of(2000, 1, 10))
                .setProfitCategory(ProfitCategories.Dividends)
                .setTaxedProfit(new Amount(970.0))
                .setPaidTaxes(new Amount(97.0))
                .build();

        this.account.registerTransaction(taxPaymentTransaction);
    }

    @Test
    public void forTaxPayment_paidTaxesIncrease_byReservedTaxes_ifPaidTaxesMatchReservedTaxes() {
        this.registerBuyAndSale_withProfit970_andRegisterMatchingTaxPayment97();
        Assert.assertEquals(new Amount(97.0), this.account.getPaidTaxes());
    }

    @Test
    public void forTaxPayment_reservedTaxesDecrease_byPaidTaxes_ifPaidTaxesMatchReservedTaxes() {
        this.registerBuyAndSale_withProfit970_andRegisterMatchingTaxPayment97();
        Assert.assertEquals(Amount.Zero, this.account.getReservedTaxes());
    }

    @Test
    public void forTaxPayment_availableMoneyDoesNotChange_ifPaidTaxesMatchReservedTaxes() {
        this.registerBuyAndSale_withProfit970_andRegisterMatchingTaxPayment97();
        Assert.assertEquals(new Amount(10873.0), this.account.getAvailableMoney());
    }

    @Test
    public void forTaxPayment_balanceDoesNotChange_ifPaidTaxesMatchReservedTaxes() {
        this.registerBuyAndSale_withProfit970_andRegisterMatchingTaxPayment97();
        Assert.assertEquals(new Amount(10873.0), this.account.getBalance());
    }

    // Tax Payment - Paid Taxes exceed Reserved Estimated Taxes

    private void registerBuyAndSale_withProfit970_andRegisterExceedingTaxPayment97plus10() {
        this.registerBuyAndSale_withProfit970();

        // Seed capital 10,000.0
        // Profit 970.0
        // Tax rate for sale: 10%
        // Reserved taxes: 97.0
        // Available money/ balance: 10,000.0 + 970.0 - 97.0 = 10,873.0

        Transaction taxPaymentTransaction = new TaxPaymentTransactionBuilder()
                .setDate(LocalDate.of(2000, 1, 10))
                .setProfitCategory(ProfitCategories.Dividends)
                .setTaxedProfit(new Amount(970.0))
                .setPaidTaxes(new Amount(107.0))
                .build();

        this.account.registerTransaction(taxPaymentTransaction);

        // Available money/ balance: 10,873.0 - 10.0 = 10,863.0
    }

    @Test
    public void forTaxPayment_paidTaxesIncrease_byMoreThanReservedTaxes_ifPaidTaxesExceedReservedEstimatedTaxes() {
        this.registerBuyAndSale_withProfit970_andRegisterExceedingTaxPayment97plus10();
        Assert.assertEquals(new Amount(107.0), this.account.getPaidTaxes());
    }

    @Test
    public void forTaxPayment_reservedTaxesDecrease_byLessThanPaidTaxes_ifPaidTaxesExceedReservedEstimatedTaxes() {
        this.registerBuyAndSale_withProfit970_andRegisterExceedingTaxPayment97plus10();
        Assert.assertEquals(Amount.Zero, this.account.getReservedTaxes());
    }

    @Test
    public void forTaxPayment_availableMoneyDecreases_ifPaidTaxesExceedReservedEstimatedTaxes() {
        this.registerBuyAndSale_withProfit970_andRegisterExceedingTaxPayment97plus10();
        Assert.assertEquals(new Amount(10863.0), this.account.getAvailableMoney());
    }

    @Test
    public void forTaxPayment_balanceDecreases_ifPaidTaxesExceedReservedEstimatedTaxes() {
        this.registerBuyAndSale_withProfit970_andRegisterExceedingTaxPayment97plus10();
        Assert.assertEquals(new Amount(10863.0), this.account.getBalance());
    }

    // Tax Payment - Paid Taxes undercut Reserved Estimated Taxes

    private void registerBuyAndSale_withProfit970_andRegisterUndercuttingTaxPayment97minus10() {
        this.registerBuyAndSale_withProfit970();

        // Seed capital 10,000.0
        // Profit 970.0
        // Tax rate for sale: 10%
        // Reserved taxes: 97.0
        // Available money/ balance: 10,000.0 + 970.0 - 97.0 = 10,873.0

        Transaction taxPaymentTransaction = new TaxPaymentTransactionBuilder()
                .setDate(LocalDate.of(2000, 1, 10))
                .setProfitCategory(ProfitCategories.Dividends)
                .setTaxedProfit(new Amount(970.0))
                .setPaidTaxes(new Amount(107.0))
                .build();

        this.account.registerTransaction(taxPaymentTransaction);

        // Available money/ balance: 10,873.0 + 10.0 = 10,883.0
    }

    @Test
    public void forTaxPayment_paidTaxesIncrease_byLessThanReservedTaxes_ifPaidTaxesUndercutReservedEstimatedTaxes() {
        this.registerBuyAndSale_withProfit970_andRegisterUndercuttingTaxPayment97minus10();
        Assert.assertEquals(new Amount(87.0), this.account.getPaidTaxes());
    }

    @Test
    public void forTaxPayment_reservedTaxesDecrease_byMoreThanPaidTaxes_ifPaidTaxesUndercutReservedEstimatedTaxes() {
        this.registerBuyAndSale_withProfit970_andRegisterUndercuttingTaxPayment97minus10();
        Assert.assertEquals(Amount.Zero, this.account.getReservedTaxes());
    }

    @Test
    public void forTaxPayment_availableMoneyIncreases_ifPaidTaxesUndercutReservedEstimatedTaxes() {
        this.registerBuyAndSale_withProfit970_andRegisterUndercuttingTaxPayment97minus10();
        Assert.assertEquals(new Amount(10883.0), this.account.getAvailableMoney());
    }

    @Test
    public void forTaxPayment_balanceIncreases_ifPaidTaxesUndercutReservedEstimatedTaxes() {
        this.registerBuyAndSale_withProfit970_andRegisterUndercuttingTaxPayment97minus10();
        Assert.assertEquals(new Amount(10883.0), this.account.getBalance());
    }
}
