package trading.domain.strategy.progressive;

import org.junit.Assert;
import org.junit.Test;
import trading.domain.Amount;
import trading.domain.DayCount;
import trading.domain.ISIN;
import trading.domain.Quantity;
import trading.domain.account.Account;
import trading.domain.account.Position;
import trading.domain.strategy.AlwaysFiresTrigger;
import trading.domain.strategy.WaitFixedPeriodTrigger;

public class ProgressiveTradingStrategyTest extends ProgressiveTradingStrategyTestBase {
    /**
     * Default Parameters
     *
     *  - ISIN: MunichRe
     *  - buyTrigger: NotImplementedTrigger
     *  - sellTrigger: NotImplementedTrigger
     *  - resetTrigger: NotImplementedTrigger
     */

    /**
     * Phase A: Wait and buy stocks
     * <p>
     * Activates buy trigger and sets buy order for given ISIN when buy trigger fires.
     * The maximum possible amount of available money is used for this order.
     * Afterwards, phase B is entered.
     */

    @Test
    public void buyOrderIsSetInitially_ifBuyTriggerFiresImmediately() {
        parametersBuilder.setBuyTriggerFactory(isin -> new WaitFixedPeriodTrigger(this.historicalMarketData, new DayCount(0)));

        beginHistory(ISIN.MunichRe, new Amount(1000.0));

        beginSimulation();
        openDay();

        assertPositionHasPositiveQuantity(ISIN.MunichRe);
    }

    @Test
    public void buyOrderIsNotSetInitially_ifBuyTriggerFiresAfterOneDay() {
        parametersBuilder.setBuyTriggerFactory(isin -> new WaitFixedPeriodTrigger(this.historicalMarketData, new DayCount(1)));

        beginHistory(ISIN.MunichRe, new Amount(1000.0));

        beginSimulation();
        openDay();

        assertNoneOrEmptyPosition(ISIN.MunichRe);
    }

    @Test
    public void buyOrderIsSetAfterOneDay_ifBuyTriggerFiresAfterOneDay() {
        parametersBuilder.setBuyTriggerFactory(isin -> new WaitFixedPeriodTrigger(this.historicalMarketData, new DayCount(1)));

        beginHistory(ISIN.MunichRe, new Amount(1000.0));

        beginSimulation();

        openDay();
        closeDay(new Amount(1100.0));

        openDay();
        assertPositionHasPositiveQuantity(ISIN.MunichRe);
    }

    @Test
    public void buyOrderMaximumQuantityIsOrdered_ifNoCommissions() {
        parametersBuilder.setBuyTriggerFactory(isin -> new WaitFixedPeriodTrigger(this.historicalMarketData, new DayCount(0)));

        beginHistory(ISIN.MunichRe, new Amount(1000.0));

        beginSimulation();
        openDay();

        // Account initial balance is: 50,000
        // Expected price = 1,000
        // Expected quantity = 50
        // Expected full price = 50,000

        Position position = account.getPosition(ISIN.MunichRe);
        Assert.assertEquals(new Quantity(50), position.getQuantity());
    }

    @Test
    public void buyOrderIsSetAboutMaximumAmount_consideringCommissions() {
        parametersBuilder.setBuyTriggerFactory(isin -> new AlwaysFiresTrigger());

        this.commissionStrategy = totalPrice -> new Amount(1500.0);

        beginHistory(ISIN.MunichRe, new Amount(1000.0));

        beginSimulation();
        openDay();

        // Account initial balance is: 50,000.0
        // Expected price = 1,000
        // Commission = 1,500
        // Expected quantity = 48
        // Expected full price = 48,000

        Position position = account.getPosition(ISIN.MunichRe);
        Assert.assertEquals(new Quantity(48), position.getQuantity());
    }

    @Test
    public void noBuyOrderIsSetIfNoMoneyAvailable() {
        Amount availableMoney = new Amount(-10000.0);
        this.account = new Account(availableMoney);

        parametersBuilder.setBuyTriggerFactory(isin -> new AlwaysFiresTrigger());

        beginHistory(ISIN.MunichRe, new Amount(1000.0));

        beginSimulation();
        openDay();

        assertNoneOrEmptyPosition(ISIN.MunichRe);
    }

    /**
     * Phase B: Wait and sell stocks
     * <p>
     * Activates sell trigger and sets sell order for bought position when sell trigger fires.
     * Afterwards, phase C is entered.
     */

