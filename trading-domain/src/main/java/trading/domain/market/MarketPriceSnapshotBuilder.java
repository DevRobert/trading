package trading.domain.market;

import trading.domain.Amount;
import trading.domain.ISIN;

import java.time.LocalDate;
import java.util.HashMap;

public class MarketPriceSnapshotBuilder {
    private final HashMap<ISIN, Amount> marketPrices = new HashMap<>();
    private LocalDate date;

    public MarketPriceSnapshotBuilder setMarketPrice(ISIN isin, Amount marketPrice) {
        if(isin == null) {
            throw new RuntimeException("The ISIN must be specified.");
        }

        if(marketPrice == null) {
            throw new RuntimeException("The market price must be specified.");
        }

        this.marketPrices.put(isin, marketPrice);
        return this;
    }

    public MarketPriceSnapshotBuilder setDate(LocalDate date) {
        this.date = date;
        return this;
    }

    public MarketPriceSnapshot build() {
        return new MarketPriceSnapshot((HashMap<ISIN, Amount>) this.marketPrices.clone(), this.date);
    }
}
