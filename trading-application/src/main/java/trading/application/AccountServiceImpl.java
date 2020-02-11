package trading.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import trading.domain.account.*;

@Component
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;
    private final TradingConfigurationService tradingConfigurationService;

    @Autowired
    public AccountServiceImpl(AccountRepository accountRepository, TradingConfigurationService tradingConfigurationService) {
        this.accountRepository = accountRepository;
        this.tradingConfigurationService = tradingConfigurationService;
    }

    @Override
    public Account getAccount(AccountId accountId) {
        return this.accountRepository.getAccount(accountId, this.tradingConfigurationService.getTaxStrategy());
    }

    @Override
    public void registerTransaction(AccountId accountId, Transaction transaction) {
        Account account = this.accountRepository.getAccount(accountId, this.tradingConfigurationService.getTaxStrategy());
        account.registerTransaction(transaction);
        this.accountRepository.saveAccount(account);
    }
}
