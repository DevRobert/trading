package trading.broker;

import trading.Amount;
import trading.ISIN;
import trading.Transaction;
import trading.TransactionType;
import trading.account.Account;
import trading.market.HistoricalMarketData;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class VirtualBroker implements Broker {
    private final Account account;
    private final HistoricalMarketData historicalMarketData;
    private Queue<OrderRequest> registeredOrderRequests;

    public VirtualBroker(Account account, HistoricalMarketData historicalMarketData) {
        this.account = account;
        this.historicalMarketData = historicalMarketData;
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
        Amount commission = new Amount(0.0);
        Transaction transaction = new Transaction(TransactionType.Buy, isin, orderRequest.getQuantity(), totalPrice, commission);
        account.registerTransaction(transaction);
    }

    private void processMarketSellOrderRequest(OrderRequest orderRequest) {
        ISIN isin = orderRequest.getIsin();
        Amount lastMarketPrice = this.historicalMarketData.getStockData(isin).getLastClosingMarketPrice();
        Amount totalPrice = lastMarketPrice.multiply(orderRequest.getQuantity());
        Amount commission = new Amount(0.0);
        Transaction transaction = new Transaction(TransactionType.Sell, isin, orderRequest.getQuantity(), totalPrice, commission);
        account.registerTransaction(transaction);
    }
}
