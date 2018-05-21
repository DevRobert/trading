package trading.domain.broker;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import trading.domain.Amount;
import trading.domain.ISIN;
import trading.domain.Quantity;
import trading.domain.account.Account;
import trading.domain.account.Position;
import trading.domain.account.MarketTransactionBuilder;
import trading.domain.account.TransactionType;
import trading.domain.market.HistoricalMarketData;
import trading.domain.market.MarketPriceSnapshot;
import trading.domain.market.MarketPriceSnapshotBuilder;

import java.time.LocalDate;

/**
 * The virtual broker is strongly dependent on the account entity.
 * In order to get meaningful test results, the dependency on the account is not replaced but used in this test suite.
 * If a test fails, at first make sure that the tests of the account class succeed.
 */
public class VirtualBrokerTest {
    private Account account;
    private HistoricalMarketData historicalMarketData;
    private CommissionStrategy commissionStrategy;

    @Before
    public void before() {
        Amount availableMoney = new Amount(50000.0);
        this.account = new Account(availableMoney);

        MarketPriceSnapshot marketPriceSnapshot = new MarketPriceSnapshotBuilder()
                .setMarketPrice(ISIN.MunichRe, new Amount(1000.0))
                .setMarketPrice(ISIN.Allianz, new Amount(500.0))
                .setDate(LocalDate.now())
                .build();

        this.historicalMarketData = new HistoricalMarketData(marketPriceSnapshot);

        this.commissionStrategy = new ZeroCommissionStrategy();
    }

    private VirtualBroker createVirtualBroker() {
        return new VirtualBroker(this.account, this.historicalMarketData, this.commissionStrategy);
    }

    @Test
    public void constructionFailsIfAccountNotSpecified() {
        this.account = null;

        try {
            createVirtualBroker();
        }
        catch(RuntimeException ex) {
            Assert.assertEquals("The account must be specified.", ex.getMessage());
            return;
        }

        Assert.fail("RuntimeException expected.");
    }

    @Test
    public void constructionFailsIfHistoricalMarketDataNotSpecified() {
        this.historicalMarketData = null;

        try {
            createVirtualBroker();
        }
        catch(RuntimeException ex) {
            Assert.assertEquals("The historical market data must be specified.", ex.getMessage());
            return;
        }

        Assert.fail("RuntimeException expected.");
    }

    @Test
    public void constructionFailsIfCommissionStrategyNotSpecified() {
        this.commissionStrategy = null;

        try {
            createVirtualBroker();
        }
        catch(RuntimeException ex) {
            Assert.assertEquals("The commission strategy must be specified.", ex.getMessage());
            return;
        }

        Assert.fail("RuntimeException expected.");
    }

    @Test
    public void transactionForBuyMarketOrderRequestIsNotCreatedBeforeDayOpened() {
        VirtualBroker virtualBroker = this.createVirtualBroker();

        Quantity quantity = new Quantity(1);

        OrderRequest orderRequest = new OrderRequest(OrderType.BuyMarket, ISIN.MunichRe, quantity);
        virtualBroker.setOrder(orderRequest);

        Assert.assertFalse(account.hasPosition(ISIN.MunichRe));
    }

    @Test
    public void transactionForBuyMarketOrderRequestIsCreatedAfterDayOpened() {
        VirtualBroker virtualBroker = this.createVirtualBroker();

        Quantity quantity = new Quantity(10);
        OrderRequest orderRequest = new OrderRequest(OrderType.BuyMarket, ISIN.MunichRe, quantity);

        virtualBroker.setOrder(orderRequest);
        virtualBroker.notifyDayOpened(LocalDate.now());

        Position position = account.getPosition(ISIN.MunichRe);
        Assert.assertEquals(quantity, position.getQuantity());
    }

    @Test
    public void transactionForSellMarketOrderRequestIsCreatedAfterDayOpened() {
        VirtualBroker virtualBroker = this.createVirtualBroker();

        Quantity quantity = new Quantity(10);
        Amount buyTotalPrice = new Amount(10000.0);
        Amount buyCommission = new Amount(0.0);

        account.registerTransaction(new MarketTransactionBuilder()
                .setTransactionType(TransactionType.Buy)
                .setIsin(ISIN.MunichRe)
                .setQuantity(quantity)
                .setTotalPrice(buyTotalPrice)
                .setCommission(buyCommission)
                .setDate(LocalDate.of(2000, 1, 1))
                .build());

        OrderRequest orderRequest = new OrderRequest(OrderType.SellMarket, ISIN.MunichRe, quantity);
        virtualBroker.setOrder(orderRequest);
        virtualBroker.notifyDayOpened(LocalDate.of(2000, 1, 2));

        Position position = account.getPosition(ISIN.MunichRe);
        Assert.assertTrue(position.getQuantity().isZero());
    }

    @Test
    public void lastMarkedPriceIsUsedForBuyMarketOrderRequest() {
        VirtualBroker virtualBroker = this.createVirtualBroker();

        Quantity quantity = new Quantity(10);

        OrderRequest orderRequest = new OrderRequest(OrderType.BuyMarket, ISIN.MunichRe, quantity);

        virtualBroker.setOrder(orderRequest);
        virtualBroker.notifyDayOpened(LocalDate.now());

        Position position = account.getPosition(ISIN.MunichRe);
        Assert.assertEquals(new Amount(10000.0), position.getFullMarketPrice());
    }

