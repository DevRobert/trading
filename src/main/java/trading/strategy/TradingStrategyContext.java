package trading.strategy;

import trading.account.Account;
import trading.broker.Broker;
import trading.market.HistoricalMarketData;

/**
 * Typical context trading strategies need to do their work.
 */
public class TradingStrategyContext {
    private Account account;
    private Broker broker;
    private HistoricalMarketData historicalMarketData;

    public Account getAccount() {
        return this.account;
    }

    public Broker getBroker() {
        return this.broker;
    }

    public HistoricalMarketData getHistoricalMarketData() {
        return this.historicalMarketData;
    }

    public TradingStrategyContext(Account account, Broker broker, HistoricalMarketData historicalMarketData) {
        if(account == null) {
            throw new RuntimeException("The account must be specified.");
        }

        if(broker == null) {
            throw new RuntimeException("The broker must be specified.");
        }

        if(historicalMarketData == null) {
            throw new RuntimeException("The historical market data must be specified.");
        }

        this.account = account;
        this.broker = broker;
        this.historicalMarketData = historicalMarketData;
    }
}
