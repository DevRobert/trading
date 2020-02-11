package trading.api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import trading.domain.account.AccountRepository;
import trading.domain.account.TaxStrategies;
import trading.domain.market.InstrumentNameProvider;
import trading.domain.simulation.MultiStockMarketDataStore;
import trading.persistence.MySqlRepositoryParameters;
import trading.persistence.MySqlRepositoryParametersBuilder;
import trading.persistence.account.MySqlAccountRepository;
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

    private MySqlRepositoryParameters getMySqlRepositoryParameters() {
        return new MySqlRepositoryParametersBuilder()
                .setServer("localhost")
                .setUsername("root")
                .setPassword("testtest")
                .setDatabase("trading_production")
                .build();
    }

    @Bean
    public InstrumentNameProvider getInstrumentNameProvider() {
        return new MySqlInstrumentNameProvider(this.getMySqlRepositoryParameters());
    }

    @Bean
    public AccountRepository getAccountRepository() {
        return new MySqlAccountRepository(this.getMySqlRepositoryParameters());
    }
}
