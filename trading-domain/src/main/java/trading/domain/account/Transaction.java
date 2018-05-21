package trading.domain.account;

import java.time.LocalDate;

public abstract class Transaction {
    protected LocalDate date;
    private TransactionId id;

    public Transaction(LocalDate date) {
        this.date = date;
    }

    public LocalDate getDate() {
        return this.date;
    }

    public TransactionId getId() {
        return id;
    }

    public void setId(TransactionId id) {
        this.id = id;
    }
}
