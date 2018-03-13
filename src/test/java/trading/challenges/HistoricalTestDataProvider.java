package trading.challenges;

import trading.market.MarketPriceSnapshot;
import trading.simulation.MongoMultiStockMarketDataStore;
import trading.simulation.MongoMultiStockMarketDataStoreParametersBuilder;

import java.util.List;

public abstract class HistoricalTestDataProvider {
    private static List<MarketPriceSnapshot> _historicalClosingPrices;

    public static List<MarketPriceSnapshot> getHistoricalClosingPrices() {
        if(_historicalClosingPrices == null) {
            MongoMultiStockMarketDataStoreParametersBuilder parametersBuilder = new MongoMultiStockMarketDataStoreParametersBuilder();
            parametersBuilder.setDatabase("trading");
            parametersBuilder.setCollection("merged-quotes");
            MongoMultiStockMarketDataStore dataStore = new MongoMultiStockMarketDataStore(parametersBuilder.build());
            _historicalClosingPrices = dataStore.getAllClosingPrices();
        }

        return _historicalClosingPrices;
    }
}
