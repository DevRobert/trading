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
 * maximum passed and declined under certain level below maximum since buying.
 */
public class LocalMaximumTradingStrategy implements TradingStrategy {
    private final ProgressiveTradingStrategy progressiveTradingStrategy;

    public LocalMaximumTradingStrategy(LocalMaximumTradingStrategyParameters parameters, TradingStrategyContext context) {
        ProgressiveTradingStrategyParametersBuilder parametersBuilder = new ProgressiveTradingStrategyParametersBuilder();

        parametersBuilder.setISIN(parameters.getIsin());

        AtomicReference<Double> buyLocalMaximum = new AtomicReference<>(0.0);
        AtomicReference<Boolean> buyLocalMaximumPassed = new AtomicReference<>(false);
        AtomicReference<Double> maximumSinceBuying = new AtomicReference<>(0.0);

        parametersBuilder.setBuyTriggerFactory(historicalMarketData -> {
            HistoricalStockData historicalStockData = historicalMarketData.getStockData(parameters.getIsin());

            return new DelegateTrigger(() -> {
                double localMaximum = historicalStockData.getMaximumClosingMarketPrice(parameters.getBuyTriggerLocalMaximumLookBehindPeriod()).getValue();
                double minDelta = localMaximum * parameters.getBuyTriggerMinDistanceFromLocalMaximumPercentage();
                double maxBuyPrice = localMaximum - minDelta;
                double lastClosingMarketPrice = historicalStockData.getLastClosingMarketPrice().getValue();

                if(lastClosingMarketPrice <= maxBuyPrice) {
                    buyLocalMaximum.set(localMaximum);
                    buyLocalMaximumPassed.set(false);
                    maximumSinceBuying.set(0.0);
                    return true;
                }
                else {
                    return false;
                }
            });
        });

        parametersBuilder.setSellTriggerFactory(historicalMarketData -> {
            HistoricalStockData historicalStockData = historicalMarketData.getStockData(parameters.getIsin());

            return new DelegateTrigger(() -> {
                double lastClosingPrice = historicalStockData.getLastClosingMarketPrice().getValue();

                if(!buyLocalMaximumPassed.get()) {
                    buyLocalMaximumPassed.set(lastClosingPrice >= buyLocalMaximum.get());
                }

                if(!buyLocalMaximumPassed.get()) {
                    return false;
                }

                if(lastClosingPrice > maximumSinceBuying.get()) {
                    maximumSinceBuying.set(lastClosingPrice);
                }

                double sellTriggerMinDeltaFromMaximumSinceBuying = parameters.getSellTriggerMinDistanceFromMaximumSinceBuyingPercentage() * maximumSinceBuying.get();
                double sellTriggerMaxPrice = maximumSinceBuying.get() - sellTriggerMinDeltaFromMaximumSinceBuying;

                return lastClosingPrice <= sellTriggerMaxPrice;
            });
        });

        parametersBuilder.setResetTriggerFactory(historicalMarketData -> new AlwaysFiresTrigger());

        this.progressiveTradingStrategy = new ProgressiveTradingStrategy(parametersBuilder.build(), context);
    }

    @Override
    public void prepareOrdersForNextTradingDay() {
        this.progressiveTradingStrategy.prepareOrdersForNextTradingDay();
    }
}
