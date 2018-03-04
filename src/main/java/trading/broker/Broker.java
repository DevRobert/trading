package trading.broker;

public interface Broker {
    void setOrder(OrderRequest orderRequest);
    void notifyDayOpened();
}
