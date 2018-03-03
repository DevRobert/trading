package trading.market;

import trading.Amount;
import trading.ISIN;

import java.util.HashMap;

public class MarketPriceSnapshot {
    private final HashMap<ISIN, Amount> marketPrices;

    public MarketPriceSnapshot(HashMap<ISIN, Amount> marketPrices) {
        this.marketPrices = marketPrices;
    }

    public Amount getMarketPrice(ISIN isin) {
        Amount marketPrice = this.marketPrices.get(isin);

        if(marketPrice == null) {
            throw new UnknownStockException();
        }

        return marketPrice;
    }
}
