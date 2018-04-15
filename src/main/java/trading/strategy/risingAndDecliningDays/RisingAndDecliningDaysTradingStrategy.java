package trading.strategy.risingAndDecliningDays;

import trading.market.HistoricalStockData;
import trading.strategy.AlwaysFiresTrigger;
import trading.strategy.DelegateTrigger;
import trading.strategy.TradingStrategy;
import trading.strategy.TradingStrategyContext;
import trading.strategy.progressive.ProgressiveTradingStrategy;
import trading.strategy.progressive.ProgressiveTradingStrategyParametersBuilder;

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
