package trading.domain.market;

import trading.domain.Amount;
import trading.domain.ISIN;

import java.time.LocalDate;
import java.util.Map;
import java.util.Set;

public class MarketPriceSnapshot {
    private final Map<ISIN, Amount> marketPrices;
    private final LocalDate date;

    public MarketPriceSnapshot(Map<ISIN, Amount> marketPrices, LocalDate date) {
        if(date == null) {
            throw new RuntimeException("The date must be specified.");
        }

        this.marketPrices = marketPrices;
        this.date = date;
    }

    public Amount getMarketPrice(ISIN isin) {
        Amount marketPrice = this.marketPrices.get(isin);

        if(marketPrice == null) {
            throw new UnknownStockException();
        }

        return marketPrice;
    }

    public Set<ISIN> getISINs() {
        return this.marketPrices.keySet();
    }

    public LocalDate getDate() {
        return this.date;
    }

    public int size() {
        return this.marketPrices.size();
    }
}
