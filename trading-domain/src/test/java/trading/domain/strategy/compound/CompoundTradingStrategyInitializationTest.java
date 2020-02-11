package trading.domain.strategy.compound;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import trading.domain.Amount;
import trading.domain.ISIN;
import trading.domain.account.Account;
import trading.domain.account.AccountBuilder;
import trading.domain.account.TaxStrategies;
import trading.domain.broker.Broker;
import trading.domain.broker.CommissionStrategies;
import trading.domain.broker.VirtualBroker;
import trading.domain.market.HistoricalMarketData;
import trading.domain.strategy.TradingStrategyContext;

import java.time.LocalDate;

public class CompoundTradingStrategyInitializationTest {
    private TradingStrategyContext tradingStrategyContext;
    private ScoringStrategy buyScoringStrategy;
    private BuyStocksSelector buyStocksSelector;
    private ScoringStrategy sellScoringStrategy;
    private SellStocksSelector sellStocksSelector;

    @Before
    public void before() {
        Account account = new AccountBuilder()
                .setAvailableMoney(new Amount(1000.0))
                .setTaxStrategy(TaxStrategies.getNoTaxesStrategy())
                .build();

        HistoricalMarketData historicalMarketData = new HistoricalMarketData(ISIN.MunichRe, new Amount(1000.0), LocalDate.now());
        Broker broker = new VirtualBroker(account, historicalMarketData, CommissionStrategies.getZeroCommissionStrategy());

        this.tradingStrategyContext = new TradingStrategyContext(account, broker, historicalMarketData);
        this.buyScoringStrategy = new FixedScoringStrategy();
        this.buyStocksSelector = new BuyStocksSelector(new Score(0.0), 1.0);
        this.sellScoringStrategy = new FixedScoringStrategy();
        this.sellStocksSelector =new SellStocksSelector(new Score(0.0));
    }

    protected CompoundTradingStrategy createCompoundTradingStrategy() {
        CompoundTradingStrategyParameters compoundTradingStrategyParameters = new CompoundTradingStrategyParametersBuilder()
                .setBuyScoringStrategy(this.buyScoringStrategy)
                .setBuyStocksSelector(this.buyStocksSelector)
                .setSellScoringStrategy(this.sellScoringStrategy)
                .setSellStocksSelector(this.sellStocksSelector)
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
        this.buyScoringStrategy = null;

        try {
            createCompoundTradingStrategy();
        }
        catch(RuntimeException ex) {
            Assert.assertEquals("The buy scoring strategy was not specified.", ex.getMessage());
            return;
        }

        Assert.fail("RuntimeException expected.");
    }

    @Test
    public void initializationFails_ifBuyStockSelectorNotSpecified() {
        this.buyStocksSelector = null;

        try {
            createCompoundTradingStrategy();
        }
        catch(RuntimeException ex) {
            Assert.assertEquals("The buy stock selector was not specified.", ex.getMessage());
            return;
        }

        Assert.fail("RuntimeException expected.");
    }


    @Test
    public void initializationFails_ifSellScoringStrategyNotSpecified() {
        this.sellScoringStrategy = null;

        try {
            createCompoundTradingStrategy();
        }
        catch(RuntimeException ex) {
            Assert.assertEquals("The sell scoring strategy was not specified.", ex.getMessage());
            return;
        }

        Assert.fail("RuntimeException expected.");
    }

    @Test
    public void initializationFails_ifSellStockSelectorNotSpecified() {
        this.sellStocksSelector = null;

        try {
            createCompoundTradingStrategy();
        }
        catch(RuntimeException ex) {
            Assert.assertEquals("The sell stock selector was not specified.", ex.getMessage());
            return;
        }

        Assert.fail("RuntimeException expected.");
    }
}
