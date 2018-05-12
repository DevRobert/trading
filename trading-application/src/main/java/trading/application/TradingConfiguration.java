package trading.application;

import trading.domain.DayCount;
import trading.domain.broker.CommissionStrategies;
import trading.domain.broker.CommissionStrategy;
import trading.domain.strategy.compoundLocalMaximum.CompoundLocalMaximumTradingStrategyParameters;

public class TradingConfiguration {
    public static CompoundLocalMaximumTradingStrategyParameters getParameters() {
        DayCount buyTriggerLocalMaximumLookBehindPeriod = new DayCount(10);
        double buyTriggerMinDeclineFromLocalMaximumPercentage = 0.1;
        double sellTriggerTrailingStopLossMinDeclineFromMaximumAfterBuyingPercentage = 0.07;
        double activateTrailingStopLossMinRaiseSinceBuyingPercentage = 0.03;
        double sellTriggerStopLossMinimumDeclineSinceBuyingPercentage = 0.07;
        double maximumPercentage = 0.2;

        return new CompoundLocalMaximumTradingStrategyParameters(
                buyTriggerLocalMaximumLookBehindPeriod,
                buyTriggerMinDeclineFromLocalMaximumPercentage,
                sellTriggerTrailingStopLossMinDeclineFromMaximumAfterBuyingPercentage,
                activateTrailingStopLossMinRaiseSinceBuyingPercentage,
                sellTriggerStopLossMinimumDeclineSinceBuyingPercentage,
                maximumPercentage
        );
    }

    public static CommissionStrategy getCommissionStrategy() {
        return CommissionStrategies.getDegiroXetraCommissionStrategy();
    }
}
