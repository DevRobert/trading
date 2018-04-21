package trading.domain.strategy.progressive;

import trading.domain.Amount;
import trading.domain.Quantity;
import trading.domain.account.Position;
import trading.domain.broker.CommissionStrategy;
import trading.domain.broker.OrderRequest;
import trading.domain.broker.OrderType;
import trading.domain.market.HistoricalStockData;
import trading.domain.strategy.*;

/**
 * Progressive Trading Strategy
 *
 * Invests all available money into one specified stock.
 * Buys and sells alternate and are dependent on the market situation
 * that is analyzed by different triggers.
 *
 * Parameters:
 *
 *  1. (ISIN) isin
 *  2. (Trigger) buyTrigger
 *  3. (Trigger) sellTrigger
 *  4. (Trigger) restartTrigger
 *
 * Phases:
 *
 * A. Wait and buy stocks
 *
 * Activates buy trigger and sets buy order for given ISIN when buy trigger fires.
 * The maximum possible amount of available money is used for this order.
 * Afterwards, phase B is entered.
 *
 * B. Wait and sell stocks
 *
 * Activates sell trigger and sets sell order for bought position when sell trigger fires.
 * Afterwards, phase C is entered.
 *
 * C. Wait and reset
 *
 * Activates reset trigger and starts phase A immediately when reset trigger fires.
 */
public class ProgressiveTradingStrategy implements TradingStrategy {
    private final ProgressiveTradingStrategyParameters parameters;
    private final TradingStrategyContext context;
    private final HistoricalStockData historicalStockData;

    private boolean initialization = true;
    private boolean inStateWaitAndBuyStocks = true;
    private boolean inStateWaitAndSellStocks = false;
    private boolean inStateWaitAndReset = false;

    private boolean activateSellTriggerAfterDayPassed = false;
    private boolean activateResetTriggerAfterDayPassed = false;

    private Trigger buyTrigger;
    private Trigger sellTrigger;
    private Trigger resetTrigger;

    public ProgressiveTradingStrategy(ProgressiveTradingStrategyParameters parameters, TradingStrategyContext context) {
        if (parameters == null) {
            throw new StrategyInitializationException("The strategy parameters must be specified.");
        }

        if (context == null) {
            throw new StrategyInitializationException("The context must be specified.");
        }

        if (!context.getHistoricalMarketData().getAvailableStocks().contains(parameters.getISIN())) {
            throw new StrategyInitializationException(String.format("The ISIN parameter '%s' does not refer to an available stock.", parameters.getISIN().getText()));
        }

        this.parameters = parameters;
        this.context = context;
        this.historicalStockData = context.getHistoricalMarketData().getStockData(parameters.getISIN());

        this.buyTrigger = this.parameters.getBuyTriggerFactory().createTrigger(parameters.getISIN());
        this.sellTrigger = null;
        this.resetTrigger = null;
    }

    @Override
    public void prepareOrdersForNextTradingDay() {
        if (activateSellTriggerAfterDayPassed) {
            activateSellTriggerAfterDayPassed = false;
            this.sellTrigger = this.parameters.getSellTriggerFactory().createTrigger(parameters.getISIN());
        }

        if (activateResetTriggerAfterDayPassed) {
            activateResetTriggerAfterDayPassed = false;
            this.resetTrigger = this.parameters.getResetTriggerFactory().createTrigger(parameters.getISIN());
        }

        if (this.inStateWaitAndReset) {
            this.waitAndReset();
            // can lead to a state change that has to be processed
            // immediately within this prepare orders call
        }

        if (this.inStateWaitAndBuyStocks) {
            this.waitAndBuyStocks();
        } else if (this.inStateWaitAndSellStocks) {
            this.waitAndSellStocks();
        }
    }

    private void waitAndBuyStocks() {
        Amount availableMoney = this.context.getAccount().getAvailableMoney();
        CommissionStrategy commissionStrategy = this.context.getBroker().getCommissionStrategy();
        Amount lastClosingMarketPrice = this.historicalStockData.getLastClosingMarketPrice();
        Quantity affordableQuantity = new AffordableQuantityCalculator().calculateAffordableQuantity(availableMoney, lastClosingMarketPrice, commissionStrategy);

        if (!affordableQuantity.isZero() && this.buyTrigger.checkFires()) {
            this.setBuyMarketOrder(affordableQuantity);

            this.inStateWaitAndBuyStocks = false;
            this.inStateWaitAndSellStocks = true;
            this.activateSellTriggerAfterDayPassed = true;
            this.buyTrigger = null;
        }
    }

    private void setBuyMarketOrder(Quantity affordableQuantity) {
        OrderRequest orderRequest = new OrderRequest(OrderType.BuyMarket, this.parameters.getISIN(), affordableQuantity);
        this.context.getBroker().setOrder(orderRequest);
    }

    private void waitAndSellStocks() {
        if (this.sellTrigger.checkFires()) {
            this.setSellMarketOrder();

            this.inStateWaitAndSellStocks = false;
            this.inStateWaitAndReset = true;
            this.activateResetTriggerAfterDayPassed = true;
            this.sellTrigger = null;
        }
    }

    private void setSellMarketOrder() {
        Position position = this.context.getAccount().getPosition(this.parameters.getISIN());
        OrderRequest orderRequest = new OrderRequest(OrderType.SellMarket, position.getISIN(), position.getQuantity());
        this.context.getBroker().setOrder(orderRequest);
    }

    private void waitAndReset() {
        if (this.resetTrigger.checkFires()) {
            this.inStateWaitAndReset = false;
            this.inStateWaitAndBuyStocks = true;

            this.buyTrigger = this.parameters.getBuyTriggerFactory().createTrigger(this.parameters.getISIN());
            this.resetTrigger = null;
        }
    }
}
