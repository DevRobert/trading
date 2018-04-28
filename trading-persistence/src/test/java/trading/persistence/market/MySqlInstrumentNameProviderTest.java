package trading.persistence.market;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import trading.domain.ISIN;
import trading.domain.market.InstrumentNameProvider;

public class MySqlInstrumentNameProviderTest {
    private InstrumentNameProvider instrumentNameProvider;

    @Before
    public void before() {
        this.instrumentNameProvider = new MySqlInstrumentNameProvider();
    }

    @Test
    public void getName() {
        String name = this.instrumentNameProvider.getInstrumentName(ISIN.MunichRe);
        Assert.assertEquals("Münchener Rückversicherungs-Gesellschaft", name);
    }

    @Test
    public void returnsNullForUnknownName() {
        String name = this.instrumentNameProvider.getInstrumentName(new ISIN("Unknown isin"));
        Assert.assertNull(name);
    }
}
