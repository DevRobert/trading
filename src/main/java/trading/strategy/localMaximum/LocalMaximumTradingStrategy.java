package trading.strategy.localMaximum;

import trading.market.HistoricalStockData;
import trading.strategy.AlwaysFiresTrigger;
import trading.strategy.DelegateTrigger;
import trading.strategy.TradingStrategy;
import trading.strategy.TradingStrategyContext;
import trading.strategy.progressive.ProgressiveTradingStrategy;
import trading.strategy.progressive.ProgressiveTradingStrategyParametersBuilder;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Trading strategy:
 *
 * Buy after distance from local maximum reached and sell after
 * local maximum reached again.
 */
public class LocalMaximumTradingStrategy implements TradingStrategy {
    private final ProgressiveTradingStrategy progressiveTradingStrategy;

    public LocalMaximumTradingStrategy(LocalMaximumTradingStrategyParameters parameters, TradingStrategyContext context) {
        ProgressiveTradingStrategyParametersBuilder parametersBuilder = new ProgressiveTradingStrategyParametersBuilder();

        parametersBuilder.setISIN(parameters.getIsin());

        AtomicReference<Double> buyLocalMaximum = new AtomicReference<>(0.0);

        parametersBuilder.setBuyTriggerFactory(historicalMarketData -> {
            HistoricalStockData historicalStockData = historicalMarketData.getStockData(parameters.getIsin());

            return new DelegateTrigger(() -> {
                double localMaximum = historicalStockData.getMaximumClosingMarketPrice(parameters.getLocalMaxiumLookBehindPeriod()).getValue();
                double minDelta = localMaximum * parameters.getMinDistanceFromLocalMaxmiumPercentage();
                double maxBuyPrice = localMaximum - minDelta;
                double lastClosingMarketPrice = historicalStockData.getLastClosingMarketPrice().getValue();

                if(lastClosingMarketPrice <= maxBuyPrice) {
                    buyLocalMaximum.set(localMaximum);
                    return true;
                }
                else {
                    return false;
                }
            });
        });

        parametersBuilder.setSellTriggerFactory(historicalMarketData -> {
            HistoricalStockData historicalStockData = historicalMarketData.getStockData(parameters.getIsin());
            return new DelegateTrigger(() -> historicalStockData.getLastClosingMarketPrice().getValue() >= buyLocalMaximum.get());
        });

        parametersBuilder.setResetTriggerFactory(historicalMarketData -> new AlwaysFiresTrigger());

        this.progressiveTradingStrategy = new ProgressiveTradingStrategy(parametersBuilder.build(), context);
    }

    @Override
    public void prepareOrdersForNextTradingDay() {
        this.progressiveTradingStrategy.prepareOrdersForNextTradingDay();
    }
}
