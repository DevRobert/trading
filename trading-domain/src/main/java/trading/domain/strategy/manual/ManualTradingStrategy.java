package trading.domain.strategy.manual;

import trading.domain.broker.Broker;
import trading.domain.broker.OrderRequest;
import trading.domain.strategy.TradingStrategy;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * The manual trading strategy collects order requests and sends them to
 * the broker after a day has been closed.
 *
 * This strategy is only used for testing purposes.
 */
public class ManualTradingStrategy implements TradingStrategy {
    private final Broker broker;
    private final Queue<OrderRequest> orderRequestQueue;

    public ManualTradingStrategy(Broker broker) {
        this.broker = broker;
        this.orderRequestQueue = new LinkedBlockingQueue<>();
    }

    public void registerOrderRequest(OrderRequest orderRequest) {
        this.orderRequestQueue.add(orderRequest);
    }

    @Override
    public void prepareOrdersForNextTradingDay() {
        OrderRequest orderRequest;

        while((orderRequest = this.orderRequestQueue.poll()) != null) {
            this.broker.setOrder(orderRequest);
        }
    }
}
