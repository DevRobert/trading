package trading.application;

import trading.domain.broker.OrderRequest;

import java.time.LocalDate;
import java.util.List;

public class TradeList {
    private LocalDate date;
    private List<OrderRequest> trades;

    public TradeList(LocalDate date, List<OrderRequest> trades) {
        this.date = date;
        this.trades = trades;
    }

    public LocalDate getDate() {
        return this.date;
    }

    public List<OrderRequest> getTrades() {
        return this.trades;
    }
}
