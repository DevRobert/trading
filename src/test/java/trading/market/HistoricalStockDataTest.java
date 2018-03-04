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
        Assert.assertEquals(new Amount(1000.0), historicalStockData.getLastClosingMarketPrice());
    }

    @Test
    public void returnsPushedMarketPriceAsMarketPrice() {
        Amount newMarketPrice = new Amount(1100.0);
        historicalStockData.registerClosedDay(newMarketPrice);
        Assert.assertEquals(newMarketPrice, historicalStockData.getLastClosingMarketPrice());
    }

    // numRisingDaysInSequence

    @Test
    public void risingDaysInSequenceIsZeroForInitialDataSet() {
        Assert.assertEquals(0, historicalStockData.getRisingDaysInSequence());
    }

    @Test
    public void risingDaysInSequenceIsOneAfterOneRisingDay() {
        historicalStockData.registerClosedDay(new Amount(1100.0));
        Assert.assertEquals(1, historicalStockData.getRisingDaysInSequence());
    }

    @Test
    public void risingDaysInSequenceIsTwoAfterTwoRisingDays() {
        historicalStockData.registerClosedDay(new Amount(1100.0));
        historicalStockData.registerClosedDay(new Amount(1200.0));
        Assert.assertEquals(2, historicalStockData.getRisingDaysInSequence());
    }

    @Test
    public void risingDaysInSequenceIsResetAfterOneFallingDay() {
        historicalStockData.registerClosedDay(new Amount(1100.0));
        historicalStockData.registerClosedDay(new Amount(1000.0));
        Assert.assertEquals(0, historicalStockData.getRisingDaysInSequence());
    }

    @Test
    public void risingDayInSequenceIsResetAfterOneStableDay() {
        historicalStockData.registerClosedDay(new Amount(1100.0));
        historicalStockData.registerClosedDay(new Amount(1100.0));
        Assert.assertEquals(0, historicalStockData.getRisingDaysInSequence());
    }

    // numDecliningDaysInSequence

    @Test
    public void decliningDaysInSequenceIsZeroForInitialDataSet() {
        Assert.assertEquals(0, historicalStockData.getDecliningDaysInSequence());
    }

    @Test
    public void decliningDaysInSequenceIsOneAfterOneDecliningDay() {
        historicalStockData.registerClosedDay(new Amount(900.0));
        Assert.assertEquals(1, historicalStockData.getDecliningDaysInSequence());
    }

    @Test
    public void decliningDaysInSequenceIsTwoAfterTwoDecliningDays() {
        historicalStockData.registerClosedDay(new Amount(900.0));
        historicalStockData.registerClosedDay(new Amount(800.0));
        Assert.assertEquals(2, historicalStockData.getDecliningDaysInSequence());
    }

    @Test
    public void decliningDaysInSequenceIsResetAfterOneRisingDay() {
        historicalStockData.registerClosedDay(new Amount(900.0));
        historicalStockData.registerClosedDay(new Amount(1000.0));
        Assert.assertEquals(0, historicalStockData.getDecliningDaysInSequence());
    }

    @Test
    public void decliningDaysInSequenceIsResetAfterOneStableDay() {
        historicalStockData.registerClosedDay(new Amount(900.0));
        historicalStockData.registerClosedDay(new Amount(900.0));
        Assert.assertEquals(0, historicalStockData.getDecliningDaysInSequence());
    }
}