    @Test
    public void lastMarketPriceIsUsedForSellMarketOrderRequest() {
        VirtualBroker virtualBroker = this.createVirtualBroker();

        // Seed capital: 50,000

        Quantity quantity = new Quantity(10);
        Amount buyTotalPrice = new Amount(10000.0);
        Amount buyCommission = new Amount(0.0);

        this.account.registerTransaction(new MarketTransactionBuilder()
                .setTransactionType(TransactionType.Buy)
                .setIsin(ISIN.MunichRe)
                .setQuantity(quantity)
                .setTotalPrice(buyTotalPrice)
                .setCommission(buyCommission)
                .setDate(this.historicalMarketData.getDate())
                .build());

        // Available money after buying: 50,000 - 10,000 = 40,000

        Assert.assertEquals(new Amount(40000.0), account.getAvailableMoney());

        MarketPriceSnapshotBuilder marketPriceSnapshotBuilder = new MarketPriceSnapshotBuilder();
        marketPriceSnapshotBuilder.setMarketPrice(ISIN.MunichRe, new Amount(2000.0));
        marketPriceSnapshotBuilder.setMarketPrice(ISIN.Allianz, new Amount(500.0));
        marketPriceSnapshotBuilder.setDate(this.historicalMarketData.getDate().plusDays(1));
        this.historicalMarketData.registerClosedDay(marketPriceSnapshotBuilder.build());

        // Expected full market price for selling: 10 * 2,000 = 20,000

        OrderRequest orderRequest = new OrderRequest(OrderType.SellMarket, ISIN.MunichRe, quantity);
        virtualBroker.setOrder(orderRequest);
        virtualBroker.notifyDayOpened(LocalDate.now());

        // Available money after selling: 40,000 + 20,000 = 60,000

        Assert.assertEquals(new Amount(60000.0), account.getAvailableMoney());
    }

    @Test
    public void commissionsAreComputedForBuyMarketOrderIfCommissionsConfigured() {
        Amount buyTotalPrice = new Amount(10000.0);
        Amount buyCommission = new Amount(10.0);

        this.commissionStrategy = totalPrice -> {
            Assert.assertEquals(buyTotalPrice, totalPrice);
            return buyCommission;
        };

        VirtualBroker virtualBroker = this.createVirtualBroker();

        // Seed capital: 50,000

        Quantity quantity = new Quantity(10);
        OrderRequest orderRequest = new OrderRequest(OrderType.BuyMarket, ISIN.MunichRe, quantity);
        virtualBroker.setOrder(orderRequest);
        virtualBroker.notifyDayOpened(LocalDate.now());

        // Available money after buying: 50,000 - 10,000 - 10 = 39,990

        Assert.assertEquals(new Amount(39990.0), account.getAvailableMoney());
    }

    @Test
    public void commissionsAreComputedForSellMarketOrderIfCommissionsConfigured() {
        Amount sellTotalPrice = new Amount(10000.0);
        Amount sellCommission = new Amount(10.0);

        this.commissionStrategy = totalPrice -> {
            Assert.assertEquals(sellTotalPrice, totalPrice);
            return sellCommission;
        };

        VirtualBroker virtualBroker = this.createVirtualBroker();

        // Seed capital: 50,000

        Quantity quantity = new Quantity(10);
        Amount buyTotalPrice = new Amount(10000.0);
        Amount buyCommission = new Amount(0.0);

        account.registerTransaction(new MarketTransactionBuilder()
                .setTransactionType(TransactionType.Buy)
                .setIsin(ISIN.MunichRe)
                .setQuantity(quantity)
                .setTotalPrice(buyTotalPrice)
                .setCommission(buyCommission)
                .setDate(LocalDate.of(2000, 1, 1))
                .build());

        // Available money after buying: 50,000 - 10,000 = 40,000

        OrderRequest orderRequest = new OrderRequest(OrderType.SellMarket, ISIN.MunichRe, quantity);
        virtualBroker.setOrder(orderRequest);
        virtualBroker.notifyDayOpened(LocalDate.of(2000, 1, 2));

        // Available money after selling: 40,000 + 10,000 - 10 = 49,990

        Assert.assertEquals(new Amount(49990.0), account.getAvailableMoney());
    }

    @Test
    public void buyOrderRequestFailsIfTotalPricePlusCommissionExceedsAvailableMoney() {
        // Available money: 50,000
        // Munich RE price: 1,000
        // 50 x 1,000 = 50,000
        // plus commission 10 = 50,010
        // is more than the available money 50,000

        this.commissionStrategy = totalPrice -> new Amount(10.0);

        OrderRequest orderRequest = new OrderRequest(OrderType.BuyMarket, ISIN.MunichRe, new Quantity(50));

        VirtualBroker virtualBroker = this.createVirtualBroker();

        virtualBroker.setOrder(orderRequest);

        try {
            virtualBroker.notifyDayOpened(this.historicalMarketData.getDate().plusDays(1));
        }
        catch(RuntimeException e) {
            Assert.assertEquals("The order request cannot be processed as it requires more money than available.", e.getMessage());
            return;
        }

        Assert.fail("RuntimeException expected.");
    }
}
