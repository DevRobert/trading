package trading.application;

import org.springframework.stereotype.Component;
import trading.domain.DayCount;
import trading.domain.account.TaxStrategies;
import trading.domain.account.TaxStrategy;
import trading.domain.broker.CommissionStrategies;
import trading.domain.broker.DynamicCommissionStrategyParameters;
import trading.domain.strategy.compoundLocalMaximum.CompoundLocalMaximumTradingStrategyParameters;

@Component
public class TradingConfigurationServiceImpl implements TradingConfigurationService {
    @Override
    public DynamicCommissionStrategyParameters getCommissionStrategyParameters() {
        return CommissionStrategies.getDegiroXetraCommissionStrategyParameters();
    }

    @Override
    public CompoundLocalMaximumTradingStrategyParameters getTradingStrategyParameters() {
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

    @Override
    public TaxStrategy getTaxStrategy() {
        return TaxStrategies.getDefaultTaxStrategy();
    }
}
