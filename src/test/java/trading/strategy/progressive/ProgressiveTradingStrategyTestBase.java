package trading.strategy.progressive;

import org.junit.Before;
import trading.ISIN;
import trading.account.Account;
import trading.broker.Broker;
import trading.market.HistoricalMarketData;
import trading.strategy.*;

public abstract class ProgressiveTradingStrategyTestBase extends TradingStrategyTestBase {
    protected ISIN isin;
    protected int buyTriggerRisingDaysInSequence;
    protected int sellTriggerDecliningDays;
    protected int sellTriggerMaxDays;
    protected int restartTriggerDecliningDays;

    @Before
    public void progressiveTradingStrategyTestBaseBefore() {
        ProgressiveTradingStrategyParameters defaultParameters = ProgressiveTradingStrategyParameters.getDefault();

        isin = defaultParameters.getISIN();
        buyTriggerRisingDaysInSequence = defaultParameters.getBuyTriggerRisingDaysInSequence();
        sellTriggerDecliningDays = defaultParameters.getSellTriggerDecliningDays();
        sellTriggerMaxDays = defaultParameters.getSellTriggerMaxDays();
        restartTriggerDecliningDays = defaultParameters.getRestartTriggerDecliningDays();
    }

    @Override
    protected TradingStrategy initializeTradingStrategy(Account account, Broker broker, HistoricalMarketData historicalMarketData) {
        ProgressiveTradingStrategyParameters parameters = new ProgressiveTradingStrategyParameters(
                isin,
                buyTriggerRisingDaysInSequence,
                sellTriggerDecliningDays,
                sellTriggerMaxDays,
                restartTriggerDecliningDays);

        return new ProgressiveTradingStrategy(parameters, account, broker, historicalMarketData);
    }
}
