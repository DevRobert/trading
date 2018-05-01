package trading.domain.strategy;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import trading.domain.Amount;
import trading.domain.ISIN;
import trading.domain.account.Account;
import trading.domain.broker.Broker;
import trading.domain.broker.CommissionStrategies;
import trading.domain.broker.VirtualBroker;
import trading.domain.market.HistoricalMarketData;

import java.time.LocalDate;

public class TradingStrategyContextTest {
    private Account account;
    private Broker broker;
    private HistoricalMarketData historicalMarketData;

    @Before
    public void before() {
        this.historicalMarketData = new HistoricalMarketData(ISIN.MunichRe, new Amount(1000.0), LocalDate.now());
        this.account = new Account(new Amount(50000.0));
        this.broker = new VirtualBroker(this.account, this.historicalMarketData, CommissionStrategies.getZeroCommissionStrategy());
    }

    private void createTradingStrategyContext() {
        new TradingStrategyContext(this.account, this.broker, this.historicalMarketData);
    }

    @Test
    public void constructionFailsIfAccountNotSpecified() {
        this.account = null;

        try {
            createTradingStrategyContext();
        }
        catch(RuntimeException ex) {
            Assert.assertEquals("The account must be specified.", ex.getMessage());
            return;
        }

        Assert.fail("RuntimeException expected.");
    }

    @Test
    public void constructionFailsIfBrokerNotSpecified() {
        this.broker = null;

        try {
            createTradingStrategyContext();
        }
        catch(RuntimeException ex) {
            Assert.assertEquals("The broker must be specified.", ex.getMessage());
            return;
        }

        Assert.fail("RuntimeException expected.");
    }

    @Test
    public void constructionFailsIfHistoricalMarketDataNotSpecified() {
        this.historicalMarketData = null;

        try {
            createTradingStrategyContext();
        }
        catch(RuntimeException ex) {
            Assert.assertEquals("The historical market data must be specified.", ex.getMessage());
            return;
        }

        Assert.fail("RuntimeException expected.");
    }
}
