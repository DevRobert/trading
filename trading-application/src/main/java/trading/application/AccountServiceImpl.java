package trading.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import trading.domain.account.*;

@Component
public class AccountServiceImpl implements AccountService {
    private AccountRepository accountRepository;

    @Autowired
    public AccountServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public Account getAccount(AccountId accountId) {
        return this.accountRepository.getAccount(accountId);
    }

    @Override
    public void registerTransaction(AccountId accountId, Transaction transaction) {
        Account account = this.accountRepository.getAccount(accountId);
        account.registerTransaction(transaction);
        this.accountRepository.saveAccount(account);
    }
}
