package trading.broker;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import trading.Amount;
import trading.ISIN;
import trading.Quantity;
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
    public void transactionForOrderRequestIsNotCreatedBeforeDayOpened() {
        Quantity quantity = new Quantity(1);

        OrderRequest orderRequest = new OrderRequest(OrderType.BuyMarket, ISIN.MunichRe, quantity);
        virtualBroker.setOrder(orderRequest);

        Assert.assertFalse(account.hasPosition(ISIN.MunichRe));
    }

    @Test
    public void transactionForOrderRequestIsCreatedAfterDayOpened() {
        Quantity quantity = new Quantity(10);
        OrderRequest orderRequest = new OrderRequest(OrderType.BuyMarket, ISIN.MunichRe, quantity);

        virtualBroker.setOrder(orderRequest);
        virtualBroker.notifyDayOpened();

        Position position = account.getPosition(ISIN.MunichRe);
        Assert.assertEquals(quantity, position.getQuantity());
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
