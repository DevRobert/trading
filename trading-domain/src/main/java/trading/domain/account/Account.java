package trading.domain.account;

import trading.domain.*;
import trading.domain.market.MarketPriceSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Account {
    private AccountId id;
    private Amount availableMoney;
    private HashMap<ISIN, Position> positions = new HashMap<>();
    private Amount commissions;
    private Amount balance;
    private final List<Transaction> processedTransactions;

    public Account(Amount availableMoney) {
        this.commissions = Amount.Zero;
        this.availableMoney = availableMoney;
        this.balance = availableMoney;
        this.processedTransactions = new ArrayList<>();
    }

    public Amount getCommissions() {
        return commissions;
    }

    public Amount getAvailableMoney() {
        return availableMoney;
    }

    public Amount getBalance() {
        return balance;
    }

    public Position getPosition(ISIN isin) throws PositionNotFoundException {
        Position position = this.positions.get(isin);

        if(position == null) {
            throw new PositionNotFoundException();
        }

        return position;
    }

    public boolean hasPosition(ISIN isin) {
        return this.positions.containsKey(isin);
    }

    public List<Transaction> getProcessedTransactions() {
        return processedTransactions;
    }

    public AccountId getId() {
        return this.id;
    }

    public void setId(AccountId accountId) {
        // todo test must not be updated if set

        this.id = accountId;
    }

    public void registerTransaction(Transaction transaction) throws AccountStateException {
        this.ensureTransactionCanBePaid(transaction);

        Position position = this.getPositionOrCreatePending(transaction.getIsin());

        try {
            this.handleTransaction(transaction, position);
        }
        catch(AccountStateException ex) {
            if(position.isCreationPending()) {
                this.positions.remove(transaction.getIsin());
            }

            throw ex;
        }

        if(position.isCreationPending()) {
            position.confirmCreation();
        }

        this.updateBalances(transaction);

        this.processedTransactions.add(transaction);
    }

    private Position getPositionOrCreatePending(ISIN isin) {
        if(this.hasPosition(isin)) {
            return this.getPosition(isin);
        }

        Position position = new Position(isin, Quantity.Zero, Amount.Zero);
        this.positions.put(isin, position);
        return position;
    }

    private void ensureTransactionCanBePaid(Transaction transaction) throws AccountStateException {
        Amount requiredAmount;

        if(transaction.getTransactionType() == TransactionType.Buy) {
            requiredAmount = transaction.getTotalPrice();
        }
        else if(transaction.getTransactionType() == TransactionType.Sell) {
            requiredAmount = Amount.Zero;
        }
        else {
            throw new RuntimeException("Transaction type not supported: " + transaction.getTransactionType());
        }

        if(requiredAmount.getValue() > 0 && requiredAmount.getValue() > this.availableMoney.getValue()) {
            throw new AccountStateException("The total price exceeds the available money.");
        }
    }

    private void updateBalances(Transaction transaction) {
        this.commissions = this.commissions.add(transaction.getCommission());

        if(transaction.getTransactionType() == TransactionType.Buy) {
            this.availableMoney = this.availableMoney.subtract(transaction.getTotalPrice());
        }
        else if(transaction.getTransactionType() == TransactionType.Sell) {
            this.availableMoney = this.availableMoney.add(transaction.getTotalPrice());
        }
        else {
            throw new RuntimeException("Transaction type not supported: " + transaction.getTransactionType());
        }

        this.availableMoney = this.availableMoney.subtract(transaction.getCommission());

        this.balance = this.balance.subtract(transaction.getCommission());
    }

    private void handleTransaction(Transaction transaction, Position position) throws AccountStateException {
        if(transaction.getTransactionType() == TransactionType.Buy) {
            this.handleBuyTransaction(transaction, position);
        }
        else if(transaction.getTransactionType() == TransactionType.Sell) {
            this.handleSellTransaction(transaction, position);
        }
        else {
            throw new RuntimeException("Transaction type not supported: " + transaction.getTransactionType());
        }
    }

    private void handleBuyTransaction(Transaction transaction, Position position) throws AccountStateException {
        if(!position.getQuantity().isZero()) {
            throw new AccountStateException("Subsequent buy transactions for non-empty positions are not supported.");
        }

        position.setQuantity(transaction.getQuantity());
        position.setFullMarketPrice(transaction.getTotalPrice());
    }

    private void handleSellTransaction(Transaction transaction, Position position) throws AccountStateException {
        if(position.isCreationPending()) {
            throw new AccountStateException("The sell transaction could not be processed because there was no respective position found.");
        }

        this.preventPartialSellTransactions(transaction, position);
        this.preventExceedingSellTransactions(transaction, position);

        Amount margin = transaction.getTotalPrice().subtract(position.getFullMarketPrice());
        this.balance = this.balance.add(margin);

        Quantity quantity = position.getQuantity().subtract(transaction.getQuantity());
        position.setQuantity(quantity);
        position.setFullMarketPrice(Amount.Zero);
    }

    private void preventPartialSellTransactions(Transaction transaction, Position position) throws AccountStateException {
        if(transaction.getQuantity().getValue() < position.getQuantity().getValue()) {
            throw new AccountStateException("Partial sell transactions are not supported.");
        }
    }

    private void preventExceedingSellTransactions(Transaction transaction, Position position) throws AccountStateException {
        if(transaction.getQuantity().getValue() > position.getQuantity().getValue()) {
            throw new AccountStateException("The sell transaction states a higher quantity than the position has.");
        }
    }

    public void reportMarketPrices(MarketPriceSnapshot marketPriceSnapshot) {
        for(Position position: this.positions.values()) {
            Amount marketPrice = marketPriceSnapshot.getMarketPrice(position.getISIN());
            Amount previousFullMarketPrice = position.getFullMarketPrice();
            Amount newFullMarketPrice = marketPrice.multiply(position.getQuantity());
            Amount delta = newFullMarketPrice.subtract(previousFullMarketPrice);
            position.setFullMarketPrice(newFullMarketPrice);
            this.balance = this.balance.add(delta);
        }
    }

    public Map<ISIN, Quantity> getCurrentStocks() {
        Map<ISIN, Quantity> currentStocks = new HashMap<>();

        for(ISIN isin: this.positions.keySet()) {
            currentStocks.put(isin, this.positions.get(isin).getQuantity());
        }

        return currentStocks;
    }

    public Amount getTotalStocksMarketPrice() {
        Amount totalStocksMarketPrice = Amount.Zero;

        for(Position position: this.positions.values()) {
            totalStocksMarketPrice = totalStocksMarketPrice.add(position.getFullMarketPrice());
        }

        return totalStocksMarketPrice;
    }

    public Quantity getTotalStocksQuantity() {
        Quantity totalStocksQuantity = Quantity.Zero;

        for(Position position: this.positions.values()) {
            totalStocksQuantity = new Quantity(totalStocksQuantity.getValue() + position.getQuantity().getValue());
        }

        return totalStocksQuantity;
    }
}
