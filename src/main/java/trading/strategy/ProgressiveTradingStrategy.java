package trading.strategy;

import trading.account.Account;
import trading.broker.Broker;

 /**
 * Progressive Trading Strategy
 *
 * Invests all available money into one specified stock.
 * Buys and sells alternate and are dependent on the market situation.
 *
 * Parameters:
 *
 *  1. (ISIN) isin
 *  2. (int) buyTriggerRisingDaysInSequence; >= 0
 *  3. (int) sellTriggerDecliningDaysInSequence; >= 0
 *  4. (int) sellTriggerMaxDays; >= 1
 *  5. (int) restartTriggerDecliningDaysInSequence; >= 0
 *
 * Phases:
 *
 *  A: Sets buy order for given ISIN after a series of {buyTriggerRisingDaysInSequence} days has passed.
 *     The maximum possible amount of available money is used for this order.
 *
 *  B: Sets sell order for bought position when one of the following condition occurs:
 *     - {sellTriggerDecliningDaysInSequence} days with negative performance have passed after buying.
 *     - {sellTriggerMaxDays} days have passed after buying.
 *
 *  C: {restartTriggerDecliningDaysInSequence} days with negative performance have to be passed, so that Phase A is entered again.
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

     @Override
     public void notifyDayClosed() {

     }
 }

// TODO sellTriggerDecliningDaysInSequence vs sellTriggerDecliningDays
// TODO restartTriggerDecliningDaysInSequence vs restartTriggerDecliningDays
