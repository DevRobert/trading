package trading.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import trading.domain.account.Account;
import trading.domain.account.AccountId;
import trading.domain.account.AccountRepository;

@Component
public class AccountServiceImpl implements AccountService {
    @Autowired
    private AccountRepository accountRepository;

    @Override
    public Account getAccount(AccountId accountId) {
        return this.accountRepository.getAccount(accountId);
    }
}
