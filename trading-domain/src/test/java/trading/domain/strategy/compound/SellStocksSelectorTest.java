package trading.domain.strategy.compound;

import org.junit.Assert;
import org.junit.Test;
import trading.domain.ISIN;
import trading.domain.Quantity;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class SellStocksSelectorTest {
    @Test
    public void selectStocksWithEqualOrHigherScore() {
        SellStocksSelector sellStocksSelector = new SellStocksSelector(new Score(0.5));

        Map<ISIN, Score> values = new HashMap<>();

        values.put(new ISIN("A"), new Score(0.0));
        values.put(new ISIN("B"), new Score(0.49));
        values.put(new ISIN("C"), new Score(0.5));
        values.put(new ISIN("D"), new Score(0.51));
        values.put(new ISIN("E"), new Score(1.0));

        Scores scores = new Scores(values, LocalDate.now());

        Map<ISIN, Quantity> currentStocks = new HashMap<>();

        currentStocks.put(new ISIN("A"), new Quantity(1));
        currentStocks.put(new ISIN("B"), new Quantity(2));
        currentStocks.put(new ISIN("C"), new Quantity(3));
        currentStocks.put(new ISIN("D"), new Quantity(4));
        currentStocks.put(new ISIN("E"), new Quantity(5));


        Map<ISIN, Quantity> sellStocks = sellStocksSelector.selectStocks(scores, currentStocks);

        Assert.assertFalse(sellStocks.containsKey(new ISIN("A")));
        Assert.assertFalse(sellStocks.containsKey(new ISIN("B")));
        Assert.assertEquals(new Quantity(3), sellStocks.get(new ISIN("C")));
        Assert.assertEquals(new Quantity(4), sellStocks.get(new ISIN("D")));
        Assert.assertEquals(new Quantity(5), sellStocks.get(new ISIN("E")));
    }
}
