package trading.domain.strategy.compound;

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
import trading.domain.strategy.NotImplementedTrigger;
import trading.domain.strategy.TradingStrategyContext;
import trading.domain.strategy.TriggerFactory;

public class CompoundTradingStrategyInitializationTest {
    private TradingStrategyContext tradingStrategyContext;
    private ScoringStrategy scoringStrategy;
    private StockSelector stockSelector;
    private TriggerFactory sellTriggerFactory;

    @Before
    public void before() {
        Account account = new Account(new Amount(1000.0));
        HistoricalMarketData historicalMarketData = new HistoricalMarketData(ISIN.MunichRe, new Amount(1000.0));
        Broker broker = new VirtualBroker(account, historicalMarketData, CommissionStrategies.getZeroCommissionStrategy());

        this.tradingStrategyContext = new TradingStrategyContext(account, broker, historicalMarketData);
        this.scoringStrategy = new FixedScoringStrategy();
        this.stockSelector = new StockSelector(new Score(0.0), 1.0);
        this.sellTriggerFactory = isin -> new NotImplementedTrigger();
    }

    protected CompoundTradingStrategy createCompoundTradingStrategy() {
        CompoundTradingStrategyParameters compoundTradingStrategyParameters = new CompoundTradingStrategyParametersBuilder()
                .setScoringStrategy(this.scoringStrategy)
                .setStockSelector(this.stockSelector)
                .setSellTriggerFactory(this.sellTriggerFactory)
                .build();

        return new CompoundTradingStrategy(compoundTradingStrategyParameters, this.tradingStrategyContext);
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
    public void initializationFails_ifParametersNotSpecified() {
        try {
            new CompoundTradingStrategy(null, this.tradingStrategyContext);
        }
        catch(RuntimeException ex) {
            Assert.assertEquals("The trading strategy parameters were not specified.", ex.getMessage());
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

    @Test
    public void initializationFails_ifSellTriggerNotSpecified() {
        this.sellTriggerFactory = null;

        try {
            createCompoundTradingStrategy();
        }
        catch(RuntimeException ex) {
            Assert.assertEquals("The sell trigger factory was not specified.", ex.getMessage());
            return;
        }

        Assert.fail("RuntimeException expected.");
    }
}
