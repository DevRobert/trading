package trading.strategy.progressive;

import trading.Amount;
import trading.Quantity;
import trading.account.Account;
import trading.account.Position;
import trading.broker.Broker;
import trading.broker.OrderRequest;
import trading.broker.OrderType;
import trading.market.HistoricalMarketData;
import trading.market.HistoricalStockData;
import trading.strategy.StrategyInitializationException;
import trading.strategy.TradingStrategy;
import trading.strategy.Trigger;

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
    private final Account account;
    private final Broker broker;
    private final HistoricalStockData historicalStockData;
    private final HistoricalMarketData historicalMarketData;

    private boolean initialization = true;
    private boolean inStateWaitAndBuyStocks = true;
    private boolean inStateWaitAndSellStocks = false;
    private boolean inStateWaitAndReset = false;

    private boolean activateSellTriggerAfterDayPassed = false;
    private boolean activateResetTriggerAfterDayPassed = false;

    private Trigger buyTrigger;
    private Trigger sellTrigger;
    private Trigger resetTrigger;

    public ProgressiveTradingStrategy(ProgressiveTradingStrategyParameters parameters, Account account, Broker broker, HistoricalMarketData historicalMarketData) {
        if (parameters == null) {
            throw new StrategyInitializationException("The strategy parameters were not specified.");
        }

        if (account == null) {
            throw new StrategyInitializationException("The account was not specified.");
        }

        if (broker == null) {
            throw new StrategyInitializationException("The broker was not specified.");
        }

        if (historicalMarketData == null) {
            throw new StrategyInitializationException("The historical market data were not specified.");
        }

        if (!historicalMarketData.getAvailableStocks().contains(parameters.getISIN())) {
            throw new StrategyInitializationException(String.format("The ISIN parameter '%s' does not refer to an available stock.", parameters.getISIN().getText()));
        }

        this.parameters = parameters;
        this.account = account;
        this.broker = broker;
        this.historicalStockData = historicalMarketData.getStockData(parameters.getISIN());
        this.historicalMarketData = historicalMarketData;

        this.buyTrigger = this.parameters.getBuyTriggerFactory().createTrigger(historicalMarketData);
        this.sellTrigger = null;
        this.resetTrigger = null;
    }

    @Override
    public void prepareOrdersForNextTradingDay() {
        if (initialization) {
            initialization = false;
        } else {
            if (this.buyTrigger != null) {
                this.buyTrigger.notifyDayPassed();
            }

            if (this.sellTrigger != null) {
                this.sellTrigger.notifyDayPassed();
            }

            if (this.resetTrigger != null) {
                this.resetTrigger.notifyDayPassed();
            }
        }

        if (activateSellTriggerAfterDayPassed) {
            activateSellTriggerAfterDayPassed = false;
            this.sellTrigger = this.parameters.getSellTriggerFactory().createTrigger(this.historicalMarketData);
        }

        if (activateResetTriggerAfterDayPassed) {
            activateResetTriggerAfterDayPassed = false;
            this.resetTrigger = this.parameters.getResetTriggerFactory().createTrigger(this.historicalMarketData);
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
        if (this.buyTrigger.checkFires()) {
            this.setBuyMarketOrder();

            this.inStateWaitAndBuyStocks = false;
            this.inStateWaitAndSellStocks = true;
            this.activateSellTriggerAfterDayPassed = true;
            this.buyTrigger = null;
        }
    }

    private void setBuyMarketOrder() {
        Amount availableMoney = account.getAvailableMoney();
        Amount lastClosingMarketPrice = historicalStockData.getLastClosingMarketPrice();
        double maxQuantity = Math.floor(availableMoney.getValue() / lastClosingMarketPrice.getValue());

        Quantity quantity = new Quantity((int) maxQuantity);
        OrderRequest orderRequest = new OrderRequest(OrderType.BuyMarket, this.parameters.getISIN(), quantity);
        this.broker.setOrder(orderRequest);
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
        Position position = account.getPosition(this.parameters.getISIN());
        OrderRequest orderRequest = new OrderRequest(OrderType.SellMarket, position.getISIN(), position.getQuantity());
        this.broker.setOrder(orderRequest);
    }

    private void waitAndReset() {
        if (this.resetTrigger.checkFires()) {
            this.inStateWaitAndReset = false;
            this.inStateWaitAndBuyStocks = true;

            this.buyTrigger = this.parameters.getBuyTriggerFactory().createTrigger(this.historicalMarketData);
            this.resetTrigger = null;
        }
    }
}
