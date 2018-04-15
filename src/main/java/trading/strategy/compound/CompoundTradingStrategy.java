package trading.strategy.compound;

import trading.Amount;
import trading.ISIN;
import trading.Quantity;
import trading.account.Account;
import trading.broker.Broker;
import trading.broker.CommissionStrategy;
import trading.broker.OrderRequest;
import trading.broker.OrderType;
import trading.market.HistoricalMarketData;
import trading.market.MarketPriceSnapshot;
import trading.strategy.TradingStrategy;
import trading.strategy.TradingStrategyContext;

import java.util.Map;

public class CompoundTradingStrategy implements TradingStrategy {
    private final TradingStrategyContext context;
    private final ScoringStrategy scoringStrategy;
    private final StockSelector stockSelector;

    public CompoundTradingStrategy(TradingStrategyContext context, ScoringStrategy scoringStrategy, StockSelector stockSelector) {
        if(context == null) {
            throw new RuntimeException("The trading strategy context was not specified.");
        }

        if(scoringStrategy == null) {
            throw new RuntimeException("The scoring strategy was not specified.");
        }

        if(stockSelector == null) {
            throw new RuntimeException("The stock selector was not specified.");
        }

        this.context = context;
        this.scoringStrategy = scoringStrategy;
        this.stockSelector = stockSelector;
    }

    @Override
    public void prepareOrdersForNextTradingDay() {
        // todo sell orders (e.g. trailing stop loss)

        Broker broker = this.context.getBroker();
        Account account = this.context.getAccount();

        HistoricalMarketData historicalMarketData = this.context.getHistoricalMarketData();
        CommissionStrategy commissionStrategy = broker.getCommissionStrategy();

        // todo tests

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
            broker.setOrder(orderRequest);
        }
    }
}
