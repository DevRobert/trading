package trading.domain.strategy.manual;

import org.junit.Assert;
import org.junit.Test;
import trading.domain.ISIN;
import trading.domain.Quantity;
import trading.domain.broker.Broker;
import trading.domain.broker.CommissionStrategy;
import trading.domain.broker.OrderRequest;
import trading.domain.broker.OrderType;

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

            @Override
            public CommissionStrategy getCommissionStrategy() {
                return null;
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
