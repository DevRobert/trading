package trading.simulation;

import trading.market.MarketPriceSnapshot;

public interface SimulationMarketDataSource {
    MarketPriceSnapshot getNextClosingMarketPrices();
}
