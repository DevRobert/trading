package trading.domain.strategy.progressive;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import trading.domain.Amount;
import trading.domain.DayCount;
import trading.domain.ISIN;
import trading.domain.account.Account;
import trading.domain.broker.Broker;
import trading.domain.broker.CommissionStrategy;
import trading.domain.broker.VirtualBroker;
import trading.domain.broker.ZeroCommissionStrategy;
import trading.domain.market.HistoricalMarketData;
import trading.domain.market.MarketPriceSnapshot;
import trading.domain.market.MarketPriceSnapshotBuilder;
import trading.domain.strategy.StrategyInitializationException;
import trading.domain.strategy.TradingStrategyContext;
import trading.domain.strategy.WaitFixedPeriodTrigger;

import java.time.LocalDate;

public class ProgressiveTradingStrategyInitializationTest {
    private Account account;
    private HistoricalMarketData historicalMarketData;
    private ProgressiveTradingStrategyParametersBuilder parametersBuilder;
    private CommissionStrategy commissionStrategy;

    @Before
    public void before() {
        this.parametersBuilder = new ProgressiveTradingStrategyParametersBuilder();
        this.parametersBuilder.setISIN(ISIN.MunichRe);
        this.parametersBuilder.setBuyTriggerFactory(isin -> new WaitFixedPeriodTrigger(this.historicalMarketData, new DayCount(1)));
        this.parametersBuilder.setSellTriggerFactory(isin -> new WaitFixedPeriodTrigger(this.historicalMarketData, new DayCount(1)));
        this.parametersBuilder.setResetTriggerFactory(isin -> new WaitFixedPeriodTrigger(this.historicalMarketData, new DayCount(1)));

        Amount availableMoney = new Amount(50000.0);
        this.account = new Account(availableMoney);

        MarketPriceSnapshot initialClosingMarketPrices = new MarketPriceSnapshotBuilder()
                .setDate(LocalDate.now())
                .build();

        this.historicalMarketData = new HistoricalMarketData(initialClosingMarketPrices);

        this.commissionStrategy = new ZeroCommissionStrategy();
    }

    private void initializeTradingStrategy() {
        ProgressiveTradingStrategyParameters parameters = null;

        if(this.parametersBuilder != null) {
            parameters = this.parametersBuilder.build();
        }

        Broker broker = new VirtualBroker(this.account, this.historicalMarketData, this.commissionStrategy);

        TradingStrategyContext tradingStrategyContext = new TradingStrategyContext(this.account, broker, this.historicalMarketData);
        new ProgressiveTradingStrategy(parameters, tradingStrategyContext);
    }

    @Test
    public void initializationFails_ifParametersNotSpecified() {
        parametersBuilder = null;

        try {
            initializeTradingStrategy();
        }
        catch(StrategyInitializationException ex) {
            Assert.assertEquals("The strategy parameters must be specified.", ex.getMessage());
            return;
        }

        Assert.fail("StrategyInitializationException expected.");
    }

    @Test
    public void initializationFails_ifContextNotSpecified() {
        ProgressiveTradingStrategyParameters parameters = this.parametersBuilder.build();
        TradingStrategyContext context = null;

        try {
            new ProgressiveTradingStrategy(parameters, context);
        }
        catch(StrategyInitializationException ex) {
            Assert.assertEquals("The context must be specified.", ex.getMessage());
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
