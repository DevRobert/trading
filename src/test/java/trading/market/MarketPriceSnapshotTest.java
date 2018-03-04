package trading.market;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import trading.Amount;
import trading.ISIN;

import java.util.Set;

public class MarketPriceSnapshotTest {
    private MarketPriceSnapshotBuilder builder;

    @Before()
    public void before() {
        builder = new MarketPriceSnapshotBuilder();
    }

    @Test
    public void returnsMarketPriceForKnownISIN() {
        Amount marketPrice = new Amount(1000.0);
        builder.setMarketPrice(ISIN.MunichRe, marketPrice);

        MarketPriceSnapshot snapshot = builder.build();

        Assert.assertEquals(marketPrice, snapshot.getMarketPrice(ISIN.MunichRe));
    }

    @Test
    public void retrievalOfMarketPriceFailsForUnknownISIN() {
        MarketPriceSnapshot snapshot = builder.build();

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
        builder.setMarketPrice(ISIN.MunichRe, new Amount(1000.0));
        builder.setMarketPrice(ISIN.Allianz, new Amount(500.0));

        MarketPriceSnapshot snapshot = builder.build();

        Set<ISIN> isins = snapshot.getISINs();
        Assert.assertTrue(isins.contains(ISIN.MunichRe));
        Assert.assertTrue(isins.contains(ISIN.Allianz));
        Assert.assertEquals(2, isins.size());
    }
}
