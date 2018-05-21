package trading.domain.account;

import trading.domain.Amount;
import trading.domain.ISIN;

import java.time.LocalDate;

public class DividendTransactionBuilder {
    private LocalDate date;
    private ISIN isin;
    private Amount amount;

    public DividendTransactionBuilder setDate(LocalDate date) {
        this.date = date;
        return this;
    }

    public DividendTransactionBuilder setIsin(ISIN isin) {
        this.isin = isin;
        return this;
    }

    public DividendTransactionBuilder setAmount(Amount amount) {
        this.amount = amount;
        return this;
    }

    public DividendTransaction build() {
        return new DividendTransaction(this.date, this.isin, this.amount);
    }
}
