package trading.strategy.localMaximum;

import trading.DayCount;
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
 *
 * The buy trigger parameters are selected depending on an detected raise or
 * decline of the stock.
 */
public class DynamicLocalMaximumTradingStrategy implements TradingStrategy {
    private final ProgressiveTradingStrategy progressiveTradingStrategy;

    public DynamicLocalMaximumTradingStrategy(DynamicLocalMaximumTradingStrategyParameters parameters, TradingStrategyContext context) {
        ProgressiveTradingStrategyParametersBuilder parametersBuilder = new ProgressiveTradingStrategyParametersBuilder();

        parametersBuilder.setISIN(parameters.getIsin());

        AtomicReference<Double> buyLocalMaximum = new AtomicReference<>(0.0);
        AtomicReference<Boolean> buyLocalMaximumPassed = new AtomicReference<>(false);
        AtomicReference<Double> maximumSinceBuying = new AtomicReference<>(0.0);

        parametersBuilder.setBuyTriggerFactory(historicalMarketData -> {
            HistoricalStockData historicalStockData = historicalMarketData.getStockData(parameters.getIsin());

            return new DelegateTrigger(() -> {
                double lookBackMarketPrice = historicalStockData.getClosingMarketPrice(parameters.getRisingIndicatorLookBehindPeriod()).getValue();
                double minRisingDelta = lookBackMarketPrice * parameters.getRisingIndicatorMinRisingPercentage();
                double minRisingPrice = lookBackMarketPrice + minRisingDelta;

                double lastClosingMarketPrice = historicalStockData.getLastClosingMarketPrice().getValue();

                DayCount buyTriggerLocalMaximumLookBehindPeriod;
                double buyTriggerMinDistanceFromLocalMaximumPercentage;

                if (lastClosingMarketPrice >= minRisingPrice) {
                    buyTriggerLocalMaximumLookBehindPeriod = parameters.getRisingBuyTriggerLocalMaximumLookBehindPeriod();
                    buyTriggerMinDistanceFromLocalMaximumPercentage = parameters.getRisingBuyTriggerMinDistanceFromLocalMaximumPercentage();
                } else {
                    buyTriggerLocalMaximumLookBehindPeriod = parameters.getDecliningBuyTriggerLocalMaximumLookBehindPeriod();
                    buyTriggerMinDistanceFromLocalMaximumPercentage = parameters.getDecliningBuyTriggerMinDistanceFromLocalMaximumPercentage();
                }

                double localMaximum = historicalStockData.getMaximumClosingMarketPrice(buyTriggerLocalMaximumLookBehindPeriod).getValue();
                double minDelta = localMaximum * buyTriggerMinDistanceFromLocalMaximumPercentage;
                double maxBuyPrice = localMaximum - minDelta;


                if (lastClosingMarketPrice <= maxBuyPrice) {
                    buyLocalMaximum.set(localMaximum);
                    buyLocalMaximumPassed.set(false);
                    maximumSinceBuying.set(0.0);
                    return true;
                } else {
                    return false;
                }
            });
        });

        parametersBuilder.setSellTriggerFactory(historicalMarketData -> {
            HistoricalStockData historicalStockData = historicalMarketData.getStockData(parameters.getIsin());

            return new DelegateTrigger(() -> {
                double lastClosingPrice = historicalStockData.getLastClosingMarketPrice().getValue();

                if (!buyLocalMaximumPassed.get()) {
                    buyLocalMaximumPassed.set(lastClosingPrice >= buyLocalMaximum.get());
                }

                if (!buyLocalMaximumPassed.get()) {
                    return false;
                }

                if (lastClosingPrice > maximumSinceBuying.get()) {
                    maximumSinceBuying.set(lastClosingPrice);
                }

                double minDeltaFromMaximumSinceBuying = parameters.getSellTriggerMinDistanceFromMaximumSinceBuyingPercentage() * maximumSinceBuying.get();
                double maxPriceSelling = maximumSinceBuying.get() - minDeltaFromMaximumSinceBuying;

                return lastClosingPrice <= maxPriceSelling;
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
