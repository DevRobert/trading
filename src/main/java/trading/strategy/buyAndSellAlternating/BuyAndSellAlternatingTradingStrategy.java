package trading.strategy.buyAndSellAlternating;

import trading.ISIN;
import trading.strategy.AlwaysFiresTrigger;
import trading.strategy.TradingStrategy;
import trading.strategy.TradingStrategyContext;
import trading.strategy.progressive.ProgressiveTradingStrategy;
import trading.strategy.progressive.ProgressiveTradingStrategyParametersBuilder;

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
