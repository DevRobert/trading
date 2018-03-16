package trading.market;

import org.junit.Assert;
import org.junit.Test;
import trading.Amount;
import trading.DayCount;
import trading.ISIN;

import java.util.Set;

public class HistoricalMarketDataTest {
    // Get initial closing market prices

    @Test
    public void retrieveLastMarketPriceForInitiallyRegisteredStocks() {
        Amount initialClosingMarketPrice = new Amount(1000.0);

        MarketPriceSnapshotBuilder marketPriceSnapshotBuilder = new MarketPriceSnapshotBuilder();
        marketPriceSnapshotBuilder.setMarketPrice(ISIN.MunichRe, initialClosingMarketPrice);

        HistoricalMarketData historicalMarketData = new HistoricalMarketData(marketPriceSnapshotBuilder.build());

        Assert.assertEquals(initialClosingMarketPrice, historicalMarketData.getStockData(ISIN.MunichRe).getLastClosingMarketPrice());
    }

    @Test
    public void retrieveLastMarketPriceForInitiallyRegisteredSingleStock() {
        Amount initialClosingMarketPrice = new Amount(1000.0);
        HistoricalMarketData historicalMarketData = new HistoricalMarketData(ISIN.MunichRe, initialClosingMarketPrice);
        Assert.assertEquals(initialClosingMarketPrice, historicalMarketData.getStockData(ISIN.MunichRe).getLastClosingMarketPrice());
    }

    @Test
    public void retrievalOfStockDataFailsForUnknownStock() {
        MarketPriceSnapshot marketPriceSnapshot = new MarketPriceSnapshotBuilder().build();
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
        MarketPriceSnapshotBuilder marketPriceSnapshotBuilder = new MarketPriceSnapshotBuilder();
        marketPriceSnapshotBuilder.setMarketPrice(ISIN.MunichRe, new Amount(1000.0));
        marketPriceSnapshotBuilder.setMarketPrice(ISIN.Allianz, new Amount(500.0));
        HistoricalMarketData historicalMarketData = new HistoricalMarketData(marketPriceSnapshotBuilder.build());

        marketPriceSnapshotBuilder.setMarketPrice(ISIN.MunichRe, new Amount(1100.0));
        marketPriceSnapshotBuilder.setMarketPrice(ISIN.Allianz, new Amount(550.0));
        historicalMarketData.registerClosedDay(marketPriceSnapshotBuilder.build());

        Assert.assertEquals(new Amount(1100.0), historicalMarketData.getStockData(ISIN.MunichRe).getLastClosingMarketPrice());
        Assert.assertEquals(new Amount(550.0), historicalMarketData.getStockData(ISIN.Allianz).getLastClosingMarketPrice());
    }

    @Test
    public void registerMarketPriceUpdateForSingleStock() {
        Amount initialClosingMarketPrice = new Amount(1000.0);
        HistoricalMarketData historicalMarketData = new HistoricalMarketData(ISIN.MunichRe, initialClosingMarketPrice);

        Amount newClosingMarketPrice = new Amount(1100.0);
        historicalMarketData.registerClosedDay(newClosingMarketPrice);

        Assert.assertEquals(newClosingMarketPrice, historicalMarketData.getStockData(ISIN.MunichRe).getLastClosingMarketPrice());
    }

    @Test
    public void registerMarketPriceUpdateForSingleStockFailsSinceMultipleStocksAvailable() {
        MarketPriceSnapshotBuilder marketPriceSnapshotBuilder = new MarketPriceSnapshotBuilder();
        marketPriceSnapshotBuilder.setMarketPrice(ISIN.MunichRe, new Amount(1000.0));
        marketPriceSnapshotBuilder.setMarketPrice(ISIN.Allianz, new Amount(500.0));
        HistoricalMarketData historicalMarketData = new HistoricalMarketData(marketPriceSnapshotBuilder.build());

        try {
            historicalMarketData.registerClosedDay(new Amount(1100.0));
        }
        catch(RuntimeException ex) {
            Assert.assertEquals("The single-stock market update function must not be used when multiple stocks registered.", ex.getMessage());
            return;
        }

        Assert.fail("RuntimeException expected.");
    }

    @Test
    public void derivesAvailableStocksFromMarketPriceSnapshot() {
        MarketPriceSnapshotBuilder marketPriceSnapshotBuilder = new MarketPriceSnapshotBuilder();
        marketPriceSnapshotBuilder.setMarketPrice(ISIN.MunichRe, new Amount(1000.0));
        marketPriceSnapshotBuilder.setMarketPrice(ISIN.Allianz, new Amount(500.0));
        HistoricalMarketData historicalMarketData = new HistoricalMarketData(marketPriceSnapshotBuilder.build());

        Set<ISIN> availableStocks = historicalMarketData.getAvailableStocks();

        Assert.assertEquals(2, availableStocks.size());
        Assert.assertTrue(availableStocks.contains(ISIN.MunichRe));
        Assert.assertTrue(availableStocks.contains(ISIN.Allianz));
    }

