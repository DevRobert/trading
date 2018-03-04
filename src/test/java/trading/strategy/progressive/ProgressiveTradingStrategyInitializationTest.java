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

public class ProgressiveTradingStrategyInitializationTest {
    private ProgressiveTradingStrategyParameters parameters;
    private Account account;
    private HistoricalMarketData historicalMarketData;
    private Broker broker;

    @Before
    public void before() {
        parameters = ProgressiveTradingStrategyParameters.getDefault();

        Amount availableMoney = new Amount(50000.0);
        account = new Account(availableMoney);

        MarketPriceSnapshotBuilder marketPriceSnapshotBuilder = new MarketPriceSnapshotBuilder();
        MarketPriceSnapshot initialClosingMarketPrices = marketPriceSnapshotBuilder.build();
        historicalMarketData = new HistoricalMarketData(initialClosingMarketPrices);

        broker = new VirtualBroker(account, historicalMarketData);
    }

    private void initializeTradingStrategy() {
        new ProgressiveTradingStrategy(parameters, account, broker, historicalMarketData);
    }

    @Test
    public void initializationFailsIfNoParametersSpecified() {
        parameters = null;

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
        parameters = new ProgressiveTradingStrategyParameters(
                ISIN.Allianz,
                parameters.getBuyTriggerRisingDaysInSequence(),
                parameters.getSellTriggerDecliningDays(),
                parameters.getSellTriggerMaxDays(),
                parameters.getRestartTriggerDecliningDays()
        );

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
