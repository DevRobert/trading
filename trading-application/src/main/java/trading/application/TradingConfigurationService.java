package trading.application;

import trading.domain.account.TaxStrategy;
import trading.domain.broker.DynamicCommissionStrategyParameters;
import trading.domain.strategy.compoundLocalMaximum.CompoundLocalMaximumTradingStrategyParameters;

public interface TradingConfigurationService {
    DynamicCommissionStrategyParameters getCommissionStrategyParameters();
    CompoundLocalMaximumTradingStrategyParameters getTradingStrategyParameters();
    TaxStrategy getTaxStrategy();
}
