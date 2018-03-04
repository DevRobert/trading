package trading.market;

import org.junit.Assert;
import org.junit.Test;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import trading.Amount;
import trading.ISIN;

import java.util.Set;

public class HistoricalMarketDataTest {
    @Test
    public void retrieveStockDataForInitiallyRegisteredStocks() {
        Amount initialMarketPrice = new Amount(1000.0);

        MarketPriceSnapshotBuilder marketPriceSnapshotBuilder = new MarketPriceSnapshotBuilder();
        marketPriceSnapshotBuilder.setMarketPrice(ISIN.MunichRe, initialMarketPrice);

        HistoricalMarketData historicalMarketData = new HistoricalMarketData(marketPriceSnapshotBuilder.build());

        Assert.assertEquals(initialMarketPrice, historicalMarketData.getStockData(ISIN.MunichRe).getLastClosingMarketPrice());
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
}
