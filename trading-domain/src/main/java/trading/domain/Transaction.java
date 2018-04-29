package trading.domain;

import java.time.LocalDate;

public class Transaction {
    private TransactionId id;
    private TransactionType transactionType;
    private ISIN isin;
    private Quantity quantity;
    private Amount totalPrice;
    private Amount commission;
    private LocalDate date;

    public Transaction(TransactionType transactionType, ISIN isin, Quantity quantity, Amount totalPrice, Amount commission, LocalDate date) {
        if(transactionType == null) {
            throw new RuntimeException("The transaction type must be specified.");
        }

        if(isin == null) {
            throw new RuntimeException("The transaction ISIN must be specified.");
        }

        if(quantity.getValue() < 0) {
            throw new RuntimeException("The transaction quantity must not be negative.");
        }

        if(quantity.getValue() == 0) {
            throw new RuntimeException("The transaction quantity must not be zero.");
        }

        if(totalPrice == null) {
            throw new RuntimeException("The transaction total price must be specified.");
        }

        if(commission == null) {
            throw new RuntimeException("The transaction commission must be specified.");
        }

        this.transactionType = transactionType;
        this.isin = isin;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.commission = commission;
        this.date = date;
    }

    public TransactionId getId() {
        return id;
    }

    public void setId(TransactionId id) {
        this.id = id;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public ISIN getIsin() {
        return isin;
    }

    public Quantity getQuantity() {
        return quantity;
    }

    public Amount getTotalPrice() {
        return totalPrice;
    }

    public Amount getCommission() {
        return commission;
    }

    public LocalDate getDate() {
        return this.date;
    }

    @Override
    public String toString() {
        return this.transactionType.toString() +
                " " + this.quantity.toString() +
                " " + this.isin.toString() +
                " for total " + this.getTotalPrice().toString() +
                " plus " + this.commission + " commission";
    }
}
