package trading.cli;

import trading.domain.simulation.MultiStockMarketDataStore;
import trading.persistence.MongoMultiStockMarketDataStore;
import trading.persistence.MongoMultiStockMarketDataStoreParameters;
import trading.persistence.MongoMultiStockMarketDataStoreParametersBuilder;

public class Dependencies {
    protected static MultiStockMarketDataStore getMultiStockMarketDataStore() {
        MongoMultiStockMarketDataStoreParameters parameters = new MongoMultiStockMarketDataStoreParametersBuilder()
                .setDatabase("trading")
                .setCollection("merged-quotes")
                .build();

        return new MongoMultiStockMarketDataStore(parameters);
    }
}
