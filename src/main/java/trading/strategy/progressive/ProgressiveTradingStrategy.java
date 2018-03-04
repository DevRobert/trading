package trading.strategy.progressive;

import trading.account.Account;
import trading.broker.Broker;
import trading.market.HistoricalMarketData;
import trading.strategy.StrategyInitializationException;
import trading.strategy.TradingStrategy;

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
 *  3. (int) sellTriggerDecliningDays; >= 0
 *  4. (int) sellTriggerMaxDays; >= 1
 *  5. (int) restartTriggerDecliningDays; >= 0
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
    private final HistoricalMarketData historicalMarketData;

    public ProgressiveTradingStrategy(ProgressiveTradingStrategyParameters parameters, Account account, Broker broker, HistoricalMarketData historicalMarketData) {
        if(parameters == null) {
            throw new StrategyInitializationException("The strategy parameters were not specified.");
        }

        if(account == null) {
            throw new StrategyInitializationException("The account was not specified.");
        }

        if(broker == null) {
            throw new StrategyInitializationException("The broker was not specified.");
        }

        if(historicalMarketData == null) {
            throw new StrategyInitializationException("The historical market data were not specified.");
        }

        if(!historicalMarketData.getAvailableStocks().contains(parameters.getISIN())) {
            throw new StrategyInitializationException(String.format("The ISIN parameter '%s' does not refer to an available stock.", parameters.getISIN().getText()));
        }

        this.parameters = null;
        this.account = null;
        this.broker = null;
        this.historicalMarketData = null;
    }

     @Override
     public void prepareOrdersForNextTradingDay() {

     }
 }
