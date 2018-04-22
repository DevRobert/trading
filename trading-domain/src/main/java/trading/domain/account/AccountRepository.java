package trading.domain.account;

import trading.domain.Amount;
import trading.domain.ClientId;

public interface AccountRepository {
    Account createAccount(ClientId clientId, Amount seedCapital);
    Account getAccount(AccountId accountId);
    void saveAccount(Account account);
}
