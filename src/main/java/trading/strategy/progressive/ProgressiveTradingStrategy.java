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

/**
 * Progressive Trading Strategy
 *
 * Invests all available money into one specified stock.
 * Buys and sells alternate and are dependent on the market situation.
 *
 * Parameters:
 *
 *  1. (ISIN) isin
 *  2. (int) buyTriggerRisingDaysInSequence; >= 0
 *  3. (int) sellTriggerDecliningDays; >= 0
 *  4. (int) sellTriggerMaxDays; >= 1
 *  5. (int) restartTriggerDecliningDays; >= 0
 *
 * Phases:
 *
 * A. Wait and buy stocks
 *
 * Sets buy order for given ISIN after a series of {buyTriggerRisingDaysInSequence} days has passed.
 * The maximum possible amount of available money is used for this order.
 *
 *
 * B. Wait and sell stocks
 *
 * Sets sell order for bought position when one of the following condition occurs:
 *  - {sellTriggerDecliningDaysInSequence} days with negative performance have passed after buying.
 *  - {sellTriggerMaxDays} days have passed after buying.
 *
 *
 * C. Wait and reset
 *
 * {restartTriggerDecliningDaysInSequence} days with negative performance have to be passed, so that Phase A is entered again.
 */
public class ProgressiveTradingStrategy implements TradingStrategy {
    private final ProgressiveTradingStrategyParameters parameters;
    private final Account account;
    private final Broker broker;
    private final HistoricalStockData historicalStockData;

    private boolean inStateWaitAndBuyStocks = true;
    private boolean inStateWaitAndSellStocks = false;
    private boolean inStateWaitAndReset = false;

    private int passedDaysSinceBuying = 0;

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
    }

    @Override
    public void prepareOrdersForNextTradingDay() {
        if(this.inStateWaitAndBuyStocks) {
            this.waitAndBuyStocks();
        }
        else if(this.inStateWaitAndSellStocks) {
            this.waitAndSellStocks();
        }
    }

    private void waitAndBuyStocks() {
        if(this.historicalStockData.getRisingDaysInSequence() >= this.parameters.getBuyTriggerRisingDaysInSequence()) {
            this.setBuyMarketOrder();

            this.inStateWaitAndBuyStocks = false;
            this.inStateWaitAndSellStocks  = true;
        }
    }

    private void setBuyMarketOrder() {
        Amount availableMoney = account.getAvailableMoney();
        Amount lastClosingMarketPrice = historicalStockData.getLastClosingMarketPrice();
        double maxQuantity = Math.floor(availableMoney.getValue() / lastClosingMarketPrice.getValue());

        Quantity quantity = new Quantity((int) maxQuantity);
        OrderRequest orderRequest = new OrderRequest(OrderType.BuyMarket, this.parameters.getISIN(), quantity);
        this.broker.setOrder(orderRequest);

        this.passedDaysSinceBuying = 0; // todo unit test
    }

    private void waitAndSellStocks() {
        this.passedDaysSinceBuying++;

        if(this.passedDaysSinceBuying >= this.parameters.getSellTriggerMaxDays()) {
            this.setSellMarketOrder();
        }
    }

    private void setSellMarketOrder() {
        Position position = account.getPosition(this.parameters.getISIN());
        OrderRequest orderRequest = new OrderRequest(OrderType.SellMarket, position.getISIN(), position.getQuantity());
        this.broker.setOrder(orderRequest);
    }
}
