package trading.domain.market;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import trading.domain.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class HistoricalMarketDataTest {
    private DateSequenceGenerator dateSequenceGenerator;

    @Before
    public void before() {
        this.dateSequenceGenerator = new DateSequenceGenerator(LocalDate.now());
    }

    // Get initial closing market prices

    @Test
    public void retrieveLastMarketPriceForInitiallyRegisteredStocks() {
        Amount initialClosingMarketPrice = new Amount(1000.0);

        MarketPriceSnapshot marketPriceSnapshot = new MarketPriceSnapshotBuilder()
                .setMarketPrice(ISIN.MunichRe, initialClosingMarketPrice)
                .setDate(LocalDate.now())
                .build();

        HistoricalMarketData historicalMarketData = new HistoricalMarketData(marketPriceSnapshot);

        Assert.assertEquals(initialClosingMarketPrice, historicalMarketData.getStockData(ISIN.MunichRe).getLastClosingMarketPrice());
    }

    @Test
    public void retrieveLastMarketPriceForInitiallyRegisteredSingleStock() {
        Amount initialClosingMarketPrice = new Amount(1000.0);
        HistoricalMarketData historicalMarketData = new HistoricalMarketData(ISIN.MunichRe, initialClosingMarketPrice, LocalDate.now());
        Assert.assertEquals(initialClosingMarketPrice, historicalMarketData.getStockData(ISIN.MunichRe).getLastClosingMarketPrice());
    }

    @Test
    public void retrievalOfStockDataFailsForUnknownStock() {
        MarketPriceSnapshot marketPriceSnapshot = new MarketPriceSnapshotBuilder().setDate(LocalDate.now()).build();
        HistoricalMarketData historicalMarketData = new HistoricalMarketData(marketPriceSnapshot);

        try {
            historicalMarketData.getStockData(ISIN.MunichRe);
        }
        catch(UnknownStockException ex) {
            return;
        }

        Assert.fail("UnknownStockException expected.");
    }

    // Register market price updates

    @Test
    public void distributeMarketPriceUpdateOverTwoStocks() {
        MarketPriceSnapshot firstMarketPriceSnapshot = new MarketPriceSnapshotBuilder()
                .setMarketPrice(ISIN.MunichRe, new Amount(1000.0))
                .setMarketPrice(ISIN.Allianz, new Amount(500.0))
                .setDate(this.dateSequenceGenerator.nextDate())
                .build();

        MarketPriceSnapshot secondMarketPriceSnapshot = new MarketPriceSnapshotBuilder()
                .setMarketPrice(ISIN.MunichRe, new Amount(1100.0))
                .setMarketPrice(ISIN.Allianz, new Amount(550.0))
                .setDate(this.dateSequenceGenerator.nextDate())
                .build();

        HistoricalMarketData historicalMarketData = new HistoricalMarketData(firstMarketPriceSnapshot);
        historicalMarketData.registerClosedDay(secondMarketPriceSnapshot);

        Assert.assertEquals(new Amount(1100.0), historicalMarketData.getStockData(ISIN.MunichRe).getLastClosingMarketPrice());
        Assert.assertEquals(new Amount(550.0), historicalMarketData.getStockData(ISIN.Allianz).getLastClosingMarketPrice());
    }

    @Test
    public void registerMarketPriceUpdateForSingleStock() {
        Amount initialClosingMarketPrice = new Amount(1000.0);
        HistoricalMarketData historicalMarketData = new HistoricalMarketData(ISIN.MunichRe, initialClosingMarketPrice, this.dateSequenceGenerator.nextDate());

        Amount newClosingMarketPrice = new Amount(1100.0);
        historicalMarketData.registerClosedDay(newClosingMarketPrice, dateSequenceGenerator.nextDate());

        Assert.assertEquals(newClosingMarketPrice, historicalMarketData.getStockData(ISIN.MunichRe).getLastClosingMarketPrice());
    }

    @Test
    public void registerMarketPriceUpdateForSingleStockFails_ifMultipleStocksAvailable() {
        MarketPriceSnapshot marketPriceSnapshot = new MarketPriceSnapshotBuilder()
                .setMarketPrice(ISIN.MunichRe, new Amount(1000.0))
                .setMarketPrice(ISIN.Allianz, new Amount(500.0))
                .setDate(this.dateSequenceGenerator.nextDate())
                .build();

        HistoricalMarketData historicalMarketData = new HistoricalMarketData(marketPriceSnapshot);

        try {
            historicalMarketData.registerClosedDay(new Amount(1100.0), this.dateSequenceGenerator.nextDate());
        }
        catch(RuntimeException ex) {
            Assert.assertEquals("The single-stock market update function must not be used when multiple stocks registered.", ex.getMessage());
            return;
        }

        Assert.fail("RuntimeException expected.");
    }

    @Test
    public void derivesAvailableStocksFromMarketPriceSnapshot() {
        MarketPriceSnapshot marketPriceSnapshot = new MarketPriceSnapshotBuilder()
                .setMarketPrice(ISIN.MunichRe, new Amount(1000.0))
                .setMarketPrice(ISIN.Allianz, new Amount(500.0))
                .setDate(LocalDate.now())
                .build();

        HistoricalMarketData historicalMarketData = new HistoricalMarketData(marketPriceSnapshot);

        Set<ISIN> availableStocks = historicalMarketData.getAvailableStocks();

        Assert.assertEquals(2, availableStocks.size());
        Assert.assertTrue(availableStocks.contains(ISIN.MunichRe));
        Assert.assertTrue(availableStocks.contains(ISIN.Allianz));
    }

    @Test
    public void marketPriceUpdateFailsIfAvailableStockMissing() {
        MarketPriceSnapshot firstMarketPriceSnapshot = new MarketPriceSnapshotBuilder()
                .setMarketPrice(ISIN.MunichRe, new Amount(1000.0))
                .setDate(this.dateSequenceGenerator.nextDate())
                .build();

        MarketPriceSnapshot secondMarketPriceSnapshot = new MarketPriceSnapshotBuilder()
                .setDate(this.dateSequenceGenerator.nextDate())
                .build();

        HistoricalMarketData historicalMarketData = new HistoricalMarketData(firstMarketPriceSnapshot);

        try {
            historicalMarketData.registerClosedDay(secondMarketPriceSnapshot);
        }
        catch(MissingDataException ex) {
            Assert.assertEquals("The market price snapshot must contain prices for all registered stocks.", ex.getMessage());
            return;
        }

        Assert.fail("MissingDataException expected.");
    }

    // Construction

    @Test
    public void constructionFailsIfISINNotSpecified() {
        ISIN isin = null;
        Amount initialClosingMarketPrice = new Amount(1000.0);

        try {
            new HistoricalMarketData(isin, initialClosingMarketPrice, LocalDate.now());
        }
        catch(RuntimeException ex) {
            Assert.assertEquals("The ISIN must be specified.", ex.getMessage());
            return;
        }

        Assert.fail("RuntimeException expected.");
    }

    @Test
    public void constructionFailsIfInitialClosingMarketPriceNotSpecified() {
        ISIN isin = ISIN.MunichRe;
        Amount initialClosingMarketPrice = null;

        try {
            new HistoricalMarketData(isin, initialClosingMarketPrice, LocalDate.now());
        }
        catch(RuntimeException ex) {
            Assert.assertEquals("The market price must be specified.", ex.getMessage());
            return;
        }

        Assert.fail("RuntimeException expected.");
    }

    // Duration

    @Test
    public void getDurationReturnsOneForOneDayHistory() {
        HistoricalMarketData historicalMarketData = new HistoricalMarketData(ISIN.MunichRe, new Amount(1000.0), LocalDate.now());
        Assert.assertEquals(new DayCount(1), historicalMarketData.getDuration());
    }

    @Test
    public void getDurationReturnsTwoForTwoDaysHistory() {
        HistoricalMarketData historicalMarketData = new HistoricalMarketData(ISIN.MunichRe, new Amount(1000.0), this.dateSequenceGenerator.nextDate());
        historicalMarketData.registerClosedDay(new Amount(1100.0), this.dateSequenceGenerator.nextDate());
        Assert.assertEquals(new DayCount(2), historicalMarketData.getDuration());
    }

    // Misc

    @Test
    public void singleStockPriceUpdateMethodCanBeUsedIfInitializedWithSingleStockClosingMarketPricesMap() {
        MarketPriceSnapshot initialClosingMarketPrices = new MarketPriceSnapshotBuilder()
                .setMarketPrice(ISIN.MunichRe, new Amount(1000.0))
                .setDate(this.dateSequenceGenerator.nextDate())
                .build();

        HistoricalMarketData historicalMarketData = new HistoricalMarketData(initialClosingMarketPrices);

        historicalMarketData.registerClosedDay(new Amount(1100.0), this.dateSequenceGenerator.nextDate());

        Assert.assertEquals(new Amount(1100.0), historicalMarketData.getStockData(ISIN.MunichRe).getLastClosingMarketPrice());
    }

    // Last closing market prices

    @Test
    public void returnsLastClosingMarketPrices_initially_forOneStock() {
        HistoricalMarketData historicalMarketData = new HistoricalMarketData(ISIN.MunichRe, new Amount(1000.0), LocalDate.now());

        MarketPriceSnapshot lastClosingMarketPrices = historicalMarketData.getLastClosingMarketPrices();

        Assert.assertEquals(new Amount(1000.0), lastClosingMarketPrices.getMarketPrice(ISIN.MunichRe));
    }

    @Test
    public void returnsLastClosingMarketPrices_initially_forTwoStocks() {
        MarketPriceSnapshot marketPriceSnapshot = new MarketPriceSnapshotBuilder()
                .setMarketPrice(ISIN.MunichRe, new Amount(1000.0))
                .setMarketPrice(ISIN.Allianz, new Amount(500.0))
                .setDate(LocalDate.now())
                .build();

        HistoricalMarketData historicalMarketData = new HistoricalMarketData(marketPriceSnapshot);

        MarketPriceSnapshot lastClosingMarketPrices = historicalMarketData.getLastClosingMarketPrices();

        Assert.assertEquals(new Amount(1000.0), lastClosingMarketPrices.getMarketPrice(ISIN.MunichRe));
        Assert.assertEquals(new Amount(500.0), lastClosingMarketPrices.getMarketPrice(ISIN.Allianz));
    }

    @Test
    public void returnsLastClosingMarketPrices_afterOneDay_forOneStock() {
        HistoricalMarketData historicalMarketData = new HistoricalMarketData(ISIN.MunichRe, new Amount(1000.0), this.dateSequenceGenerator.nextDate());
        historicalMarketData.registerClosedDay(new Amount(2000.0), this.dateSequenceGenerator.nextDate());

        MarketPriceSnapshot lastClosingMarketPrices = historicalMarketData.getLastClosingMarketPrices();

        Assert.assertEquals(new Amount(2000.0), lastClosingMarketPrices.getMarketPrice(ISIN.MunichRe));
    }

    @Test
    public void returnsLastClosingMarketPrices_afterOneDay_forTwoStocks() {
        MarketPriceSnapshot firstMarketPriceSnapshot = new MarketPriceSnapshotBuilder()
                .setMarketPrice(ISIN.MunichRe, new Amount(1000.0))
                .setMarketPrice(ISIN.Allianz, new Amount(500.0))
                .setDate(this.dateSequenceGenerator.nextDate())
                .build();

        MarketPriceSnapshot secondMarketPriceSnapshot = new MarketPriceSnapshotBuilder()
                .setMarketPrice(ISIN.MunichRe, new Amount(2000.0))
                .setMarketPrice(ISIN.Allianz, new Amount(1000.0))
                .setDate(this.dateSequenceGenerator.nextDate())
                .build();

        HistoricalMarketData historicalMarketData = new HistoricalMarketData(firstMarketPriceSnapshot);
        historicalMarketData.registerClosedDay(secondMarketPriceSnapshot);

        MarketPriceSnapshot lastClosingMarketPrices = historicalMarketData.getLastClosingMarketPrices();

        Assert.assertEquals(new Amount(2000.0), lastClosingMarketPrices.getMarketPrice(ISIN.MunichRe));
        Assert.assertEquals(new Amount(1000.0), lastClosingMarketPrices.getMarketPrice(ISIN.Allianz));
    }

    // Date

    @Test
    public void returnsDateOfSingleMarketPriceSnapshot_ifOnlyOneMarketPriceSnapshotPassed() {
        LocalDate date = LocalDate.of(2018, 4, 3);

        MarketPriceSnapshot marketPriceSnapshot = new MarketPriceSnapshotBuilder()
                .setMarketPrice(ISIN.MunichRe, new Amount(100.0))
                .setDate(date)
                .build();

        HistoricalMarketData historicalMarketData = new HistoricalMarketData(marketPriceSnapshot);

        Assert.assertSame(date, historicalMarketData.getDate());
    }

    @Test
    public void returnsDateOfLastMarketPriceSnapshot_ifThreeMarketPriceSnapshotsPassed() {
        LocalDate firstDate = LocalDate.of(2018, 4, 3);
        LocalDate secondDate = LocalDate.of(2018, 4, 4);
        LocalDate thirdDate = LocalDate.of(2018, 4, 5);

        MarketPriceSnapshot firstMarketPriceSnapshot = new MarketPriceSnapshotBuilder()
                .setMarketPrice(ISIN.MunichRe, new Amount(100.0))
                .setDate(firstDate)
                .build();

        MarketPriceSnapshot secondMarketPriceSnapshot = new MarketPriceSnapshotBuilder()
                .setMarketPrice(ISIN.MunichRe, new Amount(100.0))
                .setDate(secondDate)
                .build();

        MarketPriceSnapshot thirdMarketPriceSnapshot = new MarketPriceSnapshotBuilder()
                .setMarketPrice(ISIN.MunichRe, new Amount(100.0))
                .setDate(thirdDate)
                .build();

        HistoricalMarketData historicalMarketData = new HistoricalMarketData(firstMarketPriceSnapshot);
        historicalMarketData.registerClosedDay(secondMarketPriceSnapshot);
        historicalMarketData.registerClosedDay(thirdMarketPriceSnapshot);

        Assert.assertSame(thirdDate, historicalMarketData.getDate());
    }

    @Test
    public void returnsDateOfSingleClosingPrice_ifOnlyOneClosingPricePassed() {
        LocalDate date = LocalDate.of(2018, 4, 3);
        HistoricalMarketData historicalMarketData = new HistoricalMarketData(ISIN.MunichRe, new Amount(100.0), date);
        Assert.assertSame(date, historicalMarketData.getDate());
    }

    @Test
    public void returnsDateOfLastClosingPrice_ifThreeClosingPricesPassed() {
        LocalDate firstDate = LocalDate.of(2018, 4, 3);
        LocalDate secondDate = LocalDate.of(2018, 4, 4);
        LocalDate thirdDate = LocalDate.of(2018, 4, 5);

        HistoricalMarketData historicalMarketData = new HistoricalMarketData(ISIN.MunichRe, new Amount(100.0), firstDate);
        historicalMarketData.registerClosedDay(new Amount(100.0), secondDate);
        historicalMarketData.registerClosedDay(new Amount(100.0), thirdDate);

        Assert.assertSame(thirdDate, historicalMarketData.getDate());
    }

    @Test
    public void registerClosingPricesFails_ifSnapshotDate_before_lastSnapshotDate() {
        MarketPriceSnapshot firstMarketPriceSnapshot = new MarketPriceSnapshotBuilder()
                .setMarketPrice(ISIN.MunichRe, new Amount(100.0))
                .setDate(LocalDate.of(2018, 5, 3))
                .build();

        MarketPriceSnapshot secondMarketPriceSnapshot = new MarketPriceSnapshotBuilder()
                .setMarketPrice(ISIN.MunichRe, new Amount(100.0))
                .setDate(LocalDate.of(2018, 5, 2))
                .build();

        HistoricalMarketData historicalMarketData = new HistoricalMarketData(firstMarketPriceSnapshot);

        try {
            historicalMarketData.registerClosedDay(secondMarketPriceSnapshot);
        }
        catch(DomainException e) {
            Assert.assertEquals("The specified date must not lie before the date of the last registered market price snapshot.", e.getMessage());
            return;
        }

        Assert.fail("DomainException expected.");
    }

    @Test
    public void registerSingleClosingPriceFails_ifDate_before_lastSnapshotDate() {
        HistoricalMarketData historicalMarketData = new HistoricalMarketData(ISIN.MunichRe, new Amount(100.0), LocalDate.of(2018, 5, 3));

        try {
            historicalMarketData.registerClosedDay(new Amount(100.0), LocalDate.of(2018, 5, 2));
        }
        catch(DomainException e) {
            Assert.assertEquals("The specified date must not lie before the date of the last registered market price snapshot.", e.getMessage());
            return;
        }

        Assert.fail("DomainException expected.");
    }

    @Test
    public void registerClosingPricesFails_isSnapshotDate_equals_lastSnapshotDate() {
        MarketPriceSnapshot firstMarketPriceSnapshot = new MarketPriceSnapshotBuilder()
                .setMarketPrice(ISIN.MunichRe, new Amount(100.0))
                .setDate(LocalDate.of(2018, 5, 3))
                .build();

        MarketPriceSnapshot secondMarketPriceSnapshot = new MarketPriceSnapshotBuilder()
                .setMarketPrice(ISIN.MunichRe, new Amount(100.0))
                .setDate(LocalDate.of(2018, 5, 3))
                .build();

        HistoricalMarketData historicalMarketData = new HistoricalMarketData(firstMarketPriceSnapshot);

        try {
            historicalMarketData.registerClosedDay(secondMarketPriceSnapshot);
        }
        catch(DomainException e) {
            Assert.assertEquals("The specified date must not equal the date of the last registered market price snapshot.", e.getMessage());
            return;
        }

        Assert.fail("DomainException expected.");
    }

    @Test
    public void registerSingleClosingPriceFails_ifDate_equals_lastSnapshotDate() {
        HistoricalMarketData historicalMarketData = new HistoricalMarketData(ISIN.MunichRe, new Amount(100.0), LocalDate.of(2018, 5, 3));

        try {
            historicalMarketData.registerClosedDay(new Amount(100.0), LocalDate.of(2018, 5, 3));
        }
        catch(DomainException e) {
            Assert.assertEquals("The specified date must not equal the date of the last registered market price snapshot.", e.getMessage());
            return;
        }

        Assert.fail("DomainException expected.");
    }

    // countDaysAfter

    @Test
    public void countDaysAfter_returnsZero_ifCalledForLastDay() {
        HistoricalMarketData historicalMarketData = new HistoricalMarketData(ISIN.MunichRe, new Amount(100.0), LocalDate.of(2018, 5, 4));
        historicalMarketData.registerClosedDay(new Amount(110.0), LocalDate.of(2018, 5, 7));

        DayCount dayCount = historicalMarketData.countDaysAfter(LocalDate.of(2018, 5, 7));

        Assert.assertEquals(0, dayCount.getValue());
    }

    @Test
    public void countDaysAfter_returnsOne_ifCalledForPreviousDay() {
        HistoricalMarketData historicalMarketData = new HistoricalMarketData(ISIN.MunichRe, new Amount(100.0), LocalDate.of(2018, 5, 4));
        historicalMarketData.registerClosedDay(new Amount(110.0), LocalDate.of(2018, 5, 7));

        DayCount dayCount = historicalMarketData.countDaysAfter(LocalDate.of(2018, 5, 4));

        Assert.assertEquals(1, dayCount.getValue());
    }

    @Test
    public void countDaysAfter_returnsTwo_ifCalledForDayBeforePreviousDay() {
        HistoricalMarketData historicalMarketData = new HistoricalMarketData(ISIN.MunichRe, new Amount(100.0), LocalDate.of(2018, 5, 4));
        historicalMarketData.registerClosedDay(new Amount(110.0), LocalDate.of(2018, 5, 7));
        historicalMarketData.registerClosedDay(new Amount(110.0), LocalDate.of(2018, 5, 8));

        DayCount dayCount = historicalMarketData.countDaysAfter(LocalDate.of(2018, 5, 4));

        Assert.assertEquals(2, dayCount.getValue());
    }

    @Test
    public void countDaysAfter_fails_ifDateNotSpecified() {
        HistoricalMarketData historicalMarketData = new HistoricalMarketData(ISIN.MunichRe, new Amount(100.0), LocalDate.of(2018, 5, 4));

        try {
            historicalMarketData.countDaysAfter(null);
        }
        catch(RuntimeException e) {
            Assert.assertEquals("The date must be specified.", e.getMessage());
            return;
        }

        Assert.fail("RuntimeException expected.");
    }

    @Test
    public void countDaysAfter_fails_ifCalledForUnknownDate() {
        HistoricalMarketData historicalMarketData = new HistoricalMarketData(ISIN.MunichRe, new Amount(100.0), LocalDate.of(2018, 5, 4));
        historicalMarketData.registerClosedDay(new Amount(110.0), LocalDate.of(2018, 5, 7));

        try {
            historicalMarketData.countDaysAfter(LocalDate.of(2018, 5, 6));
        }
        catch(RuntimeException e) {
            Assert.assertEquals("The given date is unknown.", e.getMessage());
            return;
        }

        Assert.fail("RuntimeException expected.");
    }

    @Test
    public void countDaysAfter_fails_ifCalledForDateAfterHistory() {
        HistoricalMarketData historicalMarketData = new HistoricalMarketData(ISIN.MunichRe, new Amount(100.0), LocalDate.of(2018, 5, 4));
        historicalMarketData.registerClosedDay(new Amount(110.0), LocalDate.of(2018, 5, 7));

        try {
            historicalMarketData.countDaysAfter(LocalDate.of(2018, 5, 8));
        }
        catch(RuntimeException e) {
            Assert.assertEquals("The given date lies after the market data history time line.", e.getMessage());
            return;
        }

        Assert.fail("RuntimeException expected.");
    }

    @Test
    public void countDaysAfter_fails_ifCalledForDateBeforeHistory() {
        HistoricalMarketData historicalMarketData = new HistoricalMarketData(ISIN.MunichRe, new Amount(100.0), LocalDate.of(2018, 5, 4));
        historicalMarketData.registerClosedDay(new Amount(110.0), LocalDate.of(2018, 5, 7));

        try {
            historicalMarketData.countDaysAfter(LocalDate.of(2018, 5, 3));
        }
        catch(RuntimeException e) {
            Assert.assertEquals("The given date lies before the market data history time line.", e.getMessage());
            return;
        }

        Assert.fail("RuntimeException expected.");
    }

    // Multi initialization

    @Test
    public void initializeWithMultipleMarketPriceSnapshots() {
        MarketPriceSnapshot firstSnapshot = new MarketPriceSnapshotBuilder()
                .setMarketPrice(new ISIN("A"), new Amount(100.0))
                .setMarketPrice(new ISIN("B"), new Amount(200.0))
                .setDate(LocalDate.of(2000, 1, 1))
                .build();

        MarketPriceSnapshot secondSnapshot = new MarketPriceSnapshotBuilder()
                .setMarketPrice(new ISIN("A"), new Amount(120.0))
                .setMarketPrice(new ISIN("B"), new Amount(220.0))
                .setDate(LocalDate.of(2000, 1, 2))
                .build();

        MarketPriceSnapshot thirdSnapshot = new MarketPriceSnapshotBuilder()
                .setMarketPrice(new ISIN("A"), new Amount(130.0))
                .setMarketPrice(new ISIN("B"), new Amount(190.0))
                .setDate(LocalDate.of(2000, 1, 3))
                .build();

        List<MarketPriceSnapshot> marketPriceSnapshotList = Arrays.asList(
                firstSnapshot,
                secondSnapshot,
                thirdSnapshot
        );

        HistoricalMarketData historicalMarketData = HistoricalMarketData.of(marketPriceSnapshotList);

        Assert.assertEquals(thirdSnapshot.getDate(), historicalMarketData.getDate());
        Assert.assertSame(thirdSnapshot, historicalMarketData.getLastClosingMarketPrices());
        Assert.assertEquals(3, historicalMarketData.getDuration().getValue());
    }

    @Test
    public void initializeWithMultipleMarketPriceSnapshots_fails_ifListIsNull() {
        List<MarketPriceSnapshot> marketPriceSnapshotList = null;

        try {
            HistoricalMarketData.of(marketPriceSnapshotList);
        }
        catch(RuntimeException e) {
            Assert.assertEquals("The market price snapshot list must be specified.", e.getMessage());
            return;
        }

        Assert.fail("RuntimeException expected.");
    }

    @Test
    public void initializeWithMultipleMarketPriceSnapshots_fails_ifListIsEmpty() {
        List<MarketPriceSnapshot> marketPriceSnapshotList = new ArrayList<>();

        try {
            HistoricalMarketData.of(marketPriceSnapshotList);
        }
        catch(RuntimeException e) {
            Assert.assertEquals("The market price snapshot list must not be empty.", e.getMessage());
            return;
        }

        Assert.fail("RuntimeException expected.");
    }
}
