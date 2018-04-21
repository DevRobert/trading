package trading.domain.strategy;

public interface TradingStrategyFactory {
    TradingStrategy createTradingStrategy(TradingStrategyContext context);
}
