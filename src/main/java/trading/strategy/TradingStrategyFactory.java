package trading.strategy;

public interface TradingStrategyFactory {
    TradingStrategy createTradingStrategy(TradingStrategyContext context);
}
