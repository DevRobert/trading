package trading.application;

import trading.domain.account.Account;
import trading.domain.account.AccountId;
import trading.domain.account.Transaction;

public interface AccountService {
    Account getAccount(AccountId accountId);
    void registerTransaction(AccountId accountId, Transaction transaction);
}
