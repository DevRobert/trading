package trading.strategy;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TradingStrategyParametersTest {
    TradingStrategyParametersBuilder builder;

    @Before
    public void before() {
        this.builder = new TradingStrategyParametersBuilder();
    }

    @Test
    public void givenParametersAreReturned() {
        builder.setParameter("A", "A_Value");
        builder.setParameter("B", "B_Value");

        TradingStrategyParameters parameters = builder.build();

        Assert.assertEquals("A_Value", parameters.getParameter("A"));
        Assert.assertEquals("B_Value", parameters.getParameter("B"));
    }

    @Test
    public void retrievalOfUnknownParameterFails() {
        TradingStrategyParameters parameters = builder.build();

        try {
            parameters.getParameter("A");
        }
        catch(MissingParameterException ex) {
            Assert.assertEquals("The parameter 'A' has not been specified.", ex.getMessage());
            return;
        }

        Assert.fail("MissingParameterException expected.");
    }
}
