package trading.domain.challenges;

import trading.domain.ISIN;
import trading.domain.market.MarketPriceSnapshot;
import trading.domain.market.MarketPriceSnapshotBuilder;
import trading.domain.simulation.MultiStockMarketDataStore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class HistoricalTestDataProvider {
    private List<MarketPriceSnapshot> historicalClosingPrices;
    private HashMap<ISIN, List<MarketPriceSnapshot>> singleStockHistoricalPricesByISIN;
    private MultiStockMarketDataStore multiStockMarketDataStore;

    public HistoricalTestDataProvider(MultiStockMarketDataStore multiStockMarketDataStore) {
        this.multiStockMarketDataStore = multiStockMarketDataStore;
    }

    public List<MarketPriceSnapshot> getHistoricalClosingPrices() {
        populateData();
        return historicalClosingPrices;
    }

    public List<MarketPriceSnapshot> getHistoricalClosingPrices(ISIN isin) {
        populateData();
        return singleStockHistoricalPricesByISIN.get(isin);
    }

    public Set<ISIN> getISINs() {
        populateData();
        return getHistoricalClosingPrices().get(0).getISINs();
    }

    private void populateData() {
        // todo introduce locking when introducing multi-threading

        if(historicalClosingPrices == null) {
            populateHistoricalClosingPrices();
            populateHistoricalClosingPricesByISIN();
        }
    }

    private void populateHistoricalClosingPrices() {
        historicalClosingPrices = this.multiStockMarketDataStore.getAllClosingPrices();
    }

    private void populateHistoricalClosingPricesByISIN() {
        singleStockHistoricalPricesByISIN = new HashMap<>();

        for(ISIN isin: historicalClosingPrices.get(0).getISINs()) {
            MarketPriceSnapshotBuilder marketPriceSnapshotBuilder = new MarketPriceSnapshotBuilder();

            List<MarketPriceSnapshot> singleStockHistoricalClosingPrices = new ArrayList<>(historicalClosingPrices.size());

            for(MarketPriceSnapshot allStocksMarketPriceSnapshot: historicalClosingPrices) {
                marketPriceSnapshotBuilder.setMarketPrice(isin, allStocksMarketPriceSnapshot.getMarketPrice(isin));
                singleStockHistoricalClosingPrices.add(marketPriceSnapshotBuilder.build());
            }

            singleStockHistoricalPricesByISIN.put(isin, singleStockHistoricalClosingPrices);
        }
    }
}
