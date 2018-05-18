package trading.cli;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import trading.domain.account.AccountRepository;
import trading.domain.simulation.MultiStockMarketDataStore;
import trading.persistence.MySqlRepositoryParameters;
import trading.persistence.MySqlRepositoryParametersBuilder;
import trading.persistence.account.MySqlAccountRepository;
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

    @Bean
    public AccountRepository getAccountRepository() {
        MySqlRepositoryParameters parameters = new MySqlRepositoryParametersBuilder()
                .setServer("localhost")
                .setUsername("root")
                .setPassword("testtest")
                .setDatabase("trading_production")
                .build();

        return new MySqlAccountRepository(parameters);
    }
}
