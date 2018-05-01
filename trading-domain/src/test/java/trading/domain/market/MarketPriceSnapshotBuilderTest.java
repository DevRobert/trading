package trading.domain.market;

import org.junit.Assert;
import org.junit.Test;
import trading.domain.Amount;
import trading.domain.ISIN;

import java.time.LocalDate;

public class MarketPriceSnapshotBuilderTest {
    @Test
    public void onceBuiltSnapshotRemainsUnchanged_ifBuilderReusedAfterwards() {
        MarketPriceSnapshotBuilder marketPriceSnapshotBuilder = new MarketPriceSnapshotBuilder();
        marketPriceSnapshotBuilder.setMarketPrice(ISIN.MunichRe, new Amount(1000.0));
        marketPriceSnapshotBuilder.setDate(LocalDate.now());
        MarketPriceSnapshot marketPriceSnapshot = marketPriceSnapshotBuilder.build();

        marketPriceSnapshotBuilder.setMarketPrice(ISIN.MunichRe, new Amount(2000.0));

        Assert.assertEquals(new Amount(1000.0), marketPriceSnapshot.getMarketPrice(ISIN.MunichRe));
    }

    @Test
    public void marketPriceSnapshotContainsSpecifiedDate() {
        LocalDate date = LocalDate.of(2018,4, 3);
        MarketPriceSnapshot marketPriceSnapshot = new MarketPriceSnapshotBuilder().setDate(date).build();
        Assert.assertSame(date, marketPriceSnapshot.getDate());
    }

    @Test
    public void setMarketPriceFails_ifIsinNotSpecified() {
        MarketPriceSnapshotBuilder marketPriceSnapshotBuilder = new MarketPriceSnapshotBuilder();

        try {
            marketPriceSnapshotBuilder.setMarketPrice(null, new Amount(100.0));
        }
        catch(RuntimeException e) {
            Assert.assertEquals("The ISIN must be specified.", e.getMessage());
            return;
        }

        Assert.fail("RuntimeException expected.");
    }

    @Test
    public void setMarketPriceFails_ifMarketPriceNotSpecified() {
        MarketPriceSnapshotBuilder marketPriceSnapshotBuilder = new MarketPriceSnapshotBuilder();

        try {
            marketPriceSnapshotBuilder.setMarketPrice(ISIN.MunichRe, null);
        }
        catch(RuntimeException e) {
            Assert.assertEquals("The market price must be specified.", e.getMessage());
            return;
        }

        Assert.fail("RuntimeException expected.");
    }
}
