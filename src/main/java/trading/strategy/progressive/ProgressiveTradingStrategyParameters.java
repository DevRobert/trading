package trading.strategy.progressive;

import trading.ISIN;
import trading.strategy.StrategyInitializationException;
import trading.strategy.Trigger;

public class ProgressiveTradingStrategyParameters {
    private final ISIN isin;
    private final Trigger buyTrigger;
    private final Trigger sellTrigger;
    private final Trigger resetTrigger;

    public ISIN getISIN() {
        return isin;
    }

    public Trigger getBuyTrigger() {
        return buyTrigger;
    }

    public Trigger getSellTrigger() {
        return sellTrigger;
    }

    public Trigger getResetTrigger() {
        return resetTrigger;
    }

    public ProgressiveTradingStrategyParameters(ISIN isin, Trigger buyTrigger, Trigger sellTrigger, Trigger resetTrigger) {
        if(isin == null) {
            throw new StrategyInitializationException("The ISIN must be specified.");
        }

        if(buyTrigger == null) {
            throw new StrategyInitializationException("The buy trigger must be specified.");
        }

        if(sellTrigger == null) {
            throw new StrategyInitializationException("The sell trigger must be specified.");
        }

        if(resetTrigger == null) {
            throw new StrategyInitializationException("The reset trigger must be specified.");
        }

        this.isin = isin;
        this.buyTrigger = buyTrigger;
        this.sellTrigger = sellTrigger;
        this.resetTrigger = resetTrigger;
    }
}
