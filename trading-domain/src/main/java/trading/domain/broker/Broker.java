package trading.domain.broker;

public interface Broker {
    void setOrder(OrderRequest orderRequest);
    void notifyDayOpened();
    CommissionStrategy getCommissionStrategy();
}
