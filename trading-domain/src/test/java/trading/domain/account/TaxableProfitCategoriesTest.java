package trading.domain.account;

import org.junit.Assert;
import org.junit.Test;
import trading.domain.taxes.ProfitCategories;

public class TaxableProfitCategoriesTest {
    @Test
    public void profitCategoryDividendsExist() {
        Assert.assertEquals("Dividends", ProfitCategories.Dividends.getName());
    }

    @Test
    public void profitCategoryTradingExists() {
        Assert.assertEquals("Sale", ProfitCategories.Sale.getName());
    }
}
