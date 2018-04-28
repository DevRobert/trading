package trading.api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import trading.domain.market.InstrumentNameProvider;
import trading.domain.simulation.MultiStockMarketDataStore;
import trading.persistence.market.MongoMultiStockMarketDataStore;
import trading.persistence.market.MongoMultiStockMarketDataStoreParameters;
import trading.persistence.market.MongoMultiStockMarketDataStoreParametersBuilder;
import trading.persistence.market.MySqlInstrumentNameProvider;

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

    @Bean
    public InstrumentNameProvider getInstrumentNameProvider() {
        return new MySqlInstrumentNameProvider();
    }
}
