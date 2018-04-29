package trading.domain.simulation;

import trading.domain.market.MarketPriceSnapshot;

import java.util.List;

public interface MultiStockMarketDataStore {
    List<MarketPriceSnapshot> getAllClosingPrices();

    MarketPriceSnapshot getLastClosingPrices();
}
