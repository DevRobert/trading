package trading.domain.strategy.compound;

import trading.domain.strategy.TriggerFactory;

public class CompoundTradingStrategyParameters {
    private final ScoringStrategy scoringStrategy;
    private final StockSelector stockSelector;
    private final TriggerFactory sellTriggerFactory;

    public ScoringStrategy getScoringStrategy() {
        return this.scoringStrategy;
    }

    public StockSelector getStockSelector() {
        return this.stockSelector;
    }

    public TriggerFactory getSellTriggerFactory() {
        return this.sellTriggerFactory;
    }

    public CompoundTradingStrategyParameters(ScoringStrategy scoringStrategy, StockSelector stockSelector, TriggerFactory sellTriggerFactory) {
        this.scoringStrategy = scoringStrategy;
        this.stockSelector = stockSelector;
        this.sellTriggerFactory = sellTriggerFactory;
    }
}
