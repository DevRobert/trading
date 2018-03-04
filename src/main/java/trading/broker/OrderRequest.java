package trading.broker;

import trading.ISIN;
import trading.Quantity;

public class OrderRequest {
    private final OrderType orderType;
    private final ISIN isin;
    private final Quantity quantity;

    public OrderType getOrderType() {
        return orderType;
    }

    public ISIN getIsin() {
        return isin;
    }

    public Quantity getQuantity() {
        return quantity;
    }

    public OrderRequest(OrderType orderType, ISIN isin, Quantity quantity) {
        this.orderType = orderType;
        this.isin = isin;
        this.quantity = quantity;
    }
}
