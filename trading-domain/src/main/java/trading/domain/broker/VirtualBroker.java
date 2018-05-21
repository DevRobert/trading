package trading.domain.broker;

import trading.domain.Amount;
import trading.domain.DomainException;
import trading.domain.ISIN;
import trading.domain.account.Account;
import trading.domain.account.Transaction;
import trading.domain.account.TransactionBuilder;
import trading.domain.account.TransactionType;
import trading.domain.market.HistoricalMarketData;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
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
    public void notifyDayOpened(LocalDate date) {
        OrderRequest orderRequest;

        while((orderRequest = this.registeredOrderRequests.poll()) != null) {
            processOrderRequest(orderRequest, date);
        }
    }

    @Override
    public CommissionStrategy getCommissionStrategy() {
        return this.commissionStrategy;
    }

    private void processOrderRequest(OrderRequest orderRequest, LocalDate date) {
        switch (orderRequest.getOrderType()) {
            case BuyMarket:
                this.processMarketBuyOrderRequest(orderRequest, date);
                return;

            case SellMarket:
                this.processMarketSellOrderRequest(orderRequest, date);
                return;
        }

        throw new RuntimeException("The order request type is not supported.");
    }

    private void processMarketBuyOrderRequest(OrderRequest orderRequest, LocalDate date) {
        ISIN isin = orderRequest.getIsin();
        Amount lastMarketPrice = this.historicalMarketData.getStockData(isin).getLastClosingMarketPrice();
        Amount totalPrice = lastMarketPrice.multiply(orderRequest.getQuantity());
        Amount commission = this.commissionStrategy.calculateCommission(totalPrice);

        if(totalPrice.getValue() + commission.getValue() > this.account.getAvailableMoney().getValue()) {
            throw new DomainException("The order request cannot be processed as it requires more money than available.");
        }

        Transaction transaction = new TransactionBuilder()
                .setTransactionType(TransactionType.Buy)
                .setIsin(isin)
                .setQuantity(orderRequest.getQuantity())
                .setTotalPrice(totalPrice)
                .setCommission(commission)
                .setDate(date)
                .build();

        account.registerTransaction(transaction);
    }

    private void processMarketSellOrderRequest(OrderRequest orderRequest, LocalDate date) {
        ISIN isin = orderRequest.getIsin();
        Amount lastMarketPrice = this.historicalMarketData.getStockData(isin).getLastClosingMarketPrice();
        Amount totalPrice = lastMarketPrice.multiply(orderRequest.getQuantity());
        Amount commission = this.commissionStrategy.calculateCommission(totalPrice);

        Transaction transaction = new TransactionBuilder()
                .setTransactionType(TransactionType.Sell)
                .setIsin(isin)
                .setQuantity(orderRequest.getQuantity())
                .setTotalPrice(totalPrice)
                .setCommission(commission)
                .setDate(date)
                .build();

        account.registerTransaction(transaction);
    }

    public List<OrderRequest> getRegisteredOrderRequests() {
        return new ArrayList<>(this.registeredOrderRequests);
    }
}
