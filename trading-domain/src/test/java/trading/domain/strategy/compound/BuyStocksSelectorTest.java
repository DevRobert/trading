package trading.domain.strategy.compound;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import trading.domain.Amount;
import trading.domain.ISIN;
import trading.domain.Quantity;
import trading.domain.broker.CommissionStrategies;
import trading.domain.broker.CommissionStrategy;
import trading.domain.market.MarketPriceSnapshot;
import trading.domain.market.MarketPriceSnapshotBuilder;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class BuyStocksSelectorTest {
    private Amount totalCapital;
    private Amount availableMoney;
    private MarketPriceSnapshot marketPrices;
    private CommissionStrategy commissionStrategy;
    private Map<ISIN, Score> stockScores;
    private Map<ISIN, Quantity> currentStocks;
    private Score minimumBuyScore = new Score(0.0);
    private double maximumPercentage = 1.0;

    @Before
    public void before() {
        this.totalCapital = new Amount(10000.0);
        this.availableMoney = new Amount(10000.0);

        this.marketPrices = new MarketPriceSnapshotBuilder()
                .setMarketPrice(ISIN.MunichRe, new Amount(1000.0))
                .setMarketPrice(ISIN.Allianz, new Amount(500.0))
                .setDate(LocalDate.now())
                .build();

        this.commissionStrategy = CommissionStrategies.getZeroCommissionStrategy();

        this.stockScores = new HashMap<>();
        this.currentStocks = new HashMap<>();
    }

    private Map<ISIN, Quantity> selectStocks() {
        BuyStocksSelector buyStocksSelector = new BuyStocksSelector(this.minimumBuyScore, this.maximumPercentage);
        return buyStocksSelector.selectStocks(this.totalCapital, this.availableMoney, new Scores(stockScores, LocalDate.now()), this.marketPrices, this.commissionStrategy, currentStocks);
    }

    @Test
    public void distributeStockAmountsEqually_ifSameScores() {
        this.stockScores.put(ISIN.MunichRe, new Score(1.0));
        this.stockScores.put(ISIN.Allianz, new Score(1.0));

        // Available capital: 10,000
        // Zero commissions
        // 5,000 per stock
        // => Munich Re: 5 * 1,000 = 5,000
        // => Allianz: 10 * 500 = 5,000

        Map<ISIN, Quantity> selectedStocks = this.selectStocks();

        Assert.assertEquals(new Quantity(5), selectedStocks.get(ISIN.MunichRe));
        Assert.assertEquals(new Quantity(10), selectedStocks.get(ISIN.Allianz));
    }

    @Test
    public void distributeStockAmountsRespectiveToScores_ifDifferentScores() {
        this.stockScores.put(ISIN.MunichRe, new Score(1.0));
        this.stockScores.put(ISIN.Allianz, new Score(0.5));

        // Available capital: 10,000
        // Zero commissions
        // => Munich Re: 1.0 / 1.5 * 10,000 = 6,666.67 => 6 stocks à 1,000 => 6,000; then 4,000 left
        // => Allianz: 0.5 / 0.5 * 4,000 = 4,000 => 8 stocks à 500

        Map<ISIN, Quantity> selectedStocks = this.selectStocks();

        Assert.assertEquals(new Quantity(6), selectedStocks.get(ISIN.MunichRe));
        Assert.assertEquals(new Quantity(8), selectedStocks.get(ISIN.Allianz));
    }

    @Test
    public void excludesStock_ifBelowMinimumScore() {
        this.stockScores.put(ISIN.MunichRe, new Score(0.2));
        this.stockScores.put(ISIN.Allianz, new Score(0.3));

        // Available capital: 10,000
        // Zero commissions
        // => Allianz: 20 * 500 = 10,000

        this.minimumBuyScore = new Score(0.3);

        Map<ISIN, Quantity> selectedStocks = this.selectStocks();

        Assert.assertEquals(new Quantity(0), selectedStocks.get(ISIN.MunichRe));
        Assert.assertEquals(new Quantity(20), selectedStocks.get(ISIN.Allianz));
    }

    @Test
    public void excludesStock_ifAlreadyBought() {
        this.stockScores.put(ISIN.MunichRe, new Score(0.2));
        this.stockScores.put(ISIN.Allianz, new Score(0.3));

        this.currentStocks.put(ISIN.Allianz, new Quantity(1));

        // Available capital: 10,000
        // Zero commission
        // => Munich Re: 10 * 1,000 = 10,000

        Map<ISIN, Quantity> selectedStocks = this.selectStocks();

        Assert.assertEquals(new Quantity(10), selectedStocks.get(ISIN.MunichRe));
        Assert.assertEquals(new Quantity(0), selectedStocks.get(ISIN.Allianz));
    }

    @Test
    public void capsStockQuantity_respectiveToMaximumPercentage() {
        this.totalCapital = new Amount(20000.0);
        this.maximumPercentage = 0.2;

        this.stockScores.put(ISIN.MunichRe, new Score(1.0));
        this.stockScores.put(ISIN.Allianz, new Score(1.0));

        // Maximum price per stock = 20% * 20,000 = 4,000
        // 4,000 per stock
        // Zero commission
        // => Munich Re: 4 * 1,000 = 4,000
        // => Allianz: 8 * 500 = 4,000

        Map<ISIN, Quantity> selectedStocks = this.selectStocks();

        Assert.assertEquals(new Quantity(4), selectedStocks.get(ISIN.MunichRe));
        Assert.assertEquals(new Quantity(8), selectedStocks.get(ISIN.Allianz));
    }

    @Test
    public void distributeStockAmountRespectiveToScoresAndMaximum() {
        this.maximumPercentage = 0.6;

        this.stockScores.put(ISIN.MunichRe, new Score(1.0));
        this.stockScores.put(ISIN.Allianz, new Score(0.2));

        // Available money = total capital = 10,000

        // Maximum amount per stock: 60% * 10,000 = 6,000

        // Amounts:
        // Total score = 120%
        // Munich Re: 100% / 120% = 83.33%
        // Allianz: 20% / 120% = 16.67%

        // Zero commission

        // => Munich Re: 83.33% * 10,000 = 8,333
        //    maximum amount = 6,000
        //    6 stocks à 1,000

        // => Allianz: 4,000 remaining
        //    8 stocks à 500

        Map<ISIN, Quantity> selectedStocks = this.selectStocks();

        Assert.assertEquals(new Quantity(6), selectedStocks.get(ISIN.MunichRe));
        Assert.assertEquals(new Quantity(8), selectedStocks.get(ISIN.Allianz));
    }
}
