package trading.strategy.compoundLocalMaximum;

import trading.DayCount;
import trading.ISIN;
import trading.market.HistoricalMarketData;
import trading.market.HistoricalStockData;
import trading.strategy.TradingStrategy;
import trading.strategy.TradingStrategyContext;
import trading.strategy.Trigger;
import trading.strategy.TriggerFactory;
import trading.strategy.compound.*;

import java.util.HashMap;
import java.util.Map;

public class CompoundLocalMaximumTradingStrategy implements TradingStrategy {
    private final CompoundTradingStrategy compoundTradingStrategy;
    private final TradingStrategyContext context;
    private final DayCount buyTriggerLocalMaximumLookBehindPeriod;
    private final double buyTriggerMinDeclineSinceMaximumPercentage;
    private final double activateTrailingStopLossMinRaiseSinceBuyingPercentage;
    private final double sellTriggerStopLossMinimumDeclineSinceBuyingPercentage;
    private final double sellTriggerTrailingStopLossMinDeclineSinceMaximumAfterBuyingPercentage;

    private final Map<ISIN, Boolean> sellTriggerTrailingStopLossActivated = new HashMap<>();
    private final Map<ISIN, Double> maxPriceSinceBuying = new HashMap<>();
    private final Map<ISIN, Double> buyPrice = new HashMap<>();

    public CompoundLocalMaximumTradingStrategy(CompoundLocalMaximumTradingStrategyParameters parameters, TradingStrategyContext context) {
        this.context = context;
        this.buyTriggerLocalMaximumLookBehindPeriod = parameters.getBuyTriggerLocalMaximumLookBehindPeriod();
        this.buyTriggerMinDeclineSinceMaximumPercentage = parameters.getBuyTriggerMinDeclineFromLocalMaximumPercentage();
        this.activateTrailingStopLossMinRaiseSinceBuyingPercentage = parameters.getActivateTrailingStopLossMinRaiseSinceBuyingPercentage();
        this.sellTriggerStopLossMinimumDeclineSinceBuyingPercentage = parameters.getSellTriggerStopLossMinimumDeclineSinceBuyingPercentage();
        this.sellTriggerTrailingStopLossMinDeclineSinceMaximumAfterBuyingPercentage = parameters.getSellTriggerTrailingStopLossMinDeclineFromMaximumAfterBuyingPercentage();

        CompoundTradingStrategyParameters compoundTradingStrategyParameters = new CompoundTradingStrategyParametersBuilder()
                .setScoringStrategy(new ScoringStrategy() {
                    @Override
                    public Score calculateScore(HistoricalMarketData historicalMarketData, ISIN isin) {
                        if(shouldBuyStocks(isin)) {
                            return new Score(1.0);
                        }

                        return new Score(0.0);
                    }
                })
                .setSellTriggerFactory(new TriggerFactory() {
                    @Override
                    public Trigger createTrigger(ISIN isin) {
                        return new Trigger() {
                            @Override
                            public boolean checkFires() {
                                return shouldSellStocks(isin);
                            }
                        };
                    }
                })
                .setStockSelector(new StockSelector(new Score(0.5), parameters.getMaximumPercentage()))
                .build();

        this.compoundTradingStrategy = new CompoundTradingStrategy(compoundTradingStrategyParameters, context);
    }

    private boolean shouldBuyStocks(ISIN isin) {
        HistoricalStockData historicalStockData = this.context.getHistoricalMarketData().getStockData(isin);

        double lastClosingPrice = historicalStockData.getLastClosingMarketPrice().getValue();

        double localMaximum = historicalStockData.getMaximumClosingMarketPrice(this.buyTriggerLocalMaximumLookBehindPeriod).getValue();
        double maxBuyPrice = localMaximum * (1.0 - this.buyTriggerMinDeclineSinceMaximumPercentage);

        if(lastClosingPrice <= maxBuyPrice) {
            this.sellTriggerTrailingStopLossActivated.put(isin, false);
            this.maxPriceSinceBuying.put(isin, 0.0);
            this.buyPrice.put(isin, lastClosingPrice);

            return true;
        }

        return false;
    }

    private boolean shouldSellStocks(ISIN isin) {
        HistoricalStockData historicalStockData = this.context.getHistoricalMarketData().getStockData(isin);

        double lastClosingPrice = historicalStockData.getLastClosingMarketPrice().getValue();

        this.updateMaxPriceSinceBuying(isin, lastClosingPrice);
        this.updateTrailingStopLossActivation(isin, lastClosingPrice);

        if(this.stopLoss(isin, lastClosingPrice)) {
            return true;
        }

        return this.sellTriggerTrailingStopLossActivated.get(isin) && this.trailingStopLoss(isin, lastClosingPrice, this.maxPriceSinceBuying.get(isin));
    }

    private void updateMaxPriceSinceBuying(ISIN isin, double lastClosingPrice) {
        if(lastClosingPrice > this.maxPriceSinceBuying.get(isin)) {
            this.maxPriceSinceBuying.put(isin, lastClosingPrice);
        }
    }

    private void updateTrailingStopLossActivation(ISIN isin, double lastClosingPrice) {
        if(this.sellTriggerTrailingStopLossActivated.get(isin)) {
            return;
        }

        double minimumActivationPrice = this.buyPrice.get(isin) * (1.0 + this.activateTrailingStopLossMinRaiseSinceBuyingPercentage);
        this.sellTriggerTrailingStopLossActivated.put(isin, lastClosingPrice >= minimumActivationPrice);
    }

    private boolean stopLoss(ISIN isin, double lastClosingPrice) {
        double stopLossMaximumPrice = this.buyPrice.get(isin) * (1.0 - this.sellTriggerStopLossMinimumDeclineSinceBuyingPercentage);
        return lastClosingPrice <= stopLossMaximumPrice;
    }

    private boolean trailingStopLoss(ISIN isin, double lastClosingPrice, double maximumSinceBuying) {
        double trailingStopLossMaximumPrice = this.maxPriceSinceBuying.get(isin) * (1.0 - this.sellTriggerTrailingStopLossMinDeclineSinceMaximumAfterBuyingPercentage);
        return lastClosingPrice <= trailingStopLossMaximumPrice;
    }

    @Override
    public void prepareOrdersForNextTradingDay() {
        this.compoundTradingStrategy.prepareOrdersForNextTradingDay();
    }
}
