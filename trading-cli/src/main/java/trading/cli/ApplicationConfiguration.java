package trading.cli;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import trading.domain.simulation.MultiStockMarketDataStore;
import trading.persistence.market.MongoMultiStockMarketDataStore;
import trading.persistence.market.MongoMultiStockMarketDataStoreParameters;
import trading.persistence.market.MongoMultiStockMarketDataStoreParametersBuilder;

@Configuration
public class ApplicationConfiguration {
    @Bean
    public MultiStockMarketDataStore getMultiStockMarketDataStore() {
        MongoMultiStockMarketDataStoreParameters parameters = new MongoMultiStockMarketDataStoreParametersBuilder()
                .setDatabase("trading")
                .setCollection("merged-quotes")
                .build();

        return new MongoMultiStockMarketDataStore(parameters);
    }
}
