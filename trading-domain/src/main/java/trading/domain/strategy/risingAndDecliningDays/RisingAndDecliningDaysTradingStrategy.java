package trading.domain.strategy.risingAndDecliningDays;

import trading.domain.market.HistoricalStockData;
import trading.domain.strategy.AlwaysFiresTrigger;
import trading.domain.strategy.DelegateTrigger;
import trading.domain.strategy.TradingStrategy;
import trading.domain.strategy.TradingStrategyContext;
import trading.domain.strategy.progressive.ProgressiveTradingStrategy;
import trading.domain.strategy.progressive.ProgressiveTradingStrategyParametersBuilder;

public class RisingAndDecliningDaysTradingStrategy implements TradingStrategy {
    private final ProgressiveTradingStrategy progressiveTradingStrategy;

    public RisingAndDecliningDaysTradingStrategy(RisingAndDecliningDaysTradingStrategyParameters parameters, TradingStrategyContext context) {
        final int risingDaysInSequence = parameters.getBuyAfterRisingDaysInSequence().getValue();
        final int decliningDaysInSequence = parameters.getSellAfterDecliningDaysInSequence().getValue();

        ProgressiveTradingStrategyParametersBuilder parametersBuilder = new ProgressiveTradingStrategyParametersBuilder();

        parametersBuilder.setISIN(parameters.getISIN());

        parametersBuilder.setBuyTriggerFactory(isin -> {
            HistoricalStockData historicalStockData = context.getHistoricalMarketData().getStockData(isin);
            return new DelegateTrigger(() -> historicalStockData.getRisingDaysInSequence() >= risingDaysInSequence);
        });

        parametersBuilder.setSellTriggerFactory(isin -> {
            HistoricalStockData historicalStockData = context.getHistoricalMarketData().getStockData(isin);
            return new DelegateTrigger(() -> historicalStockData.getDecliningDaysInSequence() >= decliningDaysInSequence);
        });

        parametersBuilder.setResetTriggerFactory(historicalMarketData -> new AlwaysFiresTrigger());

        this.progressiveTradingStrategy = new ProgressiveTradingStrategy(parametersBuilder.build(), context);
    }

    @Override
    public void prepareOrdersForNextTradingDay() {
        this.progressiveTradingStrategy.prepareOrdersForNextTradingDay();
    }
}
