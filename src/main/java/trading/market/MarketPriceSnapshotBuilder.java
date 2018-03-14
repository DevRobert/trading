package trading.market;

import trading.Amount;
import trading.ISIN;

import java.util.HashMap;

public class MarketPriceSnapshotBuilder {
    private final HashMap<ISIN, Amount> marketPrices = new HashMap<>();

    public void setMarketPrice(ISIN isin, Amount marketPrice) {
        this.marketPrices.put(isin, marketPrice);
    }

    public MarketPriceSnapshot build() {
        return new MarketPriceSnapshot((HashMap<ISIN, Amount>) this.marketPrices.clone());
    }
}
