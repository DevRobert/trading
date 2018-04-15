package trading.strategy.compound;

import trading.strategy.TriggerFactory;

public class CompoundTradingStrategyParametersBuilder {
    private ScoringStrategy scoringStrategy;
    private StockSelector stockSelector;
    private TriggerFactory sellTriggerFactory;

    public CompoundTradingStrategyParametersBuilder setScoringStrategy(ScoringStrategy scoringStrategy) {
        this.scoringStrategy = scoringStrategy;
        return this;
    }

    public CompoundTradingStrategyParametersBuilder setStockSelector(StockSelector stockSelector) {
        this.stockSelector = stockSelector;
        return this;
    }

    public CompoundTradingStrategyParametersBuilder setSellTriggerFactory(TriggerFactory sellTriggerFactory) {
        this.sellTriggerFactory = sellTriggerFactory;
        return this;
    }

    public CompoundTradingStrategyParameters build() {
        if(this.scoringStrategy == null) {
            throw new RuntimeException("The scoring strategy was not specified.");
        }

        if(this.stockSelector == null) {
            throw new RuntimeException("The stock selector was not specified.");
        }

        if(this.sellTriggerFactory == null) {
            throw new RuntimeException("The sell trigger factory was not specified.");
        }

        return new CompoundTradingStrategyParameters(
                this.scoringStrategy,
                this.stockSelector,
                this.sellTriggerFactory
        );
    }
}
