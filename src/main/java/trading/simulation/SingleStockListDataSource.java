package trading.simulation;

import trading.Amount;
import trading.ISIN;
import trading.market.MarketPriceSnapshot;
import trading.market.MarketPriceSnapshotBuilder;

import java.util.List;

public class SingleStockListDataSource implements SimulationMarketDataSource {
    private final ISIN isin;
    private final List<Amount> closingMarketPrices;
    private int nextClosingMarketPriceIndex;

    public SingleStockListDataSource(ISIN isin, List<Amount> closingMarketPrices) {
        if(isin == null) {
            throw new RuntimeException("The ISIN has to be specified.");
        }

        if(closingMarketPrices == null) {
            throw new RuntimeException("The closing market prices list has to be specified.");
        }

        this.isin = isin;
        this.closingMarketPrices = closingMarketPrices;
        this.nextClosingMarketPriceIndex = 0;
    }

    @Override
    public MarketPriceSnapshot getNextClosingMarketPrices() {
        if(nextClosingMarketPriceIndex >= closingMarketPrices.size()) {
            throw new SimulationMarketDataSourceExhaustedException();
        }

        Amount amount = this.closingMarketPrices.get(nextClosingMarketPriceIndex);

        nextClosingMarketPriceIndex++;

        MarketPriceSnapshotBuilder marketPriceSnapshotBuilder = new MarketPriceSnapshotBuilder();
        marketPriceSnapshotBuilder.setMarketPrice(this.isin, amount);
        return marketPriceSnapshotBuilder.build();
    }
}
