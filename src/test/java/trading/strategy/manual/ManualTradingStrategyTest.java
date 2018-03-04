package trading.strategy.manual;

import org.junit.Assert;
import org.junit.Test;
import trading.ISIN;
import trading.Quantity;
import trading.broker.Broker;
import trading.broker.OrderRequest;
import trading.broker.OrderType;

import java.util.ArrayList;
import java.util.List;

public class ManualTradingStrategyTest {
    @Test
    public void setOrderRequestsAfterDayClosed() {
        final List<OrderRequest> setOrderRequests = new ArrayList();

        Broker broker = new Broker() {
            @Override
            public void setOrder(OrderRequest orderRequest) {
                setOrderRequests.add(orderRequest);
            }

            @Override
            public void notifyDayOpened() {

            }
        };

        ManualTradingStrategy manualTradingStrategy = new ManualTradingStrategy(broker);

        OrderRequest orderRequest = new OrderRequest(OrderType.BuyMarket, ISIN.MunichRe, new Quantity(1));
        manualTradingStrategy.registerOrderRequest(orderRequest);

        manualTradingStrategy.prepareOrdersForNextTradingDay();

        Assert.assertEquals(1, setOrderRequests.size());
        Assert.assertSame(orderRequest, setOrderRequests.get(0));
    }
}
