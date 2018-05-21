package trading.domain.account;

import org.junit.Assert;
import org.junit.Test;
import trading.domain.Amount;
import trading.domain.ISIN;
import trading.domain.Quantity;

import java.time.LocalDate;

public class AvailableMoneyTest extends AccountTestBase {
    @Test()
    public void accountIsInitializedWithSpecifiedAvailableMoney() {
        Assert.assertEquals(new Amount(10000.0), account.getAvailableMoney());
    }

    @Test
    public void buyTransactionReducesAvailableMoney() throws AccountStateException {
        // Initial available money: 10,000

        Amount totalPrice = new Amount(1000.0);
        Amount commission = new Amount(10.0);

        MarketTransaction transaction = new MarketTransactionBuilder()
                .setTransactionType(MarketTransactionType.Buy)
                .setIsin(ISIN.MunichRe)
                .setQuantity(new Quantity(1))
                .setTotalPrice(totalPrice)
                .setCommission(commission)
                .setDate(LocalDate.of(2000, 1, 1))
                .build();

        account.registerTransaction(transaction);

        // Expected available money: 10,000 - 1,000 - 10 = 8,990

        Assert.assertEquals(new Amount(8990.0), account.getAvailableMoney());
    }

    @Test
    public void sellTransactionIncreasesAvailableMoney() throws AccountStateException {
        // Initial available money: 10,000

        Amount totalBuyPrice = new Amount(1000.0);
        Amount buyCommission = new Amount(10.0);

        MarketTransaction buyTransaction = new MarketTransactionBuilder()
                .setTransactionType(MarketTransactionType.Buy)
                .setIsin(ISIN.MunichRe)
                .setQuantity(new Quantity(1))
                .setTotalPrice(totalBuyPrice)
                .setCommission(buyCommission)
                .setDate(LocalDate.of(2000, 1, 1))
                .build();

        account.registerTransaction(buyTransaction);

        // Interim available money: 10,000 - 1,000 - 10 = 8,990

        Amount totalSellPrice = new Amount(2000.0);
        Amount sellCommission = new Amount(20.0);

        MarketTransaction sellTransaction = new MarketTransactionBuilder()
                .setTransactionType(MarketTransactionType.Sell)
                .setIsin(ISIN.MunichRe)
                .setQuantity(new Quantity(1))
                .setTotalPrice(totalSellPrice)
                .setCommission(sellCommission)
                .setDate(LocalDate.of(2000, 1, 2))
                .build();

        account.registerTransaction(sellTransaction);

        // Expected available money: 8,990 + 2,000 - 20 = 10,970

        Assert.assertEquals(new Amount(10970.0), account.getAvailableMoney());
    }

    @Test
    public void buyTransactionFailsIfNotEnoughAvailableMoneyForTotalPrice() {
        Amount buyTotalPrice = new Amount(11000.0);
        Amount buyCommission = new Amount(1000.0);

        MarketTransaction transaction = new MarketTransactionBuilder()
                .setTransactionType(MarketTransactionType.Buy)
                .setIsin(ISIN.MunichRe)
                .setQuantity(new Quantity(1))
                .setTotalPrice(buyTotalPrice)
                .setCommission(buyCommission)
                .setDate(LocalDate.of(2000, 1, 1))
                .build();

        try {
            account.registerTransaction(transaction);
        } catch (AccountStateException ex) {
            Assert.assertEquals("The total price exceeds the available money.", ex.getMessage());
            return;
        }

        Assert.fail("AccountStateException expected.");
    }

    @Test
    public void buyTransactionSucceedsIfEnoughAvailableMoneyForTotalPriceButNotForCommission() {
        Amount buyTotalPrice = new Amount(10000.0);
        Amount buyCommission = new Amount(1000.0);

        MarketTransaction transaction = new MarketTransactionBuilder()
                .setTransactionType(MarketTransactionType.Buy)
                .setIsin(ISIN.MunichRe)
                .setQuantity(new Quantity(1))
                .setTotalPrice(buyTotalPrice)
                .setCommission(buyCommission)
                .setDate(LocalDate.of(2000, 1, 1))
                .build();

        account.registerTransaction(transaction);

        Assert.assertEquals(new Amount(-1000.0), account.getAvailableMoney());
    }

    @Test
    public void buyTransactionSucceedsIfExactlyEnoughMoneyAvailable() throws AccountStateException {
        Amount buyTotalPrice = new Amount(9000.0);
        Amount buyCommission = new Amount(1000.0);

        MarketTransaction transaction = new MarketTransactionBuilder()
                .setTransactionType(MarketTransactionType.Buy)
                .setIsin(ISIN.MunichRe)
                .setQuantity(new Quantity(1))
                .setTotalPrice(buyTotalPrice)
                .setCommission(buyCommission)
                .setDate(LocalDate.of(2000, 1, 1))
                .build();

        account.registerTransaction(transaction);

        Assert.assertEquals(new Amount(0.0), account.getAvailableMoney());
    }

