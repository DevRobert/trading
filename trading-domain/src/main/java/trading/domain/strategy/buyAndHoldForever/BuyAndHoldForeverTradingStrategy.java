package trading.domain.strategy.buyAndHoldForever;

import trading.domain.ISIN;
import trading.domain.strategy.AlwaysFiresTrigger;
import trading.domain.strategy.NeverFiresTrigger;
import trading.domain.strategy.TradingStrategy;
import trading.domain.strategy.TradingStrategyContext;
import trading.domain.strategy.progressive.ProgressiveTradingStrategy;
import trading.domain.strategy.progressive.ProgressiveTradingStrategyParametersBuilder;

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
