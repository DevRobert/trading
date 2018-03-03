package trading;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

public class AvailableStocksTest {
    @Test
    public void givenStocksAreReturned() {
        Set<ISIN> isins = new HashSet<>();

        isins.add(ISIN.MunichRe);
        isins.add(ISIN.Allianz);

        AvailableStocks availableStocks = new AvailableStocks(isins);

        Assert.assertSame(isins, availableStocks.getISINs());
    }
}
