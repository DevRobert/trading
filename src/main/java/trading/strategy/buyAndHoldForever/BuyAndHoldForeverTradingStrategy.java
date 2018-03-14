package trading.strategy.buyAndHoldForever;

import trading.ISIN;
import trading.strategy.AlwaysFiresTrigger;
import trading.strategy.NeverFiresTrigger;
import trading.strategy.TradingStrategy;
import trading.strategy.TradingStrategyContext;
import trading.strategy.progressive.ProgressiveTradingStrategy;
import trading.strategy.progressive.ProgressiveTradingStrategyParametersBuilder;

public class BuyAndHoldForeverTradingStrategy implements TradingStrategy {
    private final ProgressiveTradingStrategy progressiveTradingStrategy;

    public BuyAndHoldForeverTradingStrategy(ISIN isin, TradingStrategyContext context) {
        ProgressiveTradingStrategyParametersBuilder progressiveTradingStrategyParametersBuilder = new ProgressiveTradingStrategyParametersBuilder();

        progressiveTradingStrategyParametersBuilder.setISIN(ISIN.MunichRe);
        progressiveTradingStrategyParametersBuilder.setBuyTriggerFactory(historicalMarketData -> new AlwaysFiresTrigger());
        progressiveTradingStrategyParametersBuilder.setSellTriggerFactory(historicalMarketData -> new NeverFiresTrigger());
        progressiveTradingStrategyParametersBuilder.setResetTriggerFactory(historicalMarketData -> new AlwaysFiresTrigger());

        this.progressiveTradingStrategy = new ProgressiveTradingStrategy(progressiveTradingStrategyParametersBuilder.build(), context);
    }

    @Override
    public void prepareOrdersForNextTradingDay() {
        this.progressiveTradingStrategy.prepareOrdersForNextTradingDay();
    }
}
