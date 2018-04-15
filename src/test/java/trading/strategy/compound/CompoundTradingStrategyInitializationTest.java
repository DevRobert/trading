package trading.strategy.compound;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import trading.Amount;
import trading.ISIN;
import trading.account.Account;
import trading.broker.Broker;
import trading.broker.CommissionStrategies;
import trading.broker.VirtualBroker;
import trading.market.HistoricalMarketData;
import trading.strategy.TradingStrategyContext;

public class CompoundTradingStrategyInitializationTest {
    private TradingStrategyContext tradingStrategyContext;
    private ScoringStrategy scoringStrategy;
    private StockSelector stockSelector;

    @Before
    public void before() {
        Account account = new Account(new Amount(1000.0));
        HistoricalMarketData historicalMarketData = new HistoricalMarketData(ISIN.MunichRe, new Amount(1000.0));
        Broker broker = new VirtualBroker(account, historicalMarketData, CommissionStrategies.getZeroCommissionStrategy());

        this.tradingStrategyContext = new TradingStrategyContext(account, broker, historicalMarketData);
        this.scoringStrategy = new FixedScoringStrategy();
        this.stockSelector = new StockSelector(new Score(0.0), 1.0);
    }

    protected CompoundTradingStrategy createCompoundTradingStrategy() {
        return new CompoundTradingStrategy(this.tradingStrategyContext, this.scoringStrategy, this.stockSelector);
    }

    @Test
    public void initializationFails_ifContextNotSpecified() {
        this.tradingStrategyContext = null;

        try {
            createCompoundTradingStrategy();
        }
        catch(RuntimeException ex) {
            Assert.assertEquals("The trading strategy context was not specified.", ex.getMessage());
            return;
        }

        Assert.fail("RuntimeException expected.");
    }

    @Test
    public void initializationFails_ifScoringStrategyNotSpecified() {
        this.scoringStrategy = null;

        try {
            createCompoundTradingStrategy();
        }
        catch(RuntimeException ex) {
            Assert.assertEquals("The scoring strategy was not specified.", ex.getMessage());
            return;
        }

        Assert.fail("RuntimeException expected.");
    }

    @Test
    public void initializationFails_ifStockSelectorNotSpecified() {
        this.stockSelector = null;

        try {
            createCompoundTradingStrategy();
        }
        catch(RuntimeException ex) {
            Assert.assertEquals("The stock selector was not specified.", ex.getMessage());
            return;
        }

        Assert.fail("RuntimeException expected.");
    }
}