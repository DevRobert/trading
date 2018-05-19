package trading.domain.market;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import trading.domain.Amount;
import trading.domain.DayCount;

public class HistoricalStockDataTest {
    private HistoricalStockData historicalStockData;

    @Before
    public void before() {
        Amount initialMarketPrice = new Amount(1000.0);
        historicalStockData = new HistoricalStockData(initialMarketPrice);
    }

    // lastClosingMarketPrice

    @Test
    public void returnsInitialClosingMarketPriceAsClosingMarketPrice() {
        Assert.assertEquals(new Amount(1000.0), historicalStockData.getLastClosingMarketPrice());
    }

    @Test
    public void returnsPushedClosingMarketPriceAsClosingMarketPrice() {
        Amount newMarketPrice = new Amount(1100.0);
        historicalStockData.registerClosedDay(newMarketPrice);
        Assert.assertEquals(newMarketPrice, historicalStockData.getLastClosingMarketPrice());
    }

    // closingMarketPrice

    @Test
    public void getClosingMarketPrice_fails_ifNoLookBehindSpecified() {
        DayCount lookBehind = null;

        try {
            this.historicalStockData.getClosingMarketPrice(lookBehind);
        }
        catch(RuntimeException e) {
            Assert.assertEquals("The look behind must be specified.", e.getMessage());
            return;
        }

        Assert.fail("RuntimeException expected.");
    }

    @Test
    public void getClosingMarketPrice_fails_ifZeroLookBehindSpecified() {
        DayCount lookBehind = new DayCount(0);

        try {
            this.historicalStockData.getClosingMarketPrice(lookBehind);
        }
        catch(RuntimeException e) {
            Assert.assertEquals("The look behind must not be zero.", e.getMessage());
            return;
        }

        Assert.fail("RuntimeException expected.");
    }

    @Test
    public void getClosingMarketPrice_fails_ifNegativeLookBehindSpecified() {
        DayCount lookBehind = new DayCount(-1);

        try {
            this.historicalStockData.getClosingMarketPrice(lookBehind);
        }
        catch(RuntimeException e) {
            Assert.assertEquals("The look behind must not be negative.", e.getMessage());
            return;
        }

        Assert.fail("RuntimeException expected.");
    }

    @Test
    public void getClosingMarketPrice_fails_ifLookBehindExceedsHistoryLength() {
        DayCount lookBehind = new DayCount(2);

        try {
            this.historicalStockData.getClosingMarketPrice(lookBehind);
        }
        catch(RuntimeException e) {
            Assert.assertEquals("The given look behind (2) exceeds the history length (1).", e.getMessage());
            return;
        }

        Assert.fail("RuntimeException expected.");
    }

    @Test
    public void getClosingMarketPrice_returnsLastClosingMarketPrice_ifLookBehindOne() {
        this.historicalStockData.registerClosedDay(new Amount(1200.0));

        Amount closingMarketPrice = this.historicalStockData.getClosingMarketPrice(new DayCount(1));

        Assert.assertEquals(new Amount(1200.0), closingMarketPrice);
    }

    @Test
    public void getClosingMarketPrice_returnsPreviousClosingMarketPrice_ifLookBehindTwo() {
        this.historicalStockData.registerClosedDay(new Amount(1200.0));

        Amount closingMarketPrice = this.historicalStockData.getClosingMarketPrice(new DayCount(2));

        Assert.assertEquals(new Amount(1000.0), closingMarketPrice);
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

    // maximumPrice (lookBehind)

    @Test
    public void maximumClosingMarketPriceCalculationFails_ifNoLookBehindSpecified() {
        DayCount lookBehindPeriod = null;

        try {
            historicalStockData.getMaximumClosingMarketPrice(lookBehindPeriod);
        } catch (RuntimeException ex) {
            Assert.assertEquals("The look behind period must be specified.", ex.getMessage());
            return;
        }

        Assert.fail("RuntimeException expected.");
    }

    @Test
    public void maximumClosingMarketPriceCalculationFails_ifNegativeLookBehindSpecified() {
        DayCount lookBehindPeriod = new DayCount(-1);

        try {
            historicalStockData.getMaximumClosingMarketPrice(lookBehindPeriod);
        } catch (RuntimeException ex) {
            Assert.assertEquals("The look behind period must not be negative.", ex.getMessage());
            return;
        }

        Assert.fail("RuntimeException expected.");
    }

    @Test
    public void maximumClosingMarketPriceCalculationFails_ifZeroLookBehindSpecified() {
        DayCount lookBehindPeriod = new DayCount(0);

        try {
            historicalStockData.getMaximumClosingMarketPrice(lookBehindPeriod);
        } catch (RuntimeException ex) {
            Assert.assertEquals("The look behind period must not be zero.", ex.getMessage());
            return;
        }

        Assert.fail("RuntimeException expected.");
    }

    @Test
    public void maximumClosingMarketPriceCalculationFails_ifLookBehindExceedsHistory() {
        historicalStockData.registerClosedDay(new Amount(800.0));

        DayCount lookBehindPeriod = new DayCount(3);

        try {
            historicalStockData.getMaximumClosingMarketPrice(lookBehindPeriod);
        }
        catch(RuntimeException e) {
            Assert.assertEquals("The look behind period exceeds the available market data history.", e.getMessage());
            return;
        }

        Assert.fail("RuntimeException expected.");
    }

    @Test
    public void returnsInitialClosingMarketPriceAsMaximumClosingPrice() {
        DayCount lookBehindPeriod = new DayCount(1);
        Amount maximumClosingPrice = historicalStockData.getMaximumClosingMarketPrice(lookBehindPeriod);

        Assert.assertEquals(new Amount(1000.0), maximumClosingPrice);
    }

    @Test
    public void returnsLastClosingMarketPrice_ifLookBehindIsOneDay_andDayBeforeWasHigher() {
        historicalStockData.registerClosedDay(new Amount(800.0));

        DayCount lookBehindPeriod = new DayCount(1);
        Amount maximumClosingPrice = historicalStockData.getMaximumClosingMarketPrice(lookBehindPeriod);

        Assert.assertEquals(new Amount(800.0), maximumClosingPrice);
    }

    @Test
    public void returnsPreviousDayPrice_ifLookBehindIsTwoDays_andDayBeforeWasHigher() {
        historicalStockData.registerClosedDay(new Amount(900.0));
        historicalStockData.registerClosedDay(new Amount(800.0));

        DayCount lookBehindPeriod = new DayCount(2);
        Amount maximumClosingPrice = historicalStockData.getMaximumClosingMarketPrice(lookBehindPeriod);

        Assert.assertEquals(new Amount(900.0), maximumClosingPrice);
    }

    // duration

    @Test
    public void getDuration_returnsOne_forOneDay() {
        HistoricalStockData historicalStockData = new HistoricalStockData(new Amount(100.0));
        Assert.assertEquals(new DayCount(1), historicalStockData.getDuration());
    }

    @Test
    public void getDuration_returnsTwo_forTwoDays() {
        HistoricalStockData historicalStockData = new HistoricalStockData(new Amount(100.0));
        historicalStockData.registerClosedDay(new Amount(120.0));
        Assert.assertEquals(new DayCount(2), historicalStockData.getDuration());
    }
}
