package trading.broker;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import trading.*;
import trading.account.Account;
import trading.account.Position;
import trading.market.HistoricalMarketData;
import trading.market.MarketPriceSnapshotBuilder;

/**
 * The virtual broker is strongly dependent on the account entity.
 * In order to get meaningful test results, the dependency on the account is not replaced but used in this test suite.
 * If a test fails, at first make sure that the tests of the account class succeed.
 */
public class VirtualBrokerTest {
    private Account account;
    private HistoricalMarketData historicalMarketData;
    private VirtualBroker virtualBroker;

    @Before
    public void before() {
        Amount availableMoney = new Amount(50000.0);
        account = new Account(availableMoney);

        MarketPriceSnapshotBuilder marketPriceSnapshotBuilder = new MarketPriceSnapshotBuilder();
        marketPriceSnapshotBuilder.setMarketPrice(ISIN.MunichRe, new Amount(1000.0));
        marketPriceSnapshotBuilder.setMarketPrice(ISIN.Allianz, new Amount(500.0));
        historicalMarketData = new HistoricalMarketData(marketPriceSnapshotBuilder.build());

        virtualBroker = new VirtualBroker(account, historicalMarketData);
    }

    @Test
    public void transactionForBuyMarketOrderRequestIsNotCreatedBeforeDayOpened() {
        Quantity quantity = new Quantity(1);

        OrderRequest orderRequest = new OrderRequest(OrderType.BuyMarket, ISIN.MunichRe, quantity);
        virtualBroker.setOrder(orderRequest);

        Assert.assertFalse(account.hasPosition(ISIN.MunichRe));
    }

    @Test
    public void transactionForBuyMarketOrderRequestIsCreatedAfterDayOpened() {
        Quantity quantity = new Quantity(10);
        OrderRequest orderRequest = new OrderRequest(OrderType.BuyMarket, ISIN.MunichRe, quantity);

        virtualBroker.setOrder(orderRequest);
        virtualBroker.notifyDayOpened();

        Position position = account.getPosition(ISIN.MunichRe);
        Assert.assertEquals(quantity, position.getQuantity());
    }

    @Test
    public void transactionForSellMarketOrderRequestIsCreatedAfterDayOpened() {
        Quantity quantity = new Quantity(10);
        Amount buyTotalPrice = new Amount(10000.0);
        Amount buyCommission = new Amount(0.0);
        account.registerTransaction(new Transaction(TransactionType.Buy, ISIN.MunichRe, quantity, buyTotalPrice, buyCommission));

        OrderRequest orderRequest = new OrderRequest(OrderType.SellMarket, ISIN.MunichRe, quantity);
        virtualBroker.setOrder(orderRequest);
        virtualBroker.notifyDayOpened();

        Position position = account.getPosition(ISIN.MunichRe);
        Assert.assertTrue(position.getQuantity().isZero());
    }

    @Test
    public void lastMarkedPriceIsUsedForBuyMarketOrderRequest() {
        Quantity quantity = new Quantity(10);

        OrderRequest orderRequest = new OrderRequest(OrderType.BuyMarket, ISIN.MunichRe, quantity);

        virtualBroker.setOrder(orderRequest);
        virtualBroker.notifyDayOpened();

        Position position = account.getPosition(ISIN.MunichRe);
        Assert.assertEquals(new Amount(10000.0), position.getFullMarketPrice());
    }

    @Test
    public void lastMarketPriceIsUsedForSellMarketOrderRequest() {
        // Seed capital: 50,000

        Quantity quantity = new Quantity(10);
        Amount buyTotalPrice = new Amount(10000.0);
        Amount buyCommission = new Amount(0.0);
        account.registerTransaction(new Transaction(TransactionType.Buy, ISIN.MunichRe, quantity, buyTotalPrice, buyCommission));

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
    public void noCommissionsAreComputedForBuyMarketOrderIfZeroCommissionsConfigured() {

    }

    @Test
    public void commissionsAreComputedForBuyMarketOrderIfCommissionsConfigured() {

    }

    @Test
    public void noCommissionAreComputedForSellMarketOrderIfZeroCommissionsConfigured() {

    }

    @Test
    public void commissionsAreComputedForSellMarketOrderIfCommissionsConfigured() {

    }

    // TODO Tests for feasibility checks of order requests
}
