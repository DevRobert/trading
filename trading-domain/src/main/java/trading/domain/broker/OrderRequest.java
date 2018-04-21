package trading.domain.broker;

import trading.domain.ISIN;
import trading.domain.Quantity;

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
        if(quantity.getValue() < 0) {
            throw new RuntimeException("The quantity must not be negative.");
        }

        if(quantity.getValue() == 0) {
            throw new RuntimeException("The quantity must not be zero.");
        }

        this.orderType = orderType;
        this.isin = isin;
        this.quantity = quantity;
    }
}
