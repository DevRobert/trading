package trading.domain.strategy.compound;

public class CompoundTradingStrategyParametersBuilder {
    private ScoringStrategy buyScoringStrategy;
    private BuyStocksSelector buyStocksSelector;
    private ScoringStrategy sellScoringStrategy;
    private SellStocksSelector sellStocksSelector;

    public CompoundTradingStrategyParametersBuilder setBuyScoringStrategy(ScoringStrategy scoringStrategy) {
        this.buyScoringStrategy = scoringStrategy;
        return this;
    }

    public CompoundTradingStrategyParametersBuilder setBuyStocksSelector(BuyStocksSelector buyStocksSelector) {
        this.buyStocksSelector = buyStocksSelector;
        return this;
    }

    public CompoundTradingStrategyParametersBuilder setSellScoringStrategy(ScoringStrategy scoringStrategy) {
        this.sellScoringStrategy = scoringStrategy;
        return this;
    }

    public CompoundTradingStrategyParametersBuilder setSellStocksSelector(SellStocksSelector sellStocksSelector) {
        this.sellStocksSelector = sellStocksSelector;
        return this;
    }

    public CompoundTradingStrategyParameters build() {
        return new CompoundTradingStrategyParameters(
                this.buyScoringStrategy,
                this.buyStocksSelector,
                this.sellScoringStrategy,
                this.sellStocksSelector
        );
    }
}
