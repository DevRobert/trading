package trading.simulation;

import trading.market.MarketPriceSnapshot;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class MultiStockListDataSource implements SimulationMarketDataSource {
    private final Queue<MarketPriceSnapshot> marketPriceSnapshots;

    public MultiStockListDataSource(List<MarketPriceSnapshot> marketPriceSnapshots) {
        if(marketPriceSnapshots == null) {
            throw new RuntimeException("The market price snapshot list has to be specified.");
        }

        this.marketPriceSnapshots = new LinkedBlockingQueue<>(marketPriceSnapshots);
    }

    @Override
    public MarketPriceSnapshot getNextClosingMarketPrices() {
        if(this.marketPriceSnapshots.size() > 0) {
            return this.marketPriceSnapshots.poll();
        }

        throw new SimulationMarketDataSourceExhaustedException();
    }
}
