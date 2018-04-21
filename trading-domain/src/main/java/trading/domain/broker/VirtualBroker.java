package trading.domain.broker;

import trading.domain.Amount;
import trading.domain.ISIN;
import trading.domain.Transaction;
import trading.domain.TransactionType;
import trading.domain.account.Account;
import trading.domain.market.HistoricalMarketData;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class VirtualBroker implements Broker {
    private final Account account;
    private final HistoricalMarketData historicalMarketData;
    private final CommissionStrategy commissionStrategy;
    private Queue<OrderRequest> registeredOrderRequests;

    public VirtualBroker(Account account, HistoricalMarketData historicalMarketData, CommissionStrategy commissionStrategy) {
        if(account == null) {
            throw new RuntimeException("The account must be specified.");
        }

        if(historicalMarketData == null) {
            throw new RuntimeException("The historical market data must be specified.");
        }

        if(commissionStrategy == null) {
            throw new RuntimeException("The commission strategy must be specified.");
        }

        this.account = account;
        this.historicalMarketData = historicalMarketData;
        this.commissionStrategy = commissionStrategy;
        this.registeredOrderRequests = new LinkedBlockingQueue<>();
    }

    @Override
    public void setOrder(OrderRequest orderRequest) {
        this.registeredOrderRequests.add(orderRequest);
    }

    @Override
    public void notifyDayOpened() {
        OrderRequest orderRequest;

        while((orderRequest = this.registeredOrderRequests.poll()) != null) {
            processOrderRequest(orderRequest);
        }
    }

    @Override
    public CommissionStrategy getCommissionStrategy() {
        return this.commissionStrategy;
    }

    private void processOrderRequest(OrderRequest orderRequest) {
        switch (orderRequest.getOrderType()) {
            case BuyMarket:
                this.processMarketBuyOrderRequest(orderRequest);
                return;

            case SellMarket:
                this.processMarketSellOrderRequest(orderRequest);
                return;
        }

        throw new RuntimeException("The order request type is not supported.");
    }

    private void processMarketBuyOrderRequest(OrderRequest orderRequest) {
        ISIN isin = orderRequest.getIsin();
        Amount lastMarketPrice = this.historicalMarketData.getStockData(isin).getLastClosingMarketPrice();
        Amount totalPrice = lastMarketPrice.multiply(orderRequest.getQuantity());
        Amount commission = this.commissionStrategy.calculateCommission(totalPrice);
        Transaction transaction = new Transaction(TransactionType.Buy, isin, orderRequest.getQuantity(), totalPrice, commission);
        account.registerTransaction(transaction);
    }

    private void processMarketSellOrderRequest(OrderRequest orderRequest) {
        ISIN isin = orderRequest.getIsin();
        Amount lastMarketPrice = this.historicalMarketData.getStockData(isin).getLastClosingMarketPrice();
        Amount totalPrice = lastMarketPrice.multiply(orderRequest.getQuantity());
        Amount commission = this.commissionStrategy.calculateCommission(totalPrice);
        Transaction transaction = new Transaction(TransactionType.Sell, isin, orderRequest.getQuantity(), totalPrice, commission);
        account.registerTransaction(transaction);
    }
}
