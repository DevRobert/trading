package trading.strategy.progressive;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import trading.Amount;
import trading.DayCount;
import trading.ISIN;
import trading.account.Account;
import trading.broker.Broker;
import trading.broker.CommissionStrategy;
import trading.broker.VirtualBroker;
import trading.broker.ZeroCommissionStrategy;
import trading.market.HistoricalMarketData;
import trading.market.MarketPriceSnapshot;
import trading.market.MarketPriceSnapshotBuilder;
import trading.strategy.StrategyInitializationException;
import trading.strategy.WaitFixedPeriodTrigger;

public class ProgressiveTradingStrategyInitializationTest {
    private Account account;
    private HistoricalMarketData historicalMarketData;
    private ProgressiveTradingStrategyParametersBuilder parametersBuilder;
    private CommissionStrategy commissionStrategy;

    @Before
    public void before() {
        this.parametersBuilder = new ProgressiveTradingStrategyParametersBuilder();
        this.parametersBuilder.setISIN(ISIN.MunichRe);
        this.parametersBuilder.setBuyTriggerFactory((historicalMarketData) -> new WaitFixedPeriodTrigger(new DayCount(1)));
        this.parametersBuilder.setSellTriggerFactory((historicalMarketData) -> new WaitFixedPeriodTrigger(new DayCount(1)));
        this.parametersBuilder.setResetTriggerFactory((historicalMarketData) -> new WaitFixedPeriodTrigger(new DayCount(1)));

        Amount availableMoney = new Amount(50000.0);
        this.account = new Account(availableMoney);

        MarketPriceSnapshotBuilder marketPriceSnapshotBuilder = new MarketPriceSnapshotBuilder();
        MarketPriceSnapshot initialClosingMarketPrices = marketPriceSnapshotBuilder.build();
        this.historicalMarketData = new HistoricalMarketData(initialClosingMarketPrices);

        this.commissionStrategy = new ZeroCommissionStrategy();
    }

    private void initializeTradingStrategy() {
        ProgressiveTradingStrategyParameters parameters = null;

        if(this.parametersBuilder != null) {
            parameters = this.parametersBuilder.build();
        }

        Broker broker = new VirtualBroker(this.account, this.historicalMarketData, this.commissionStrategy);
        new ProgressiveTradingStrategy(parameters, this.account, broker, this.historicalMarketData);
    }

    @Test
    public void initializationFailsIfNoParametersSpecified() {
        parametersBuilder = null;

        try {
            initializeTradingStrategy();;
        }
        catch(StrategyInitializationException ex) {
            Assert.assertEquals("The strategy parameters were not specified.", ex.getMessage());
            return;
        }

        Assert.fail("StrategyInitializationException expected.");
    }

    @Test
    public void initializationFailsIfNoAccountSpecified() {
        ProgressiveTradingStrategyParameters parameters = this.parametersBuilder.build();
        Account account = null;
        Broker broker = new VirtualBroker(this.account, this.historicalMarketData, this.commissionStrategy);
        HistoricalMarketData historicalMarketData = this.historicalMarketData;

        try {
            new ProgressiveTradingStrategy(parameters, account, broker, historicalMarketData);
        }
        catch(RuntimeException ex) {
            Assert.assertEquals("The account was not specified.", ex.getMessage());
            return;
        }

        Assert.fail("StrategyInitializationException expected.");
    }

    @Test
    public void initializationFailsIfNoBrokerSpecified() {
        Broker broker = null;
        ProgressiveTradingStrategyParameters parameters = this.parametersBuilder.build();

        try {
            new ProgressiveTradingStrategy(parameters, this.account, broker, this.historicalMarketData);
        }
        catch(StrategyInitializationException ex) {
            Assert.assertEquals("The broker was not specified.", ex.getMessage());
            return;
        }

        Assert.fail("StrategyInitializationException expected.");
    }

    @Test
    public void initializationFailsIfNoHistoricalDataSpecified() {
        ProgressiveTradingStrategyParameters parameters = this.parametersBuilder.build();
        Account account = this.account;
        Broker broker = new VirtualBroker(this.account, this.historicalMarketData, this.commissionStrategy);
        HistoricalMarketData historicalMarketData = null;

        try {
            new ProgressiveTradingStrategy(parameters, account, broker, historicalMarketData);
        }
        catch(StrategyInitializationException ex) {
            Assert.assertEquals("The historical market data were not specified.", ex.getMessage());
            return;
        }

        Assert.fail("StrategyInitializationException expected.");
    }

    @Test
    public void initializationFailsIfISINParameterDoesNotReferToAnAvailableStock() {
        parametersBuilder.setISIN(ISIN.Allianz);

        try {
            initializeTradingStrategy();
        }
        catch(StrategyInitializationException ex) {
            Assert.assertEquals(String.format("The ISIN parameter '%s' does not refer to an available stock.", ISIN.Allianz.getText()), ex.getMessage());
            return;
        }

        Assert.fail("StrategyInitializationException expected.");
    }
}