    @Test
    public void sellOrderIsSetOneDayAfterBuying_ifSellTriggerFiresImmediately() {
        parametersBuilder.setBuyTriggerFactory(isin -> new WaitFixedPeriodTrigger(historicalMarketData, new DayCount(0)));
        parametersBuilder.setSellTriggerFactory(isin -> new WaitFixedPeriodTrigger(historicalMarketData, new DayCount(0)));
        parametersBuilder.setResetTriggerFactory(isin -> new WaitFixedPeriodTrigger(historicalMarketData, new DayCount(10)));

        beginHistory(ISIN.MunichRe, new Amount(1000.0));
        beginSimulation();

        openDay();
        assertPositionHasPositiveQuantity(ISIN.MunichRe);
        closeDay(new Amount(1100.0));

        openDay();
        assertNoneOrEmptyPosition(ISIN.MunichRe);
    }

    @Test
    public void sellOrderIsSetTwoDaysAfterBuying_ifSellTriggerFiresAfterOneDay() {
        parametersBuilder.setBuyTriggerFactory(isin -> new WaitFixedPeriodTrigger(historicalMarketData, new DayCount(0)));
        parametersBuilder.setSellTriggerFactory(isin -> new WaitFixedPeriodTrigger(historicalMarketData, new DayCount(1)));
        parametersBuilder.setResetTriggerFactory(isin -> new WaitFixedPeriodTrigger(historicalMarketData, new DayCount(10)));

        beginHistory(ISIN.MunichRe, new Amount(1000.0));
        beginSimulation();

        openDay();
        assertPositionHasPositiveQuantity(ISIN.MunichRe);
        closeDay(new Amount(1100.0));

        openDay();
        assertPositionHasPositiveQuantity(ISIN.MunichRe);
        closeDay(new Amount(1200.0));

        openDay();
        assertNoneOrEmptyPosition(ISIN.MunichRe);
    }

    /**
     * Phase C: Wait and reset
     * <p>
     * Activates reset trigger and starts phase A immediately when reset trigger fires.
     */
    @Test
    public void sellOrderIsSetAgainOneDayAfterSelling_ifResetTriggerFiresImmediately_andBuyTriggerFiresImmediately() {
        parametersBuilder.setBuyTriggerFactory(isin -> new WaitFixedPeriodTrigger(historicalMarketData, new DayCount(0)));
        parametersBuilder.setSellTriggerFactory(isin -> new WaitFixedPeriodTrigger(historicalMarketData, new DayCount(0)));
        parametersBuilder.setResetTriggerFactory(isin -> new WaitFixedPeriodTrigger(historicalMarketData, new DayCount(0)));

        beginHistory(ISIN.MunichRe, new Amount(1000.0));

        beginSimulation();
        assertNoneOrEmptyPosition(ISIN.MunichRe);

        // First buying expected for next day (due to buy trigger period = 0)

        openDay();
        assertPositionHasPositiveQuantity(ISIN.MunichRe);
        closeDay(new Amount(1100.0));

        // First selling expected for next day (due to sell trigger period = 0)

        openDay();
        assertNoneOrEmptyPosition(ISIN.MunichRe);
        closeDay(new Amount(900.0));

        // Second buying expected for next day (due to reset trigger period = 0 and buy trigger period = 0)

        openDay();
        assertPositionHasPositiveQuantity(ISIN.MunichRe);
    }

    @Test
    public void sellOrderIsSetAgainTwoDaysAfterSelling_ifResetTriggerFiresAfterOneDay_andBuyTriggerFiresImmediately() {
        parametersBuilder.setBuyTriggerFactory(isin -> new WaitFixedPeriodTrigger(historicalMarketData, new DayCount(0)));
        parametersBuilder.setSellTriggerFactory(isin -> new WaitFixedPeriodTrigger(historicalMarketData, new DayCount(0)));
        parametersBuilder.setResetTriggerFactory(isin -> new WaitFixedPeriodTrigger(historicalMarketData, new DayCount(1)));

        beginHistory(ISIN.MunichRe, new Amount(1000.0));

        beginSimulation();
        assertNoneOrEmptyPosition(ISIN.MunichRe);

        // First buying expected for next day (due to buy trigger period = 0)

        openDay();
        assertPositionHasPositiveQuantity(ISIN.MunichRe);
        closeDay(new Amount(1100.0));

        // First selling expected for next day (due to sell trigger period = 0)

        openDay();
        assertNoneOrEmptyPosition(ISIN.MunichRe);
        closeDay(new Amount(1200.0));

        // Wait expected for next day (due to reset trigger period = 1 / not fulfilled yet)

        openDay();
        assertNoneOrEmptyPosition(ISIN.MunichRe);
        closeDay(new Amount(1100.0)); // declined (1 day in sequence)

        // Second buying expected for next day (due to reset trigger period = 1 / now fulfilled)

        openDay();
        assertPositionHasPositiveQuantity(ISIN.MunichRe);
    }
}
