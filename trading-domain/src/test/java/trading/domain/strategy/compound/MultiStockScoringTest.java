package trading.domain.strategy.compound;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import trading.domain.Amount;
import trading.domain.ISIN;
import trading.domain.market.HistoricalMarketData;
import trading.domain.market.MarketPriceSnapshot;
import trading.domain.market.MarketPriceSnapshotBuilder;

import java.time.LocalDate;
import java.util.Set;

public class MultiStockScoringTest {
    private HistoricalMarketData historicalMarketData;
    private Set<ISIN> isins;

    @Before
        public void before() {
        MarketPriceSnapshot initialClosingMarketPrices = new MarketPriceSnapshotBuilder()
                .setMarketPrice(ISIN.MunichRe, new Amount(1000.0))
                .setMarketPrice(ISIN.Allianz, new Amount(500.0))
                .setDate(LocalDate.now())
                .build();

        this.historicalMarketData = new HistoricalMarketData(initialClosingMarketPrices);
        this.isins = this.historicalMarketData.getAvailableStocks();
    }

    @Test
    public void calculationFailsIfNoHistoricalMarketDataSpecified() {
        HistoricalMarketData historicalMarketData = null;

        ScoringStrategy scoringStrategy = new ScoringStrategy() {
            @Override
            public Score calculateScore(HistoricalMarketData historicalMarketData, ISIN isin) {
                return new Score(0.0);
            }
        };

        try {
            new MultiStockScoring().calculateScores(historicalMarketData, scoringStrategy);
        }
        catch(RuntimeException ex) {
            Assert.assertEquals("The historical market data have to be specified.", ex.getMessage());
            return;
        }

        Assert.fail("RuntimeException expected.");
    }

    @Test
    public void calculationFailsIfNoScoringStrategySpecified() {
        HistoricalMarketData historicalMarketData = new HistoricalMarketData(ISIN.MunichRe, new Amount(1000.0), LocalDate.now());
        ScoringStrategy scoringStrategy = null;

        try {
            new MultiStockScoring().calculateScores(historicalMarketData, scoringStrategy);
        }
        catch(RuntimeException ex) {
            Assert.assertEquals("The scoring strategy has to be specified.", ex.getMessage());
            return;
        }

        Assert.fail("RuntimeException expected.");
    }

    @Test
    public void multiStockScoringCalculatesScoresUsingScoringStrategy() {
        Scores scores = new MultiStockScoring().calculateScores(historicalMarketData, new ScoringStrategy() {
            @Override
            public Score calculateScore(HistoricalMarketData historicalMarketData, ISIN isin) {
                if(isin.equals(ISIN.MunichRe)) {
                    return new Score(1.0);
                }

                if(isin.equals(ISIN.Allianz)) {
                    return new Score(0.1);
                }

                return null;
            }
        });

        Score munichReScore  = scores.get(ISIN.MunichRe);
        Score allianzScore = scores.get(ISIN.Allianz);

        Assert.assertEquals(1.0, munichReScore.getValue(), 0.0);
        Assert.assertEquals(0.1, allianzScore.getValue(), 0.0);
    }
}
