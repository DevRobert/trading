package trading.domain.strategy.progressive;

import trading.domain.ISIN;
import trading.domain.strategy.TriggerFactory;

public class ProgressiveTradingStrategyParametersBuilder {
    private ISIN isin;
    private TriggerFactory buyTriggerFactory;
    private TriggerFactory sellTriggerFactory;
    private TriggerFactory resetTriggerFactory;

    public void setISIN(ISIN isin) {
        this.isin = isin;
    }

    public void setBuyTriggerFactory(TriggerFactory buyTriggerFactory) {
        this.buyTriggerFactory = buyTriggerFactory;
    }

    public void setSellTriggerFactory(TriggerFactory sellTriggerFactory) {
        this.sellTriggerFactory = sellTriggerFactory;
    }

    public void setResetTriggerFactory(TriggerFactory resetTriggerFactory) {
        this.resetTriggerFactory = resetTriggerFactory;
    }

    public ProgressiveTradingStrategyParameters build() {
        return new ProgressiveTradingStrategyParameters(
                isin,
                buyTriggerFactory,
                sellTriggerFactory,
                resetTriggerFactory
        );
    }
}
