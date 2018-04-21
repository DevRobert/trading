package trading.domain.strategy.buyAndSellAlternating;

import trading.domain.ISIN;
import trading.domain.strategy.AlwaysFiresTrigger;
import trading.domain.strategy.TradingStrategy;
import trading.domain.strategy.TradingStrategyContext;
import trading.domain.strategy.progressive.ProgressiveTradingStrategy;
import trading.domain.strategy.progressive.ProgressiveTradingStrategyParametersBuilder;

public class BuyAndSellAlternatingTradingStrategy implements TradingStrategy {
    private final ProgressiveTradingStrategy progressiveTradingStrategy;

    public BuyAndSellAlternatingTradingStrategy(ISIN isin, TradingStrategyContext context) {
        ProgressiveTradingStrategyParametersBuilder progressiveTradingStrategyParametersBuilder = new ProgressiveTradingStrategyParametersBuilder();

        progressiveTradingStrategyParametersBuilder.setISIN(isin);
        progressiveTradingStrategyParametersBuilder.setBuyTriggerFactory(historicalMarketData -> new AlwaysFiresTrigger());
        progressiveTradingStrategyParametersBuilder.setSellTriggerFactory(historicalMarketData -> new AlwaysFiresTrigger());
        progressiveTradingStrategyParametersBuilder.setResetTriggerFactory(historicalMarketData -> new AlwaysFiresTrigger());

        this.progressiveTradingStrategy = new ProgressiveTradingStrategy(progressiveTradingStrategyParametersBuilder.build(), context);
    }

    @Override
    public void prepareOrdersForNextTradingDay() {
        this.progressiveTradingStrategy.prepareOrdersForNextTradingDay();
    }
}
