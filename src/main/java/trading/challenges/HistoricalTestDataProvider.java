package trading.challenges;

import trading.ISIN;
import trading.market.MarketPriceSnapshot;
import trading.market.MarketPriceSnapshotBuilder;
import trading.simulation.MongoMultiStockMarketDataStore;
import trading.simulation.MongoMultiStockMarketDataStoreParametersBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public abstract class HistoricalTestDataProvider {
    private static List<MarketPriceSnapshot> _historicalClosingPrices;
    private static HashMap<ISIN, List<MarketPriceSnapshot>> _singleStockHistoricalPricesByISIN;

    public static List<MarketPriceSnapshot> getHistoricalClosingPrices() {
        populateData();
        return _historicalClosingPrices;
    }

    public static List<MarketPriceSnapshot> getHistoricalClosingPrices(ISIN isin) {
        populateData();
        return _singleStockHistoricalPricesByISIN.get(isin);
    }

    public static Set<ISIN> getISINs() {
        populateData();
        return getHistoricalClosingPrices().get(0).getISINs();
    }

    private static void populateData() {
        // todo introduce locking when introducing multi-threading

        if(_historicalClosingPrices == null) {
            populateHistoricalClosingPrices();
            populateHistoricalClosingPricesByISIN();
        }
    }

    private static void populateHistoricalClosingPrices() {
        MongoMultiStockMarketDataStoreParametersBuilder parametersBuilder = new MongoMultiStockMarketDataStoreParametersBuilder();
        parametersBuilder.setDatabase("trading");
        parametersBuilder.setCollection("merged-quotes");
        MongoMultiStockMarketDataStore dataStore = new MongoMultiStockMarketDataStore(parametersBuilder.build());
        _historicalClosingPrices = dataStore.getAllClosingPrices();
    }

    private static void populateHistoricalClosingPricesByISIN() {
        _singleStockHistoricalPricesByISIN = new HashMap<>();

        for(ISIN isin: _historicalClosingPrices.get(0).getISINs()) {
            MarketPriceSnapshotBuilder marketPriceSnapshotBuilder = new MarketPriceSnapshotBuilder();

            List<MarketPriceSnapshot> singleStockHistoricalClosingPrices = new ArrayList<>(_historicalClosingPrices.size());

            for(MarketPriceSnapshot allStocksMarketPriceSnapshot: _historicalClosingPrices) {
                marketPriceSnapshotBuilder.setMarketPrice(isin, allStocksMarketPriceSnapshot.getMarketPrice(isin));
                singleStockHistoricalClosingPrices.add(marketPriceSnapshotBuilder.build());
            }

            _singleStockHistoricalPricesByISIN.put(isin, singleStockHistoricalClosingPrices);
        }
    }
}
