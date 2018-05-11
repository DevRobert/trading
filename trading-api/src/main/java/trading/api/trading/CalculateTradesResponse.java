package trading.api.trading;

import java.time.LocalDate;
import java.util.List;

public class CalculateTradesResponse {
    private LocalDate marketPricesDate;
    private List<TradeDto> trades;

    public LocalDate getMarketPricesDate() {
        return marketPricesDate;
    }

    public void setMarketPricesDate(LocalDate marketPricesDate) {
        this.marketPricesDate = marketPricesDate;
    }

    public List<TradeDto> getTrades() {
        return trades;
    }

    public void setTrades(List<TradeDto> trades) {
        this.trades = trades;
    }
}
