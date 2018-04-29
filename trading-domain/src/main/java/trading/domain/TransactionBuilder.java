package trading.domain;

import java.time.LocalDate;

public class TransactionBuilder {
    private TransactionType transactionType;
    private ISIN isin;
    private Quantity quantity;
    private Amount totalPrice;
    private Amount commission;
    private LocalDate date;

    public TransactionBuilder setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
        return this;
    }

    public TransactionBuilder setIsin(ISIN isin) {
        this.isin = isin;
        return this;
    }

    public TransactionBuilder setQuantity(Quantity quantity) {
        this.quantity = quantity;
        return this;
    }

    public TransactionBuilder setTotalPrice(Amount totalPrice) {
        this.totalPrice = totalPrice;
        return this;
    }

    public TransactionBuilder setCommission(Amount commission) {
        this.commission = commission;
        return this;
    }

    public TransactionBuilder setDate(LocalDate date) {
        this.date = date;
        return this;
    }

    public Transaction build() {
        return new Transaction(this.transactionType, this.isin, this.quantity, this.totalPrice, this.commission, this.date);
    }
}
