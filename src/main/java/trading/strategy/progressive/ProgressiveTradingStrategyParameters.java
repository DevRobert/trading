package trading.strategy.progressive;

import trading.ISIN;
import trading.strategy.StrategyInitializationException;
import trading.strategy.TriggerFactory;

public class ProgressiveTradingStrategyParameters {
    private final ISIN isin;
    private final TriggerFactory buyTriggerFactory;
    private final TriggerFactory sellTriggerFactory;
    private final TriggerFactory resetTriggerFactory;

    public ISIN getISIN() {
        return isin;
    }

    public TriggerFactory getBuyTriggerFactory() {
        return this.buyTriggerFactory;
    }

    public TriggerFactory getSellTriggerFactory() {
        return this.sellTriggerFactory;
    }

    public TriggerFactory getResetTriggerFactory() {
        return this.resetTriggerFactory;
    }

    public ProgressiveTradingStrategyParameters(ISIN isin, TriggerFactory buyTriggerFactory, TriggerFactory sellTriggerFactory, TriggerFactory resetTriggerFactory) {
        if(isin == null) {
            throw new StrategyInitializationException("The ISIN must be specified.");
        }

        if(buyTriggerFactory == null) {
            throw new StrategyInitializationException("The buy trigger factory must be specified.");
        }

        if(sellTriggerFactory == null) {
            throw new StrategyInitializationException("The sell trigger factory must be specified.");
        }

        if(resetTriggerFactory == null) {
            throw new StrategyInitializationException("The reset trigger factory must be specified.");
        }

        this.isin = isin;
        this.buyTriggerFactory = buyTriggerFactory;
        this.sellTriggerFactory = sellTriggerFactory;
        this.resetTriggerFactory = resetTriggerFactory;
    }
}
