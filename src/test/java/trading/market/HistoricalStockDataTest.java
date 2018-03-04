package trading.market;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import trading.Amount;

public class HistoricalStockDataTest {
    private HistoricalStockData historicalStockData;

    @Before
    public void before() {
        Amount initialMarketPrice = new Amount(1000.0);
        historicalStockData = new HistoricalStockData(initialMarketPrice);
    }

    // marketPrice

    @Test
    public void returnsInitialMarketPriceAsMarketPrice() {
        Assert.assertEquals(new Amount(1000.0), historicalStockData.getLastMarketPrice());
    }

    @Test
    public void returnsPushedMarketPriceAsMarketPrice() {
        Amount newMarketPrice = new Amount(1100.0);
        historicalStockData.pushMarketPrice(newMarketPrice);
        Assert.assertEquals(newMarketPrice, historicalStockData.getLastMarketPrice());
    }

    // numRisingDaysInSequence

    @Test
    public void risingDaysInSequenceIsZeroForEmptyDataTest() {
        Assert.assertEquals(0, historicalStockData.getRisingDaysInSequence());
    }

    @Test
    public void risingDaysInSequenceIsOneAfterOneRisingDay() {
        historicalStockData.pushMarketPrice(new Amount(1100.0));
        Assert.assertEquals(1, historicalStockData.getRisingDaysInSequence());
    }

    @Test
    public void risingDaysInSequenceIsTwoAfterTwoRisingDays() {
        historicalStockData.pushMarketPrice(new Amount(1100.0));
        historicalStockData.pushMarketPrice(new Amount(1200.0));
        Assert.assertEquals(2, historicalStockData.getRisingDaysInSequence());
    }

    @Test
    public void risingDaysInSequenceIsResetAfterOneFallingDay() {
        historicalStockData.pushMarketPrice(new Amount(1100.0));
        historicalStockData.pushMarketPrice(new Amount(1000.0));
        Assert.assertEquals(0, historicalStockData.getRisingDaysInSequence());
    }

    @Test
    public void risingDayInSequenceIsResetAfterOneStableDay() {
        historicalStockData.pushMarketPrice(new Amount(1100.0));
        historicalStockData.pushMarketPrice(new Amount(1100.0));
        Assert.assertEquals(0, historicalStockData.getRisingDaysInSequence());
    }

    // numDecliningDaysInSequence

    @Test
    public void decliningDaysInSequenceisZeroForEmptyDataSet() {
        Assert.assertEquals(0, historicalStockData.getDecliningDaysInSequence());
    }

    @Test
    public void decliningDaysInSequenceIsOneAfterOneDecliningDay() {
        historicalStockData.pushMarketPrice(new Amount(900.0));
        Assert.assertEquals(1, historicalStockData.getDecliningDaysInSequence());
    }

    @Test
    public void decliningDaysInSequenceIsTwoAfterTwoDecliningDays() {
        historicalStockData.pushMarketPrice(new Amount(900.0));
        historicalStockData.pushMarketPrice(new Amount(800.0));
        Assert.assertEquals(2, historicalStockData.getDecliningDaysInSequence());
    }

    @Test
    public void decliningDaysInSequenceIsResetAfterOneRisingDay() {
        historicalStockData.pushMarketPrice(new Amount(900.0));
        historicalStockData.pushMarketPrice(new Amount(1000.0));
        Assert.assertEquals(0, historicalStockData.getDecliningDaysInSequence());
    }

    @Test
    public void decliningDaysInSequenceIsResetAfterOneStableDay() {
        historicalStockData.pushMarketPrice(new Amount(900.0));
        historicalStockData.pushMarketPrice(new Amount(900.0));
        Assert.assertEquals(0, historicalStockData.getDecliningDaysInSequence());
    }
}
