package trading.application;

import trading.domain.Transaction;
import trading.domain.account.Account;
import trading.domain.account.AccountId;

public interface AccountService {
    Account getAccount(AccountId accountId);
    void registerTransaction(AccountId accountId, Transaction transaction);
}
