package trading.domain.market;

import org.junit.Assert;
import org.junit.Test;
import trading.domain.Amount;
import trading.domain.ISIN;

public class MarketPriceSnapshotBuilderTest {
    @Test
    public void createdSnapshotWillNoBeChanged() {
        MarketPriceSnapshotBuilder marketPriceSnapshotBuilder = new MarketPriceSnapshotBuilder();
        marketPriceSnapshotBuilder.setMarketPrice(ISIN.MunichRe, new Amount(1000.0));
        MarketPriceSnapshot marketPriceSnapshot = marketPriceSnapshotBuilder.build();

        marketPriceSnapshotBuilder.setMarketPrice(ISIN.MunichRe, new Amount(2000.0));

        Assert.assertEquals(new Amount(1000.0), marketPriceSnapshot.getMarketPrice(ISIN.MunichRe));
    }
}
