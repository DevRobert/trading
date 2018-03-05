package trading.strategy.progressive;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import trading.Amount;
import trading.ISIN;
import trading.account.Account;
import trading.broker.Broker;
import trading.broker.VirtualBroker;
import trading.market.HistoricalMarketData;
import trading.market.MarketPriceSnapshot;
import trading.market.MarketPriceSnapshotBuilder;
import trading.strategy.StrategyInitializationException;
import trading.strategy.WaitFixedPeriodTrigger;

public class ProgressiveTradingStrategyInitializationTest {
    private Account account;
    private HistoricalMarketData historicalMarketData;
    private Broker broker;
    private ProgressiveTradingStrategyParametersBuilder parametersBuilder;

    @Before
    public void before() {
        parametersBuilder = new ProgressiveTradingStrategyParametersBuilder();
        parametersBuilder.setISIN(ISIN.MunichRe);
        parametersBuilder.setBuyTrigger(new WaitFixedPeriodTrigger(1));
        parametersBuilder.setSellTrigger(new WaitFixedPeriodTrigger(1));
        parametersBuilder.setResetTrigger(new WaitFixedPeriodTrigger(1));

        Amount availableMoney = new Amount(50000.0);
        account = new Account(availableMoney);

        MarketPriceSnapshotBuilder marketPriceSnapshotBuilder = new MarketPriceSnapshotBuilder();
        MarketPriceSnapshot initialClosingMarketPrices = marketPriceSnapshotBuilder.build();
        historicalMarketData = new HistoricalMarketData(initialClosingMarketPrices);

        broker = new VirtualBroker(account, historicalMarketData);
    }

    private void initializeTradingStrategy() {
        ProgressiveTradingStrategyParameters parameters = null;

        if(parametersBuilder != null) {
            parameters = parametersBuilder.build();
        }

        new ProgressiveTradingStrategy(parameters, account, broker, historicalMarketData);
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
        account = null;

        try {
            initializeTradingStrategy();;
        }
        catch(StrategyInitializationException ex) {
            Assert.assertEquals("The account was not specified.", ex.getMessage());
            return;
        }

        Assert.fail("StrategyInitializationException expected.");
    }

    @Test
    public void initializationFailsIfNoBrokerSpecified() {
        broker = null;

        try {
            initializeTradingStrategy();;
        }
        catch(StrategyInitializationException ex) {
            Assert.assertEquals("The broker was not specified.", ex.getMessage());
            return;
        }

        Assert.fail("StrategyInitializationException expected.");
    }

    @Test
    public void initializationFailsIfNoHistoricalDataSpecified() {
        historicalMarketData = null;

        try {
            initializeTradingStrategy();;
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
