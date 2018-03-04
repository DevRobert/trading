package trading.market;

import trading.Amount;
import trading.ISIN;

import java.util.Map;
import java.util.Set;

public class MarketPriceSnapshot {
    private final Map<ISIN, Amount> marketPrices;

    public MarketPriceSnapshot(Map<ISIN, Amount> marketPrices) {
        this.marketPrices = marketPrices;
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
}
