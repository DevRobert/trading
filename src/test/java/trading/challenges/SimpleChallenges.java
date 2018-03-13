package trading.challenges;

import org.junit.Test;
import trading.ISIN;
import trading.strategy.AlwaysFiresTrigger;
import trading.strategy.NeverFiresTrigger;

public class SimpleChallenges extends ProgressiveChallengesBase {
    @Test
    public void buyAndHoldForever() {
        this.progressiveTradingStrategyParametersBuilder.setISIN(ISIN.MunichRe);
        this.progressiveTradingStrategyParametersBuilder.setBuyTriggerFactory(historicalMarketData -> new AlwaysFiresTrigger());
        this.progressiveTradingStrategyParametersBuilder.setSellTriggerFactory(historicalMarketData -> new NeverFiresTrigger());
        this.progressiveTradingStrategyParametersBuilder.setResetTriggerFactory(historicalMarketData -> new AlwaysFiresTrigger());
    }

    @Test
    public void buyAndSellAlternating() {
        this.progressiveTradingStrategyParametersBuilder.setISIN(ISIN.MunichRe);
        this.progressiveTradingStrategyParametersBuilder.setBuyTriggerFactory(historicalMarketData -> new AlwaysFiresTrigger());
        this.progressiveTradingStrategyParametersBuilder.setSellTriggerFactory(historicalMarketData -> new AlwaysFiresTrigger());
        this.progressiveTradingStrategyParametersBuilder.setResetTriggerFactory(historicalMarketData -> new AlwaysFiresTrigger());
    }
}
