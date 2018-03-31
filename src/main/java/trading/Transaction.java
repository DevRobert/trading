package trading;

public class Transaction {
    private TransactionType transactionType;
    private ISIN isin;
    private Quantity quantity;
    private Amount totalPrice;
    private Amount commission;

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

    public Transaction(TransactionType transactionType, ISIN isin, Quantity quantity, Amount totalPrice, Amount commission) {
        if(quantity.getValue() < 0) {
            throw new RuntimeException("The transaction quantity must not be negative.");
        }

        if(quantity.getValue() == 0) {
            throw new RuntimeException("The transaction quantity must not be zero.");
        }

        this.transactionType = transactionType;
        this.isin = isin;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.commission = commission;
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
