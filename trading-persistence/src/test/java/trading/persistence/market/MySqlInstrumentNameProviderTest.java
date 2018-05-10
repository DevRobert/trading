package trading.persistence.market;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import trading.domain.ISIN;
import trading.domain.market.InstrumentNameProvider;
import trading.persistence.MySqlRepositoryParameters;
import trading.persistence.MySqlRepositoryParametersBuilder;

public class MySqlInstrumentNameProviderTest {
    private InstrumentNameProvider instrumentNameProvider;

    @Before
    public void before() {
        MySqlRepositoryParameters parameters = new MySqlRepositoryParametersBuilder()
                .setServer("localhost")
                .setUsername("root")
                .setPassword("testtest")
                .setDatabase("trading-test")
                .build();

        this.instrumentNameProvider = new MySqlInstrumentNameProvider(parameters);
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
