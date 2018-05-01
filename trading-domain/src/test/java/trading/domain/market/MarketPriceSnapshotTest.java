package trading.domain.market;

import org.junit.Assert;
import org.junit.Test;
import trading.domain.Amount;
import trading.domain.ISIN;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MarketPriceSnapshotTest {
    @Test
    public void returnsMarketPriceForKnownISIN() {
        Amount marketPrice = new Amount(1000.0);

        MarketPriceSnapshot snapshot = new MarketPriceSnapshotBuilder()
                .setMarketPrice(ISIN.MunichRe, marketPrice)
                .setDate(LocalDate.now())
                .build();

        Assert.assertEquals(marketPrice, snapshot.getMarketPrice(ISIN.MunichRe));
    }

    @Test
    public void retrievalOfMarketPriceFailsForUnknownISIN() {
        MarketPriceSnapshot snapshot = new MarketPriceSnapshotBuilder()
                .setDate(LocalDate.now())
                .build();

        try {
            snapshot.getMarketPrice(ISIN.MunichRe);
        }
        catch(UnknownStockException ex) {
            return;
        }

        Assert.fail("UnknownStockException expected.");
    }

    @Test
    public void listOfReturnedISINsCoversRegisteredStocks() {
        MarketPriceSnapshot snapshot = new MarketPriceSnapshotBuilder()
                .setMarketPrice(ISIN.MunichRe, new Amount(1000.0))
                .setMarketPrice(ISIN.Allianz, new Amount(500.0))
                .setDate(LocalDate.now())
                .build();

        Set<ISIN> isins = snapshot.getISINs();

        Assert.assertTrue(isins.contains(ISIN.MunichRe));
        Assert.assertTrue(isins.contains(ISIN.Allianz));
        Assert.assertEquals(2, isins.size());
    }

    @Test
    public void initializationFails_ifDateNotSpecified() {
        LocalDate date = null;
        Map<ISIN, Amount> marketPrices = new HashMap<>();

        try {
            new MarketPriceSnapshot(marketPrices, date);
        }
        catch(RuntimeException e) {
            Assert.assertEquals("The date must be specified.", e.getMessage());
            return;
        }

        Assert.fail("RuntimeException expected.");
    }

    @Test
    public void returnsDate() {
        LocalDate date = LocalDate.of(2018, 4, 3);
        Map<ISIN, Amount> marketPrices = new HashMap<>();

        MarketPriceSnapshot marketPriceSnapshot = new MarketPriceSnapshot(marketPrices, date);

        Assert.assertSame(date, marketPriceSnapshot.getDate());
    }
}
