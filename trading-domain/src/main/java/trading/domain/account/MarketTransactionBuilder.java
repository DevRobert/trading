package trading.domain.account;

import trading.domain.Amount;
import trading.domain.ISIN;
import trading.domain.Quantity;

import java.time.LocalDate;

public class MarketTransactionBuilder {
    private MarketTransactionType transactionType;
    private ISIN isin;
    private Quantity quantity;
    private Amount totalPrice;
    private Amount commission;
    private LocalDate date;

    public MarketTransactionBuilder setTransactionType(MarketTransactionType transactionType) {
        this.transactionType = transactionType;
        return this;
    }

    public MarketTransactionBuilder setIsin(ISIN isin) {
        this.isin = isin;
        return this;
    }

    public MarketTransactionBuilder setQuantity(Quantity quantity) {
        this.quantity = quantity;
        return this;
    }

    public MarketTransactionBuilder setTotalPrice(Amount totalPrice) {
        this.totalPrice = totalPrice;
        return this;
    }

    public MarketTransactionBuilder setCommission(Amount commission) {
        this.commission = commission;
        return this;
    }

    public MarketTransactionBuilder setDate(LocalDate date) {
        this.date = date;
        return this;
    }

    public MarketTransaction build() {
        return new MarketTransaction(this.transactionType, this.isin, this.quantity, this.totalPrice, this.commission, this.date);
    }
}
