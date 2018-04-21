package trading.domain.market;

import trading.domain.Amount;
import trading.domain.ISIN;

import java.util.HashMap;

public class MarketPriceSnapshotBuilder {
    private final HashMap<ISIN, Amount> marketPrices = new HashMap<>();

    public MarketPriceSnapshotBuilder setMarketPrice(ISIN isin, Amount marketPrice) {
        this.marketPrices.put(isin, marketPrice);
        return this;
    }

    public MarketPriceSnapshot build() {
        return new MarketPriceSnapshot((HashMap<ISIN, Amount>) this.marketPrices.clone());
    }
}
