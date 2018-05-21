package trading.domain.account;

import trading.domain.Amount;
import trading.domain.DomainException;
import trading.domain.ISIN;
import trading.domain.Quantity;

import java.time.LocalDate;

public class MarketTransaction extends Transaction {
    private MarketTransactionType transactionType;
    private ISIN isin;
    private Quantity quantity;
    private Amount totalPrice;
    private Amount commission;

    MarketTransaction(MarketTransactionType transactionType, ISIN isin, Quantity quantity, Amount totalPrice, Amount commission, LocalDate date) {
        super(date);

        if(transactionType == null) {
            throw new DomainException("The transaction type must be specified.");
        }

        if(isin == null) {
            throw new DomainException("The ISIN must be specified.");
        }

        if(quantity == null) {
            throw new DomainException("The quantity must be specified.");
        }

        if(quantity.getValue() < 0) {
            throw new DomainException("The quantity must not be negative.");
        }

        if(quantity.getValue() == 0) {
            throw new DomainException("The quantity must not be zero.");
        }

        if(totalPrice == null) {
            throw new DomainException("The total price must be specified.");
        }

        if(commission == null) {
            throw new DomainException("The commission must be specified.");
        }

        if(date == null) {
            throw new DomainException("The date must be specified.");
        }

        this.transactionType = transactionType;
        this.isin = isin;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.commission = commission;
        this.date = date;
    }

    public MarketTransactionType getTransactionType() {
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

    @Override
    public String toString() {
        return this.transactionType.toString() +
                " " + this.quantity.toString() +
                " " + this.isin.toString() +
                " for total " + this.getTotalPrice().toString() +
                " plus " + this.commission + " commission";
    }
}
