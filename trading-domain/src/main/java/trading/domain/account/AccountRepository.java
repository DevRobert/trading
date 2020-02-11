package trading.domain.account;

import trading.domain.Amount;
import trading.domain.ClientId;

// TODO RB 11 Feb 2020 - Tax may should be saved as part of the account
public interface AccountRepository {
    Account createAccount(ClientId clientId, Amount seedCapital, TaxStrategy taxStrategy);
    Account getAccount(AccountId accountId, TaxStrategy taxStrategy);
    void saveAccount(Account account);
}
