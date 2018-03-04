package trading.market;

import org.junit.Assert;
import org.junit.Test;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import trading.Amount;
import trading.ISIN;

public class HistoricalMarketDataTest {
    @Test
    public void retrieveStockDataForInitiallyRegisteredStocks() {
        Amount initialMarketPrice = new Amount(1000.0);

        MarketPriceSnapshotBuilder marketPriceSnapshotBuilder = new MarketPriceSnapshotBuilder();
        marketPriceSnapshotBuilder.setMarketPrice(ISIN.MunichRe, initialMarketPrice);

        HistoricalMarketData historicalMarketData = new HistoricalMarketData(marketPriceSnapshotBuilder.build());

        Assert.assertEquals(initialMarketPrice, historicalMarketData.getStockData(ISIN.MunichRe).getLastMarketPrice());
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
        throw new NotImplementedException();
    }

    @Test
    public void marketPriceUpdateFailsIfAvailableStockMissing() {
        throw new NotImplementedException();
    }
}
