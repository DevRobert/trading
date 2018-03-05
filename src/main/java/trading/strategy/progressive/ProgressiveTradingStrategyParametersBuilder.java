package trading.strategy.progressive;

import trading.ISIN;
import trading.strategy.Trigger;

public class ProgressiveTradingStrategyParametersBuilder {
    private ISIN isin;
    private Trigger buyTrigger;
    private Trigger sellTrigger;
    private Trigger resetTrigger;

    public void setISIN(ISIN isin) {
        this.isin = isin;
    }

    public void setBuyTrigger(Trigger buyTrigger) {
        this.buyTrigger = buyTrigger;
    }

    public void setSellTrigger(Trigger sellTrigger) {
        this.sellTrigger = sellTrigger;
    }

    public void setResetTrigger(Trigger resetTrigger) {
        this.resetTrigger = resetTrigger;
    }

    public ProgressiveTradingStrategyParameters build() {
        return new ProgressiveTradingStrategyParameters(
                isin,
                buyTrigger,
                sellTrigger,
                resetTrigger
        );
    }
}
