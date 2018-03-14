package trading.challenges;

import trading.ISIN;
import trading.market.MarketPriceSnapshot;
import trading.simulation.MongoMultiStockMarketDataStore;
import trading.simulation.MongoMultiStockMarketDataStoreParametersBuilder;

import java.util.List;
import java.util.Set;

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

    public static Set<ISIN> getISINs() {
        return getHistoricalClosingPrices().get(0).getISINs();
    }
}
