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
        this.transactionType = transactionType;
        this.isin = isin;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.commission = commission;
    }
}
