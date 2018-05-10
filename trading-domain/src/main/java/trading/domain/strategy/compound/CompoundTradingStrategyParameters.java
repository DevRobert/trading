package trading.domain.strategy.compound;

public class CompoundTradingStrategyParameters {
    private final ScoringStrategy buyScoringStrategy;
    private final BuyStocksSelector buyStocksSelector;
    private final ScoringStrategy sellScoringStrategy;
    private final SellStocksSelector sellStocksSelector;

    public ScoringStrategy getBuyScoringStrategy() {
        return this.buyScoringStrategy;
    }

    public BuyStocksSelector getBuyStocksSelector() {
        return this.buyStocksSelector;
    }

    public ScoringStrategy getSellScoringStrategy() {
        return this.sellScoringStrategy;
    }

    public SellStocksSelector getSellStocksSelector() {
        return this.sellStocksSelector;
    }

    public CompoundTradingStrategyParameters(ScoringStrategy buyScoringStrategy, BuyStocksSelector buyStocksSelector, ScoringStrategy sellScoringStrategy, SellStocksSelector sellStocksSelector) {
        if(buyScoringStrategy == null) {
            throw new RuntimeException("The buy scoring strategy was not specified.");
        }

        if(buyStocksSelector == null) {
            throw new RuntimeException("The buy stock selector was not specified.");
        }

        if(sellScoringStrategy == null) {
            throw new RuntimeException("The sell scoring strategy was not specified.");
        }


        if(sellStocksSelector == null) {
            throw new RuntimeException("The sell stock selector was not specified.");
        }


        this.buyScoringStrategy = buyScoringStrategy;
        this.buyStocksSelector = buyStocksSelector;
        this.sellScoringStrategy = sellScoringStrategy;
        this.sellStocksSelector = sellStocksSelector;
    }
}
