package trading.domain.account;

import trading.domain.Amount;
import trading.domain.DomainException;
import trading.domain.ISIN;

import java.time.LocalDate;

public class DividendTransaction {
    private LocalDate date;
    private ISIN isin;
    private Amount amount;

    DividendTransaction(LocalDate date, ISIN isin, Amount amount) {
        if(date == null) {
            throw new DomainException("The date must be specified.");
        }

        if(isin == null) {
            throw new DomainException("The ISIN must be specified.");
        }

        if(amount == null) {
            throw new DomainException("The amount must be specified.");
        }

        this.date = date;
        this.isin = isin;
        this.amount = amount;
    }

    public LocalDate getDate() {
        return this.date;
    }

    public ISIN getIsin() {
        return this.isin;
    }

    public Amount getAmount() {
        return this.amount;
    }
}
