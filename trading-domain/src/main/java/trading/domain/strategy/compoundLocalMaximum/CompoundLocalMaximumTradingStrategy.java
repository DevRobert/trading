package trading.domain.strategy.compoundLocalMaximum;

import trading.domain.strategy.TradingStrategy;
import trading.domain.strategy.TradingStrategyContext;
import trading.domain.strategy.compound.*;

public class CompoundLocalMaximumTradingStrategy implements TradingStrategy {
    private final CompoundTradingStrategy compoundTradingStrategy;

    public CompoundLocalMaximumTradingStrategy(CompoundLocalMaximumTradingStrategyParameters parameters, TradingStrategyContext context) {
        CompoundTradingStrategyParameters compoundTradingStrategyParameters = new CompoundTradingStrategyParametersBuilder()
                .setBuyScoringStrategy(new LocalMaximumBuyScoringStrategy(
                        parameters.getBuyTriggerLocalMaximumLookBehindPeriod(),
                        parameters.getBuyTriggerMinDeclineFromLocalMaximumPercentage()))
                .setSellStocksSelector(new SellStocksSelector(new Score(0.5)))
                .setSellScoringStrategy(new LocalMaximumSellScoringStrategy(
                        parameters.getActivateTrailingStopLossMinRaiseSinceBuyingPercentage(),
                        parameters.getSellTriggerStopLossMinimumDeclineSinceBuyingPercentage(),
                        parameters.getSellTriggerTrailingStopLossMinDeclineFromMaximumAfterBuyingPercentage()
                ))
                .setBuyStocksSelector(new BuyStocksSelector(new Score(0.5), parameters.getMaximumPercentage()))
                .build();

        this.compoundTradingStrategy = new CompoundTradingStrategy(compoundTradingStrategyParameters, context);
    }

    @Override
    public void prepareOrdersForNextTradingDay() {
        this.compoundTradingStrategy.prepareOrdersForNextTradingDay();
    }
}
