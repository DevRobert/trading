package trading.domain.simulation;

import org.junit.Assert;
import org.junit.Test;
import trading.domain.Amount;
import trading.domain.ISIN;
import trading.domain.market.MarketPriceSnapshot;
import trading.domain.market.MarketPriceSnapshotBuilder;

import java.util.ArrayList;
import java.util.List;

public class MultiStockListDataSourceTest {
    @Test
    public void constructionFails_ifNoSnapshotListSpecified() {
        List<MarketPriceSnapshot> marketPriceSnapshots = null;

        try {
            new MultiStockListDataSource(marketPriceSnapshots);
        }
        catch(Exception ex) {
            Assert.assertEquals("The market price snapshot list has to be specified.", ex.getMessage());
            return;
        }

        Assert.fail("Exception expected.");
    }

    @Test
    public void getNextClosingMarketPrices_initially_fails_IfEmptyList() {
        List<MarketPriceSnapshot> marketPriceSnapshots = new ArrayList<>();
        SimulationMarketDataSource multiStockListDataSource = new MultiStockListDataSource(marketPriceSnapshots);

        try {
            multiStockListDataSource.getNextClosingMarketPrices();
        }
        catch(SimulationMarketDataSourceExhaustedException ex) {
            return;
        }

        Assert.fail("SimulationMarketDataSourceExhaustedException expected.");
    }

    @Test
    public void getNextClosingMarketPrices_initially_returnsCorrectSnapshot() {
        List<MarketPriceSnapshot> marketPriceSnapshots = new ArrayList<>();

        MarketPriceSnapshotBuilder marketPriceSnapshotBuilder = new MarketPriceSnapshotBuilder();
        marketPriceSnapshotBuilder.setMarketPrice(ISIN.MunichRe, new Amount(100.0));
        MarketPriceSnapshot marketPriceSnapshot = marketPriceSnapshotBuilder.build();
        marketPriceSnapshots.add(marketPriceSnapshot);

        MultiStockListDataSource multiStockListDataSource = new MultiStockListDataSource(marketPriceSnapshots);
        Assert.assertSame(marketPriceSnapshot, multiStockListDataSource.getNextClosingMarketPrices());
    }

    @Test
    public void getNextClosingMarketPrices_afterOneDay_fails_ofOneItemList() {
        List<MarketPriceSnapshot> marketPriceSnapshots = new ArrayList<>();

        MarketPriceSnapshotBuilder marketPriceSnapshotBuilder = new MarketPriceSnapshotBuilder();
        marketPriceSnapshotBuilder.setMarketPrice(ISIN.MunichRe, new Amount(100.0));
        MarketPriceSnapshot marketPriceSnapshot = marketPriceSnapshotBuilder.build();
        marketPriceSnapshots.add(marketPriceSnapshot);

        SimulationMarketDataSource multiStockListDataSource = new MultiStockListDataSource(marketPriceSnapshots);

        multiStockListDataSource.getNextClosingMarketPrices();

        try {
            multiStockListDataSource.getNextClosingMarketPrices();
        }
        catch(SimulationMarketDataSourceExhaustedException ex) {
            return;
        }

        Assert.fail("SimulationMarketDataSourceExhaustedException expected.");
    }

    @Test
    public void getNextClosingMarketPrices_afterOneDay_returnsCorrectSnapshot_ifTwoItemsList() {
        List<MarketPriceSnapshot> marketPriceSnapshots = new ArrayList<>();

        MarketPriceSnapshotBuilder marketPriceSnapshotBuilder = new MarketPriceSnapshotBuilder();

        marketPriceSnapshotBuilder.setMarketPrice(ISIN.MunichRe, new Amount(100.0));
        MarketPriceSnapshot firstDayMarketPriceSnapshot = marketPriceSnapshotBuilder.build();
        marketPriceSnapshots.add(firstDayMarketPriceSnapshot);

        marketPriceSnapshotBuilder.setMarketPrice(ISIN.MunichRe, new Amount(100.0));
        MarketPriceSnapshot secondDayMarketPriceSnapshot = marketPriceSnapshotBuilder.build();
        marketPriceSnapshots.add(secondDayMarketPriceSnapshot);

        MultiStockListDataSource multiStockListDataSource = new MultiStockListDataSource(marketPriceSnapshots);

        multiStockListDataSource.getNextClosingMarketPrices();

        Assert.assertSame(secondDayMarketPriceSnapshot, multiStockListDataSource.getNextClosingMarketPrices());
    }
}
