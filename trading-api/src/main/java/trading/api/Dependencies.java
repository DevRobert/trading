package trading.api;

import trading.domain.simulation.MultiStockMarketDataStore;
import trading.persistence.market.MongoMultiStockMarketDataStore;
import trading.persistence.market.MongoMultiStockMarketDataStoreParameters;
import trading.persistence.market.MongoMultiStockMarketDataStoreParametersBuilder;

public class Dependencies {
    public static MultiStockMarketDataStore getMultiStockMarketDataStore() {
        MongoMultiStockMarketDataStoreParameters parameters = new MongoMultiStockMarketDataStoreParametersBuilder()
                .setDatabase("trading")
                .setCollection("merged-quotes")
                .build();

        return new MongoMultiStockMarketDataStore(parameters);
    }
}
