package trading.domain.simulation;

import trading.domain.market.MarketPriceSnapshot;

public interface SimulationMarketDataSource {
    MarketPriceSnapshot getNextClosingMarketPrices();
}
