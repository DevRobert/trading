package trading.cli;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import trading.domain.Amount;
import trading.domain.ClientId;
import trading.domain.account.Account;
import trading.domain.account.AccountId;
import trading.domain.account.AccountRepository;
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

    @Bean
    public AccountRepository getAccountRepository() {
        return new AccountRepository() {
            @Override
            public Account createAccount(ClientId clientId, Amount seedCapital) {
                return null;
            }

            @Override
            public Account getAccount(AccountId accountId) {
                return null;
            }

            @Override
            public void saveAccount(Account account) {

            }
        };
    }
}