    @Test
    public void marketPriceUpdateFailsIfAvailableStockMissing() {
        MarketPriceSnapshotBuilder marketPriceSnapshotBuilder = new MarketPriceSnapshotBuilder();
        marketPriceSnapshotBuilder.setMarketPrice(ISIN.MunichRe, new Amount(1000.0));
        HistoricalMarketData historicalMarketData = new HistoricalMarketData(marketPriceSnapshotBuilder.build());

        marketPriceSnapshotBuilder = new MarketPriceSnapshotBuilder();

        try {
            historicalMarketData.registerClosedDay(marketPriceSnapshotBuilder.build());
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
            new HistoricalMarketData(isin, initialClosingMarketPrice);
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
            new HistoricalMarketData(isin, initialClosingMarketPrice);
        }
        catch(RuntimeException ex) {
            Assert.assertEquals("The initialClosingMarketPrice must be specified.", ex.getMessage());
            return;
        }

        Assert.fail("RuntimeException expected.");
    }

    // Duration

    @Test
    public void getDurationReturnsOneForOneDayHistory() {
        HistoricalMarketData historicalMarketData = new HistoricalMarketData(ISIN.MunichRe, new Amount(1000.0));
        Assert.assertEquals(new DayCount(1), historicalMarketData.getDuration());
    }

    @Test
    public void getDurationReturnsTwoForTwoDaysHistory() {
        HistoricalMarketData historicalMarketData = new HistoricalMarketData(ISIN.MunichRe, new Amount(1000.0));
        historicalMarketData.registerClosedDay(new Amount(1100.0));
        Assert.assertEquals(new DayCount(2), historicalMarketData.getDuration());
    }

    // Misc

    @Test
    public void singleStockPriceUpdateMethodCanBeUsedIfInitializedWithSingleStockClosingMarketPricesMap() {
        MarketPriceSnapshotBuilder marketPriceSnapshotBuilder = new MarketPriceSnapshotBuilder();
        marketPriceSnapshotBuilder.setMarketPrice(ISIN.MunichRe, new Amount(1000.0));
        MarketPriceSnapshot initialClosingMarketPrices = marketPriceSnapshotBuilder.build();

        HistoricalMarketData historicalMarketData = new HistoricalMarketData(initialClosingMarketPrices);
        historicalMarketData.registerClosedDay(new Amount(1100.0));

        Assert.assertEquals(new Amount(1100.0), historicalMarketData.getStockData(ISIN.MunichRe).getLastClosingMarketPrice());
    }

    // Last closing market prices

    @Test
    public void returnsLastClosingMarketPrices_initially_forOneStock() {
        HistoricalMarketData historicalMarketData = new HistoricalMarketData(ISIN.MunichRe, new Amount(1000.0));

        MarketPriceSnapshot lastClosingMarketPrices = historicalMarketData.getLastClosingMarketPrices();

        Assert.assertEquals(new Amount(1000.0), lastClosingMarketPrices.getMarketPrice(ISIN.MunichRe));
    }

    @Test
    public void returnsLastClosingMarketPrices_initially_forTwoStocks() {
        MarketPriceSnapshotBuilder marketPriceSnapshotBuilder = new MarketPriceSnapshotBuilder();
        marketPriceSnapshotBuilder.setMarketPrice(ISIN.MunichRe, new Amount(1000.0));
        marketPriceSnapshotBuilder.setMarketPrice(ISIN.Allianz, new Amount(500.0));

        HistoricalMarketData historicalMarketData = new HistoricalMarketData(marketPriceSnapshotBuilder.build());

        MarketPriceSnapshot lastClosingMarketPrices = historicalMarketData.getLastClosingMarketPrices();

        Assert.assertEquals(new Amount(1000.0), lastClosingMarketPrices.getMarketPrice(ISIN.MunichRe));
        Assert.assertEquals(new Amount(500.0), lastClosingMarketPrices.getMarketPrice(ISIN.Allianz));
    }

    @Test
    public void returnsLastClosingMarketPrices_afterOneDay_forOneStock() {
        HistoricalMarketData historicalMarketData = new HistoricalMarketData(ISIN.MunichRe, new Amount(1000.0));
        historicalMarketData.registerClosedDay(new Amount(2000.0));

        MarketPriceSnapshot lastClosingMarketPrices = historicalMarketData.getLastClosingMarketPrices();

        Assert.assertEquals(new Amount(2000.0), lastClosingMarketPrices.getMarketPrice(ISIN.MunichRe));
    }

    @Test
    public void returnsLastClosingMarketPrices_afterOneDay_forTwoStocks() {
        MarketPriceSnapshotBuilder marketPriceSnapshotBuilder = new MarketPriceSnapshotBuilder();
        marketPriceSnapshotBuilder.setMarketPrice(ISIN.MunichRe, new Amount(1000.0));
        marketPriceSnapshotBuilder.setMarketPrice(ISIN.Allianz, new Amount(500.0));

        HistoricalMarketData historicalMarketData = new HistoricalMarketData(marketPriceSnapshotBuilder.build());

        marketPriceSnapshotBuilder.setMarketPrice(ISIN.MunichRe, new Amount(2000.0));
        marketPriceSnapshotBuilder.setMarketPrice(ISIN.Allianz, new Amount(1000.0));
        historicalMarketData.registerClosedDay(marketPriceSnapshotBuilder.build());

        MarketPriceSnapshot lastClosingMarketPrices = historicalMarketData.getLastClosingMarketPrices();

        Assert.assertEquals(new Amount(2000.0), lastClosingMarketPrices.getMarketPrice(ISIN.MunichRe));
        Assert.assertEquals(new Amount(1000.0), lastClosingMarketPrices.getMarketPrice(ISIN.Allianz));
    }
}
