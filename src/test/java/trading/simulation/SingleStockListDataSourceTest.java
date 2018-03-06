package trading.simulation;

import org.junit.Assert;
import org.junit.Test;
import trading.Amount;
import trading.ISIN;
import trading.market.MarketPriceSnapshot;
import trading.market.UnknownStockException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SingleStockListDataSourceTest {
    @Test
    public void constructionFails_ifNoISINSpecified() {
        ISIN isin = null;
        List<Amount> closingMarketPrices = new ArrayList<>();

        try {
            new SingleStockListDataSource(isin, closingMarketPrices);
        }
        catch(Exception ex) {
            Assert.assertEquals("The ISIN has to be specified.", ex.getMessage());
            return;
        }

        Assert.fail("Exception expected.");
    }

    @Test
    public void constructionFails_ifNoListSpecified() {
        ISIN isin = ISIN.MunichRe;
        List<Amount> closingMarketPrices = null;

        try {
            new SingleStockListDataSource(isin, closingMarketPrices);
        }
        catch(Exception ex) {
            Assert.assertEquals("The closing market prices list has to be specified.", ex.getMessage());
            return;
        }

        Assert.fail("Exception expected.");
    }

    @Test
    public void getNextClosingMarketPrices_initially_fails_ifEmptyList() {
        List<Amount> closingMarketPrices = new ArrayList<>();
        SingleStockListDataSource singleStockListDataSource = new SingleStockListDataSource(ISIN.MunichRe, closingMarketPrices);

        try  {
            singleStockListDataSource.getNextClosingMarketPrices();
        }
        catch(SimulationMarketDataSourceExhaustedException ex) {
            Assert.assertEquals("There are no further closing market prices available.", ex.getMessage());
            return;
        }

        Assert.fail("SimulationMarketDataSourceExhaustedException expected.");
    }

    @Test
    public void getNextClosingMarketPrices_initially_fails_ifISINUnknown() {
        List<Amount> closingMarketPrices = Arrays.asList(new Amount(1000.0 ));
        SingleStockListDataSource singleStockListDataSource = new SingleStockListDataSource(ISIN.MunichRe, closingMarketPrices);

        MarketPriceSnapshot marketPriceSnapshot = singleStockListDataSource.getNextClosingMarketPrices();

        try  {
            marketPriceSnapshot.getMarketPrice(ISIN.Allianz);
        }
        catch(UnknownStockException ex) {
            return;
        }

        Assert.fail("UnknownStockException expected.");
    }

    @Test
    public void getNextClosingMarketPrices_initially_returnsCorrectPrice_ifOneItemList() {
        List<Amount> closingMarketPrices = Arrays.asList(new Amount(1000.0));
        SingleStockListDataSource singleStockListDataSource = new SingleStockListDataSource(ISIN.MunichRe, closingMarketPrices);

        Assert.assertEquals(new Amount(1000.0), singleStockListDataSource.getNextClosingMarketPrices().getMarketPrice(ISIN.MunichRe));
    }

    @Test
    public void getNextClosingMarketPrice_afterOneDay_fails_ifOneItemList() {
        List<Amount> closingMarketPrices = Arrays.asList(new Amount(1000.0));
        SingleStockListDataSource singleStockListDataSource = new SingleStockListDataSource(ISIN.MunichRe, closingMarketPrices);

        singleStockListDataSource.getNextClosingMarketPrices();

        try  {
            singleStockListDataSource.getNextClosingMarketPrices();
        }
        catch(SimulationMarketDataSourceExhaustedException ex) {
            Assert.assertEquals("There are no further closing market prices available.", ex.getMessage());
            return;
        }

        Assert.fail("SimulationMarketDataSourceExhaustedException expected.");
    }

    @Test
    public void getNextClosingMarketPrice_afterOneDay_returnsCorrectPrice_ifTwoItemsList() {
        List<Amount> closingMarketPrices = Arrays.asList(new Amount(1000.0), new Amount(1100.0));
        SingleStockListDataSource singleStockListDataSource = new SingleStockListDataSource(ISIN.MunichRe, closingMarketPrices);

        singleStockListDataSource.getNextClosingMarketPrices();

        Assert.assertEquals(new Amount(1100.0), singleStockListDataSource.getNextClosingMarketPrices().getMarketPrice(ISIN.MunichRe));
    }
}
