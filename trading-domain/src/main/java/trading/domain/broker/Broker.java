package trading.domain.broker;

import java.time.LocalDate;

public interface Broker {
    void setOrder(OrderRequest orderRequest);
    void notifyDayOpened(LocalDate date);
    CommissionStrategy getCommissionStrategy();
}