    @Test
    public void buyTransactionSucceedsIfMoreThanEnoughMoneyAvailable() throws AccountStateException {
        Amount buyTotalPrice = new Amount(8000.0);
        Amount buyCommission = new Amount(1000.0);

        MarketTransaction transaction = new MarketTransactionBuilder()
                .setTransactionType(MarketTransactionType.Buy)
                .setIsin(ISIN.MunichRe)
                .setQuantity(new Quantity(1))
                .setTotalPrice(buyTotalPrice)
                .setCommission(buyCommission)
                .setDate(LocalDate.of(2000, 1, 1))
                .build();

        account.registerTransaction(transaction);

        Assert.assertEquals(new Amount(1000.0), account.getAvailableMoney());
    }

    @Test
    public void sellTransactionSucceedsEvenIfCommissionExceedsTotalPriceAndNoMoneyIsAvailable() throws AccountStateException {
        Amount buyTotalPrice = new Amount(9000.0);
        Amount buyCommission = new Amount(1000.0);

        MarketTransaction buyTransaction = new MarketTransactionBuilder()
                .setTransactionType(MarketTransactionType.Buy)
                .setIsin(ISIN.MunichRe)
                .setQuantity(new Quantity(1))
                .setTotalPrice(buyTotalPrice)
                .setCommission(buyCommission)
                .setDate(LocalDate.of(2000, 1, 1))
                .build();

        account.registerTransaction(buyTransaction);

        // Interim available money: 10,000 - 9,000 - 1,000 = 0

        Amount sellTotalPrice = new Amount(5.0);
        Amount sellCommission = new Amount(10.0);

        MarketTransaction sellTransaction = new MarketTransactionBuilder()
                .setTransactionType(MarketTransactionType.Sell)
                .setIsin(ISIN.MunichRe)
                .setQuantity(new Quantity(1))
                .setTotalPrice(sellTotalPrice)
                .setCommission(sellCommission)
                .setDate(LocalDate.of(2000, 1, 2))
                .build();

        account.registerTransaction(sellTransaction);

        // Available money: 0 + 5 - 10 = -5

        Assert.assertEquals(new Amount(-5.0), account.getAvailableMoney());
    }

    @Test
    public void sellTransactionSucceedsEvenIfCommissionExceedsTotalPriceAvailableMoneyIsNegative() {
        Amount buyTotalPrice1 = new Amount(8000.0);
        Amount buyCommission1 = new Amount(0.0);

        MarketTransaction buyTransaction1 = new MarketTransactionBuilder()
                .setTransactionType(MarketTransactionType.Buy)
                .setIsin(ISIN.MunichRe)
                .setQuantity(new Quantity(1))
                .setTotalPrice(buyTotalPrice1)
                .setCommission(buyCommission1)
                .setDate(LocalDate.of(2000, 1, 1))
                .build();

        account.registerTransaction(buyTransaction1);

        // Interim available money: 10,000 - 8,000 = 2,000

        Amount buyTotalPrice2 = new Amount(2000.0);
        Amount buyCommission2 = new Amount(0.0);

        MarketTransaction buyTransaction2 = new MarketTransactionBuilder()
                .setTransactionType(MarketTransactionType.Buy)
                .setIsin(ISIN.Allianz)
                .setQuantity(new Quantity(1))
                .setTotalPrice(buyTotalPrice2)
                .setCommission(buyCommission2)
                .setDate(LocalDate.of(2000, 1, 2))
                .build();

        account.registerTransaction(buyTransaction2);

        // Interim available money: 2,000 - 2,000 = 0

        Amount sellTotalPrice1 = new Amount(5.0);
        Amount sellCommission1 = new Amount(10.0);

        MarketTransaction sellTransaction1 = new MarketTransactionBuilder()
                .setTransactionType(MarketTransactionType.Sell)
                .setIsin(ISIN.MunichRe)
                .setQuantity(new Quantity(1))
                .setTotalPrice(sellTotalPrice1)
                .setCommission(sellCommission1)
                .setDate(LocalDate.of(2000, 1, 3))
                .build();

        account.registerTransaction(sellTransaction1);

        // Interim available money: 0 + 5 - 10 = -5

        Amount sellTotalPrice2 = new Amount(5.0);
        Amount sellCommission2 = new Amount(10.0);

        MarketTransaction sellTransaction2 = new MarketTransactionBuilder()
                .setTransactionType(MarketTransactionType.Sell)
                .setIsin(ISIN.Allianz)
                .setQuantity(new Quantity(1))
                .setTotalPrice(sellTotalPrice2)
                .setCommission(sellCommission2)
                .setDate(LocalDate.of(2000, 1, 4))
                .build();

        account.registerTransaction(sellTransaction2);

        // Available money: -5 + 5 - 10 = -10

        Assert.assertEquals(new Amount(-10.0), account.getAvailableMoney());
    }
}
