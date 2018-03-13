package trading.challenges;

import org.junit.Test;
import trading.ISIN;
import trading.market.HistoricalStockData;
import trading.strategy.AlwaysFiresTrigger;
import trading.strategy.DelegateTrigger;

public class RisingAndDecliningDaysChallenges extends ProgressiveChallengesBase {
    @Test
    public void buyAfterOneRisingDayAndSellAfterOneDecliningDay() {
        ISIN isin = ISIN.MunichRe;

        this.progressiveTradingStrategyParametersBuilder.setISIN(isin);

        this.progressiveTradingStrategyParametersBuilder.setBuyTriggerFactory(historicalMarketData -> {
            HistoricalStockData historicalStockData = historicalMarketData.getStockData(isin);
            return new DelegateTrigger(() -> historicalStockData.getRisingDaysInSequence() >= 1);
        });

        this.progressiveTradingStrategyParametersBuilder.setSellTriggerFactory(historicalMarketData -> {
            HistoricalStockData historicalStockData = historicalMarketData.getStockData(isin);
            return new DelegateTrigger(() -> historicalStockData.getDecliningDaysInSequence() >= 1);
        });

        this.progressiveTradingStrategyParametersBuilder.setResetTriggerFactory(historicalMarketData -> new AlwaysFiresTrigger());
    }

}
