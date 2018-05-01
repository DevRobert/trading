package trading.domain.simulation;

import org.junit.Assert;
import org.junit.Test;
import trading.domain.Amount;
import trading.domain.ISIN;
import trading.domain.market.MarketPriceSnapshot;
import trading.domain.market.MarketPriceSnapshotBuilder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
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
        MarketPriceSnapshot marketPriceSnapshot = new MarketPriceSnapshotBuilder()
                .setMarketPrice(ISIN.MunichRe, new Amount(100.0))
                .setDate(LocalDate.now())
                .build();

        MultiStockListDataSource multiStockListDataSource = new MultiStockListDataSource(Arrays.asList(marketPriceSnapshot));
        Assert.assertSame(marketPriceSnapshot, multiStockListDataSource.getNextClosingMarketPrices());
    }

    @Test
    public void getNextClosingMarketPrices_afterOneDay_fails_ofOneItemList() {
        MarketPriceSnapshot marketPriceSnapshot = new MarketPriceSnapshotBuilder()
                .setMarketPrice(ISIN.MunichRe, new Amount(100.0))
                .setDate(LocalDate.now())
                .build();

        SimulationMarketDataSource multiStockListDataSource = new MultiStockListDataSource(Arrays.asList(marketPriceSnapshot));

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
        MarketPriceSnapshot firstMarketPriceSnapshot = new MarketPriceSnapshotBuilder()
                .setMarketPrice(ISIN.MunichRe, new Amount(100.0))
                .setDate(LocalDate.of(2018, 1, 1))
                .build();

        MarketPriceSnapshot secondMarketPriceSnapshot = new MarketPriceSnapshotBuilder()
                .setMarketPrice(ISIN.MunichRe, new Amount(100.0))
                .setDate(LocalDate.of(2018, 1, 2))
                .build();

        List<MarketPriceSnapshot> marketPriceSnapshots = Arrays.asList(firstMarketPriceSnapshot, secondMarketPriceSnapshot);

        MultiStockListDataSource multiStockListDataSource = new MultiStockListDataSource(marketPriceSnapshots);

        multiStockListDataSource.getNextClosingMarketPrices();

        Assert.assertSame(secondMarketPriceSnapshot, multiStockListDataSource.getNextClosingMarketPrices());
    }
}
