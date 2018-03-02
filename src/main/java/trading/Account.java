package trading;

import java.util.HashMap;

public class Account {
    private Amount availableMoney;
    private HashMap<ISIN, Position> positions = new HashMap<>();
    private Amount commissions;

    public Amount getCommissions() {
        return commissions;
    }

    public Amount getAvailableMoney() {
        return availableMoney;
    }

    public Account(Amount availableMoney) {
        this.commissions = Amount.Zero;
        this.availableMoney = availableMoney;
    }

    public void registerTransaction(Transaction transaction) throws StateException {
        this.ensureTransactionCanBePaid(transaction);

        // TODO Refactor Merge paths; create (revocable) position if not existing

        if(this.hasPosition(transaction.getIsin())) {
            this.handleTransactionForExistingPosition(transaction);
        }
        else {
            this.handleTransactionForNewPosition(transaction);
        }

        this.updateBalances(transaction);
    }

    private void ensureTransactionCanBePaid(Transaction transaction) {

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
    }

    private void handleTransactionForNewPosition(Transaction transaction) throws StateException {
        if(transaction.getTransactionType() == TransactionType.Buy) {
            this.handleBuyTransactionForNewPosition(transaction);
        }
        else if(transaction.getTransactionType() == TransactionType.Sell) {
            this.handleSellTransactionForNewPosition(transaction);
        }
        else {
            throw new RuntimeException("Transaction type not supported: " + transaction.getTransactionType());
        }
    }

    private void handleBuyTransactionForNewPosition(Transaction transaction) {
        ISIN isin = transaction.getIsin();
        Quantity quantity = transaction.getQuantity();
        Amount fullMarketPrice = transaction.getTotalPrice();

        Position position = new Position(isin, quantity, fullMarketPrice);

        positions.put(position.getISIN(), position);
    }

    private void handleSellTransactionForNewPosition(Transaction transaction) throws StateException {
        throw new StateException("The sell transaction could not be processed because there was no respective position found.");
    }

    private void handleTransactionForExistingPosition(Transaction transaction) throws StateException {
        Position position = this.getPosition(transaction.getIsin());

        if(transaction.getTransactionType() == TransactionType.Buy) {
            this.handleBuyTransactionForExistingPosition(transaction, position);
        }
        else if(transaction.getTransactionType() == TransactionType.Sell) {
            this.handleSellTransactionForExistingPosition(transaction, position);
        }
        else {
            throw new RuntimeException("Transaction type not supported: " + transaction.getTransactionType());
        }
    }

    private void handleBuyTransactionForExistingPosition(Transaction transaction, Position position) throws StateException {
        if(!position.getQuantity().isZero()) {
            throw new StateException("Subsequent buy transactions for uncompensated positions are not supported.");
        }

        position.setQuantity(transaction.getQuantity());
        position.setFullMarketPrice(transaction.getTotalPrice());
    }

    private void handleSellTransactionForExistingPosition(Transaction transaction, Position position) throws StateException {
        this.preventPartialSellTransactions(transaction, position);
        this.preventExceedingSellTransactions(transaction, position);

        Quantity quantity = position.getQuantity().subtract(transaction.getQuantity());
        position.setQuantity(quantity);
        position.setFullMarketPrice(Amount.Zero);
    }

    private void preventPartialSellTransactions(Transaction transaction, Position position) throws StateException {
        if(transaction.getQuantity().getValue() < position.getQuantity().getValue()) {
            throw new StateException("Partial sell transactions are not supported.");
        }
    }

    private void preventExceedingSellTransactions(Transaction transaction, Position position) throws StateException {
        if(transaction.getQuantity().getValue() > position.getQuantity().getValue()) {
            throw new StateException("The sell transaction states a higher quantity than the position has.");
        }
    }

    public Position getPosition(ISIN isin) {
        Position position = this.positions.get(isin);

        if(position == null) {
            throw new RuntimeException("Position not found.");
        }

        return position;
    }

    private boolean hasPosition(ISIN isin) {
        return this.positions.containsKey(isin);
    }
}
