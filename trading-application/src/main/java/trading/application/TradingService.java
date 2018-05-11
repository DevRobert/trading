package trading.application;

import trading.domain.account.Account;

public interface TradingService {
    TradeList calculateTrades(Account account);
}
