package trading.strategy;

import trading.account.Account;
import trading.market.MarketPriceSnapshot;
import trading.order.Broker;

/**
 * Progressive Trading Strategy
 *
 * Invests all available money into one specified stock.
 * Buys and sells alternate and are dependent on the market situation.
 *
 * Parameters:
 *
 *  1. isin
 *  2. buyTriggerPositiveSeriesNumDays; >= 0
 *  3. sellTriggerNumNegativeDays; >= 0
 *  4. sellTriggerNumMaxDays; >= 1
 *  5. restartTriggerNumNegativeDays; >= 0
 *
 * Phases:
 *
 *  A: Sets buy order for given ISIN after a series of {buyTriggerPositiveSeriesNumDays} days has passed.
 *     The maximum possible amount of available money is used for this order.
 *
 *  B: Sets sell order for bought position when one of the following condition occurs:
 *     - {sellTriggerNumNegativeDays} days with negative performance have passed after buying.
 *     - {sellTriggerNumMaxDays} days have passed after buying.
 *
 *  C: {restartTriggerNumNegativeDays} days with negative performance have to be passed, so that Phase A is entered again.
 */
public class ProgressiveTradingStrategy implements TradingStrategy {
    private final ProgressiveTradingStrategyParameters parameters;
    private final Account account;
    private final Broker broker;

    protected ProgressiveTradingStrategy(ProgressiveTradingStrategyParameters parameters, Account account, Broker broker) {
        this.parameters = null;
        this.account = null;
        this.broker = null;
    }
}
