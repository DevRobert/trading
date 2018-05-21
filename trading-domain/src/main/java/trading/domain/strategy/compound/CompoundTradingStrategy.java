package trading.domain.strategy.compound;

import trading.domain.Amount;
import trading.domain.ISIN;
import trading.domain.Quantity;
import trading.domain.account.Account;
import trading.domain.broker.CommissionStrategy;
import trading.domain.broker.OrderRequest;
import trading.domain.broker.OrderType;
import trading.domain.market.HistoricalMarketData;
import trading.domain.market.MarketPriceSnapshot;
import trading.domain.strategy.TradingStrategy;
import trading.domain.strategy.TradingStrategyContext;

import java.util.Map;
import java.util.Set;

public class CompoundTradingStrategy implements TradingStrategy {
    private final TradingStrategyContext context;
    private final ScoringStrategy buyScoringStrategy;
    private final BuyStocksSelector buyStocksSelector;
    private final ScoringStrategy sellScoringStrategy;
    private final SellStocksSelector sellStocksSelector;

    public CompoundTradingStrategy(CompoundTradingStrategyParameters parameters, TradingStrategyContext context) {
        if(parameters == null) {
            throw new RuntimeException("The trading strategy parameters were not specified.");
        }

        if(context == null) {
            throw new RuntimeException("The trading strategy context was not specified.");
        }

        this.context = context;
        this.buyScoringStrategy = parameters.getBuyScoringStrategy();
        this.buyStocksSelector = parameters.getBuyStocksSelector();
        this.sellScoringStrategy = parameters.getSellScoringStrategy();
        this.sellStocksSelector = parameters.getSellStocksSelector();
    }

    @Override
    public void prepareOrdersForNextTradingDay() {
        this.prepareSellOrders();
        this.prepareBuyOrders();
    }

    private void prepareSellOrders() {
        Map<ISIN, Quantity> currentStocks = this.context.getAccount().getCurrentStocks();
        Set<ISIN> currentStockIsins = currentStocks.keySet();

        Scores scores = new MultiStockScoring().calculateScores(
                this.context.getHistoricalMarketData(),
                this.context.getAccount(),
                this.sellScoringStrategy,
                currentStockIsins
        );

        Map<ISIN, Quantity> sellStocks = this.sellStocksSelector.selectStocks(scores, currentStocks);

        for(ISIN isin: sellStocks.keySet()) {
            Quantity quantity = sellStocks.get(isin);
            OrderRequest orderRequest = new OrderRequest(OrderType.SellMarket, isin, quantity);
            this.context.getBroker().setOrder(orderRequest);
        }
    }

    private void prepareBuyOrders() {
        Account account = this.context.getAccount();

        HistoricalMarketData historicalMarketData = this.context.getHistoricalMarketData();
        CommissionStrategy commissionStrategy = this.context.getBroker().getCommissionStrategy();

        Amount totalCapital = account.getBalance();
        Amount availableMoney = account.getAvailableMoney();
        MarketPriceSnapshot marketPrices = historicalMarketData.getLastClosingMarketPrices();

        Map<ISIN, Quantity> currentStocks = account.getCurrentStocks();

        Set<ISIN> isins = historicalMarketData.getAvailableStocks();
        Scores scores = new MultiStockScoring().calculateScores(historicalMarketData, account, this.buyScoringStrategy, isins);

        Map<ISIN, Quantity> buyStocks = this.buyStocksSelector.selectStocks(totalCapital, availableMoney, scores, marketPrices, commissionStrategy, currentStocks);

        for(ISIN isin: buyStocks.keySet()) {
            Quantity buyQuantity = buyStocks.get(isin);

            if(buyQuantity.isZero()) {
                continue;
            }

            OrderRequest orderRequest = new OrderRequest(OrderType.BuyMarket, isin, buyQuantity);
            this.context.getBroker().setOrder(orderRequest);
        }
    }
}
