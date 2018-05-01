package trading.domain.strategy.compound;

import org.junit.Assert;
import org.junit.Test;
import trading.domain.Amount;
import trading.domain.DayCount;
import trading.domain.ISIN;
import trading.domain.account.Position;
import trading.domain.market.MarketPriceSnapshot;
import trading.domain.market.MarketPriceSnapshotBuilder;
import trading.domain.strategy.*;

import java.time.LocalDate;

public class CompoundTradingStrategyTest extends TradingStrategyTestBase {
    private ScoringStrategy scoringStrategy;
    private StockSelector stockSelector;
    private TriggerFactory sellTriggerFactory;

    @Override
    protected TradingStrategy initializeTradingStrategy(TradingStrategyContext tradingStrategyContext) {
        CompoundTradingStrategyParameters compoundTradingStrategyParameters = new CompoundTradingStrategyParametersBuilder()
                .setScoringStrategy(this.scoringStrategy)
                .setStockSelector(this.stockSelector)
                .setSellTriggerFactory(this.sellTriggerFactory)
                .build();

        return new CompoundTradingStrategy(compoundTradingStrategyParameters, tradingStrategyContext);
    }

    @Test
    public void dividesBuyAmountEquallyBetweenTwoStocksWithSameScore() {
        this.scoringStrategy = new FixedScoringStrategy()
                .setScore(ISIN.MunichRe, new Score(1.0))
                .setScore(ISIN.Allianz, new Score(1.0));

        this.stockSelector = new StockSelector(new Score(0.0), 1.0);

        this.sellTriggerFactory = (historicalMarketData) -> new NeverFiresTrigger();

        MarketPriceSnapshot marketPriceSnapshot = new MarketPriceSnapshotBuilder()
                .setMarketPrice(ISIN.MunichRe, new Amount(1000.0))
                .setMarketPrice(ISIN.Allianz, new Amount(500.0))
                .setDate(LocalDate.now())
                .build();

        this.beginHistory(marketPriceSnapshot);

        this.beginSimulation();
        this.openDay();

        // Seed capital: 50,000
        // Zero commissions
        // 25,000 EUR per stock
        // => Munich Re: 25 * 1,000 = 25,000
        // => Allianz: 50 * 1,000 = 25,000

        Position munichRePosition = this.account.getPosition(ISIN.MunichRe);
        Assert.assertEquals(25, munichRePosition.getQuantity().getValue());

        Position allianzPosition = this.account.getPosition(ISIN.Allianz);
        Assert.assertEquals(50, allianzPosition.getQuantity().getValue());
    }

    @Test
    public void excludedStocksBelowMinScoreWhenBuyingStocks() {
        this.scoringStrategy = new FixedScoringStrategy()
                .setScore(ISIN.MunichRe, new Score(0.3))
                .setScore(ISIN.Allianz, new Score(0.2));

        this.stockSelector = new StockSelector(new Score(0.3), 1.0);

        this.sellTriggerFactory = (historicalMarketData) -> new NeverFiresTrigger();

        MarketPriceSnapshot marketPriceSnapshot = new MarketPriceSnapshotBuilder()
                .setMarketPrice(ISIN.MunichRe, new Amount(1000.0))
                .setMarketPrice(ISIN.Allianz, new Amount(500.0))
                .setDate(LocalDate.now())
                .build();

        this.beginHistory(marketPriceSnapshot);

        this.beginSimulation();
        this.openDay();

        // Seed capital: 50,000
        // Zero commissions
        // consider only Munich Re
        // => 50 *  1,000 = 50,000

        Position munichRePosition = this.account.getPosition(ISIN.MunichRe);
        Assert.assertEquals(50, munichRePosition.getQuantity().getValue());

        Assert.assertFalse(this.account.hasPosition(ISIN.Allianz));
    }
    
    @Test
    public void stockIsSoldAfterTriggerFires() {
        this.scoringStrategy = new FixedScoringStrategy()
                .setScore(ISIN.MunichRe, new Score(1.0));

        this.stockSelector = new StockSelector(new Score(0.2), 1.0);

        this.sellTriggerFactory = isin -> new WaitFixedPeriodTrigger(historicalMarketData, new DayCount(1));

        this.beginHistory(ISIN.MunichRe, new Amount(1000.0), LocalDate.now());

        this.beginSimulation();
        this.openDay();

        // Seed capital: 50,000
        // Zero commissions
        // => 50 * 1,000 = 50,000

        Position munichRePosition = this.account.getPosition(ISIN.MunichRe);
        Assert.assertEquals(50, munichRePosition.getQuantity().getValue());

        this.closeDay(new Amount(1000.0), historicalMarketData.getDate().plusDays(1));
        this.openDay();

        // First day passed
        // Stocks should not have been sold yet

        Assert.assertEquals(50, munichRePosition.getQuantity().getValue());

        this.closeDay(new Amount(1000.0), historicalMarketData.getDate().plusDays(1));
        this.openDay();

        // Second day passed
        // Stocks should have been sold

        Assert.assertEquals(0, munichRePosition.getQuantity().getValue());
    }
}
