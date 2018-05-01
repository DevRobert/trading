package trading.domain.simulation;

import org.junit.Assert;
import org.junit.Test;
import trading.domain.Amount;
import trading.domain.DateSequenceGenerator;
import trading.domain.ISIN;
import trading.domain.market.MarketPriceSnapshot;
import trading.domain.market.UnknownStockException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SingleStockListDataSourceTest {
    @Test
    public void constructionFails_ifIsinNotSpecified() {
        ISIN isin = null;
        List<Amount> closingMarketPrices = new ArrayList<>();
        List<LocalDate> dates = new ArrayList<>();

        try {
            new SingleStockListDataSource(isin, closingMarketPrices, dates);
        }
        catch(RuntimeException ex) {
            Assert.assertEquals("The ISIN must be specified.", ex.getMessage());
            return;
        }

        Assert.fail("RuntimeException expected.");
    }

    @Test
    public void constructionFails_ifPriceListNotSpecified() {
        ISIN isin = ISIN.MunichRe;
        List<Amount> closingMarketPrices = null;
        List<LocalDate> dates = new ArrayList<>();

        try {
            new SingleStockListDataSource(isin, closingMarketPrices, dates);
        }
        catch(RuntimeException ex) {
            Assert.assertEquals("The closing market prices list must be specified.", ex.getMessage());
            return;
        }

        Assert.fail("RuntimeException expected.");
    }

    @Test
    public void constructionFails_ifDateListNotSpecified() {
        ISIN isin = ISIN.MunichRe;

        List<Amount> closingMarketPrices = Arrays.asList(
                new Amount(1000.0),
                new Amount(1100.0)
        );

        List<LocalDate> dates = null;

        try {
            new SingleStockListDataSource(isin, closingMarketPrices, dates);
        }
        catch(RuntimeException ex) {
            Assert.assertEquals("The date list must be specified.", ex.getMessage());
            return;
        }

        Assert.fail("RuntimeException expected.");
    }

    @Test
    public void constructionFails_ifPriceListAndDateListHaveDifferentSizes() {
        ISIN isin = ISIN.MunichRe;

        List<Amount> closingMarketPrices = Arrays.asList(
                new Amount(1000.0),
                new Amount(1100.0)
        );

        List<LocalDate> dates = Arrays.asList(
                LocalDate.now()
        );

        try {
            new SingleStockListDataSource(isin, closingMarketPrices, dates);
        }
        catch(RuntimeException ex) {
            Assert.assertEquals("The sizes of the price list and the date list must equal.", ex.getMessage());
            return;
        }

        Assert.fail("RuntimeException expected.");
    }

    @Test
    public void getNextClosingMarketPrices_initially_fails_ifEmptyList() {
        List<Amount> closingMarketPrices = new ArrayList<>();
        List<LocalDate> dates = new ArrayList<>();

        SingleStockListDataSource singleStockListDataSource = new SingleStockListDataSource(ISIN.MunichRe, closingMarketPrices, dates);

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
        List<LocalDate> dates = new DateSequenceGenerator(LocalDate.now()).nextDates(1);

        SingleStockListDataSource singleStockListDataSource = new SingleStockListDataSource(ISIN.MunichRe, closingMarketPrices, dates);

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
        List<LocalDate> dates = new DateSequenceGenerator(LocalDate.now()).nextDates(1);

        SingleStockListDataSource singleStockListDataSource = new SingleStockListDataSource(ISIN.MunichRe, closingMarketPrices, dates);

        Assert.assertEquals(new Amount(1000.0), singleStockListDataSource.getNextClosingMarketPrices().getMarketPrice(ISIN.MunichRe));
    }

    @Test
    public void getNextClosingMarketPrices_initially_returnsCorrectDate_ifOneItemList() {
        LocalDate firstDate = LocalDate.now();

        List<Amount> closingMarketPrices = Arrays.asList(new Amount(1000.0));
        List<LocalDate> dates = new DateSequenceGenerator(firstDate).nextDates(1);

        SingleStockListDataSource singleStockListDataSource = new SingleStockListDataSource(ISIN.MunichRe, closingMarketPrices, dates);

        Assert.assertEquals(firstDate, singleStockListDataSource.getNextClosingMarketPrices().getDate());
    }

    @Test
    public void getNextClosingMarketPrices_afterOneDay_fails_ifOneItemList() {
        List<Amount> closingMarketPrices = Arrays.asList(new Amount(1000.0));
        List<LocalDate> dates = new DateSequenceGenerator(LocalDate.now()).nextDates(1);

        SingleStockListDataSource singleStockListDataSource = new SingleStockListDataSource(ISIN.MunichRe, closingMarketPrices, dates);

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
    public void getNextClosingMarketPrices_afterOneDay_returnsCorrectPrice_ifTwoItemsList() {
        List<Amount> closingMarketPrices = Arrays.asList(new Amount(1000.0), new Amount(1100.0));
        List<LocalDate> dates = new DateSequenceGenerator(LocalDate.now()).nextDates(2);

        SingleStockListDataSource singleStockListDataSource = new SingleStockListDataSource(ISIN.MunichRe, closingMarketPrices, dates);

        singleStockListDataSource.getNextClosingMarketPrices();

        Assert.assertEquals(new Amount(1100.0), singleStockListDataSource.getNextClosingMarketPrices().getMarketPrice(ISIN.MunichRe));
    }

    @Test
    public void getNextClosingMarketPrices_afterOneDay_returnsCorrectDate_ifTwoItemsList() {
        LocalDate firstDate = LocalDate.now();
        LocalDate secondDate = firstDate.plusDays(1);

        List<Amount> closingMarketPrices = Arrays.asList(new Amount(1000.0), new Amount(1100.0));
        List<LocalDate> dates = Arrays.asList(firstDate, secondDate);

        SingleStockListDataSource singleStockListDataSource = new SingleStockListDataSource(ISIN.MunichRe, closingMarketPrices, dates);

        singleStockListDataSource.getNextClosingMarketPrices();

        Assert.assertEquals(secondDate, singleStockListDataSource.getNextClosingMarketPrices().getDate());
    }
}
