package trading.domain.strategy.compound;

import trading.domain.Amount;
import trading.domain.ISIN;
import trading.domain.Quantity;
import trading.domain.account.Account;
import trading.domain.account.Position;
import trading.domain.broker.CommissionStrategy;
import trading.domain.broker.OrderRequest;
import trading.domain.broker.OrderType;
import trading.domain.market.HistoricalMarketData;
import trading.domain.market.MarketPriceSnapshot;
import trading.domain.strategy.TradingStrategy;
import trading.domain.strategy.TradingStrategyContext;
import trading.domain.strategy.Trigger;
import trading.domain.strategy.TriggerFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CompoundTradingStrategy implements TradingStrategy {
    private final TradingStrategyContext context;
    private final ScoringStrategy scoringStrategy;
    private final StockSelector stockSelector;
    private final TriggerFactory sellTriggerFactory;
    private final Map<ISIN, Trigger> sellTriggers;
    private final Set<ISIN> createSellTriggersAfterStocksBought;

    public CompoundTradingStrategy(CompoundTradingStrategyParameters parameters, TradingStrategyContext context) {
        if(parameters == null) {
            throw new RuntimeException("The trading strategy parameters were not specified.");
        }

        if(context == null) {
            throw new RuntimeException("The trading strategy context was not specified.");
        }

        this.context = context;
        this.scoringStrategy = parameters.getScoringStrategy();
        this.stockSelector = parameters.getStockSelector();
        this.sellTriggerFactory = parameters.getSellTriggerFactory();
        this.sellTriggers = new HashMap<>();
        this.createSellTriggersAfterStocksBought = new HashSet<>();
    }

    @Override
    public void prepareOrdersForNextTradingDay() {
        this.prepareSellOrders();
        this.prepareBuyOrders();
    }

    private void prepareSellOrders() {
        this.createSellTriggers();

        Set<ISIN> deleteSellTriggers = new HashSet<>();

        for(ISIN isin: this.sellTriggers.keySet()) {
            Trigger sellTrigger = this.sellTriggers.get(isin);

            if(sellTrigger.checkFires()) {
                Position position = this.context.getAccount().getPosition(isin);
                OrderRequest orderRequest = new OrderRequest(OrderType.SellMarket, isin, position.getQuantity());
                this.context.getBroker().setOrder(orderRequest);
                deleteSellTriggers.add(isin);
            }
        }

        for(ISIN isin: deleteSellTriggers) {
            this.sellTriggers.remove(isin);
        }
    }

    private void createSellTriggers() {
        for(ISIN isin: this.createSellTriggersAfterStocksBought) {
            Trigger sellTrigger = this.sellTriggerFactory.createTrigger(isin);
            this.sellTriggers.put(isin, sellTrigger);
        }

        this.createSellTriggersAfterStocksBought.clear();
    }

    private void prepareBuyOrders() {
        Account account = this.context.getAccount();

        HistoricalMarketData historicalMarketData = this.context.getHistoricalMarketData();
        CommissionStrategy commissionStrategy = this.context.getBroker().getCommissionStrategy();

        Amount totalCapital = account.getBalance();
        Amount availableMoney = account.getAvailableMoney();
        MarketPriceSnapshot marketPrices = historicalMarketData.getLastClosingMarketPrices();

        Map<ISIN, Quantity> currentStocks = account.getCurrentStocks();

        Scores scores = new MultiStockScoring().calculateScores(historicalMarketData, this.scoringStrategy);

        Map<ISIN, Quantity> buyStocks = this.stockSelector.selectStocks(totalCapital, availableMoney, scores, marketPrices, commissionStrategy, currentStocks);

        for(ISIN isin: buyStocks.keySet()) {
            Quantity buyQuantity = buyStocks.get(isin);

            if(buyQuantity.isZero()) {
                continue;
            }

            OrderRequest orderRequest = new OrderRequest(OrderType.BuyMarket, isin, buyQuantity);
            this.context.getBroker().setOrder(orderRequest);
            this.createSellTriggersAfterStocksBought.add(isin);
        }
    }
}