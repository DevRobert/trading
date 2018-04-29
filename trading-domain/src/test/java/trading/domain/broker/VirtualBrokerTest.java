package trading.domain.broker;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import trading.domain.*;
import trading.domain.account.Account;
import trading.domain.account.Position;
import trading.domain.market.HistoricalMarketData;
import trading.domain.market.MarketPriceSnapshotBuilder;

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

        MarketPriceSnapshotBuilder marketPriceSnapshotBuilder = new MarketPriceSnapshotBuilder();
        marketPriceSnapshotBuilder.setMarketPrice(ISIN.MunichRe, new Amount(1000.0));
        marketPriceSnapshotBuilder.setMarketPrice(ISIN.Allianz, new Amount(500.0));
        this.historicalMarketData = new HistoricalMarketData(marketPriceSnapshotBuilder.build());

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
        virtualBroker.notifyDayOpened();

        Position position = account.getPosition(ISIN.MunichRe);
        Assert.assertEquals(quantity, position.getQuantity());
    }

    @Test
    public void transactionForSellMarketOrderRequestIsCreatedAfterDayOpened() {
        VirtualBroker virtualBroker = this.createVirtualBroker();

        Quantity quantity = new Quantity(10);
        Amount buyTotalPrice = new Amount(10000.0);
        Amount buyCommission = new Amount(0.0);

        account.registerTransaction(new TransactionBuilder()
                .setTransactionType(TransactionType.Buy)
                .setIsin(ISIN.MunichRe)
                .setQuantity(quantity)
                .setTotalPrice(buyTotalPrice)
                .setCommission(buyCommission)
                .build());

        OrderRequest orderRequest = new OrderRequest(OrderType.SellMarket, ISIN.MunichRe, quantity);
        virtualBroker.setOrder(orderRequest);
        virtualBroker.notifyDayOpened();

        Position position = account.getPosition(ISIN.MunichRe);
        Assert.assertTrue(position.getQuantity().isZero());
    }

    @Test
    public void lastMarkedPriceIsUsedForBuyMarketOrderRequest() {
        VirtualBroker virtualBroker = this.createVirtualBroker();

        Quantity quantity = new Quantity(10);

        OrderRequest orderRequest = new OrderRequest(OrderType.BuyMarket, ISIN.MunichRe, quantity);

        virtualBroker.setOrder(orderRequest);
        virtualBroker.notifyDayOpened();

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

        account.registerTransaction(new TransactionBuilder()
                .setTransactionType(TransactionType.Buy)
                .setIsin(ISIN.MunichRe)
                .setQuantity(quantity)
                .setTotalPrice(buyTotalPrice)
                .setCommission(buyCommission)
                .build());

        // Available money after buying: 50,000 - 10,000 = 40,000

        Assert.assertEquals(new Amount(40000.0), account.getAvailableMoney());

        MarketPriceSnapshotBuilder marketPriceSnapshotBuilder = new MarketPriceSnapshotBuilder();
        marketPriceSnapshotBuilder.setMarketPrice(ISIN.MunichRe, new Amount(2000.0));
        marketPriceSnapshotBuilder.setMarketPrice(ISIN.Allianz, new Amount(500.0));
        historicalMarketData.registerClosedDay(marketPriceSnapshotBuilder.build());

        // Expected full market price for selling: 10 * 2,000 = 20,000

        OrderRequest orderRequest = new OrderRequest(OrderType.SellMarket, ISIN.MunichRe, quantity);
        virtualBroker.setOrder(orderRequest);
        virtualBroker.notifyDayOpened();

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
        virtualBroker.notifyDayOpened();

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

        account.registerTransaction(new TransactionBuilder()
                .setTransactionType(TransactionType.Buy)
                .setIsin(ISIN.MunichRe)
                .setQuantity(quantity)
                .setTotalPrice(buyTotalPrice)
                .setCommission(buyCommission)
                .build());

        // Available money after buying: 50,000 - 10,000 = 40,000

        OrderRequest orderRequest = new OrderRequest(OrderType.SellMarket, ISIN.MunichRe, quantity);
        virtualBroker.setOrder(orderRequest);
        virtualBroker.notifyDayOpened();

        // Available money after selling: 40,000 + 10,000 - 10 = 49,990

        Assert.assertEquals(new Amount(49990.0), account.getAvailableMoney());
    }

    // TODO Tests for feasibility checks of order requests
}
